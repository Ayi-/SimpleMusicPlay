package com.ae.simplemusicplay.Myadapter;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ae.simplemusicplay.R;
import com.ae.simplemusicplay.Util.OpUtil;
import com.ae.simplemusicplay.activity.MainActivity;
import com.ae.simplemusicplay.model.SongInfo;

import java.util.List;


public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ItemViewHolder> {
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
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ItemViewHolder(mLayoutInflater.inflate(R.layout.image_text_view, parent, false));

    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        SongInfo songInfo = messageList.get(position);


        //设置图片
        //((ItemViewHolder) holder).songImgView.setText(messageList.get(position).get);
        //设置歌名
        holder.nameTextView.setText(songInfo.getSongName());
        //设置歌手名
        holder.singerTextView.setText(songInfo.getArtistName());

        //设置图片
        //((ItemViewHolder) holder).songImgView.setImageBitmap(ImageUtil.getArtwork(mContext, messageList.get(position).getSongId(), messageList.get(position).getAlbumId(), true, true));
        //设置音乐时长
        holder.durationTextView.setText(formatTime(songInfo.getDuration()));

        Uri uri = ContentUris.withAppendedId(OpUtil.ARTISTURI, songInfo.getAlbumId());
        MainActivity.imageLoader.displayImage(String.valueOf(uri), holder.songImgView, MainActivity.options);


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
        TextView durationTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            durationTextView = (TextView) itemView.findViewById(R.id.music_duration);

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

    /**
     * 格式化时间，将毫秒转换为分:秒格式
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);

    }
}
