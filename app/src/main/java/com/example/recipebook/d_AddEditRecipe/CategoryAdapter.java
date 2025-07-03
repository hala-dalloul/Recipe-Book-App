package com.example.recipebook.d_AddEditRecipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipebook.databinding.CardBinding;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryVH> {
  ArrayList<String> categories;
  CategoryListener listener;
  public CategoryAdapter(ArrayList<String> categories, CategoryListener listener) {
    this.categories = categories;
    this.listener = listener;
  }

  @NonNull
  @Override
  public CategoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new CategoryVH(CardBinding.inflate
            (LayoutInflater.from(parent.getContext()),parent,false));
  }

  @Override
  public void onBindViewHolder(@NonNull CategoryVH holder, int position) {
    int pos = position;
    holder.textCategory.setText(categories.get(pos));
    holder.card.setOnClickListener(view -> {
      listener.getCategory(pos);
    });
  }

  @Override
  public int getItemCount() {
    return categories.size();
  }

  class CategoryVH extends RecyclerView.ViewHolder{
    CardView card;
    TextView textCategory;
    public CategoryVH(CardBinding binding) {
      super(binding.getRoot());
      card = binding.getRoot();
      textCategory = binding.textCategory;
    }
  }
  interface CategoryListener{
    void getCategory(int position);
  }
}
