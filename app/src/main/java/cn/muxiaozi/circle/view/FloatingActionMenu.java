package cn.muxiaozi.circle.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import cn.muxiaozi.circle.R;

/**
 * Created by 慕宵子 on 2016/5/8.
 * <p/>
 * 自定义悬浮菜单
 */
public class FloatingActionMenu extends ViewGroup implements View.OnClickListener {

    public static final int STATE_EXPAND = 0;       //打开状态
    public static final int STATE_COLLAPSE = 1;     //关闭状态
    public static final int STATE_WORK = 2;         //工作状态

    private static final int ANIMATOR_DURATION = 100; //动画持续时间

    public interface OnItemClickListener {
        int ACTION_JOIN = 0;
        int ACTION_INVITE = 1;
        int ACTION_CANCEL = 2;

        void onItemClick(int action);
    }

    /**
     * 主按钮
     */
    private FloatingActionButton mAddButton;

    /**
     * 菜单当前状态
     */
    private int mCurrentState = STATE_COLLAPSE;

    /**
     * 按钮点击监听
     */
    private OnItemClickListener mOnMenuItemClickListener;

    /**
     * 默认的按钮样式
     */
    private ColorStateList mOldColorStateList;

    public FloatingActionMenu(Context context) {
        this(context, null);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(View v) {
        if (mOnMenuItemClickListener != null) {
            if (v.equals(mAddButton)) {
                switch (mCurrentState) {
                    case STATE_COLLAPSE:
                        setState(STATE_EXPAND);
                        break;
                    case STATE_EXPAND:
                        setState(STATE_COLLAPSE);
                        break;
                    case STATE_WORK:
                        mOnMenuItemClickListener.onItemClick(OnItemClickListener.ACTION_CANCEL);
                        break;
                }
            } else if (v.equals(getChildAt(1))) {
                mOnMenuItemClickListener.onItemClick(OnItemClickListener.ACTION_INVITE);
                setState(STATE_COLLAPSE);
            } else if (v.equals(getChildAt(2))) {
                mOnMenuItemClickListener.onItemClick(OnItemClickListener.ACTION_JOIN);
                setState(STATE_COLLAPSE);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果菜单为展开状态，则拦截点击事件，并收缩菜单
        if (mCurrentState == STATE_EXPAND) {
            setState(STATE_COLLAPSE);
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //如果菜单为展开状态，则为屏幕加一层蒙板，以凸显菜单选项
        if (mCurrentState == STATE_EXPAND) {
            canvas.drawColor(Color.parseColor("#CC000000"));
        }
    }

    /**
     * 展开
     */
    private void expand(int oldState) {
        for (int i = 1; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(VISIBLE);
        }
        if (oldState == STATE_COLLAPSE) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(mAddButton, "rotation", 0.0f, 45.0f);
            animator.setDuration(ANIMATOR_DURATION);
            animator.start();
        } else if (oldState == STATE_WORK) {
            mAddButton.setBackgroundTintList(mOldColorStateList);
        }
    }

    /**
     * 收缩
     */
    private void collapse(int oldState) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mAddButton, "rotation", 45.0f, 0.0f);
        animator.setDuration(ANIMATOR_DURATION);
        animator.start();
        if (oldState == STATE_EXPAND) {
            for (int i = 1; i < getChildCount(); i++) {
                getChildAt(i).setVisibility(INVISIBLE);
            }
        } else if (oldState == STATE_WORK) {
            mAddButton.setBackgroundTintList(mOldColorStateList);
        }
    }

    /**
     * 工作
     */
    private void work(int oldState) {
        mAddButton.setBackgroundTintList(
                getResources().getColorStateList(R.color.selector_fab_menu_red));

        if (oldState == STATE_EXPAND) {
            for (int i = 1; i < getChildCount(); i++) {
                getChildAt(i).setVisibility(INVISIBLE);
            }
        } else if (oldState == STATE_COLLAPSE) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(mAddButton, "rotation", 0.0f, 45.0f);
            animator.setDuration(ANIMATOR_DURATION);
            animator.start();
        }
    }

    /**
     * 开始工作状态
     */
    public void startWork() {
        setState(STATE_WORK);
    }

    /**
     * 取消工作状态
     */
    public void cancelWork() {
        setState(STATE_COLLAPSE);
    }

    /**
     * 设置菜单状态
     *
     * @param newState 新状态
     */
    private void setState(int newState) {
        if (mCurrentState != newState) {
            switch (newState) {
                case STATE_COLLAPSE:
                    collapse(mCurrentState);
                    break;
                case STATE_EXPAND:
                    expand(mCurrentState);
                    break;
                case STATE_WORK:
                    work(mCurrentState);
                    break;
                default:
                    break;
            }
            mCurrentState = newState;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            for (int i = 0; i < 3; i++) {
                View childView = getChildAt(i);
                childView.setOnClickListener(this);
                int childWidth = childView.getMeasuredWidth();
                int childHeight = childView.getMeasuredHeight();

                switch (i) {
                    case 0:
                        l = (int) (getWidth() - childWidth - TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
                        t = (int) (getHeight() - childHeight - TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
                        break;

                    case 1:
                        childView.setVisibility(INVISIBLE);
                        l = (int) (getChildAt(0).getLeft() - childWidth - TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
                        t = getChildAt(0).getTop();
                        break;

                    case 2:
                        childView.setVisibility(INVISIBLE);
                        l = getChildAt(0).getLeft();
                        t = (int) (getChildAt(0).getTop() - childHeight - TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
                        break;
                }

                childView.layout(l, t, l + childWidth, t + childHeight);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 3) {
            throw new IllegalArgumentException("Child counts must be 3,include one addButton and two actionButton");
        }
        mAddButton = (FloatingActionButton) getChildAt(0);
        mOldColorStateList = mAddButton.getBackgroundTintList();
    }

    public void setOnMenuItemClickListener(OnItemClickListener listener) {
        this.mOnMenuItemClickListener = listener;
    }

    public boolean onBackPressed() {
        if (mCurrentState == STATE_EXPAND) {
            setState(STATE_COLLAPSE);
            return false;
        } else {
            return true;
        }
    }
}
