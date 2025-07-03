package com.example.recipebook.d_AddEditRecipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipebook.databinding.CardViewTextBinding;

import java.util.ArrayList;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder> {
  ArrayList<String> texts;
  CardInterface listener;

  public CardViewAdapter(ArrayList<String> texts, CardInterface listener) {
    this.texts = texts;
    this.listener = listener;
  }

  @NonNull
  @Override
  public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new CardViewHolder(CardViewTextBinding.inflate
            (LayoutInflater.from(parent.getContext()),parent,false));
  }

  @Override
  public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
    int pos = position;
    holder.text.setText(texts.get(pos));
    holder.cardView.setOnClickListener(view -> {
      listener.getCard(pos);
    });
  }

  @Override
  public int getItemCount() {
    return texts.size();
  }

  class CardViewHolder extends RecyclerView.ViewHolder{
    TextView text;
    CardView cardView;
    public CardViewHolder(CardViewTextBinding binding) {
      super(binding.getRoot());
      text = binding.text;
      cardView = binding.getRoot();
    }
  }

  public interface CardInterface{
    void getCard(int position);
  }
}
