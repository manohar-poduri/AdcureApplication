package com.example.adcureapplication;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adcureapplication.Interface.ItemClickListener;

public class MyDoctorView extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView nme,spe,dte,amnt;
    public ItemClickListener listener;

    public MyDoctorView(@NonNull View itemView) {
        super(itemView);
        nme=(TextView)itemView.findViewById(R.id.name_my);
        spe=(TextView)itemView.findViewById(R.id.spec_mydctr);
         dte=(TextView)itemView.findViewById(R.id.dte_my);
        amnt=(TextView)itemView.findViewById(R.id.amnt_my);
     }
    public void setItemClickListener(ItemClickListener listener){
        this.listener=listener;
    }
    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);

    }
}
