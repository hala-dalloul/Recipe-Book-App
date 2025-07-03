package com.example.recipebook.e_RecipeDetails;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.recipebook.R;
import com.example.recipebook.Recipe;
import com.example.recipebook.Utils;
import com.example.recipebook.c_Home.HomeActivity;
import com.example.recipebook.d_AddEditRecipe.OperationRecipeActivity;
import com.example.recipebook.databinding.ActivityRecipeDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeDetailsActivity extends AppCompatActivity {
  ActivityRecipeDetailsBinding binding;
  FirebaseFirestore fireStore;
  AlertDialog loadingDialog;
  String recipe_id, recipe_category, identifier;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    binding = ActivityRecipeDetailsBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
    binding.mainLayoutRA.setVisibility(View.INVISIBLE);
    loadingDialog = new AlertDialog.Builder(RecipeDetailsActivity.this)
            .setView(Utils.createLoadingView(RecipeDetailsActivity.this,"Get The Recipe..."))
            .setCancelable(false)
            .create();
    loadingDialog.show();


    binding.deleteButtonRD.setVisibility(View.INVISIBLE);
    binding.editButtonRD.setVisibility(View.INVISIBLE);
    fireStore = FirebaseFirestore.getInstance();
    recipe_id = getIntent().getStringExtra(Utils.RECIPE_ID);
    recipe_category = getIntent().getStringExtra(Utils.RECIPE_CATEGORY);
    identifier = getIntent().getStringExtra(Utils.USER_IDENTIFIER);
    Log.d("TAGTAG", "onCreate: id"+recipe_id);
    Log.d("TAGTAG", "onCreate: category"+recipe_category);
    if (recipe_id != null && recipe_category != null) {
      fireStore.collection(Utils.CATEGORIES_COLLECTION)
              .document(recipe_category).get()
              .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  if (task.isSuccessful()) {
                    String categoryName = task.getResult().getString(Utils.CATEGORY_NAME);

                    fireStore.collection(Utils.CATEGORIES_COLLECTION)
                            .document(recipe_category)
                            .collection(Utils.RECIPES_COLLECTION)
                            .document(recipe_id).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                              @Override
                              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                  Recipe recipe = task.getResult().toObject(Recipe.class);
                                  if (recipe != null) {
                                    loadingDialog.dismiss();
                                    binding.mainLayoutRA.setVisibility(View.VISIBLE);
                                    Log.d("TAG", "onComplete: " + recipe.toString());
                                    Picasso.get().load(recipe.getRecipe_image()).into(binding.recipeImageRA);
                                    binding.recipeTitleRD.setText(recipe.getRecipe_title());
                                    binding.cookTimeRD.setText(recipe.getRecipe_time() + "m");
                                    binding.chefNameRD.setText(recipe.getChef_name());
                                    binding.categoryNameRD.setText(categoryName);

                                    binding.ingredientsLayoutRD.setMovementMethod(new ScrollingMovementMethod());
                                    ArrayList<String> ingredients = recipe.getRecipe_ingredients();
                                    binding.ingredientsLayoutRD.setText("");
                                    for (String ingredient : ingredients) {
                                      binding.ingredientsLayoutRD.append(ingredient + "\n");
                                    }
                                    Log.d("TAG", "onComplete: " + ingredients.toString());

                                    ArrayList<String> steps = recipe.getRecipe_steps();
                                    binding.stepsLayoutRD.setText("");
                                    for (String step : steps) {
                                      binding.stepsLayoutRD.append(step + "\n");
                                    }
                                    binding.stepsLayoutRD.setMovementMethod(new ScrollingMovementMethod());

                                    binding.youtubeLinkRD.setOnClickListener(v -> {
//                            todo handel open youtube link in youtube or in browser
                                      String videoUrl = recipe.getYoutube_link();
                                      Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                              android.net.Uri.parse(videoUrl));
                                      startActivity(webIntent);
                                    });

//                                todo handel delete button

                                    fireStore.collection(Utils.USERS_COLLECTION)
                                            .document(identifier).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                              @Override
                                              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                  if (task.getResult().getString("user_name").equals(recipe.getChef_name())) {
                                                    binding.deleteButtonRD.setVisibility(View.VISIBLE);
                                                    binding.deleteButtonRD.setOnClickListener(v -> {
                                                      loadingDialog = new AlertDialog.Builder(RecipeDetailsActivity.this)
                                                              .setView(Utils.createLoadingView(RecipeDetailsActivity.this, "Start Delete this Recipe..."))
                                                              .setCancelable(false)
                                                              .create();
                                                      loadingDialog.show();
                                                      fireStore.collection(Utils.USERS_COLLECTION).document(identifier)
                                                              .collection(Utils.RECIPES_COLLECTION).document(recipe_id)
                                                              .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                  if (task.isSuccessful()) {
                                                                    fireStore.collection(Utils.CATEGORIES_COLLECTION)
                                                                            .document(Utils.CATEGORY_ALL)
                                                                            .collection(Utils.RECIPES_COLLECTION)
                                                                            .document(recipe_id).delete()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                              @Override
                                                                              public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                  fireStore.collection(Utils.CATEGORIES_COLLECTION)
                                                                                          .document(recipe_category)
                                                                                          .collection(Utils.RECIPES_COLLECTION)
                                                                                          .document(recipe_id).delete()
                                                                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                              if (task.isSuccessful()) {
                                                                                                loadingDialog.dismiss();
                                                                                                Toast.makeText(RecipeDetailsActivity.this, "DELETE Recipe Successful!", Toast.LENGTH_SHORT).show();
                                                                                                startActivity(new Intent(RecipeDetailsActivity.this, HomeActivity.class)
                                                                                                        .putExtra(Utils.USER_IDENTIFIER, identifier));
                                                                                                finish();
                                                                                              }
                                                                                            }
                                                                                          });
                                                                                }
                                                                              }
                                                                            });
                                                                  }
                                                                }
                                                              });
                                                    });

//                                                todo handel edit button
                                                    binding.editButtonRD.setVisibility(View.VISIBLE);
                                                    binding.editButtonRD.setOnClickListener(v -> {
                                                      startActivity(new Intent
                                                              (RecipeDetailsActivity.this, OperationRecipeActivity.class)
                                                              .putExtra(Utils.EDIT_OPERATION, 2)
                                                              .putExtra(Utils.USER_IDENTIFIER, identifier)
                                                              .putExtra(Utils.RECIPE_ID, recipe.getRecipe_id())
                                                              .putExtra(Utils.RECIPE_CATEGORY, recipe.getRecipe_category())
                                                      );
                                                    });
                                                  }
                                                }
                                              }
                                            });
                                  }else{
                                    Log.d("TAG", "onComplete: "+identifier);
                                    startActivity(new Intent(RecipeDetailsActivity.this, HomeActivity.class)
                                            .putExtra(Utils.USER_IDENTIFIER,identifier));
                                    finish();
                                  }
                                }
                              }
                            });
                  }
                }
              });

    }

  }

}