package com.example.faceproject;

import android.content.Context;
import android.content.Intent;
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
    int parentNo;

    public MarketAdapter(Context context, ArrayList<MarketItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View itemView= inflater.inflate(R.layout.recycler_item, parent, false);
        VH vh= new VH(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

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
        holder.tvComments.setText(item.commentsNum);
        holder.tvdates.setText(item.date);

        //좋아요 토글버튼
        if(item.favor==0) holder.tbFavor.setChecked(false);
        else holder.tbFavor.setChecked(true);
        //holder.tbFavor.setChecked((item.favor==1)?true:false);

        parentNo = item.no;
    }

    @Override
    public int getItemCount() {
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
        TextView tvComments;
        ToggleButton tbFavor;

        TextView gotocomment;

        public VH(@NonNull View itemView) {
            super(itemView);

            ivProfile= itemView.findViewById(R.id.profileimg);
            tvWriternickname = itemView.findViewById(R.id.tv_name);
            tvMsg= itemView.findViewById(R.id.tv_msg);
            ivMsg = itemView.findViewById(R.id.iv_msg);
            tvlikes = itemView.findViewById(R.id.tv_likes);
            tvComments = itemView.findViewById(R.id.tv_comments);
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
                    intent.putExtra("parentNo",parentNo);
                    context.startActivity(intent);
                }
            });




        }
    }
}