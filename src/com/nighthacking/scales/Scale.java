package com.nighthacking.scales;

import java.util.function.DoublePredicate;

/**
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public interface Scale {

  /**
   * Connect to the scale
   *
   * @throws IllegalStateException Thrown if the connection can't be opened.
   */
  void connect() throws IllegalStateException;

  /**
   * Gets the last known weight from the scale in grams. If the weight exceeds
   * the scale capacity, Double.MAX_VALUE will be returned. If the weight is
   * below zero relative to the last tare, either a negative value or
   * -Double.MAX_VALUE will be returned.
   *
   * @return Weight in grams
   */
  double getWeight();

  /**
   * Returns true if the scale is in a stable state. This will use the hardware
   * stability if it is supported, otherwise it will compare multiple readings
   * to approximate.
   *
   * @return True if stable, false if still changing
   */
  boolean isStable();

  /**
   * Block until the condition is met (returns true). The value passed in to the
   * predicate condition is the scale weight.
   *
   * @param condition Predicate lambda expression
   */
  void waitFor(DoublePredicate condition);

  /**
   * Block until the condition is met (returns true). The value passed in to the
   * predicate condition is the scale weight once a stable state is reached. If
   * the scale supports stable monitoring, it will use that capability,
   * otherwise this will approximate stability based on multiple readings. Note
   * that only positive scale weights are considered stable, so negative,
   * overweight, and zero readings will never trigger this condition.
   *
   * @param condition Predicate lambda expression
   */
  void waitForStable(DoublePredicate condition);

  /**
   * Zero the scale, or throw an exception if programmatic taring is not
   * supported.
   *
   * @throws UnsupportedOperationException Thrown if the scale doesn't support
   * programmatic taring
   */
  void tare() throws UnsupportedOperationException;

  /**
   * Close resources that are in use by the scale implementation.
   */
  void close();

}
