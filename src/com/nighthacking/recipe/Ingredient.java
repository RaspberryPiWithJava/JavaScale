package com.nighthacking.recipe;

/**
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public class Ingredient {

  private double weight;
  private String description;

  public static Ingredient byWeight(double weight, String description) {
    Ingredient ingredient = new Ingredient();
    ingredient.weight = weight;
    ingredient.description = description;
    return ingredient;
  }

  public double getWeight() {
    return weight;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return String.format("%,.1fg of %s", weight, description);
  }
}
