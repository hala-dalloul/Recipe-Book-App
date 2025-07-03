package com.example.recipebook.f_Profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.util.Util;
import com.example.recipebook.R;
import com.example.recipebook.Recipe;
import com.example.recipebook.Utils;
import com.example.recipebook.b_RegisterToTheApp.RegisterActivity;
import com.example.recipebook.c_Home.RecipeAdapter;
import com.example.recipebook.d_AddEditRecipe.OperationRecipeActivity;
import com.example.recipebook.databinding.ActivityProfileBinding;
import com.example.recipebook.e_RecipeDetails.RecipeDetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements UserRecipeAdapter.UserRecipeListener {
  ActivityProfileBinding binding;
  FirebaseFirestore fireStore;
  AlertDialog loadingDialog;
  FirebaseAuth auth;
  String user_image;
  ArrayList<Recipe> recipes;
  UserRecipeAdapter adapter;
  String identifier;
  int position;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    binding = ActivityProfileBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
    fireStore = FirebaseFirestore.getInstance();
    auth = FirebaseAuth.getInstance();
    recipes = new ArrayList<>();

    identifier = getIntent().getStringExtra(Utils.USER_IDENTIFIER);

    if(identifier != null){
//    todo get user information's bu user id or code
      fireStore.collection(Utils.USERS_COLLECTION)
              .document(identifier).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  if (task.isSuccessful()) {
//                  todo tack all information
                    user_image = task.getResult().getString(Utils.USER_IMAGE);

                    binding.userNamePA.setText(task.getResult().getString(Utils.USER_Name));
                    binding.userEmailPA.setText(task.getResult().getString(Utils.USER_EMAIL));

                    if (user_image != null)
                      Picasso.get().load(user_image).into(binding.profileImagePA);
//                  todo get all recipe's current user
                    fireStore.collection(Utils.USERS_COLLECTION).document(identifier)
                            .collection(Utils.RECIPES_COLLECTION).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                              @Override
                              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.isComplete()) {
//                                todo handel all recipes in recycler
                                  for (QueryDocumentSnapshot doc : task.getResult()) {
                                    Recipe recipe = doc.toObject(Recipe.class);
                                    recipes.add(recipe);
                                  }
                                  if(!recipes.isEmpty()){
                                    adapter = new UserRecipeAdapter(recipes, ProfileActivity.this);
                                    binding.yourRecipesRecyclerPA.setAdapter(adapter);
                                    binding.yourRecipesRecyclerPA.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                                  }else{
                                    binding.addFirstRecipeButton.setVisibility(View.VISIBLE);
                                    binding.addFirstRecipeButton.setOnClickListener(v->{
                                      startActivity(new Intent
                                              (ProfileActivity.this, OperationRecipeActivity.class)
                                              .putExtra(Utils.ADD_OPERATION,1)
                                              .putExtra(Utils.USER_IDENTIFIER,identifier));
                                      finish();
                                    });
                                  }

                                }
                              }
                            });
//                  todo handel log out button
                    binding.logOutButtonPA.setOnClickListener(v->{
                      logout();
                    });
                  }else{
                    Toast.makeText(ProfileActivity.this, "User identifier is null", Toast.LENGTH_SHORT).show();
                  }
                }
              });

    }else{
      Toast.makeText(this, "User identifier is null", Toast.LENGTH_SHORT).show();
    }

  }

  @Override
  public void getRecipeDetails(Recipe recipe) {
    startActivity(new Intent(ProfileActivity.this, RecipeDetailsActivity.class)
            .putExtra(Utils.USER_IDENTIFIER,identifier)
            .putExtra(Utils.RECIPE_ID,recipe.getRecipe_id())
            .putExtra(Utils.RECIPE_CATEGORY,recipe.getRecipe_category()));
  }

//  todo handel this function to delete on in user recipes
  @Override
  public void deleteRecipe(Recipe recipe) {
//    todo delete the recipe in recipes collection from user document
    fireStore.collection(Utils.USERS_COLLECTION)
             .document(identifier)
             .collection(Utils.RECIPES_COLLECTION)
             .document(recipe.getRecipe_id())
             .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                loadingDialog = new AlertDialog.Builder(ProfileActivity.this)
                        .setView(Utils.createLoadingView(ProfileActivity.this,"Start Delete this Recipe..."))
                        .setCancelable(false)
                        .create();
                loadingDialog.show();
                if(task.isSuccessful()){
//                todo delete the recipe in recipes collection from all category document
                  fireStore.collection(Utils.CATEGORIES_COLLECTION)
                           .document(Utils.CATEGORY_ALL)
                           .collection(Utils.RECIPES_COLLECTION)
                           .document(recipe.getRecipe_id()).delete()
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful()){
//                      todo delete the recipe in recipes collection from current category document
                        fireStore.collection(Utils.CATEGORIES_COLLECTION)
                                 .document(recipe.getRecipe_category())
                                 .collection(Utils.RECIPES_COLLECTION)
                                 .document(recipe.getRecipe_id())
                                 .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
//                                   todo this code will run in successful all previous operation
                                     if(task.isSuccessful()){
                                       loadingDialog.dismiss();
                                       Toast.makeText(ProfileActivity.this, "Recipe is DELETED!", Toast.LENGTH_SHORT).show();
                                       for (int i = 0; i < recipes.size(); i++) {
                                         if (recipes.get(i).getRecipe_id().equals(recipe.getRecipe_id())) {
                                           position = i;
                                           break;
                                         }
                                       }
                                       if (position != -1) {
                                         recipes.remove(position);
                                         adapter.notifyItemRemoved(position);
                                       }
                                       recipes.remove(recipe);
                                     }else{
                                       Toast.makeText(ProfileActivity.this, "DELETED operation is Fail ", Toast.LENGTH_SHORT).show();
                                     }
                                   }
                                 });
                      }else{
                        Toast.makeText(ProfileActivity.this, "DELETED operation is Fail ", Toast.LENGTH_SHORT).show();
                      }
                    }
                  });
                }else {
                  Toast.makeText(ProfileActivity.this, "DELETED operation is Fail ", Toast.LENGTH_SHORT).show();
                }
              }
            });

  }

//  todo handel this function to go Edit Operation Activity
  @Override
  public void editRecipe(Recipe recipe) {
    startActivity(new Intent
            (ProfileActivity.this, OperationRecipeActivity.class)
            .putExtra(Utils.EDIT_OPERATION,2)
            .putExtra(Utils.USER_IDENTIFIER,identifier)
            .putExtra(Utils.RECIPE_ID,recipe.getRecipe_id())
            .putExtra(Utils.RECIPE_CATEGORY,recipe.getRecipe_category()));
    finish();
  }
  private void logout() {
    SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.clear();
    editor.apply();
    FirebaseAuth.getInstance().signOut();
    startActivity(new Intent(this, RegisterActivity.class));
    finish();
  }
}
