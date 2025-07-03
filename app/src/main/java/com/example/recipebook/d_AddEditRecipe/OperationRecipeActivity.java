package com.example.recipebook.d_AddEditRecipe;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.Util;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.recipebook.CloudinaryLib;
import com.example.recipebook.R;
import com.example.recipebook.Recipe;
import com.example.recipebook.Utils;
import com.example.recipebook.c_Home.HomeActivity;
import com.example.recipebook.databinding.ActivityOperationRecipeBinding;
import com.example.recipebook.e_RecipeDetails.RecipeDetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OperationRecipeActivity extends AppCompatActivity implements CardViewAdapter.CardInterface, CategoryAdapter.CategoryListener {
  ActivityOperationRecipeBinding binding;
  AlertDialog loadingDialog;
  FirebaseFirestore fireStore;
  Map<String, Object> recipe;
  String image;
  CardViewAdapter adapterIngredients;
  CardViewAdapter adapterSteps;
  CategoryAdapter adapterCategory;
  ArrayList<String> ingredients;
  ArrayList<String> steps;
  ArrayList<String> category;
  String category_id;
  String recipe_title;
  int recipe_time;
  String chef_id;
  String docId;
  String recipe_id;
  String recipe_category;
  String youtube_link;
  Uri uri;

//  todo launcher علشان يقوم بأخذ صورة الوصفة
  ActivityResultLauncher<Intent> launcher = registerForActivityResult
          (new ActivityResultContracts.StartActivityForResult(),
                  new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult o) {
              if (o.getResultCode() == RESULT_OK && o.getData() != null) {
                uri = o.getData().getData();
                binding.chooseImageOA.setImageURI(uri);
                image = uri.toString();
              }
            }
          });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    binding = ActivityOperationRecipeBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
    category = new ArrayList<>();
    ingredients = new ArrayList<>();
    steps = new ArrayList<>();
    fireStore = FirebaseFirestore.getInstance();
    recipe = new HashMap<>();
    chef_id = getIntent().getStringExtra(Utils.USER_IDENTIFIER);

//  todo  Photo in studio Intent to pick the image
    CloudinaryLib.init(OperationRecipeActivity.this);
    binding.chooseImageOA.setOnClickListener(v->{
      Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
      intent.setType("image/*");
      launcher.launch(intent);
    });



//  todo category recycler
    fireStore.collection(Utils.CATEGORIES_COLLECTION).
            get().addOnCompleteListener(
                    new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                  for (DocumentSnapshot document : task.getResult()){
                    if(!document.get("category_name").equals("All"))
                      category.add(document.getString("category_name"));
                  }
                  adapterCategory = new CategoryAdapter(category,OperationRecipeActivity.this::getCategory);
                  binding.categoryRecyclerOA.setAdapter(adapterCategory);
                  binding.categoryRecyclerOA.setLayoutManager(new LinearLayoutManager
                          (OperationRecipeActivity.this, RecyclerView.HORIZONTAL,false));

                }}});
//    todo handel ingredients and steps recycler
    //ingredients
    adapterIngredients = new CardViewAdapter(ingredients,this::getCard);
    binding.ingredientsRecyclerOA.setAdapter(adapterIngredients);
    binding.ingredientsRecyclerOA.setLayoutManager(new LinearLayoutManager(this));

    //steps
    adapterSteps = new CardViewAdapter(steps,this::getCard);
    binding.stepRecyclerOA.setAdapter(adapterSteps);
    binding.stepRecyclerOA.setLayoutManager(new LinearLayoutManager(this));

//   todo  handel plus button :
//    1: in ingredients layout
    binding.addIngredientsBtuOA.setOnClickListener(v->{
      String item = binding.newIngredientsAO.getText().toString().trim();
      if(item.equals("")){
        Toast.makeText(this, "Please inter ingredient", Toast.LENGTH_SHORT).show();
      }else{
        ingredients.add(item);
        adapterIngredients.notifyDataSetChanged();
        binding.newIngredientsAO.setText("");
      }
    });
