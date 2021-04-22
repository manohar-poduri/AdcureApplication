package com.example.adcureapplication;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adcureapplication.Interface.ItemClickListener;

public class PaymntsView extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView nme,spe,tme,dte,amnt;
    public ItemClickListener listener; public Button btn;

    public PaymntsView(@NonNull View itemView) {
        super(itemView);
        nme=(TextView)itemView.findViewById(R.id.dctr_paid);
        spe=(TextView)itemView.findViewById(R.id.spec_paid);

        amnt=(TextView)itemView.findViewById(R.id.amnt_dctr_paid);
        btn=(Button)itemView.findViewById(R.id.detai_btn);
    }
    public void setItemClickListener(ItemClickListener listener){
        this.listener=listener;
    }
    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);

    }
}