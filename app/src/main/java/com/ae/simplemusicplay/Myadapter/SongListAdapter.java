package com.ae.simplemusicplay.Myadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ae.simplemusicplay.R;
import com.ae.simplemusicplay.model.SongInfo;

import java.util.List;


public class SongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    List<SongInfo> messageList;

    private static ClickListener clickListener;
    //初始化
    public SongListAdapter(Context context, List<SongInfo> messageList) {
        this.mContext = context;
        this.messageList = messageList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    //没什么用
    public SongListAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    //创建新View，被LayoutManager所调用
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ItemViewHolder(mLayoutInflater.inflate(R.layout.image_text_view, parent, false));

    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        //设置图片
        //((ItemViewHolder) holder).songImgView.setText(messageList.get(position).get);
        //设置歌名
        ((ItemViewHolder) holder).nameTextView.setText(messageList.get(position).getSongName());
        //设置歌手名
        ((ItemViewHolder) holder).singerTextView.setText(messageList.get(position).getArtistName());

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }




    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //创建3个view，对应列表的图片，歌名，歌手名
        ImageView songImgView;
        TextView nameTextView;
        TextView singerTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            songImgView = (ImageView) itemView.findViewById(R.id.image_view);
            nameTextView = (TextView) itemView.findViewById(R.id.text_view_name);
            singerTextView = (TextView) itemView.findViewById(R.id.text_view_singer);
            itemView.setOnClickListener(this);
            //itemView.setOnLongClickListener(this);

            //一般的设置点击事件
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i("TextViewHolder", "onClick--> position = " + getPosition());
//
//                }
//            });
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);

        }

        //长按事件
//        @Override
//        public boolean onLongClick(View v) {
//            clickListener.onItemLongClick(getAdapterPosition(), v);
//            return false;
//        }
    }
    public void setOnItemClickListener(ClickListener clickListener) {
        SongListAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        // 长按事件
        // void onItemLongClick(int position, View v);
    }

}
