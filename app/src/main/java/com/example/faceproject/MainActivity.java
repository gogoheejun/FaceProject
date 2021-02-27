package com.example.faceproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    ArrayList<MarketItem> items= new ArrayList<>();
    RecyclerView recyclerView;
    MarketAdapter adapter;

    SwipeRefreshLayout refreshLayout;
    CircleImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile = findViewById(R.id.main_profile);

        recyclerView= findViewById(R.id.recycler);
        adapter= new MarketAdapter(this, items);
        recyclerView.setAdapter(adapter);

        Glide.with(this).load(GUser.profileUrl).into(profile);


        refreshLayout = findViewById(R.id.layout_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
                refreshLayout.setRefreshing(false);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("scroll", dy + "");



            }
        });

//
//        //동적퍼미션
//        String[] permissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        if( ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED ){
//            ActivityCompat.requestPermissions(this, permissions, 100);
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        loadData();
    }

    private void loadData() {
        Retrofit retrofit = RetrofitHelper.getRetrofitInstanceGson();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ArrayList<MarketItem>> call = retrofitService.loadDataFromServer(); //★★★
        call.enqueue(new Callback<ArrayList<MarketItem>>() {
            @Override
            public void onResponse(Call<ArrayList<MarketItem>> call, Response<ArrayList<MarketItem>> response) {
                items.clear();
                adapter.notifyDataSetChanged();

                ArrayList<MarketItem> list = response.body();
                for(MarketItem item: list){
                    items.add(0, item);
                    adapter.notifyItemInserted(0);
                }
                Log.d("main", "loadData: items에 추가");
            }

            @Override
            public void onFailure(Call<ArrayList<MarketItem>> call, Throwable t) {
                Log.d("main", "loadData: 에러"+t.getMessage());
                if(t.getMessage().equals("unexpected end of stream")) loadData();
            }
        });
    }

    public void clickEdit(View view) {
        Intent intent= new Intent(this, EditActivity.class);
        startActivity(intent);
    }
}