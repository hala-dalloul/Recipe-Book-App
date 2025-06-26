package com.example.recipebook.b_RegisterToTheApp;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.recipebook.CloudinaryLib;
import com.example.recipebook.R;
import com.example.recipebook.Utils;
import com.example.recipebook.databinding.FragmentSingUpBinding;

import java.util.HashMap;


public class SingUpFragment extends Fragment {
  SingUpFragmentEvent event;
  FragmentSingUpBinding binding;
  Uri uri;
  boolean isPasswordVisible = false;
  String image;
  ActivityResultLauncher<Intent> launcher = registerForActivityResult
          (new ActivityResultContracts.StartActivityForResult(),
                  new ActivityResultCallback<ActivityResult>() {

                    @Override
                    public void onActivityResult(ActivityResult o) {
                      if (o.getResultCode() == RESULT_OK && o.getData() != null) {
                        uri = o.getData().getData();
                        binding.imageViewFS.setImageURI(uri);
                        image = uri.toString();
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


    binding.showHidePasswordFS.setOnClickListener(view -> {
      if (isPasswordVisible) {
        binding.passwordEditTextFS.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        binding.passwordConEditTextFS.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        binding.showHidePasswordFS.setImageResource(R.drawable.icon_open_eye);
      } else {
        binding.passwordEditTextFS.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        binding.passwordConEditTextFS.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        binding.showHidePasswordFS.setImageResource(R.drawable.icon_close_eye);
      }
      isPasswordVisible = !isPasswordVisible;
      binding.passwordEditTextFS.setSelection(binding.passwordEditTextFS.getText().length());
      binding.passwordConEditTextFS.setSelection(binding.passwordConEditTextFS.getText().length());
    });

    ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),R.array.country_list, android.R.layout.simple_dropdown_item_1line);
    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
    binding.spinnerCountryList.setAdapter(adapter);

    binding.singInButtonFS.setOnClickListener(view -> {
      event.goToSingIn();
    });

    binding.imageViewFS.setOnClickListener(view -> {
      launcher.launch(new Intent(Intent.ACTION_PICK,
              MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
              .setType("image/*"));
    });


    binding.singUpButtonFS.setOnClickListener(view -> {
      HashMap<String ,Object> information = new HashMap<>();

      String name = binding.nameUserEditTextFS.getText().toString();
      String email = binding.emailUserEditTextFS.getText().toString().trim();
      String password = binding.passwordEditTextFS.getText().toString().trim();
      String passwordCon = binding.passwordConEditTextFS.getText().toString().trim();
      String country = binding.spinnerCountryList.getSelectedItem().toString().trim();
      if(!name.isEmpty()) {
        if (!email.isEmpty()) {
          if (!password.isEmpty()) {
            if (!passwordCon.isEmpty()) {
              if (image != null) {
                if (password.equals(passwordCon) && password.length() > 6) {
                  information.put(Utils.USER_Country, country);
                  information.put(Utils.USER_Name, name);
                  information.put(Utils.USER_EMAIL, email);
                  information.put(Utils.USER_PASSWORD, password);
                  information.put(Utils.USER_IMAGE, image);
                  event.singUpUser(information);
                } else {
                  Toast.makeText(getActivity(), "Check Your Password", Toast.LENGTH_SHORT).show();
                }
              }else{
                Toast.makeText(getActivity(), "Sure Fill all Fields", Toast.LENGTH_SHORT).show();
              }
            }else{
              Toast.makeText(getActivity(), "Sure Fill all Fields", Toast.LENGTH_SHORT).show();
            }
          }else{
            Toast.makeText(getActivity(), "Sure Fill all Fields", Toast.LENGTH_SHORT).show();
          }
        }else{
          Toast.makeText(getActivity(), "Sure Fill all Fields", Toast.LENGTH_SHORT).show();
        }
      }else {
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