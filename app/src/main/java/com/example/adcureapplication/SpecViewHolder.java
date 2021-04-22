package com.example.adcureapplication;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adcureapplication.Interface.ItemClickListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpecViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ItemClickListener listener;
    public Button btn;
    public SpecViewHolder(@NonNull View itemView) {
        super(itemView);

    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener=listener;
    }
    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }


}
