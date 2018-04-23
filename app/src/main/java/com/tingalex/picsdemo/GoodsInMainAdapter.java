package com.tingalex.picsdemo;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tingalex.picsdemo.db.Good;

import java.util.List;

/**
 * Created by Ting on 2018/4/19.
 */

public class GoodsInMainAdapter extends RecyclerView.Adapter<GoodsInMainAdapter.ViewHolder> {
    private List<Good> goodList;
    private Context context;
    private onItemClickListener tempOnItemClickListener;
    public interface onItemClickListener{
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(onItemClickListener mOnItemClickListener){
        tempOnItemClickListener=mOnItemClickListener;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView goodView;
        ImageView goodImage;
        TextView goodName;
        public ViewHolder(View view){
            super(view);
            goodView=(CardView) view;
            goodImage=view.findViewById(R.id.good_image);
            goodName=view.findViewById(R.id.good_name);
        }
    }
    public GoodsInMainAdapter(Context mcontext, List<Good> mgoodList){
        context=mcontext;
        goodList=mgoodList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_goods_cell_in_main,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=holder.getAdapterPosition();
//                Good good=goodList.get(0);
                tempOnItemClickListener.onItemClick(holder.itemView,position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Good good=goodList.get(position);
        Log.i("bmob", "onBindViewHolder: "+good.getTitle());
        Glide.with(context).load(good.getPicurls().get(0)).into(holder.goodImage);
        holder.goodName.setText(good.getTitle());
    }


    @Override
    public int getItemCount() {
        return goodList.size();
    }
}
