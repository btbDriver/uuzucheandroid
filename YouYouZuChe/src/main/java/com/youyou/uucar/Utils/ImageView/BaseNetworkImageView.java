package com.youyou.uucar.Utils.ImageView;

import android.content.Context;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by taurusxi on 14-10-2.
 */
public class BaseNetworkImageView extends NetworkImageView {
    public BaseNetworkImageView(Context context) {
        this(context, null, 0);
    }

    public BaseNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    private static final ImageView.ScaleType SCALE_TYPE = ImageView.ScaleType.CENTER_CROP;
//
//
//    private static final int DEFAULT_BORDER_WIDTH = 0;
//
//    private final RectF mDrawableRect = new RectF();
//
//    private final Matrix mShaderMatrix = new Matrix();
//    private final Paint mBitmapPaint = new Paint();
//
//    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
//
//    private Bitmap mBitmap;
//    private BitmapShader mBitmapShader;
//    private int mBitmapWidth;
//    private int mBitmapHeight;
//
//    public void setImageSrc(Bitmap bm){
//        mBitmap = bm;
//        setup();
//    }
//
//    private void setup() {
//        if (mBitmap == null) {
//            return;
//        }
//        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//        mBitmapPaint.setAntiAlias(true);
//        mBitmapPaint.setShader(mBitmapShader);
//        mBitmapHeight = mBitmap.getHeight();
//        mBitmapWidth = mBitmap.getWidth();
//        mDrawableRect.set(0, 0, mBitmapWidth, mBitmapHeight);
//        updateShaderMatrix();
//        invalidate();
//    }
//
//    private void updateShaderMatrix() {
//        float scale;
//        float dx = 0;
//        float dy = 0;
//        mShaderMatrix.set(null);
//        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
//            scale = mDrawableRect.height() / (float) mBitmapHeight;
//            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
//        } else {
//            scale = mDrawableRect.width() / (float) mBitmapWidth;
//            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
//        }
//
//        mShaderMatrix.setScale(scale, scale);
//        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) + mBorderWidth);
//        mBitmapShader.setLocalMatrix(mShaderMatrix);
//    }
//
//    @Override
//    public ImageView.ScaleType getScaleType() {
//        return SCALE_TYPE;
//    }
//
//    @Override
//    public void setScaleType(ImageView.ScaleType scaleType) {
//        if (scaleType != SCALE_TYPE) {
//            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
//        }
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (getDrawable() == null) {
//            return;
//        }
//        canvas.drawRect(mDrawableRect, mBitmapPaint);
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        setup();
//    }

}
