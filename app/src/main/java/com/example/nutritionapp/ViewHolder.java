package com.example.nutritionapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView semana, circBrazo;


    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        semana = itemView.findViewById(R.id.post_semana);
        circBrazo = itemView.findViewById(R.id.post_circBrazo);

    }
}
