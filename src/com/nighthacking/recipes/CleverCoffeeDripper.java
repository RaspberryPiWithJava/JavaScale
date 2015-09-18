package com.nighthacking.recipes;

import com.nighthacking.recipe.Step;
import com.nighthacking.recipe.Ingredient;
import com.nighthacking.recipe.Recipe;

/**
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public class CleverCoffeeDripper implements Recipe {

  private static final double CUP_SIZE = 300; // grams
  private final Ingredient beans;
  private final Ingredient water;

  public CleverCoffeeDripper(double strength) {
    beans = Ingredient.byWeight(CoffeeCalculator.groundWeight(strength, CUP_SIZE), "Graos de Cafe");
    water = Ingredient.byWeight(CUP_SIZE, "Agua");
  }

  @Override
  public String name() {
    return "Clever Coffee Dripper";
  }
  
  @Override
  public String description() {
    return "Preparacao precisa de uma caneca de cafe usando "
        + " um coador de cafe e uma balanca.";
  }

  @Override
  public Ingredient[] ingredients() {
    return new Ingredient[]{beans, water};
  }

  @Override
  public Step[] steps() {
    return new Step[]{
      Step.say("Adicione " + beans),
      Step.waitFor(beans),
      Step.say("Otimo, agora tire os graos de cafe da balanca."),
      Step.waitForClear(),
      Step.say("Hora do trabalho pesado! Moa os graos de cafe!!!"),
      Step.say("Coloque o filtro no coador de cafe e jogue o cafe moido dentro"),
      Step.waitForContents(),
      Step.tare(),
      Step.say("Coloque " + water + " a 97oC"),
      Step.waitForAtLeast(water),
      Step.countdown(60),
      Step.say("Pronto!  Coloque o coador de cafe em cima da caneca."),
      Step.waitForClear(),
      Step.say("Experimente o seu Java cafe agora!")
    };
  }
}
