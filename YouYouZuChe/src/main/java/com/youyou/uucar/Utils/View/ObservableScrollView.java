package com.youyou.uucar.Utils.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.youyou.uucar.Utils.DisplayUtil;
import com.youyou.uucar.Utils.Support.MLog;


/**
 * Created on 15/3/2.
 *
 * @author xicheng
 * @email 505591443@qq.com
 * @github https://github.com/TaurusXi
 */
public class ObservableScrollView extends ScrollView {


    private boolean isReadyedForStart = true;
    private int oldScrollY = UNKOWN;
    //    private final static int START = 0;
//    private final static int END = 1;
    private final static int SCROLLING = 2;
    private final static int UNKOWN = -19232;
    private final Object obj = new Object();
    private Handler handler = new Handler() {

        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
//                case START:
//                    if (scrollViewListener != null) {
//                        scrollViewListener.onStartScroll();
//                    }
//                    break;
//                case END:
//                    if (scrollViewListener != null) {
//                        scrollViewListener.onEndScroll();
//                    }
//                    break;
                case SCROLLING:
                    int y = getScrollY();
                    if (msg.arg1 == getScrollY()) {
                        if (scrollViewListener != null) {
                            scrollViewListener.onEndScroll();
                        }
                    } else {
                        Message newMsg = new Message();
                        newMsg.what = SCROLLING;
                        newMsg.arg1 = y;
                        handler.sendMessageDelayed(newMsg, 10);
                    }
                    break;
            }

        }
    };
    private OnScrollListener scrollViewListener = null;

    public OnScrollListener getScrollViewListener() {
        return scrollViewListener;
    }

    public void setScrollViewListener(OnScrollListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }


    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScroll(t);
        }
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        switch (eventAction) {
            case MotionEvent.ACTION_UP:
                Message msg = new Message();
                msg.what = SCROLLING;
                msg.arg1 = getScrollY();
                handler.sendMessageDelayed(msg, 10);
                isReadyedForStart = true;
                oldScrollY = UNKOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isReadyedForStart) {
                    synchronized (obj) {
                        if (isReadyedForStart) {
                            if (oldScrollY == UNKOWN) {
                                oldScrollY = getScrollY();
                            }
                            int newY = getScrollY();
                            if (newY != oldScrollY) {
                                isReadyedForStart = false;
                                scrollViewListener.onStartScroll();
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                isReadyedForStart = true;
                oldScrollY = getScrollY();
                break;
            default:
                break;
        }

        float eventY = event.getY();
        MLog.e("ObservableScrollView","getScrollY() = " + getScrollY()+" eventY = " + eventY);
        if(getScrollY() <=10 &&  DisplayUtil.px2dip(getContext(),eventY)<222)
        {
            return false;
        }
        else
        {
            return super.onTouchEvent(event);
        }
//        return false;
    }

    public interface OnScrollListener {
        /**
         * 回调方法， 返回MyScrollView滑动的Y方向距离
         *
         * @param scrollY 、
         */
        public void onScroll(int scrollY);

        public void onStartScroll();

        public void onEndScroll();

        public void onScrollChanged(View view, int x, int y, int oldx, int oldy);

    }
}
