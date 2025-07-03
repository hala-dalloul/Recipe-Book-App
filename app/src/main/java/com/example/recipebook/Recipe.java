package com.example.recipebook;

import java.util.ArrayList;

public class Recipe {
  private String recipe_id;
  private String recipe_title;
  private ArrayList<String> recipe_ingredients;
  private ArrayList<String> recipe_steps;
  private String recipe_image;
  private int recipe_time;
  private String recipe_category;
  private String chef_name;
  private String youtube_link;
  public Recipe(){}

  public Recipe(String recipe_id, String recipe_title, ArrayList<String> recipe_ingredients
          , ArrayList<String> recipe_steps, String recipe_image, int recipe_time
          , String recipe_category, String chef_name, String youtube_link) {
    this.recipe_id = recipe_id;
    this.recipe_title = recipe_title;
    this.recipe_ingredients = recipe_ingredients;
    this.recipe_steps = recipe_steps;
    this.recipe_image = recipe_image;
    this.recipe_time = recipe_time;
    this.recipe_category = recipe_category;
    this.chef_name = chef_name;
    this.youtube_link = youtube_link;
  }

  public String getRecipe_id() {
    return recipe_id;
  }

  public void setRecipe_id(String recipe_id) {
    this.recipe_id = recipe_id;
  }

  public String getRecipe_title() {
    return recipe_title;
  }

  public void setRecipe_title(String recipe_title) {
    this.recipe_title = recipe_title;
  }

  public ArrayList<String> getRecipe_ingredients() {
    return recipe_ingredients;
  }

  public void setRecipe_ingredients(ArrayList<String> recipe_ingredients) {
    this.recipe_ingredients = recipe_ingredients;
  }

  public ArrayList<String> getRecipe_steps() {
    return recipe_steps;
  }

  public void setRecipe_steps(ArrayList<String> recipe_steps) {
    this.recipe_steps = recipe_steps;
  }

  public String getRecipe_image() {
    return recipe_image;
  }

  public void setRecipe_image(String recipe_image) {
    this.recipe_image = recipe_image;
  }

  public int getRecipe_time() {
    return recipe_time;
  }

  public void setRecipe_time(int recipe_time) {
    this.recipe_time = recipe_time;
  }

  public String getRecipe_category() {
    return recipe_category;
  }

  public void setRecipe_category(String recipe_category) {
    this.recipe_category = recipe_category;
  }

  public String getChef_name() {
    return chef_name;
  }

  public void setChef_name(String chef_name) {
    this.chef_name = chef_name;
  }

  public String getYoutube_link() {
    return youtube_link;
  }

  public void setYoutube_link(String youtube_link) {
    this.youtube_link = youtube_link;
  }

  //  to test goals
  @Override
  public String toString() {
    return "Recipe{" +
            "recipe_id='" + recipe_id + '\'' +
            ", recipe_title='" + recipe_title + '\'' +
            ", recipe_ingredients=" + recipe_ingredients +
            ", recipe_instructions=" + recipe_steps +
            ", recipe_image='" + recipe_image + '\'' +
            ", recipe_time=" + recipe_time +
            ", recipe_category='" + recipe_category + '\'' +
            ", chef_name='" + chef_name + '\'' +
            '}';
  }
}
