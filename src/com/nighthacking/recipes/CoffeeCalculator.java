package com.nighthacking.recipes;

/**
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public class CoffeeCalculator {

  public static final double REGULAR = 55;
  public static final double RICH = 58;
  public static final double STRONG = 62;

  /**
   * Calculates the weight of grinds needed to reach a target coffee strength.
   *
   * @param strength Target strength in grams/liter
   * @param water Final water volume in grams (1g = 1ml)
   * @return Amount of grinds needed in grams
   */
  public static double grindWeight(double strength, double water) {
    return strength * (water / 1000);
  }
}