//    todo 2: in steps layout
    binding.addStepBtuOA.setOnClickListener(v->{
      String step = binding.newStepAO.getText().toString().trim();
      if(step.equals("")){
        Toast.makeText(this, "Please inter new Step", Toast.LENGTH_SHORT).show();
      }else{
        steps.add(step);
        adapterSteps.notifyDataSetChanged();
        binding.newStepAO.setText("");
      }
    });

    // todo Add operation  =>
    if(getIntent().getIntExtra(Utils.ADD_OPERATION,0) ==1){
//      todo handel the text title and button text
      setTexts("Add Operation","ADD");
//      todo check fill all fields
      binding.operationButtonOA.setOnClickListener(v->{
         loadingDialog = new AlertDialog.Builder(this)
                .setView(Utils.createLoadingView(this,"Start Insert New Recipe..."))
                .setCancelable(false)
                .create();
        loadingDialog.show();

        recipe_title = binding.recipeTitleAO.getText().toString().trim();
        youtube_link = binding.recipeYoutubeLinkAO.getText().toString().trim();
        recipe_time = Integer.parseInt(binding.recipeTimeAO.getText().toString());
        if(recipe_title.isEmpty() && youtube_link.isEmpty() && recipe_time == 0 && category_id == null
            && image.equals("") && ingredients.isEmpty() && steps.isEmpty()){
          Toast.makeText(this, "Check fill all Filed", Toast.LENGTH_SHORT).show();
        }else{
          //todo GET chef user name
           fireStore.collection(Utils.USERS_COLLECTION).document(chef_id)
                   .get().addOnCompleteListener
                   (new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if(task.isSuccessful()){
//                 todo put in hash map
                 String user_name = task.getResult().getString(Utils.USER_Name);
                 recipe.put(Utils.RECIPE_Chef_NAME,user_name);
//               todo get category id by category name and put this in hash map
               fireStore.collection(Utils.CATEGORIES_COLLECTION)
                       .whereEqualTo(Utils.CATEGORY_NAME, category_id.trim())
                       .get()
                       .addOnSuccessListener(queryDocumentSnapshots -> {
                         for (DocumentSnapshot document : queryDocumentSnapshots) {
//                         todo put all current information
                           docId = document.getId();
                           Log.d("CATEGORY_ID", "Document ID: " + docId);
                           recipe.put(Utils.RECIPE_TITLE,recipe_title);
                           recipe.put(Utils.RECIPE_TIME,recipe_time);
                           recipe.put(Utils.YOUTUBE_LINK,youtube_link);
                           recipe.put(Utils.RECIPE_CATEGORY,docId);
                           recipe.put(Utils.RECIPE_INGREDIENTS,ingredients);
                           recipe.put(Utils.RECIPE_STEPS,steps);
//                         todo put new recipe in collection in current category document
                           fireStore.collection(Utils.CATEGORIES_COLLECTION)
                                   .document(docId).collection(Utils.RECIPES_COLLECTION)
                                   .add(recipe).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                     @Override
                                     public void onComplete(@NonNull Task<DocumentReference> task) {
                                       if(task.isSuccessful() && task.isComplete()){
                                         Log.d("TAG", "onComplete: is in recipe");
//                                       todo get recipe id
                                         recipe_id = task.getResult().getId();
                                         Log.d("TAG", "onComplete: "+image);
//                                       todo upload the image in cloudinary library
                                         MediaManager.get().upload(Uri.parse(image))
                                                 .option("folder", Utils.RECIPES_FOLDER)
                                                 .option("public_id",recipe_id)
                                                 .callback(new UploadCallback() {
                                                   @Override
                                                   public void onStart(String requestId) {
                                                     Log.d("TAG", "onStart: Start Process");
                                                   }
                                                   @Override
                                                   public void onProgress(String requestId, long bytes, long totalBytes) {}
                                                   @Override
                                                   public void onSuccess(String requestId, Map resultData) {
//                                                    todo upload the image and recipe id in current recipe document
                                                     image = (String) resultData.get("secure_url");
                                                     recipe.put(Utils.RECIPE_IMAGE,image);
                                                     recipe.put(Utils.RECIPE_ID,recipe_id);
                                                     fireStore.collection(Utils.CATEGORIES_COLLECTION)
                                                             .document(docId).collection(Utils.RECIPES_COLLECTION)
                                                             .document(recipe_id)
                                                             .update(Utils.RECIPE_IMAGE, image,Utils.RECIPE_ID,recipe_id)
                                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                 if(task.isSuccessful()){
                                                                   runOnUiThread(()->{
//                                                                   todo get image and open it in image view
                                                                     Toast.makeText(OperationRecipeActivity.this, "Upload image success!", Toast.LENGTH_SHORT).show();
                                                                     Picasso.get().load(image).into(binding.chooseImageOA);
//                                                                   todo add new recipe in recipes collection in current user document
                                                                     fireStore.collection(Utils.USERS_COLLECTION)
                                                                             .document(chef_id)
                                                                             .collection(Utils.RECIPES_COLLECTION).document(recipe_id).set(recipe)
                                                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                               @Override
                                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                                 if(task.isSuccessful()) {
//                                                                                 todo add new recipe in recipes collection in current category document
                                                                                   fireStore.collection(Utils.CATEGORIES_COLLECTION).document(Utils.CATEGORY_ALL)
                                                                                           .collection(Utils.RECIPES_COLLECTION).document(recipe_id)
                                                                                           .set(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                             @Override
                                                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                                               if(task.isSuccessful() && task.isComplete()) {
                                                                                                loadingDialog.dismiss();
//                                                                                               todo is all previous add operation is successful start intent to back to home page
                                                                                                 startActivity(new Intent
                                                                                                         (OperationRecipeActivity.this, HomeActivity.class).putExtra(Utils.USER_IDENTIFIER, chef_id));
                                                                                                 finish();
                                                                                                 Toast.makeText(OperationRecipeActivity.this, "Add recipe is Completed!", Toast.LENGTH_SHORT).show();
                                                                                               }
                                                                                             }
                                                                                           });

                                                                                 }
                                                                                 else
                                                                                   Toast.makeText(OperationRecipeActivity.this, "Create recipe in user collection is fail", Toast.LENGTH_SHORT).show();
                                                                               }
                                                                             });
                                                                   });

                                                                 }else{
                                                                   Toast.makeText(OperationRecipeActivity.this, "Fail to upload image ", Toast.LENGTH_SHORT).show();
                                                                 }
                                                               }
                                                             });
                                                   }

                                                   @Override
                                                   public void onError(String requestId, ErrorInfo error) {
                                                     Toast.makeText(OperationRecipeActivity.this, "Cloudinary upload failed", Toast.LENGTH_SHORT).show();
                                                   }

                                                   @Override
                                                   public void onReschedule(String requestId, ErrorInfo error) {

                                                   }
                                                 }).dispatch();
                                       }
                                     }
                                   });
                         }
                         if (queryDocumentSnapshots.isEmpty()) {
                           Log.d("CATEGORY_ID", "This category is not exist");
                         }
                       })
                       .addOnFailureListener(e -> {
                         Log.e("FIRE STORE", e.getMessage(), e);
                       });
             }}
           });

        }
      });
    }
    //todo Edit operation
    else if(getIntent().getIntExtra(Utils.EDIT_OPERATION,0) ==2){
      setTexts("Update Operation","UPDATE");
      recipe_id = getIntent().getStringExtra(Utils.RECIPE_ID);
      recipe_category = getIntent().getStringExtra(Utils.RECIPE_CATEGORY);

      fireStore.collection(Utils.CATEGORIES_COLLECTION)
               .document(recipe_category)
               .collection(Utils.RECIPES_COLLECTION)
               .document(recipe_id).get()
               .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  if(task.isSuccessful()){
                    Recipe r = task.getResult().toObject(Recipe.class);
                    if(r != null){
                      Picasso.get().load(r.getRecipe_image()).into(binding.chooseImageOA);
                      binding.recipeTitleAO.setText(r.getRecipe_title());
                      binding.recipeTimeAO.setText(String.valueOf(r.getRecipe_time()));
                      binding.recipeYoutubeLinkAO.setText(r.getYoutube_link());

                      if(r.getRecipe_ingredients() != null){
                        ingredients.clear();
                        ingredients = r.getRecipe_ingredients();
                        adapterIngredients = new CardViewAdapter(ingredients, OperationRecipeActivity.this);
                        binding.ingredientsRecyclerOA.setAdapter(adapterIngredients);
                        binding.ingredientsRecyclerOA.setLayoutManager(new LinearLayoutManager(OperationRecipeActivity.this));
                      }

                      if(r.getRecipe_steps() != null){
                        steps.clear();
                        steps = r.getRecipe_steps();
                        adapterSteps = new CardViewAdapter(steps,OperationRecipeActivity.this);
                        binding.stepRecyclerOA.setAdapter(adapterSteps);
                        binding.stepRecyclerOA.setLayoutManager(new LinearLayoutManager(OperationRecipeActivity.this));
                      }
                      fireStore.collection(Utils.CATEGORIES_COLLECTION)
                              .document(recipe_category)
                              .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                  binding.chooseCategoryText.setText(documentSnapshot.getString("category_name"));
                                }
                              });
