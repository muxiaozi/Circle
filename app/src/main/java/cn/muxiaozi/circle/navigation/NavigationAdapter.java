package cn.muxiaozi.circle.navigation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.utils.InfoUtil;

/**
 * Created by 慕宵子 on 2016/7/28.
 * <p>
 * 侧边菜单栏
 */
public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ViewHolder> {

    private ArrayList<NavItemBean> mData;
    private LayoutInflater mInflater;
    private OnNavItemClickListener mListener;

    public interface OnNavItemClickListener {
        void onClick(String title);
    }

    public NavigationAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        String version = InfoUtil.getVersion(context);

        mData = new ArrayList<>(5);
        mData.add(new NavItemBean(R.mipmap.nav_info, "个人资料", null, NavItemBean.TYPE_NORMAL));
        mData.add(new NavItemBean(R.mipmap.nav_setting, "设置", null, NavItemBean.TYPE_NORMAL));
        mData.add(new NavItemBean(R.mipmap.nav_upgrade, "版本更新", "V " + version, NavItemBean.TYPE_NORMAL));
        mData.add(new NavItemBean(R.mipmap.nav_feedback, "意见反馈", null, NavItemBean.TYPE_NORMAL));
        mData.add(new NavItemBean(R.mipmap.nav_about, "关于圈圈", null, NavItemBean.TYPE_NORMAL));
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getType();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder;
        switch (viewType) {
            case NavItemBean.TYPE_NORMAL:
                View item = mInflater.inflate(R.layout.listitem_navigation, parent, false);
                holder = new ViewHolder(item);
                break;
            case NavItemBean.TYPE_SEPARATOR:
                View separator = mInflater.inflate(R.layout.listitem_separator, parent, false);
                separator.setClickable(false);
                holder = new ViewHolder(separator);
                break;
            default:
                holder = null;
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final NavItemBean data = mData.get(position);
        switch (data.getType()) {
            case NavItemBean.TYPE_NORMAL:
                holder.title.setText(data.getTitle());
                if (data.getSubHead() != null) {
                    holder.subHead.setText(data.getSubHead());
                }
                holder.title.setCompoundDrawablesWithIntrinsicBounds(data.getIcon(), 0, 0, 0);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onClick(data.getTitle());
                        }
                    }
                });
                break;

            case NavItemBean.TYPE_SEPARATOR:
                break;
        }
    }

    public void setOnNavItemClickListener(OnNavItemClickListener l) {
        this.mListener = l;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subHead;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            subHead = (TextView) itemView.findViewById(R.id.tv_sub_head);
        }
    }
}
