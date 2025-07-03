package com.example.recipebook.c_Home;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipebook.R;
import com.example.recipebook.Recipe;
import com.example.recipebook.databinding.CardRecipeBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> implements Filterable{
  ArrayList<Recipe> recipes;
  private List<Recipe> fullFilteredList;
  RecipeListener listener;
  public RecipeAdapter(ArrayList<Recipe> recipes, RecipeListener listener) {
    this.recipes = recipes;
    this.listener = listener;
    fullFilteredList = new ArrayList<>(recipes);
  }
  public Filter getFilter(){
    return recipeFilter;
  }
  private Filter recipeFilter = new Filter() {
    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
      List<Recipe> filteredList = new ArrayList<>();
      if (charSequence == null || charSequence.length() == 0) {
        filteredList.addAll(fullFilteredList);
      } else {
        String filterPattern = charSequence.toString().toLowerCase().trim();
        for (Recipe item : fullFilteredList) {
          if (item.getRecipe_title().toLowerCase().contains(filterPattern)) {
            filteredList.add(item);
          }
        }
      }
      FilterResults results = new FilterResults();
      results.values = filteredList;
      return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
      recipes.clear();
      recipes.addAll((List) filterResults.values);
      notifyDataSetChanged();
    }
  };

  @NonNull
  @Override
  public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new RecipeViewHolder(CardRecipeBinding.inflate(LayoutInflater.from(parent.getContext())
            ,parent,false));
  }
  // دالة لتحديث البيانات كاملة
  public void updateData(List<Recipe> newRecipes) {
    fullFilteredList.clear();
    fullFilteredList.addAll(newRecipes);
    recipes.clear();
    recipes.addAll(newRecipes);
    notifyDataSetChanged();
  }

  @Override
  public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
    int pos = position;
    holder.recipeTitle.setText(recipes.get(pos).getRecipe_title());
    holder.recipeTime.setText("Cook time =>"+recipes.get(pos).getRecipe_time()+"minute");
    holder.recipeChef.setText("Chef name =>"+recipes.get(pos).getChef_name());
    Picasso.get()
            .load(recipes.get(pos).getRecipe_image())
            .placeholder(R.drawable.pan_logo_icon)
            .error(R.drawable.pan_logo_icon)
            .into(holder.recipeImage);
    holder.root.setOnClickListener(view -> {
      listener.onRecipeClicked(recipes.get(pos));
    });
  }

  @Override
  public int getItemCount() {
    return recipes.size();
  }


  class RecipeViewHolder extends RecyclerView.ViewHolder {
    ImageView recipeImage;
    TextView recipeTitle;
    TextView recipeTime;
    TextView recipeChef;
    CardView root;
    public RecipeViewHolder(CardRecipeBinding binding) {
      super(binding.getRoot());
      recipeImage = binding.recipeImageCR;
      recipeTitle = binding.recipeTitleCR;
      recipeTime = binding.recipeTimeCR;
      recipeChef = binding.recipeChefCR;
      root = binding.getRoot();
    }
  }
  public interface RecipeListener{
    void onRecipeClicked(Recipe recipe);
  }
}
