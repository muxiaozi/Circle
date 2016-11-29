package cn.muxiaozi.circle.room;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.utils.ImageUtil;

/**
 * Created by 慕宵子 on 2016/7/24.
 */
public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {

    private ArrayList<UserBean> mData;
    private LayoutInflater mInflater;
    private AssetManager mAssetManager;

    private OnItemClickListener mListener;

    interface OnItemClickListener {
        void onClick(UserBean player);
    }

    PlayerAdapter(Context context, ArrayList<UserBean> mData) {
        mInflater = LayoutInflater.from(context);
        mAssetManager = context.getAssets();
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.listitem_game, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UserBean player = mData.get(position);
        Bitmap bitmap = ImageUtil.getHeadImg(mAssetManager, player.getHeadImage());
        if(bitmap != null){
            holder.headImage.setImageBitmap(bitmap);
        }

        holder.title.setText(player.getName() + (player.isPrepare() ? "-已准备" : "-未准备"));
        holder.detail.setText(player.getAutograph());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(player);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView headImage;
        TextView title;
        TextView detail;

        public ViewHolder(View itemView) {
            super(itemView);
            headImage = (ImageView) itemView.findViewById(R.id.iv_cover);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            detail = (TextView) itemView.findViewById(R.id.tv_detail);
        }
    }
}