//                    todo handel update operation
                      binding.operationButtonOA.setOnClickListener(v->{
                        ProgressBar progressBar = new ProgressBar(OperationRecipeActivity.this);
                        progressBar.setIndeterminate(true);
                        progressBar.getIndeterminateDrawable().setColorFilter(
                                getResources().getColor(R.color.orange),
                                android.graphics.PorterDuff.Mode.SRC_IN
                        );
                        loadingDialog = new AlertDialog.Builder(OperationRecipeActivity.this)
                                .setView(Utils.createLoadingView(OperationRecipeActivity.this,"Start Edit Recipe..."))
                                .setCancelable(false)
                                .create();
                        loadingDialog.show();
//                        todo handel edit operation
                        recipe_title = binding.recipeTitleAO.getText().toString().trim();
                        youtube_link = binding.recipeYoutubeLinkAO.getText().toString().trim();
                        recipe_time = Integer.parseInt(binding.recipeTimeAO.getText().toString());
                        if(recipe_title.isEmpty() && youtube_link.isEmpty() && recipe_time == 0
                                && image.equals("") && ingredients.isEmpty() && steps.isEmpty()){
                          Toast.makeText(OperationRecipeActivity.this, "Check fill all Filed", Toast.LENGTH_SHORT).show();
                        }else{
                          recipe = new HashMap<>();
                          recipe.put(Utils.RECIPE_TITLE,recipe_title);
                          recipe.put(Utils.RECIPE_TIME,recipe_time);
                          recipe.put(Utils.YOUTUBE_LINK,youtube_link);
                          recipe.put(Utils.RECIPE_Chef_NAME,r.getChef_name());
                          if(category_id == null){
                            category_id = r.getRecipe_category();
                            recipe.put(Utils.RECIPE_CATEGORY,r.getRecipe_category());
                          }else{
                            recipe.put(Utils.RECIPE_CATEGORY,category_id);
                          }
                          if(ingredients == null){
                            recipe.put(Utils.RECIPE_INGREDIENTS,r.getRecipe_ingredients());
                          }else{
                            recipe.put(Utils.RECIPE_INGREDIENTS,ingredients);
                          }
                          if(steps == null){
                            recipe.put(Utils.RECIPE_STEPS,r.getRecipe_steps());
                          }else{
                            recipe.put(Utils.RECIPE_STEPS,steps);
                          }
                          if(image == null){
                            recipe.put(Utils.RECIPE_IMAGE,r.getRecipe_image());
                            updateRecipe();
                          }else{
                            MediaManager.get().upload(Uri.parse(image))
                                    .option("folder", Utils.RECIPES_FOLDER)
                                    .option("public_id",r.getRecipe_id())
                                    .option("overwrite",true)
                                    .callback(new UploadCallback() {
                                      @Override
                                      public void onStart(String requestId) {
                                        Log.d("TAG", "onStart: Start upload image");
                                      }

                                      @Override
                                      public void onProgress(String requestId, long bytes, long totalBytes) {

                                      }

                                      @Override
                                      public void onSuccess(String requestId, Map resultData) {
                                        image = (String) resultData.get("secure_url");
                                        recipe.put(Utils.RECIPE_IMAGE,image);
                                        updateRecipe();
                                      }

                                      @Override
                                      public void onError(String requestId, ErrorInfo error) {
                                        Toast.makeText(OperationRecipeActivity.this, "Update the image is failed!?", Toast.LENGTH_SHORT).show();
                                      }

                                      @Override
                                      public void onReschedule(String requestId, ErrorInfo error) {

                                      }
                                    });
                          }

                        }

                      });

                    }

                  }
                }
              });

    }
  }
