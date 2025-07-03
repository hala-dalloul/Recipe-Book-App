package com.example.recipebook.c_Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipebook.R;
import com.example.recipebook.Recipe;
import com.example.recipebook.Utils;
import com.example.recipebook.databinding.FragmentCategoryBinding;
import com.example.recipebook.e_RecipeDetails.RecipeDetailsActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class CategoryFragment extends Fragment {
  FragmentCategoryBinding binding;
  private static final String CATEGORY_ID = "categoryId";
  private static final String IDENTIFIER = "identifier";
  private String categoryId;
  private String identifier;
  FirebaseFirestore firestore;
  ArrayList<Recipe> recipes = new ArrayList<>();
  RecipeAdapter recipeAdapter;

  public void filterRecipes(String query) {
    if (recipeAdapter != null) {
      recipeAdapter.getFilter().filter(query);
    }
  }

  public CategoryFragment() {
    // Required empty public constructor
  }

  public static CategoryFragment newInstance(String categoryId,String identifier) {
    CategoryFragment fragment = new CategoryFragment();
    Bundle args = new Bundle();
    args.putString(CATEGORY_ID, categoryId);
    args.putString(IDENTIFIER,identifier);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      categoryId = getArguments().getString(CATEGORY_ID);
      identifier = getArguments().getString(IDENTIFIER);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = FragmentCategoryBinding.inflate(inflater, container, false);
    firestore = FirebaseFirestore.getInstance();

    recipeAdapter = new RecipeAdapter(recipes, recipe -> {
      Log.d("TAG", "onCreateView: "+recipe.toString());
      startActivity(new Intent(getContext(), RecipeDetailsActivity.class)
              .putExtra(Utils.USER_IDENTIFIER,identifier)
              .putExtra(Utils.RECIPE_ID, recipe.getRecipe_id())
              .putExtra(Utils.RECIPE_CATEGORY, recipe.getRecipe_category()));
    });
    binding.categoryFragment.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.categoryFragment.setAdapter(recipeAdapter);

    loadRecipes();

    return binding.getRoot();
  }
  private void loadRecipes(){
    recipes.clear();
    firestore.collection(Utils.CATEGORIES_COLLECTION)
            .whereEqualTo(Utils.CATEGORY_NAME, categoryId)
            .get()
            .addOnCompleteListener(task -> {
              if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot categoryDoc = task.getResult().getDocuments().get(0);
                String categoryIdFromDb = categoryDoc.getString("category_id");

                if (Utils.CATEGORY_ALL.equals(categoryIdFromDb)) {
                  loadRecipesFrom(Utils.CATEGORY_ALL);
                } else {
                 loadRecipesFrom(categoryIdFromDb);
                }
              }
            });
  }
  private void loadRecipesFrom(String categoryDocId){
    firestore.collection(Utils.CATEGORIES_COLLECTION)
            .document(categoryDocId)
            .collection(Utils.RECIPES_COLLECTION)
            .get()
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                for (DocumentSnapshot doc : task.getResult()) {
                  Recipe recipe = doc.toObject(Recipe.class);
                  recipe.setRecipe_id(doc.getId());
                  recipes.add(recipe);
                }
                if(recipes.size() == 0){
                  binding.image.setImageResource(R.drawable.icon_need_recipes);
                }
                recipeAdapter.updateData(new ArrayList<>(recipes)); // تحديث البيانات والفلترة
              }
            });
  }

}