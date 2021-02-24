package com.example.faceproject;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WritecommentActivity extends AppCompatActivity {

    ArrayList<CommentItem> items = new ArrayList<>();
    RecyclerView recyclerView;
    CommentRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writecomment);

        recyclerView = findViewById(R.id.writecomment_recycler);
        adapter = new CommentRecyclerAdapter(this, items);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        int parentNo = intent.getIntExtra("parentNo",0);
        Log.d("comments",parentNo+"");

        //동적퍼미션
        String[] permissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if( ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED ){
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        loadData();
    }
//
//    private void loadData() {
//        Retrofit retrofit = RetrofitHelper.getRetrofitInstanceGson();
//        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
//        Call<ArrayList<MarketItem>> call = retrofitService.loadDataFromServer(); //★★★
//        call.enqueue(new Callback<ArrayList<MarketItem>>() {
//            @Override
//            public void onResponse(Call<ArrayList<MarketItem>> call, Response<ArrayList<MarketItem>> response) {
//                items.clear();
//                adapter.notifyDataSetChanged();
//
//                ArrayList<MarketItem> list = response.body();
//                for(MarketItem item: list){
//                    items.add(0, item);
//                    adapter.notifyItemInserted(0);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<MarketItem>> call, Throwable t) {
//            }
//        });
//    }
}