package com.example.faceproject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

    //POST로 데이터를 보낼때는 @Field 사용 - @FormUrlEncoded와 함께 써야함
    //이미지 파일을 보낼때는 @Part 사용 - @Multipart와 함께 써야함
    //@FormUrlEncoded 와 @Multipart는 동시에 사용 불가
    //@Field처럼 php에서 $_POST로 받으려면 마치 GET방식의 @QueryMap처럼 @PartMap 사용
    @Multipart
    @POST("/Market2/insertDB.php")
    Call<String> postDataToServer(@PartMap Map<String, String> dataPart,
                                  @Part MultipartBody.Part filePart);

    @Multipart
    @POST("/Market2/commentInsert.php")
    Call<String> postCommentsToServer(@PartMap Map<String, String> dataPart);


    //서버에서 데이터를 json으로 파싱하여 가져오는 추상메소드
    @GET("/Market2/loadDB.php")
    Call<ArrayList<MarketItem>> loadDataFromServer();

    //코멘트 가지고 오기
    @GET("/Market2/loadComments.php")
    Call<ArrayList<CommentItem>> loadCommentsFromServer(@Query("parentNo") String parentNo);

    //코멘트 수 가져오기
    @GET("/Market2/loadCommentsNum.php")
    Call<String> loadCommentsNumFromServer(@Query("parentNo") String parentNo);

    //게시판글의 no가져오기
    @GET("/Market2/loadBigNo.php")
    Call<String> loadBigNumFromServer(@Query("time") String BigNo);


    //"좋아요" 클릭으로 데이터의 변경을 시키는 작업을 해주는 php를 실행시키기
    @PUT("/Market2/{fileName}")
    Call<MarketItem> updateData(@Path("fileName") String fileName, @Body MarketItem item);
}
