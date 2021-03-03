package com.hjhj.faceproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
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

public class SignupActivity extends AppCompatActivity {
    CircleImageView profileimg;
    String imgPath;
    EditText etId, etPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        profileimg = findViewById(R.id.signupProfileImage);
        etId = findViewById(R.id.signup_et_id);
        etPw = findViewById(R.id.signup_et_password);
    }

    //이미지고르기
    public void clickSignupProfile(View view) {
        Intent intent= new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 10);
    }

    //이미지결과받기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10 && resultCode==RESULT_OK){
            Uri uri = data.getData();
            if(uri != null){
                Glide.with(this).load(uri).into(profileimg);
                imgPath = getRealPathFromUri(uri);
            }
        }
    }
    //Uri -- > 절대경로로 바꿔서 리턴시켜주는 메소드
    String getRealPathFromUri(Uri uri){
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return  result;
    }

    //완료
    public void clickSignupOK(View view) {
        String id = etId.getText().toString();
        String pw = etPw.getText().toString();

        Retrofit retrofit = RetrofitHelper.getRetrofitInstanceScalars();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        MultipartBody.Part filePart = null;
        if(imgPath != null){
            File file = new File(imgPath);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            filePart = MultipartBody.Part.createFormData("img",file.getName(), requestBody);
        }

        Map<String, String> dataPart = new HashMap<>();
        dataPart.put("ID", id);
        dataPart.put("PW", pw);
//        dataPart.put("profileUrl", pw);

        Call<String> call = retrofitService.postSingupToServer(dataPart,filePart);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String s =response.body();
                Toast.makeText(SignupActivity.this, ""+s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "error"+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("failed", "onFailure: "+t.getMessage());
            }
        });
        finish();
    }


}