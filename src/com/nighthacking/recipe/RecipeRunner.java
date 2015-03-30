package com.nighthacking.recipe;

import com.nighthacking.scales.Scale;
import com.nighthacking.scales.UsbScale;

/**
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public abstract class RecipeRunner implements RecipeEnvironment {

  private Scale scale;

  @Override
  public Scale getScale() {
    return scale;
  }

  public RecipeRunner() {
    scale = UsbScale.findScale();
    if (scale == null) {
      throw new IllegalStateException("USB device not found. Check deviceId/productId in code against dmesg output.");
    }
    scale.connect();
  }

  public void runRecipe(Recipe recipe) throws InterruptedException {
    getDisplay().say(recipe.description());
    getScale().waitFor(w -> getScale().isStable());
    if (getScale().getWeight() != 0) {
      getDisplay().say("Please empty the scale before we begin");
      getScale().waitFor(w -> w == 0);
    }

    for (Step step : recipe.steps()) {
      step.execute(this);
    }
  }

  public void close() {
    scale.close();
  }
}
