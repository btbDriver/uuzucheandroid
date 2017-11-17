
package com.youyou.uucar.Utils.View;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import com.dd.processbutton.iml.ActionProcessButton;
import com.material.widget.CircularProgress;
import com.youyou.uucar.R;


public class LoadingFooter {
    //    protected ActionProcessButton mLoadingFooter;
//    CircularProgress mLoadingFooter;
    View mLoadingFooter;
    protected State mState;

    public static enum State {
        Idle, TheEnd, Loading
    }

    public LoadingFooter(Context context) {
        mLoadingFooter = LayoutInflater.from(context).inflate(R.layout.circular_progress_footer, null);
//        mLoadingFooter.setMode(ActionProcessButton.Mode.ENDLESS);
        setState(State.Idle);
    }

    public View getView() {
        return mLoadingFooter;
    }

    public State getState() {
        return mState;
    }

    public void setState(final State state, long delay) {
//        mLoadingFooter.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                setState(state);
//            }
//        }, delay);
    }

    public void setState(State status) {
//        if (mState == status) {
//            return;
//        }
//        mState = status;
//
//        mLoadingFooter.setVisibility(View.VISIBLE);
//
//        switch (status) {
//            case Loading:
//                mLoadingFooter.setEnabled(false);
//                mLoadingFooter.setProgress(50);
//                break;
//            case TheEnd:
//                mLoadingFooter.setEnabled(true);
//                mLoadingFooter.setProgress(100);
//                break;
//            default:
//                mLoadingFooter.setEnabled(true);
//                mLoadingFooter.setProgress(0);
//                break;
//        }
    }
}
