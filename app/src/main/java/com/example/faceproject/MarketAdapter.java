package com.example.faceproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.VH> {

    Context context;
    ArrayList<MarketItem> items;
    String parentNo;
    String commentsNum;

    public MarketAdapter(Context context, ArrayList<MarketItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("commentsnum", "111");
        LayoutInflater inflater= LayoutInflater.from(context);
        View itemView= inflater.inflate(R.layout.recycler_item, parent, false);
        VH vh= new VH(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Log.d("commentsnum", "222");
        MarketItem item= items.get(position);

        //이미지 설정 [DB에는 이미지경로가 "./uploads/IMG_20210240_moana01.jpg"임]
        //안드로이드에서는 서버(dothome)의 전체 주소가 필요하기에
        String ivUrl="http://alexang.dothome.co.kr/Market2/"+item.msgImage;
        Glide.with(context).load(ivUrl).into(holder.ivMsg);

        String ivUrl2=item.writerProfileUrl;
        Glide.with(context).load(ivUrl2).into(holder.ivProfile);

        //텍스트들 지정
        holder.tvWriternickname.setText(item.writerNickname);
        holder.tvMsg.setText(item.msg);
        holder.tvlikes.setText(item.likesNum);
        holder.tvdates.setText(item.date);

//        holder.tvCommentsNum.setText(commentsNum);//getCommentsNum()함수 안으로 이동

        //좋아요 토글버튼
        if(item.favor==0) holder.tbFavor.setChecked(false);
        else holder.tbFavor.setChecked(true);

        holder.parentnum = item.no;
        getCommentsNum(item.no, holder);
    }


    public void getCommentsNum(int parentNo, VH holder){//게시판글의 no와 뷰홀더를 인자로 받아온다!
        //parentNo의 댓글수 갖고오기
        Retrofit retrofit = RetrofitHelper.getRetrofitInstanceGson();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<String> call = retrofitService.loadCommentsNumFromServer(parentNo+""); //★loadCommentsNum.php

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                commentsNum = response.body();
//                Toast.makeText(context, commentsNum, Toast.LENGTH_SHORT).show();
                holder.tvCommentsNum.setText("댓글 "+commentsNum+"개");

                Log.d("commentsnum", "갖고옴: parentNo:"+parentNo+"  commentsnum: "+commentsNum);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context, "error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("commentsnum", "onFailure: "+t.getMessage());
            }
        });
        return;
    }

    @Override
    public int getItemCount() {
        Log.d("commentsnum", "444");
        return items.size();
    }


    //뷰홀더 이너클래스
    class VH extends RecyclerView.ViewHolder{
        ImageView ivProfile;
        ImageView ivMsg;
        TextView tvWriternickname;
        TextView tvMsg;
        TextView tvlikes;
        TextView tvdates;
        TextView tvCommentsNum;
        ToggleButton tbFavor;
        int parentnum;

        TextView gotocomment;

        public VH(@NonNull View itemView) {
            super(itemView);
            Log.d("commentsnum", "555");

            ivProfile= itemView.findViewById(R.id.profileimg);
            tvWriternickname = itemView.findViewById(R.id.tv_name);
            tvMsg= itemView.findViewById(R.id.tv_msg);
            ivMsg = itemView.findViewById(R.id.iv_msg);
            tvlikes = itemView.findViewById(R.id.tv_likes);
            tvCommentsNum = itemView.findViewById(R.id.tv_commentsNum);
            tbFavor= itemView.findViewById(R.id.tb_favor);
            tvdates = itemView.findViewById(R.id.tv_dates);
            gotocomment = itemView.findViewById(R.id.gotoComment);
//            "좋아요" 하트아이콘을 체크하였을때 서버에 체크값 보내기
            tbFavor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    //클릭한 번째의 MarketItem객체의 favor값 변경
                    int position= getLayoutPosition();
                    MarketItem item= items.get(position);
                    item.favor= isChecked? 1:0; //favor변수 값 변경

                    //DB에 새로운 Data를 insert하는 것이 아니라 바뀌도록 Update 를 해야함
                    Retrofit retrofit= RetrofitHelper.getRetrofitInstanceGson();
                    RetrofitService retrofitService= retrofit.create(RetrofitService.class);
                    Call<MarketItem> call= retrofitService.updateData("updateFavor.php", item);
                    call.enqueue(new Callback<MarketItem>() {
                        @Override
                        public void onResponse(Call<MarketItem> call, Response<MarketItem> response) {
                            Toast.makeText(context, "관심이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<MarketItem> call, Throwable t) {
                            Toast.makeText(context, "error:"+t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            gotocomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //댓글쓰는 창으로 이동
                    Intent intent = new Intent(context, WritecommentActivity.class);
                    intent.putExtra("parentNo",parentnum);
                    context.startActivity(intent);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, parentnum+"", Toast.LENGTH_SHORT).show();
                }
            });
            tvCommentsNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //댓글쓰는 창으로 이동
                    Intent intent = new Intent(context, WritecommentActivity.class);
                    intent.putExtra("parentNo",parentnum);
                    context.startActivity(intent);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, parentnum+"", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}