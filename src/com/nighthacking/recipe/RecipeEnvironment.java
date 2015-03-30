package com.nighthacking.recipe;

import com.nighthacking.scales.Scale;

/**
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public interface RecipeEnvironment {

  Display getDisplay();

  Scale getScale();
}
