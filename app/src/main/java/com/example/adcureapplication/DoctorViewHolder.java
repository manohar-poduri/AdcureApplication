package com.example.adcureapplication;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adcureapplication.Interface.ItemClickListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView doctorname,doctorSpecialist,doctorExp,doctorAdre,dctrHsptl,dctrQual,dctrAmnt;
        public CircleImageView doctorImage;
        public ItemClickListener listener;
        public Button btn;
    public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
             btn =(Button)itemView.findViewById(R.id.btn_dctr);
            doctorname=(TextView)itemView.findViewById(R.id.name_dctr);
            doctorSpecialist=(TextView)itemView.findViewById(R.id.spec_dctr);
            doctorExp=(TextView)itemView.findViewById(R.id.exp_dctr);
            doctorAdre=(TextView)itemView.findViewById(R.id.addr_dctr);
        dctrHsptl=(TextView)itemView.findViewById(R.id.hsptl_dctr);
        dctrQual=(TextView)itemView.findViewById(R.id.qual_dctr);
        dctrAmnt=(TextView)itemView.findViewById(R.id.amnt_dctr);
          }
        public void setItemClickListener(ItemClickListener listener){
            this.listener=listener;
        }
        @Override
        public void onClick(View view) {
            listener.onClick(view,getAdapterPosition(),false);
        }




}
