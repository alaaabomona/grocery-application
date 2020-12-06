package com.example.groceryapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryapp.adapters.AdapterSupermarket;
import com.example.groceryapp.Constants;
import com.example.groceryapp.adapters.AdapterProduct;
import com.example.groceryapp.R;
import com.example.groceryapp.models.ModelSupermarket;
import com.example.groceryapp.models.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainUserActivity extends AppCompatActivity {

    private TextView nameTv,emailTv,tabProductsTv,tabShoppingListTv,tabHistoryTv,tabSupermarketTv,filteredProductTv;
    private EditText searchProductEt;
    private ImageButton logoutBtn, filterProductBtn;
    private RelativeLayout productsRl,shoppingListRl,historyRl,supermarketRl;
    private RecyclerView productsRv,supermarketRv;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productList;
    private ArrayList<ModelSupermarket> supermarketList;
    private AdapterProduct adapterProduct;
    private AdapterSupermarket adapterSupermarket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        tabProductsTv = findViewById(R.id.tabProductsTv);
        tabShoppingListTv = findViewById(R.id.tabShoppingListTv);
        tabSupermarketTv = findViewById(R.id.tabSupermarketTv);
        tabHistoryTv = findViewById(R.id.tabHistoryTv);
        searchProductEt = findViewById(R.id.searchProductEt);
        logoutBtn = findViewById(R.id.logoutBtn);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        productsRl = findViewById(R.id.productsRl);
        shoppingListRl = findViewById(R.id.shoppingListRl);
        supermarketRl = findViewById(R.id.supermarketRl);
        historyRl = findViewById(R.id.historyRl);
        filteredProductTv = findViewById(R.id.filteredProductTv);
        productsRv = findViewById(R.id.productsRv);
        supermarketRv = findViewById(R.id.supermarketRv);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();

        //check if there is a logged user
        checkUser();

        loadAllProducts();

        //at start show products ui
        showProductsUI();

        //search
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProduct.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make offline
                //sign out
                //go to login activity
                makeMeOffline();
            }
        });

        tabProductsTv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //load products
                showProductsUI();
            }
        });

        tabShoppingListTv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //load shopping list
                showShoppingListUI();

            }
        });

        tabHistoryTv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //load history
                showHistoryUI();

            }
        });

        tabSupermarketTv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //load supermarkets
                showSupermarketUI();
                loadSupermarkets();

            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainUserActivity.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constants.productCategories1[which];
                                filteredProductTv.setText(selected);
                                if(selected.equals("All")){
                                    //load all
                                    loadAllProducts();
                                }
                                else{
                                    //load filtered
                                    loadFilteredProducts(selected);
                                }
                            }
                        })
                .show();
            }
        });
    }

    private void loadSupermarkets() {
        supermarketList = new ArrayList<>();

        //get all supermarkets
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Supermarkets");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //before getting reset list
                supermarketList.clear();
                for(DataSnapshot ds1: dataSnapshot.getChildren()){
                    ModelSupermarket modelSupermarket = ds1.getValue(ModelSupermarket.class);
                    supermarketList.add(modelSupermarket);
                }

                //setup adapter
                adapterSupermarket = new AdapterSupermarket(MainUserActivity.this,supermarketList);
                //set adapter
                supermarketRv.setAdapter(adapterSupermarket);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadFilteredProducts(String selected) {
        productList = new ArrayList<>();

        //get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //before getting reset list
                productList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    String productCategory = ""+ds.child("productCategory").getValue();

                    //if selected category matches product category then add in list
                    if(selected.equals(productCategory)){
                        ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                        productList.add(modelProduct);
                    }
                }

                //setup adapter
                adapterProduct = new AdapterProduct(MainUserActivity.this,productList);
                //set adapter
                productsRv.setAdapter(adapterProduct);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

   private void loadAllProducts() {

        productList = new ArrayList<>();

        //get all products
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Products");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //before getting reset list
                productList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                    productList.add(modelProduct);
                }

                //setup adapter
                adapterProduct = new AdapterProduct(MainUserActivity.this,productList);
                //set adapter
                productsRv.setAdapter(adapterProduct);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showProductsUI() {
        //show products ui and hide shopping list ui and hide history ui
        productsRl.setVisibility(View.VISIBLE);
        shoppingListRl.setVisibility(View.GONE);
        historyRl.setVisibility(View.GONE);
        supermarketRl.setVisibility(View.GONE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabProductsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabShoppingListTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabShoppingListTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabHistoryTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabHistoryTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabSupermarketTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabSupermarketTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showShoppingListUI() {
        //show shopping list ui and hide products ui and hide history ui
        productsRl.setVisibility(View.GONE);
        historyRl.setVisibility(View.GONE);
        shoppingListRl.setVisibility(View.VISIBLE);
        supermarketRl.setVisibility(View.GONE);

        tabShoppingListTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabShoppingListTv.setBackgroundResource(R.drawable.shape_rect04);

        tabProductsTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabHistoryTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabHistoryTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabSupermarketTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabSupermarketTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showSupermarketUI() {
        productsRl.setVisibility(View.GONE);
        historyRl.setVisibility(View.GONE);
        shoppingListRl.setVisibility(View.GONE);
        supermarketRl.setVisibility(View.VISIBLE);

        tabSupermarketTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabSupermarketTv.setBackgroundResource(R.drawable.shape_rect04);

        tabProductsTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabHistoryTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabHistoryTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabShoppingListTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabShoppingListTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showHistoryUI() {
        //show history ui and hide products ui and hide shopping list ui
        productsRl.setVisibility(View.GONE);
        historyRl.setVisibility(View.VISIBLE);
        shoppingListRl.setVisibility(View.GONE);
        supermarketRl.setVisibility(View.GONE);

        tabHistoryTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabHistoryTv.setBackgroundResource(R.drawable.shape_rect04);

        tabProductsTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabShoppingListTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabShoppingListTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabSupermarketTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabSupermarketTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void makeMeOffline() {
        //after logging in , make user online
        progressDialog.setMessage("Logging out ...");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update, successfully
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed updating
                        progressDialog.dismiss();
                        Toast.makeText(MainUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
            finish();
        }
        else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            //get user data
                            String name = ""+ds.child("name").getValue();
                            String email = ""+ds.child("email").getValue();

                            //set user data
                            nameTv.setText(name);
                            emailTv.setText(email);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}