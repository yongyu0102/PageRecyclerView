package com.pengzhang.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/**
 * description:
 * author：pz
 * 时间：2017/4/19 :22:24
 */

public class PagingScrollHelper {
    private RecyclerView mRecyclerView = null;
    private ValueAnimator mAnimator = null;
    private MyOnScrollListener mOnScrollListener = new MyOnScrollListener();
    private onPageChangeListener mOnPageChangeListener;
    private MyOnFlingListener mOnFlingListener = new MyOnFlingListener();
    //当前滑动距离
    private int offsetY = 0;
    private int offsetX = 0;

    //按下屏幕点
    private int startY = 0;
    private int startX = 0;

    //最后一个可见 view 位置
    private int lastItemPosition = -1;
    //第一个可见view的位置
    private int firstItemPosition = -2;
    //总 itemView 数量
    private int totalNum;
    //滑动至耨一页
    private int pageNum = -1;
    //一次滚动 n 页
    private int indexPage;

    private enum ORIENTATION {
        HORIZONTAL, VERTICAL, NULL
    }

    private ORIENTATION mOrientation = ORIENTATION.HORIZONTAL;

    public void setUpRecycleView(RecyclerView recycleView) {
        if (recycleView == null) {
            throw new IllegalArgumentException("recycleView must be not null");
        }
        mRecyclerView = recycleView;
        recycleView.setOnFlingListener(mOnFlingListener);
        recycleView.setOnScrollListener(mOnScrollListener);
        recycleView.setOnTouchListener(mOnTouchListener);
        updateLayoutManger();
    }

    public void updateLayoutManger() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager.canScrollVertically()) {
                mOrientation = ORIENTATION.VERTICAL;
            } else if (layoutManager.canScrollHorizontally()) {
                mOrientation = ORIENTATION.HORIZONTAL;
            } else {
                mOrientation = ORIENTATION.NULL;
            }
            if (mAnimator != null) {
                mAnimator.cancel();
            }
            startX = 0;
            startY = 0;
            offsetX = 0;
            offsetY = 0;

        }

    }

    private class MyOnFlingListener extends RecyclerView.OnFlingListener {

        @Override
        public boolean onFling(int velocityX, int velocityY) {
            if (mOrientation == ORIENTATION.NULL) {
                return false;
            }
            //获取开始滚动时所在页面的index
            int page = getStartPageIndex();

            //记录滚动开始和结束的位置
            int endPoint = 0;
            int startPoint = 0;

            //如果是垂直方向
            if (mOrientation == ORIENTATION.VERTICAL) {
                //开始滚动位置，当前开始执行 scrollBy 位置
                startPoint = offsetY;
                if(velocityY==Integer.MAX_VALUE){
                    page +=indexPage;
                }else if (velocityY < 0) {
                    page--;
                } else if (velocityY > 0) {
                    page++;
                } else if (pageNum != -1) {
                    startPoint=0;
                    page = pageNum-1;
                }
                //更具不同的速度判断需要滚动的方向
                //一次滚动一个 mRecyclerView 高度
                endPoint = page * mRecyclerView.getHeight();

            } else {
                startPoint = offsetX;
                if(velocityX==Integer.MAX_VALUE){
                    page +=indexPage;
                }else if (velocityX < 0) {
                    page--;
                } else if (velocityX > 0) {
                    page++;
                } else if (pageNum != -1) {
                    startPoint=0;
                    page = pageNum-1;
                }
                endPoint = page * mRecyclerView.getWidth();

            }
            //使用动画处理滚动
            if (mAnimator == null) {
                mAnimator = ValueAnimator.ofInt(startPoint, endPoint);
                mAnimator.setDuration(300);
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int nowPoint = (int) animation.getAnimatedValue();

                        if (mOrientation == ORIENTATION.VERTICAL) {
                            int dy = nowPoint - offsetY;
                            if (dy == 0) return;
                            //这里通过RecyclerView的scrollBy方法实现滚动。
                            mRecyclerView.scrollBy(0, dy);
                        } else {
                            int dx = nowPoint - offsetX;
                            mRecyclerView.scrollBy(dx, 0);
                        }
                    }
                });
                mAnimator.addListener(new AnimatorListenerAdapter() {
                    //动画结束
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //回调监听
                        if (null != mOnPageChangeListener) {
                            mOnPageChangeListener.onPageChange(getPageIndex());
                        }
                        startY = offsetY;
                        startX = offsetX;
                        //滚动完成，进行判断是否滚到头了或者滚到尾部了
                        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                        //判断是当前layoutManager是否为LinearLayoutManager
                        // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                        if (layoutManager instanceof LinearLayoutManager) {
                            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                            //获取最后一个可见view的位置
                            lastItemPosition = linearManager.findLastVisibleItemPosition();
                            //获取第一个可见view的位置
                            firstItemPosition = linearManager.findFirstVisibleItemPosition();

                        }
                        totalNum = mRecyclerView.getAdapter().getItemCount();
                        if (totalNum == lastItemPosition + 1) {
                            updateLayoutManger();
                        }
                        if (firstItemPosition == 0) {
                            updateLayoutManger();
                        }
                    }
                });
            } else {
                mAnimator.cancel();
                mAnimator.setIntValues(startPoint, endPoint);
            }

            mAnimator.start();

            return true;
        }
    }

    private class MyOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE && mOrientation != ORIENTATION.NULL) {
                boolean move;
                int vX = 0, vY = 0;
                if (mOrientation == ORIENTATION.VERTICAL) {
                    int absY = Math.abs(offsetY - startY);
                    //如果滑动的距离超过屏幕的一半表示需要滑动到下一页
                    move = absY > recyclerView.getHeight() / 2;
                    vY = 0;
                    if (move) {
                        vY = offsetY - startY < 0 ? -1000 : 1000;
                    }
                } else {
                    int absX = Math.abs(offsetX - startX);
                    move = absX > recyclerView.getWidth() / 2;
                    if (move) {
                        vX = offsetX - startX < 0 ? -1000 : 1000;
                    }
                }
                mOnFlingListener.onFling(vX, vY);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //滚动结束记录滚动的偏移量
            //记录当前滚动到的位置
            offsetY += dy;
            offsetX += dx;
        }
    }

    private MyOnTouchListener mOnTouchListener = new MyOnTouchListener();


    private class MyOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    }

    //获取当前滚动的页数
    private int getPageIndex() {
        int p = 0;
        if (mOrientation == ORIENTATION.VERTICAL) {
            //当前滚动到的位置除以屏幕高度的整数就是当前滚动的位置
            p = offsetY / mRecyclerView.getHeight();
        } else {
            p = offsetX / mRecyclerView.getWidth();
        }
        return p;
    }

    private int getStartPageIndex() {
        int p = 0;
        if (mOrientation == ORIENTATION.VERTICAL) {
            //当前按下坐标大对应的页数
            p = startY / mRecyclerView.getHeight();
        } else {
            p = startX / mRecyclerView.getWidth();
        }
        return p;
    }

    public void setOnPageChangeListener(onPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    public interface onPageChangeListener {
        void onPageChange(int index);
    }

    public void setPageNum(int page) {
        mRecyclerView.scrollToPosition(0);
        updateLayoutManger();
        this.pageNum = page;
        mOnFlingListener.onFling(0, 0);
    }

    public void setIndexPage(int indexPage){
        this.indexPage=indexPage;
        mOnFlingListener.onFling(Integer.MAX_VALUE,Integer.MAX_VALUE);
    }

}
