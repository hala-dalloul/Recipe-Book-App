package com.example.recipebook.c_Home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class CategoryAdapter extends FragmentStateAdapter {
  ArrayList<CategoryFragment> categoryFragments;

  public CategoryAdapter(@NonNull FragmentActivity fragmentActivity,ArrayList<CategoryFragment> categoryFragments) {
    super(fragmentActivity);
    this.categoryFragments = categoryFragments;
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    return categoryFragments.get(position);
  }

  @Override
  public int getItemCount() {
    return categoryFragments.size();
  }
}
