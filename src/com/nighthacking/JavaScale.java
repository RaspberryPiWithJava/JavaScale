package com.nighthacking;

import com.nighthacking.recipes.AeroPressCoffee;
import com.nighthacking.recipe.CommandLineRecipeRunner;
import com.nighthacking.recipes.CoffeeCalculator;

/**
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public class JavaScale {

  public static void main(String[] args) throws InterruptedException {
    CommandLineRecipeRunner runner = new CommandLineRecipeRunner();
    runner.runRecipe(new AeroPressCoffee(CoffeeCalculator.STRONG));
    runner.close();
  }
}
