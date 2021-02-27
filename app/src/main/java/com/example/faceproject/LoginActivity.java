package com.example.faceproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.security.Guard;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    EditText et_id, et_pw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_id = findViewById(R.id.login_id);
        et_pw = findViewById(R.id.login_pw);



        //키 해시값 얻어와서 Logcat창에 출력하기  - 카카오개발자 키해시값 등록해야 해서
        String keyHash = Utility.INSTANCE.getKeyHash(this);
        Log.i("KeyHash",keyHash);


        //동적퍼미션
        String[] permissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,  Manifest.permission.READ_EXTERNAL_STORAGE};
        if( ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED
            || ActivityCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }

    public void clickLogin(View view) {
        //카카오 계정으로 로그인하기
        LoginClient.getInstance().loginWithKakaoAccount(this, new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if(oAuthToken !=null){ //로그인 정보객체가 있다면
                    //카카오디벨로퍼에서, 내어플리케이션/제품설정/카카오로그인/동의항목 에서 설정먼저해주셈
                    Toast.makeText(LoginActivity.this,"로그인성공",Toast.LENGTH_SHORT).show();

                    //로그인한 계정정보 얻어오기
                    UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                        @Override
                        public Unit invoke(User user, Throwable throwable) {
                            if(user != null){

                                GUser.userId = user.getId();//카카오 회원번호
                                //필수동의 항목의 회원프로필 정보(닉네임, 프로필이미지의 url)
                                GUser.nickname = user.getKakaoAccount().getProfile().getNickname();
                                GUser.profileUrl = user.getKakaoAccount().getProfile().getThumbnailImageUrl();
//                                Glide.with(LoginActivity.this).load(profileImageUrl).into(ivProfile);

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                //원래는 다시 로그인안하게 하려면 sharedpreference나 서버에다가 저장해서 불러오게해야함
                            }else{
                                Toast.makeText(LoginActivity.this, "사용자 정보요청 실패: "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            return null;
                        }
                    });

                }else{
                    Toast.makeText(LoginActivity.this, "로그인실패: "+throwable, Toast.LENGTH_SHORT).show();
                    Log.d("login", "_"+throwable.getMessage());
                }
                return null;
            }
        });

    }

    public void clickSignup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void clickLogin2(View view) {
        String id = et_id.getText().toString();
        String pw = et_pw.getText().toString();

        RetrofitService retrofitService = RetrofitHelper.getRetrofitInstanceGson().create(RetrofitService.class);
        Call<UserItem> call = retrofitService.checkID(id,pw);
        call.enqueue(new Callback<UserItem>() {
            @Override
            public void onResponse(Call<UserItem> call, Response<UserItem> response) {
                UserItem item = response.body();
                Log.d(".php", item.profileUrl);
                GUser.nickname = item.userID;
                GUser.profileUrl = "http://alexang.dothome.co.kr/Market2/"+item.profileUrl;
                GUser.userId = Long.parseLong(item.no);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onFailure(Call<UserItem> call, Throwable t) {
                Log.d(".php", "failed");
            }
        });

    }
}