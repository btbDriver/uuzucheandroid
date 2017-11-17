package com.youyou.uucar.Utils.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.youyou.uucar.R;
import com.youyou.uucar.Utils.animation.AnimationUtils;

/**
 * Created by taurusxi on 14-9-2.
 */
public class UUProgressFramelayout extends FrameLayout {

    private Context mContext;
    private View progressView;
    private View noDataLayout;
    private View mainView;

    private int progressLayoutId;
    private int noDataLayoutId;
    public final int STATUS_LOADING = 0;
    public final int STATUS_FINISH = 1;
    public final int STATUS_NODATA = 2;
    public final int STATUS_ERROR = 3;
    public int status = STATUS_LOADING;

    public UUProgressFramelayout(Context context) {
        this(context, null, 0);
    }

    public UUProgressFramelayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UUProgressFramelayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.UUProgressFramelayout, defStyle, 0);

        progressLayoutId = a.getResourceId(R.styleable.UUProgressFramelayout_progress, R.layout.progress_framelayout);
        noDataLayoutId = a.getResourceId(R.styleable.UUProgressFramelayout_no_data, R.layout.nodata_framelayout);
        init();
    }

    private void init() {
        progressView = LayoutInflater.from(mContext).inflate(progressLayoutId, null, false);
        noDataLayout = LayoutInflater.from(mContext).inflate(noDataLayoutId, null, false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1) {
            throw new IllegalStateException("只能拥有一个孩子");
        }
        mainView = getChildAt(0);
        this.addView(noDataLayout);
        this.addView(progressView);
    }

    public void makeProgreeDismiss() {
        noDataLayout.setVisibility(GONE);
        AnimationUtils.makeProgressDismiss(progressView, mainView);
    }

    public void reloading() {
        AnimationUtils.makeProgressDismiss(mainView, progressView);
    }

    public void noDataReloading() {
        switch (status) {
            case STATUS_ERROR:

                break;
            case STATUS_FINISH:

                break;
            case STATUS_LOADING:

                break;
            case STATUS_NODATA:

                break;
        }

        noDataLayout.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
//        AnimationUtils.makeProgressDismiss(noDataLayout, progressView);
    }

    public void makeProgreeNoData() {
        mainView.setVisibility(GONE);

        AnimationUtils.makeProgressDismiss(progressView, noDataLayout);
    }


    private View getProgressView() {
        return progressView;
    }

    private View getMainView() {
        return mainView;
    }

}
