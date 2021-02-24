 package com.example.faceproject;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.appcompat.widget.Toolbar;
 import androidx.loader.content.CursorLoader;

 import android.content.Intent;
 import android.database.Cursor;
 import android.net.Uri;
 import android.os.Bundle;
 import android.provider.MediaStore;
 import android.util.Log;
 import android.view.Menu;
 import android.view.MenuItem;
 import android.view.View;
 import android.widget.EditText;
 import android.widget.ImageView;
 import android.widget.TextView;
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

 public class EditActivity extends AppCompatActivity {
     EditText etMsg;
     ImageView iv;

     String imgPath; //업로드할 이미지의 절대경로

     CircleImageView civ;
     TextView writername;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_edit);
         etMsg= findViewById(R.id.et_msg);
         iv= findViewById(R.id.iv_msg);
         civ = findViewById(R.id.EditActivity_iv_profile);
         writername = findViewById(R.id.EditActivity_tv_profileName);

         Glide.with(this).load(GUser.profileUrl).into(civ);
         writername.setText(GUser.nickname);

         Toolbar toolbar = findViewById(R.id.EditActivity_toolbar);
         setSupportActionBar(toolbar);
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.toolbaroption, menu);
         return super.onCreateOptionsMenu(menu);
     }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         switch (item.getItemId()){
             case android.R.id.home:
                 onBackPressed();
                 return true;
             case R.id.menu_upload:
                 clickComplete();
                 return true;

         }
         return super.onOptionsItemSelected(item);
     }

     public void clickSelectImage(View view) {
         Intent intent= new Intent(Intent.ACTION_PICK);
         intent.setType("image/*");
         startActivityForResult(intent, 10);
     }


     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if(requestCode==10 && resultCode==RESULT_OK){
             Uri uri= data.getData();
             if(uri != null){
                 Glide.with(this).load(uri).into(iv);

                 //이미지 uri를 절대주소로 변경해야 파일업로드가 가능함
                 //uri --> 절대경로
                 imgPath= getRealPathFromUri(uri);
                 //경로가 잘 되었는지 확인
                 //new AlertDialog.Builder(this).setMessage(imgPath).show();
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


     public void clickComplete() {


         //전송할 데이터들 [ name, title, msg, price, imgPath ]
         Log.d("edittext",etMsg.getText().toString());
         String msg= etMsg.getText().toString();

         //레트로핏 작업 5단계
         Retrofit retrofit= RetrofitHelper.getRetrofitInstanceScalars();
         RetrofitService retrofitService= retrofit.create(RetrofitService.class);

         //이미지파일을 MultipartBody.Part로 포장 : @Part
         MultipartBody.Part filePart= null;
         if(imgPath!=null){
             File file= new File(imgPath);
             RequestBody requestBody= RequestBody.create(MediaType.parse("image/*"), file); //mime타입 및 file을 포장..즉 requestbody는 택배상자, multipartbody는 택배트럭!
             filePart= MultipartBody.Part.createFormData("img", file.getName(), requestBody); //서버에서 받는 식별자, 파일의이름, 택배상자
         }

         //나머지 String 데이터들은 Map Collection에 저장 : @PartMap
         Map<String, String> dataPart= new HashMap<>();

         dataPart.put("writerID",GUser.userId+"");
         dataPart.put("writerNickname",GUser.nickname);
         dataPart.put("writerProfileUrl",GUser.profileUrl);
         dataPart.put("msg", msg);
         dataPart.put("likesNum",11+""); //기본값 설정해줌
         dataPart.put("commentsNum",10+"");

         //글정보와 사진정보 함께 전달
         Call<String> call = retrofitService.postDataToServer(dataPart, filePart);
         call.enqueue(new Callback<String>() {
             @Override
             public void onResponse(Call<String> call, Response<String> response) {
                 String s = response.body();
                 Toast.makeText(EditActivity.this, ""+s, Toast.LENGTH_SHORT).show();
             }

             @Override
             public void onFailure(Call<String> call, Throwable t) {
                 Toast.makeText(EditActivity.this, "error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                 Log.d("upload", "onFailure: "+t.getMessage());
             }
         });

         finish();
     }
 }