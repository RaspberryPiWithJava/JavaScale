package com.nighthacking;

import com.nighthacking.recipes.AeroPressCoffee;
import com.nighthacking.recipe.CommandLineReciperRunner;
import com.nighthacking.recipes.CoffeeCalculator;

/**
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public class JavaScale {

  public static void main(String[] args) throws InterruptedException {
    CommandLineReciperRunner runner = new CommandLineReciperRunner();
    runner.runRecipe(new AeroPressCoffee(CoffeeCalculator.STRONG));
    runner.close();
  }
}
