package com.example.recipebook.c_Home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.util.Util;
import com.example.recipebook.R;
import com.example.recipebook.Recipe;
import com.example.recipebook.Utils;
import com.example.recipebook.d_AddEditRecipe.OperationRecipeActivity;
import com.example.recipebook.databinding.ActivityHomeBinding;
import com.example.recipebook.f_Profile.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
  ActivityHomeBinding binding;
  ArrayList<String> tabsName;
  ArrayList<CategoryFragment> categoryFragments;
  FirebaseFirestore fireStore;
  String identifier;
  @Override
  protected void onResume() {
    super.onResume();
    getInfo();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    binding = ActivityHomeBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
    identifier = getIntent().getStringExtra(Utils.USER_IDENTIFIER);
    fireStore = FirebaseFirestore.getInstance();

    binding.searchViewHA.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        int position = binding.viewCategoryRecipesList.getCurrentItem();
        if (position >= 0 && position < categoryFragments.size()) {
          categoryFragments.get(position).filterRecipes(newText);
        }
        return true;
      }
    });
    binding.searchViewHA.setOnCloseListener(new SearchView.OnCloseListener() {
      @Override
      public boolean onClose() {
        int position = binding.viewCategoryRecipesList.getCurrentItem();
        if (position >= 0 && position < categoryFragments.size()) {
          categoryFragments.get(position).filterRecipes("");
        }
        return false;
      }
    });

    binding.addNewRecipe.setOnClickListener(view -> {
      startActivity(new Intent(this,
              OperationRecipeActivity.class)
              .putExtra(Utils.ADD_OPERATION,1)
              .putExtra(Utils.USER_IDENTIFIER,identifier));
    });

    binding.profileIconAH.setOnClickListener(view ->{
      startActivity(new Intent(HomeActivity.this,
              ProfileActivity.class).
              putExtra(Utils.USER_IDENTIFIER,identifier));

    });

    getInfo();
  }
  private void getInfo(){
    // the categories
    categoryFragments = new ArrayList<>();
    tabsName = new ArrayList<>();
    fireStore.collection("categories").
            orderBy("category_name", Query.Direction.ASCENDING).
            get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()){
                  for (DocumentSnapshot document : task.getResult()){
                    String category_name = document.getString("category_name");
                    tabsName.add(category_name);
                    categoryFragments.add(CategoryFragment.newInstance(category_name,identifier));

                  }
                  CategoryAdapter adapter = new CategoryAdapter(HomeActivity.this, categoryFragments);
                  binding.viewCategoryRecipesList.setAdapter(adapter);
                  new TabLayoutMediator(binding.tabLayout, binding.viewCategoryRecipesList,
                          new TabLayoutMediator.TabConfigurationStrategy() {
                            @Override
                            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                              tab.setText(tabsName.get(position));
                            }
                          }).attach();
                }

              }
            });

  }
}