package com.nighthacking.scales;

import java.util.List;
import java.util.concurrent.Phaser;
import java.util.function.DoublePredicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbPipe;
import javax.usb.UsbServices;
import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import javax.usb.event.UsbPipeListener;

/**
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public class UsbScale implements Scale, UsbPipeListener {

  private final UsbDevice device;
  private UsbInterface iface;
  private UsbPipe pipe;
  private final byte[] data = new byte[6];
  private volatile boolean busy;
  private volatile boolean closing;
  private final Phaser scalePhaser = new Phaser(1);
  private volatile int weight;
  private volatile int scalingFactor;
  private volatile boolean grams;
  private volatile boolean negative;
  private volatile boolean overweight;
  private volatile boolean empty;
  private final int[] pastWeights = new int[] {-1, -1};
  private volatile boolean stable;
  // todo: Put the right conversion value in here:
  private static final double GRAMS_IN_OUNCE = 22;

  private UsbScale(UsbDevice device) {
    this.device = device;
  }

  public static Scale findScale() {
    try {
      UsbServices services = UsbHostManager.getUsbServices();
      UsbHub rootHub = services.getRootUsbHub();
      // Dymo M10 Scale:
      UsbDevice device = findDevice(rootHub, (short) 0x0922, (short) 0x8003);
      // Dymo M25 Scale:
      if (device == null) {
        device = findDevice(rootHub, (short) 0x0922, (short) 0x8004);
      }
      if (device == null) {
        return null;
      }
      return new UsbScale(device);
    } catch (UsbException ex) {
      throw new IllegalStateException("Unable to find devices", ex);
    }
  }

  private static UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
    for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
      UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
      if (desc.idVendor() == vendorId && desc.idProduct() == productId) {
        return device;
      }
      if (device.isUsbHub()) {
        device = findDevice((UsbHub) device, vendorId, productId);
        if (device != null) {
          return device;
        }
      }
    }
    return null;
  }

  private double scaleWeight() {
    double decimalWeight = weight;
    decimalWeight *= Math.pow(10, scalingFactor);
    return decimalWeight;
  }

  @Override
  public double getWeight() {
    return negative ? -Double.MAX_VALUE
        : overweight ? Double.MAX_VALUE
            : empty ? 0
                : grams ? scaleWeight()
                    : scaleWeight() / GRAMS_IN_OUNCE;
  }

  @Override
  public boolean isStable() {
    return stable;
  }

  @Override
  public void tare() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void connect() {
    try {
      open();
      pipe.asyncSubmit(data);
      busy = true;
    } catch (UsbException ex) {
      throw new IllegalStateException("Unable to connect to scale", ex);
    }
  }

  private void open() throws UsbException {
    UsbConfiguration configuration = device.getActiveUsbConfiguration();
    iface = configuration.getUsbInterface((byte) 0);
    // this allows us to steal the lock from the kernel
    iface.claim(usbInterface -> true);
    final List<UsbEndpoint> endpoints = iface.getUsbEndpoints();
    pipe = endpoints.get(0).getUsbPipe(); // there is only 1 endpoint
    pipe.addUsbPipeListener(this);
    pipe.open();
  }

  @Override
  public void waitFor(DoublePredicate condition) {
    while (!condition.test(getWeight())) {
      scalePhaser.awaitAdvance(scalePhaser.getPhase());
    }
  }

  @Override
  public void waitForStable(DoublePredicate condition) {
    double lastWeight, currentWeight = getWeight();
    do {
      lastWeight = currentWeight;
      while (!stable || empty || negative || lastWeight == currentWeight) {
        scalePhaser.awaitAdvance(scalePhaser.getPhase());
        currentWeight = getWeight();
      }
    } while (!condition.test(currentWeight));
  }

  @Override
  public void errorEventOccurred(UsbPipeErrorEvent upee) {
    busy = false;
    Logger.getLogger(UsbScale.class.getName()).log(Level.SEVERE, "Scale malfunction, closing...", upee.getUsbException());
    close();
    System.exit(-1);
  }

  @Override
  public void dataEventOccurred(UsbPipeDataEvent upde) {
    empty = data[1] == 2;
    overweight = data[1] == 6;
    negative = data[1] == 5;
    grams = data[2] == 2;
    scalingFactor = data[3];
    updateWeight((data[4] & 0xFF) + (data[5] << 8));
    if (closing) {
      busy = false;
    } else {
      try {
        pipe.asyncSubmit(data);
      } catch (UsbException ex) {
        errorEventOccurred(new UsbPipeErrorEvent(upde.getUsbPipe(), ex));
      }
    }
    scalePhaser.arrive();
  }

  private void updateWeight(int newWeight) {
    boolean isStable = true;
    for (int i = 0; i < pastWeights.length - 1; i++) {
      pastWeights[i] = pastWeights[i + 1];
      if (pastWeights[i] != weight) {
        isStable = false;
      }
    }
    pastWeights[pastWeights.length - 1] = weight;
    if (weight != newWeight) {
      isStable = false;
      weight = newWeight;
    }
    stable = isStable;
  }

  @Override
  public String toString() {
    return "USB Scale reading: " + scaleWeight() + (grams ? "g" : "oz");
  }

  @Override
  public void close() {
    int phase = scalePhaser.getPhase();
    closing = true;
    while (busy) {
      phase = scalePhaser.awaitAdvance(phase);
    }
    try {
      if (pipe.isOpen()) {
        pipe.close();
      }
      if (iface.isActive()) {
        iface.release();
      }
    } catch (UsbException | UsbDisconnectedException ex) {
      Logger.getLogger(UsbScale.class.getName()).log(Level.SEVERE, "Unable to close scale cleanly", ex);
    }
  }
}
