package com.nighthacking.recipe;

import com.nighthacking.scales.Scale;

/**
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public class Step {

  private final StepCommand command;

  private Step(StepCommand command) {
    this.command = command;
  }

  public Step() {
    this.command = null;
  }

  public void execute(RecipeEnvironment env) throws InterruptedException {
    command.execute(env);
  }

  public static Step say(String string) {
    return new Step(e -> e.getDisplay().say(string));
  }

  public static Step say(String string, Object... args) {
    return new Step(e -> e.getDisplay().say(string, args));
  }

  public static Step waitForFull() {
    return new Step(e -> e.getScale().waitForStable(w -> w > 0));
  }

  public static Step waitForClear() {
    return new Step(e -> e.getScale().waitFor(w -> w <= 0));
  }

  public static Step waitForContents() {
    return new Step(e -> {
      Scale s = e.getScale();
      final double originalWeight = s.getWeight();
      s.waitForStable(w -> w != originalWeight);
    });
  }

  public static Step tare() {
    return new Step(e -> {
      try {
        e.getScale().tare();
      } catch (UnsupportedOperationException ex) {
        e.getDisplay().say("Press the 'tare' button on the scale.");
        e.getScale().waitFor(w -> w == 0);
      }
    });
  }

  public static Step waitFor(Ingredient ingredient) {
    return waitFor(ingredient, ingredient.getWeight() / 10);
  }

  public static Step waitFor(Ingredient ingredient, double margin) {
    return new Step(e -> e.getScale().waitForStable(w -> Math.abs(w - ingredient.getWeight()) < margin));
  }

  public static Step waitForAtLeast(Ingredient ingredient) {
    return waitForAtLeast(ingredient, ingredient.getWeight() / 10);
  }

  public static Step waitForAtLeast(Ingredient ingredient, double margin) {
    return new Step(e -> e.getScale().waitFor(w -> w > ingredient.getWeight() - margin / 2));
  }

  public static Step countdown(int seconds) {
    return new Step(e -> e.getDisplay().countdown(seconds));
  }

  @FunctionalInterface
  private static interface StepCommand {

    public void execute(RecipeEnvironment environment) throws InterruptedException;
  }
}
