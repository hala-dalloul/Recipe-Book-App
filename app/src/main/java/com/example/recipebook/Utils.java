package com.example.recipebook;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class Utils {
//    Hash map keys
  public static final String USER_IDENTIFIER = "user_identifier";
  public static final String USER_EMAIL = "user_email";
  public static final String USER_PASSWORD = "user_password";
  public static final String USER_Name = "user_name";
  public static final String USER_Country = "user_country";
  public static final String USER_IMAGE = "user_image";
//  recipe keys
  public static final String CATEGORY_NAME= "category_name";
  public static final String RECIPE_ID = "recipe_id";
  public static final String RECIPE_TITLE = "recipe_title";
  public static final String YOUTUBE_LINK = "youtube_link";
  public static final String RECIPE_TIME = "recipe_time";
  public static final String RECIPE_Chef_NAME = "chef_name";
  public static final String RECIPE_CATEGORY = "recipe_category";
  public static final String RECIPE_IMAGE = "recipe_image";
  public static final String RECIPE_INGREDIENTS = "recipe_ingredients";
  public static final String RECIPE_STEPS = "recipe_steps";


//  Intent Keys
  public static final String ADD_OPERATION = "add_operation"; // equal 1
  public static final String EDIT_OPERATION = "edit_operation";//equal 2

//  Images Folder name
  public static final String USERS_FOLDER = "users";
  public static final String RECIPES_FOLDER = "recipes";

//  Firebase firestore collection name;
  public static final String USERS_COLLECTION = "users";
  public static final String RECIPES_COLLECTION = "recipes";
  public static final String CATEGORIES_COLLECTION = "categories";
  public static final String CATEGORY_ALL = "4eYyexHSPQTQqyCnTryf";

//  SharedPreferences key ->
  public static final String SP_KEY = "loginPrefs";
  public static final String BOOLEAN_KEY = "rememberMe";
  public static View createLoadingView(Context context, String text) {
    LinearLayout layout = new LinearLayout(context);
    layout.setOrientation(LinearLayout.HORIZONTAL);
    layout.setPadding(50, 50, 50, 50);
    layout.setGravity(Gravity.CENTER_VERTICAL);

    ProgressBar progressBar = new ProgressBar(context);
    progressBar.setIndeterminate(true);

    progressBar.getIndeterminateDrawable().setColorFilter(
            ContextCompat.getColor(context, R.color.orange),
            android.graphics.PorterDuff.Mode.SRC_IN
    );

    TextView message = new TextView(context);
    message.setText(text);
    message.setTextSize(16);
    message.setPadding(30, 0, 0, 0);

    layout.addView(progressBar);
    layout.addView(message);

    return layout;
  }
}
