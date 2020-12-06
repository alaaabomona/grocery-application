package com.example.groceryapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.R;
import com.example.groceryapp.models.ModelSupermarket;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterSupermarket extends RecyclerView.Adapter<AdapterSupermarket.HolderSupermarket>{

    private Context context;
    public ArrayList<ModelSupermarket> supermarketList;

    public AdapterSupermarket(Context context, ArrayList<ModelSupermarket> supermarketList) {
        this.context = context;
        this.supermarketList = supermarketList;
    }

    @NonNull
    @Override
    public HolderSupermarket onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.show_supermarket,parent,false);
        return new HolderSupermarket(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSupermarket holder, int position) {

        //get data
        ModelSupermarket modelSupermarket = supermarketList.get(position);
        String id = modelSupermarket.getSupermarketId();
        String supermarketAddress = modelSupermarket.getAddress();
        String icon = modelSupermarket.getSupermarketImage();
        String name = modelSupermarket.getSupermarketName();

        ///set data
        holder.supermarketNameTv.setText(name);
        holder.supermarketAddressTv.setText(supermarketAddress);

        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_store).into(holder.supermarketIconIv);
        }
        catch (Exception e){
            holder.supermarketIconIv.setImageResource(R.drawable.ic_store);
        }

    }

    @Override
    public int getItemCount() {
        return supermarketList.size();
    }

    //view holder
    class HolderSupermarket extends RecyclerView.ViewHolder{

        private ImageView supermarketIconIv;
        private TextView supermarketNameTv, supermarketAddressTv;

        public HolderSupermarket(@NonNull View itemView) {
            super(itemView);

            supermarketIconIv = itemView.findViewById(R.id.supermarketIconIv);
            supermarketNameTv = itemView.findViewById(R.id.supermarketNameTv);
            supermarketAddressTv = itemView.findViewById(R.id.supermarketAddressTv);
        }
    }
}
