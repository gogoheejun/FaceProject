package com.example.faceproject;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WritecommentActivity extends AppCompatActivity {
    EditText et_comment;
    int parentNo;
    CircleImageView writerProfile;

    ArrayList<CommentItem> items = new ArrayList<>();
    RecyclerView recyclerView;
    CommentRecyclerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writecomment);

        et_comment = findViewById(R.id.et_writecomment);
        writerProfile = findViewById(R.id.iv_commentProfile);

        recyclerView = findViewById(R.id.writecomment_recycler);
        adapter = new CommentRecyclerAdapter(this, items);
        recyclerView.setAdapter(adapter);



        Intent intent = getIntent();
        parentNo = intent.getIntExtra("parentNo",0);
        Log.d("comments",parentNo+"");

        Glide.with(this).load(GUser.profileUrl).into(writerProfile);

        //동적퍼미션
        String[] permissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if( ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED ){
            ActivityCompat.requestPermissions(this, permissions, 100);
        }

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        loadData();
    }

    public void click_sendComment(View view) {
        String commentMsg = et_comment.getText().toString();
        //레트로핏 작업 5단계
        Retrofit retrofit= RetrofitHelper.getRetrofitInstanceScalars();
        RetrofitService retrofitService= retrofit.create(RetrofitService.class);

        //이미지파일을 MultipartBody.Part로 포장 : @Part
        MultipartBody.Part filePart= null;
        if(GUser.profileUrl!=null){
            File file= new File(GUser.profileUrl);
            RequestBody requestBody= RequestBody.create(MediaType.parse("image/*"), file); //mime타입 및 file을 포장..즉 requestbody는 택배상자, multipartbody는 택배트럭!
            filePart= MultipartBody.Part.createFormData("img", file.getName(), requestBody); //서버에서 받는 식별자, 파일의이름, 택배상자
        }

        //나머지 String 데이터들은 Map Collection에 저장 : @PartMap
        Map<String, String> dataPart= new HashMap<>();

        dataPart.put("writerID",GUser.userId+"");
        dataPart.put("writerNickname",GUser.nickname);
        dataPart.put("writerProfileUrl",GUser.profileUrl);
        dataPart.put("msg", commentMsg);
        dataPart.put("parentNo",parentNo+""); //기본값 설정해줌


        Call<String> call = retrofitService.postCommentsToServer(dataPart);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String s = response.body();
                Toast.makeText(WritecommentActivity.this, ""+s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(WritecommentActivity.this, "error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadData() {
        Log.d("comment", "loadData: 시작");
        Retrofit retrofit = RetrofitHelper.getRetrofitInstanceGson();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ArrayList<CommentItem>> call = retrofitService.loadCommentsFromServer(parentNo+""); //★★★

        call.enqueue(new Callback<ArrayList<CommentItem>>() {
            @Override
            public void onResponse(Call<ArrayList<CommentItem>> call, Response<ArrayList<CommentItem>> response) {
                items.clear();
                adapter.notifyDataSetChanged();

                ArrayList<CommentItem> list = response.body();
                for(CommentItem item: list){
                    items.add(0, item);
                    adapter.notifyItemInserted(0);
                }
                Log.d("comment", "loadData: items에 추가");
            }

            @Override
            public void onFailure(Call<ArrayList<CommentItem>> call, Throwable t) {
                Log.d("comment", "loadData: 에러"+t.getMessage());
            }
        });
    }
}