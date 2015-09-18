package com.nighthacking.recipes;

import com.nighthacking.recipe.Step;
import com.nighthacking.recipe.Ingredient;
import com.nighthacking.recipe.Recipe;

/**
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public class AeroPressCoffee implements Recipe {

  private static final double CUP_SIZE = 300; // grams
  private static final double BREW_RATIO = .2; // beans / water
  private final Ingredient beans;
  private final Ingredient brewingWater;
  private final Ingredient extraWater;

  public AeroPressCoffee(double strength) {
    beans = Ingredient.byWeight(CoffeeCalculator.groundWeight(strength, CUP_SIZE), "Coffee Beans");
    brewingWater = Ingredient.byWeight(beans.getWeight() / BREW_RATIO, "Water");
    extraWater = Ingredient.byWeight(CUP_SIZE - brewingWater.getWeight(), "Water");
  }

  @Override
  public String name() {
    return "Aero Press Coffee";
  }
  
  @Override
  public String description() {
    return "Precisely brews 1 cup of coffee using an Aeropress Coffee Maker and a scale.";
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
      Step.say("Put your AeroPress filled with grounds on"),
      Step.waitForContents(),
      Step.tare(),
      Step.say("Pour " + brewingWater + " at 200F"),
      Step.waitFor(brewingWater),
      Step.say("Brewing time!  Stir for 10 seconds and wait for countdown to finish."),
      Step.countdown(60),
      Step.say("Done!  Take the AeroPress off the scale before proceeding."),
      Step.waitForClear(),
      Step.say("Now press it down firmly until all the liquid is extracted.\n"
      + "After you are done, place your cup on the scale"),
      Step.waitForContents(),
      Step.tare(),
      Step.say("Now add " + extraWater),
      Step.waitFor(extraWater),
      Step.say("Enjoy your Java brew!")
    };
  }
}
