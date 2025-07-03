package com.example.recipebook.f_Profile;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipebook.R;
import com.example.recipebook.Recipe;
import com.example.recipebook.databinding.CardUserRecipeBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserRecipeAdapter extends RecyclerView.Adapter<UserRecipeAdapter.UserRecipeVH> {
  ArrayList<Recipe> recipes;
  UserRecipeListener listener;

  public UserRecipeAdapter(ArrayList<Recipe> recipes, UserRecipeListener listener) {
    this.recipes = recipes;
    this.listener = listener;
  }

  @NonNull
  @Override
  public UserRecipeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new UserRecipeVH(CardUserRecipeBinding
            .inflate(LayoutInflater.from(parent.getContext()),parent,false));
  }

  @Override
  public void onBindViewHolder(@NonNull UserRecipeVH holder, int position) {
    int pos = position;
    holder.title.setText(recipes.get(pos).getRecipe_title());
    Picasso.get()
            .load(recipes.get(pos).getRecipe_image())
            .placeholder(R.drawable.pan_logo_icon)
            .error(R.drawable.pan_logo_icon)
            .into(holder.image);
    holder.edit.setOnClickListener(v->{
      listener.editRecipe(recipes.get(pos));
    });
    holder.delete.setOnClickListener(v->{
      listener.deleteRecipe(recipes.get(pos));
    });
    holder.card.setOnClickListener(v->{
      listener.getRecipeDetails(recipes.get(pos));
    });
  }

  @Override
  public int getItemCount() {
    return recipes.size();
  }

  class UserRecipeVH extends RecyclerView.ViewHolder {
    TextView title;
    ImageView image;
    ImageButton delete ,edit;
    CardView card;
    public UserRecipeVH(CardUserRecipeBinding binding) {
      super(binding.getRoot());
      title = binding.recipeTitleUC;
      image = binding.recipeImageUC;
      delete = binding.deleteButtonUC;
      edit = binding.editButtonUC;
      card = binding.getRoot();
    }
  }
  interface UserRecipeListener{
    //TODO handel the params
    void getRecipeDetails(Recipe recipe_id);
    void deleteRecipe(Recipe recipe_id);
    void editRecipe(Recipe recipe_id);
  }
}
