package com.example.faceproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {

    TextView tvNickname;
    TextView tvEmail;
    CircleImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvNickname = findViewById(R.id.tv_nickname);
        tvEmail = findViewById(R.id.tv_email);
        ivProfile = findViewById(R.id.iv_msg);

        //키 해시값 얻어와서 Logcat창에 출력하기  - 카카오개발자 키해시값 등록해야 해서
        String keyHash = Utility.INSTANCE.getKeyHash(this);
        Log.i("KeyHash",keyHash);
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

    public void clickLogout(View view) {
        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                if(throwable!=null){  //throwable이 있다는 건 에러가 있다는것임
                    Toast.makeText(LoginActivity.this, "로그아웃실패", Toast.LENGTH_SHORT).show();
                    Log.d("login",throwable.getMessage());
                }else{
                    Toast.makeText(LoginActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();

                    //로그인 회원정보 화면들 모두 초기화
                    tvEmail.setText("이메일");
                    tvNickname.setText("닉네임");
                    Glide.with(LoginActivity.this).load(R.mipmap.ic_launcher).into(ivProfile);
                }
                return null;
            }
        });
    }
}