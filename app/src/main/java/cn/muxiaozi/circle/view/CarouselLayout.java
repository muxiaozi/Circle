package cn.muxiaozi.circle.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.utils.ImageUtil;

/**
 * Created by 慕宵子 on 2016/7/15.
 * <p/>
 * 轮播控件
 */
public class CarouselLayout extends HorizontalScrollView implements View.OnClickListener {

    //页面数
    private int mPageCount;
    //当前选择页面
    private int mCurrentPage = 0;
    //指示器之间距离
    private int mIndicatorMargin;
    //指示器半径
    private int mIndicatorRadius;

    //指示器与容器上边距
    private int mTopMargin;
    //指示器与容器左边距
    private int mLeftMargin;

    //屏幕大小
    private Point mScreenSize = new Point();

    //速度检测器
    private VelocityTracker mVelocityTracker;

    //指示器画笔
    Paint mCirclePaint = new Paint();

    private LinearLayout mWapper;

    private ArrayList<Integer> mGamesID;

    public interface onPageClickListener {
        void onClick(int gameID);
    }

    private onPageClickListener mOnPageClickListener;

    public CarouselLayout(Context context) {
        this(context, null);
        mWapper = new LinearLayout(getContext());
        mWapper.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mWapper.setOrientation(LinearLayout.HORIZONTAL);
        addView(mWapper, 0);
    }

    public CarouselLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mGamesID = new ArrayList<>(10);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(mScreenSize);

        mCirclePaint.setStrokeWidth(1);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);

        mIndicatorRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CarouselLayout);
        mIndicatorMargin = (int) a.getDimension(R.styleable.CarouselLayout_item_padding,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics()));
        a.recycle();

        setHorizontalScrollBarEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if (mOnPageClickListener != null)
            mOnPageClickListener.onClick(mGamesID.get(mCurrentPage));
    }

    private void updatePage() {
        AssetManager assetManager = getContext().getAssets();
        mWapper.removeAllViewsInLayout();
        for (Integer gameID : mGamesID) {
            ImageView iv = new ImageView(getContext());
            iv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageBitmap(ImageUtil.getGameCover(assetManager, gameID));
            mWapper.addView(iv);
        }
        requestLayout();
    }

    public void setPage(int[] gameIDs) {
        mGamesID.clear();
        for (int gameID : gameIDs) {
            mGamesID.add(gameID);
        }
        updatePage();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //如果布局中没有任何控件
        if (getChildCount() == 0) {
            mWapper = new LinearLayout(getContext());
            mWapper.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mWapper.setOrientation(LinearLayout.HORIZONTAL);
            addView(mWapper, 0);
        } else {
            mWapper = (LinearLayout) getChildAt(0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //限制容器最大高度
        int limitHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200,
                getResources().getDisplayMetrics());
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        height = Math.min(height, limitHeight);
        setMeasuredDimension(width, height);

        //设置内容宽度
        mWapper.setOnClickListener(this);
        for (int i = 0; i < mWapper.getChildCount(); i++) {
            mWapper.getChildAt(i).getLayoutParams().width = mScreenSize.x;
        }
        measureChildren(widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));

        mPageCount = ((LinearLayout) getChildAt(0)).getChildCount();
        mTopMargin = (int) (getHeight() * 0.85);
        mLeftMargin = (int) (mScreenSize.x / 2f - ((mPageCount - 1) * mIndicatorMargin / 2f));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                final int tempPage = getScrollX() / mScreenSize.x;
                if (mVelocityTracker.getXVelocity() > 0) {
                    smoothScrollTo(tempPage * mScreenSize.x, 0);
                    mCurrentPage = tempPage;
                } else {
                    smoothScrollTo((tempPage + 1) * mScreenSize.x, 0);
                    mCurrentPage = tempPage < mPageCount - 1 ? tempPage + 1 : tempPage;
                }
                if (null != mVelocityTracker) {
                    mVelocityTracker.clear();
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (int i = 0; i < mPageCount; i++) {
            if (i == mCurrentPage) {
                mCirclePaint.setColor(Color.WHITE);
                canvas.drawCircle(getScrollX() + mLeftMargin + i * mIndicatorMargin,
                        mTopMargin, mIndicatorRadius, mCirclePaint);
            } else {
                mCirclePaint.setColor(Color.GRAY);
                canvas.drawCircle(getScrollX() + mLeftMargin + i * mIndicatorMargin,
                        mTopMargin, mIndicatorRadius, mCirclePaint);
            }
        }
    }

    public void setOnPageClickListener(onPageClickListener listener) {
        this.mOnPageClickListener = listener;
    }
}
