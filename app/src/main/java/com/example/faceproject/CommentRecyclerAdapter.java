package com.example.faceproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.service.controls.templates.TemperatureControlTemplate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.VH> {
    Context context;
    ArrayList<CommentItem> items;

    public CommentRecyclerAdapter(Context context, ArrayList<CommentItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CommentItem item = items.get(position);
        Glide.with(context).load(item.writerProfile).into(holder.profileImg);
        holder.tv_nickname.setText(item.writerNickname);
        holder.tv_msg.setText(item.commentMsg);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class VH extends RecyclerView.ViewHolder {
        CircleImageView profileImg;
        TextView tv_nickname;
        TextView tv_msg;

        public VH(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profileimg);
            tv_nickname = itemView.findViewById(R.id.tv_nickname);
            tv_msg = itemView.findViewById(R.id.tv_msg);
        }
    }

}