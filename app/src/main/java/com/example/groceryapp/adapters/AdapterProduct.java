package com.example.groceryapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.FilterProduct;
import com.example.groceryapp.R;
import com.example.groceryapp.models.ModelProduct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.HolderProduct> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProduct filter;


    public AdapterProduct(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.product_item,parent,false);
        return new HolderProduct(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderProduct holder, int position) {
        //get data
        ModelProduct modelProduct = productList.get(position);
        String id = modelProduct.getProductId();
        String category = modelProduct.getProductCategory();
        String icon = modelProduct.getProductIcon();
        String name = modelProduct.getProductName();

       ///set data
        holder.productNameTv.setText(name);

        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_shopping_cart).into(holder.productIconIv);
        }
        catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_shopping_cart);
        }

        holder.addToShoppingListTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add to shopping list
                showQuantityDialog(modelProduct);
            }
        });

    }

    private int quantity = 0;
    private void showQuantityDialog(ModelProduct modelProduct) {
        //inflate layout for dialog
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity,null);
        //init layout views
        ImageView productIv = view.findViewById(R.id.productIv);
        TextView pNameTv = view.findViewById(R.id.pNameTv);
        ImageButton decrementBtn = view.findViewById(R.id.decrementBtn);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        ImageButton incrementBtn = view.findViewById(R.id.incrementBtn);
        Button continueBtn = view.findViewById(R.id.continueBtn);
        
        //get data from model
        String id = modelProduct.getProductId();
        String icon = modelProduct.getProductIcon();
        String name = modelProduct.getProductName();
        quantity = 1;
        
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_cart).into(productIv);
        }
        catch (Exception e) {
            productIv.setImageResource(R.drawable.ic_cart);
        }
        
        pNameTv.setText(""+name);
        quantityTv.setText(""+quantity);
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        //increase quantity of the product
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                quantityTv.setText(""+quantity);
            }
        });

        //decrease quantity of the product
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantity>1){
                    quantity--;
                    quantityTv.setText(""+quantity);
                }
            }
        });

        //increase quantity of the product
         continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = pNameTv.getText().toString().trim();
                String quantity = quantityTv.getText().toString().trim();
                
                //add to db
                addToShoppingList(id,name,quantity);
                dialog.dismiss();;
            }
        });
    }

    private void addToShoppingList(String id, String name, String quantity) {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("pid",""+id);
        hashMap.put("productName",""+name);
        hashMap.put("productQuantity",""+quantity);
        

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null) {
            filter = new FilterProduct(this, filterList);
        }
        return filter;
    }

    class HolderProduct extends RecyclerView.ViewHolder{

        private ImageView productIconIv;
        private TextView productNameTv, addToShoppingListTv;

        public HolderProduct(@NonNull View itemView) {
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            productNameTv = itemView.findViewById(R.id.productNameTv);
            addToShoppingListTv = itemView.findViewById(R.id.addToShoppingListTv);
        }
    }
}
