package com.nighthacking.recipes;

import com.nighthacking.recipe.Step;
import com.nighthacking.recipe.Ingredient;
import com.nighthacking.recipe.Recipe;

/**
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public class PourOverCoffee implements Recipe {

  private static final double CUP_SIZE = 200; // grams
  private static final double BREW_RATIO = .15; // beans / water
  private final Ingredient beans;
  private final Ingredient brewingWater;
  private final Ingredient extraWater;

  public PourOverCoffee(double strength) {
    beans = Ingredient.byWeight(CoffeeCalculator.grindWeight(strength, CUP_SIZE), "Coffee Beans");
    brewingWater = Ingredient.byWeight(CUP_SIZE * BREW_RATIO, "Water");
    extraWater = Ingredient.byWeight(CUP_SIZE - brewingWater.getWeight(), "Water");
  }

  @Override
  public String name() {
    return "Pour Over Coffee";
  }
  
  @Override
  public String description() {
    return "Precisely brews 1 cup of coffee using a pour over method and a scale.";
  }

  @Override
  public Ingredient[] ingredients() {
    return new Ingredient[]{beans, brewingWater, extraWater};
  }

  @Override
  public Step[] steps() {
    return new Step[]{
      Step.say("Add " + beans),
      Step.waitFor(beans),
      Step.say("Great, take your beans off the scale now."),
      Step.waitForClear(),
      Step.say("Put your cup, dripper, filter, and grinds on"),
      Step.waitForContents(),
      Step.tare(),
      Step.say("Pour " + brewingWater + " at 200F"),
      Step.waitForAtLeast(brewingWater),
      Step.tare(),
      Step.countdown(30),
      Step.say("Pour an additional " + extraWater),
      Step.waitForAtLeast(extraWater),
      Step.countdown(60),
      Step.say("Done!  Take your coffee off the scale."),
      Step.waitForClear(),
      Step.say("Enjoy your Java brew!")
    };
  }
}
