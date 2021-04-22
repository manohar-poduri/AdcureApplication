package com.example.adcureapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyADapter extends RecyclerView.Adapter<MyADapter.ViewHolder> {
    private ArrayList<String> dataModalArrayList;
    private Context context;

    // constructor class for our Adapter
    public MyADapter(ArrayList<String> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyADapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new MyADapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.all_specialists, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyADapter.ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        final String modal = dataModalArrayList.get(position);
        holder.btn.setText(modal);

        // we are using Picasso to load images
        // from URL inside our image view.

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting on click listener
                // for our items of recycler i
                // tems.
                String butn=holder.btn.getText().toString();  // Toast.makeText(context, ""+butn, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(v.getContext(),ConsultNowActivity2.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("name",butn);
                v.getContext().startActivity(intent);
            }
        });
    }
public interface  ClickItem{
    void onClick(View view, int position);
}
    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btn=(itemView).findViewById(R.id.spec_btn);
        }
        // creating variables for our
        // views of recycler items.

    }

}