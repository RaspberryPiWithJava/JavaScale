package com.nighthacking.recipe;

/**
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public interface Recipe {

  /**
   * 
   * 
   * @return
   */
  String name();

  /**
   *
   * @return
   */
  String description();

  /**
   *
   * @return
   */
  Ingredient[] ingredients();

  /**
   *
   * @return
   */
  Step[] steps();
}
