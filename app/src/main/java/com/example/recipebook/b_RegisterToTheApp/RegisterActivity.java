package com.example.recipebook.b_RegisterToTheApp;

import android.content.Intent;
import android.graphics.Bitmap;
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

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.recipebook.CloudinaryLib;
import com.example.recipebook.R;
import com.example.recipebook.Utils;
import com.example.recipebook.c_Home.HomeActivity;
import com.example.recipebook.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity implements LoginFragment.LoginFragmentEvent , SingUpFragment.SingUpFragmentEvent{
  ActivityRegisterBinding binding;
  FirebaseAuth auth;
  FirebaseFirestore fireStore;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    binding = ActivityRegisterBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
    CloudinaryLib.init(RegisterActivity.this);
    binding.textWelcomeChef.setVisibility(View.INVISIBLE);
    getSupportFragmentManager().beginTransaction().add(R.id.register_fragment,LoginFragment.newInstance()).commit();
    auth = FirebaseAuth.getInstance();
    fireStore = FirebaseFirestore.getInstance();
  }

  @Override
  public void startSingUp() {
    getSupportFragmentManager().beginTransaction().replace(R.id.register_fragment, SingUpFragment.newInstance()).commit();
  }

  @Override
  public void singInUser(HashMap<String, Object> information) {
    auth.signInWithEmailAndPassword(
            String.valueOf(information.get(Utils.USER_EMAIL)),
            String.valueOf(information.get(Utils.USER_PASSWORD)))
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isComplete()){
          String identifier = task.getResult().getUser().getUid();
          binding.textWelcomeChef.setVisibility(View.VISIBLE);
          startActivity(new Intent
                  (RegisterActivity.this, HomeActivity.class)
                  .putExtra(Utils.USER_IDENTIFIER,identifier));
        }else{
          Toast.makeText(RegisterActivity.this, "Check Your information is correct", Toast.LENGTH_SHORT).show();
        }
      }
    });
  }
//Sing Up user fragment
  @Override
  public void goToSingIn() {
    getSupportFragmentManager().beginTransaction().replace(R.id.register_fragment, LoginFragment.newInstance()).commit();
  }

  @Override
  public void singUpUser(HashMap<String, Object> information) {
    auth.createUserWithEmailAndPassword
            (String.valueOf(information.get(Utils.USER_EMAIL))
            ,String.valueOf(information.get(Utils.USER_PASSWORD)))
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isComplete() && task.isSuccessful()){

          String identifier = task.getResult().getUser().getUid();
          Log.d("TAG", "onComplete: in sing up function");
          Bitmap image = (Bitmap) information.get(Utils.USER_IMAGE);
          Log.d("TAG", "onComplete: Bi"+(image!=null));
          information.remove(Utils.USER_IMAGE);
          fireStore.collection("users").document(identifier)
                  .set(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if(task.isComplete() && task.isSuccessful()){
                        Log.d("TAG", "onComplete: in fire store");

                        binding.registerFragment.setVisibility(View.INVISIBLE);
                        binding.textWelcomeChef.setText(String.format("Welcome %s", information.get(Utils.USER_Name)));
                        binding.textWelcomeChef.setVisibility(View.VISIBLE);

                        startActivity(new Intent(RegisterActivity.this,
                                HomeActivity.class).putExtra(Utils.USER_IDENTIFIER,identifier));
                      }else{
                        Toast.makeText(RegisterActivity.this, "This User is not created in FireStore", Toast.LENGTH_SHORT).show();
                      }
                    }
                  });

        }else{
          Exception e = task.getException();
          Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
      }
    });
  }
}