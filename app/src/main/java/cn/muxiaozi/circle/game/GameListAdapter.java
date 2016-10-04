package cn.muxiaozi.circle.game;

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
 * Created by 慕宵子 on 2016/7/11.
 */
class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;

    private ArrayList<GameListBean> mDatas;
    private LayoutInflater mInflater;
    private View mHeaderView;
    private AssetManager mAssetManager;

    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener {
        void onClick(int gameID);
    }

    GameListAdapter(Context context, ArrayList<GameListBean> datas) {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
        mAssetManager = context.getAssets();
    }

    void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER && mHeaderView != null)
            return new ViewHolder(mHeaderView);
        return new ViewHolder(mInflater.inflate(R.layout.listitem_game, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;

        final int pos = getRealPosition(holder);
        final GameListBean bean = mDatas.get(pos);

        Bitmap bm = ImageUtil.getGameIcon(mAssetManager, bean.getGameID());
        if(bm != null){
            holder.cover.setImageBitmap(bm);
        }
        holder.title.setText(bean.getTitle());
        holder.detail.setText(bean.getDetail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onClick(bean.getGameID());
                }
            }
        });
    }

    private int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mDatas.size() : mDatas.size() + 1;
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title;
        TextView detail;

        ViewHolder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) return;

            cover = (ImageView) itemView.findViewById(R.id.iv_cover);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            detail = (TextView) itemView.findViewById(R.id.tv_detail);
        }
    }
}

