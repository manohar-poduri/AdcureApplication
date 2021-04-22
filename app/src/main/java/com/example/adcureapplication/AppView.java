package com.example.adcureapplication;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adcureapplication.Interface.ItemClickListener;

public class AppView extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView nme,spe,tme,dte,amnt;
    public ItemClickListener listener; public Button btn;

    public AppView(@NonNull View itemView) {
        super(itemView);
        nme=(TextView)itemView.findViewById(R.id.name_dctr_app);
        spe=(TextView)itemView.findViewById(R.id.spec_dctr_app);
        tme=(TextView)itemView.findViewById(R.id.time_app);
        dte=(TextView)itemView.findViewById(R.id.dte_app);
        amnt=(TextView)itemView.findViewById(R.id.amnt_dctr_app);
   btn=(Button)itemView.findViewById(R.id.chat_link);
    }
    public void setItemClickListener(ItemClickListener listener){
        this.listener=listener;
    }
    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);

    }
}
