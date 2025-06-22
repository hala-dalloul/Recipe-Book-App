package com.example.recipebook.b_RegisterToTheApp;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.cloudinary.android.MediaManager;
import com.example.recipebook.CloudinaryLib;
import com.example.recipebook.R;
import com.example.recipebook.Utils;
import com.example.recipebook.databinding.FragmentSingUpBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class SingUpFragment extends Fragment {
  SingUpFragmentEvent event;
  FragmentSingUpBinding binding;
  Bitmap image;

  ActivityResultLauncher launcher = registerForActivityResult
          (new ActivityResultContracts.StartActivityForResult(),
           new ActivityResultCallback<ActivityResult>() {
    @Override
    public void onActivityResult(ActivityResult result) {
      if(result.getResultCode() == RESULT_OK && result.getData() != null){
        image = (Bitmap) result.getData().getExtras().get("data");
        Glide.with(getActivity())
                .load(image)
                .transform(new CircleCrop())
                .into(binding.imageViewFS);
      }
    }
  });

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    event = (SingUpFragmentEvent)context;
  }

  public SingUpFragment() {
    // Required empty public constructor
  }

  public static SingUpFragment newInstance() {
    SingUpFragment fragment = new SingUpFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = FragmentSingUpBinding.inflate(inflater,container,false);
    ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),R.array.country_list, android.R.layout.simple_dropdown_item_1line);
    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
    binding.spinnerCountryList.setAdapter(adapter);

    binding.singInButtonFS.setOnClickListener(view -> {
      event.goToSingIn();
    });

    binding.imageViewFS.setOnClickListener(view -> {
      launcher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
    });


    binding.singUpButtonFS.setOnClickListener(view -> {
      HashMap<String ,Object> information = new HashMap<>();
      information.put(Utils.USER_IMAGE,image);
      String name = binding.nameUserEditTextFS.getText().toString();
      String email = binding.emailUserEditTextFS.getText().toString();
      String password = binding.passwordEditTextFS.getText().toString();
      String passwordCon = binding.passwordConEditTextFS.getText().toString();
      String country = binding.spinnerCountryList.getSelectedItem().toString();
      if(!(name.isEmpty() && email.isEmpty() && password.isEmpty()&&
           passwordCon.isEmpty()  && country.isEmpty()) ){
        if(password.equals(passwordCon) && password.length()>6){
          // بدنا نضيف كود تحويل الصورة و رفعها هنا
          information.put(Utils.USER_Country,country);
          information.put(Utils.USER_Name,name);
          information.put(Utils.USER_EMAIL,email);
          information.put(Utils.USER_PASSWORD,password);
          event.singUpUser(information);
        }else {
          Toast.makeText(getActivity(), "Check Your Password", Toast.LENGTH_SHORT).show();
        }
      }else{
        Toast.makeText(getActivity(), "Sure Fill all Fields", Toast.LENGTH_SHORT).show();
      }

    });

    return binding.getRoot();
  }
  interface SingUpFragmentEvent{
    void goToSingIn();
    void singUpUser(HashMap<String,Object> information);
  }
}