//  todo this function to handel texts with operation type
  public void setTexts(String title, String button){
    binding.titleAO.setText(title);
    binding.operationButtonOA.setText(button);
  }
//  todo this function to handel click action in ingredients and steps recycler
  @Override
  public void getCard(int position) {/*edit ingredients and steps*/}
//  todo handel click from category recycler
  @Override
  public void getCategory(int position) {
    category_id = category.get(position);
    Toast.makeText(this, "Your selected category "+category_id, Toast.LENGTH_SHORT).show();
  }
  private void updateRecipe() {
    if(category_id == null){
      category_id = binding.chooseCategoryText.getText().toString();
    }
    Log.d("TAG", "onComplete: "+category_id);

    if(recipe_category.equals(category_id)){
      fireStore.collection(Utils.CATEGORIES_COLLECTION)
              .document(recipe_category)
              .collection(Utils.RECIPES_COLLECTION)
              .document(recipe_id).update(recipe)
              .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  if(task.isSuccessful()){
                    fireStore.collection(Utils.CATEGORIES_COLLECTION)
                            .document(Utils.CATEGORY_ALL)
                            .collection(Utils.RECIPES_COLLECTION)
                            .document(recipe_id).update(recipe)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                  fireStore.collection(Utils.USERS_COLLECTION)
                                          .document(chef_id)
                                          .collection(Utils.RECIPES_COLLECTION)
                                          .document(recipe_id).update(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                              Toast.makeText(OperationRecipeActivity.this, "Update this recipe Successful!", Toast.LENGTH_SHORT).show();
                                              loadingDialog.dismiss();
                                              startActivity(new Intent
                                                      (OperationRecipeActivity.this,HomeActivity.class)
                                                      .putExtra(Utils.USER_IDENTIFIER,category_id));
                                              finish();
                                            }
                                          });
                                }
                              }
                            });
                  }
                }
              });
    }
    else {
      fireStore.collection(Utils.CATEGORIES_COLLECTION)
              .whereEqualTo(Utils.CATEGORY_NAME, category_id.trim())
              .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                    String new_category_id = task.getResult().getDocuments().get(0).getString("category_id").trim();
                    Log.d("TAG", "onComplete: " + new_category_id);

                    fireStore.collection(Utils.CATEGORIES_COLLECTION)
                            .document(recipe_category)
                            .collection(Utils.RECIPES_COLLECTION)
                            .document(recipe_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                  recipe.put(Utils.RECIPE_CATEGORY,new_category_id);

                                  fireStore.collection(Utils.CATEGORIES_COLLECTION)
                                          .document(new_category_id)
                                          .collection(Utils.RECIPES_COLLECTION)
                                          .document(recipe_id).set(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                              if (task.isSuccessful()) {
                                                fireStore.collection(Utils.CATEGORIES_COLLECTION)
                                                         .document(Utils.CATEGORY_ALL)
                                                         .collection(Utils.RECIPES_COLLECTION)
                                                         .document(recipe_id).update(recipe)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()) {
                                                      fireStore.collection(Utils.USERS_COLLECTION)
                                                              .document(chef_id)
                                                              .collection(Utils.RECIPES_COLLECTION)
                                                              .document(recipe_id).update(recipe)
                                                              .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                  if(task.isSuccessful()) {
                                                                    loadingDialog.dismiss();
                                                                    startActivity(new Intent(OperationRecipeActivity.this, HomeActivity.class).putExtra(Utils.USER_IDENTIFIER, chef_id));
                                                                  }
                                                                }
                                                              });
                                                    }
                                                  }
                                                });
                                              }
                                            }
                                          });
                                }
                              }
                            });
                  }
                }
              });
    }
  }

}