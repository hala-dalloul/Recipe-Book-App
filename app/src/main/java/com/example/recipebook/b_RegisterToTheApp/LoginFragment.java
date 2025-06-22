package com.example.recipebook.b_RegisterToTheApp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.recipebook.R;
import com.example.recipebook.Utils;
import com.example.recipebook.databinding.FragmentLoginBinding;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {
  LoginFragmentEvent event;
  String userEmail;
  String userPassword;

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    event = (LoginFragmentEvent)context;
  }

  public LoginFragment() {
    // Required empty public constructor
  }

  public static LoginFragment newInstance() {
    LoginFragment fragment = new LoginFragment();
    Bundle args = new Bundle();

    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {

    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    FragmentLoginBinding binding = FragmentLoginBinding.inflate(inflater,container,false);
    binding.singUpButtonFL.setOnClickListener(view -> {
      event.startSingUp();
    });
    binding.singInButtonFL.setOnClickListener(view -> {
      HashMap<String,Object> information = new HashMap<>();
      userEmail =binding.emailUserEditTextFL.getText().toString();
      userPassword = binding.passwordUserEditTextFL.getText().toString();
      if(!userEmail.equals("")){
        if(userPassword.length() > 6){
          information.put(Utils.USER_EMAIL,userEmail);
          information.put(Utils.USER_PASSWORD,userPassword);
          event.singInUser(information);
        }else{
          Toast.makeText(getActivity(), "Your Password is Short", Toast.LENGTH_SHORT).show();
        }
      }else{
        Toast.makeText(getActivity(), "Your Email is Empty", Toast.LENGTH_SHORT).show();
      }



    });


    return binding.getRoot();
  }
  interface LoginFragmentEvent{
    void startSingUp();
    void singInUser(HashMap<String,Object> information);
  }
}