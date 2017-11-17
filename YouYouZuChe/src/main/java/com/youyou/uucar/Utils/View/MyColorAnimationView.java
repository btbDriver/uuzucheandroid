package com.youyou.uucar.Utils.View;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;

import com.youyou.uucar.Utils.DisplayUtil;

import java.util.ArrayList;

/**
 * Created by taurusxi on 14/10/14.
 */
public class MyColorAnimationView extends View implements ValueAnimator.AnimatorUpdateListener {

    private static final float BALL_SIZE = DisplayUtil.density * 84 + 0.5f;
    private static final int INIT_COLOR = 0xff55acef;
    private static final int CHANGE_COLOR = 0xff007aff;
    public final ArrayList<ShapeHolder> balls = new ArrayList<ShapeHolder>();
    ValueAnimator colorAnim = null;

    public MyColorAnimationView(Context context) {
        super(context);
        addBall(0, 0, INIT_COLOR);
    }

    private void createAnimation() {
        if (colorAnim == null) {
            colorAnim = ObjectAnimator.ofInt(balls.get(0), "color", INIT_COLOR, CHANGE_COLOR);
            colorAnim.setDuration(1200);
            colorAnim.addUpdateListener(this);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        }
    }

    public void startAnimation() {
        createAnimation();
        colorAnim.start();
    }

    public void clearColorAnimation() {

        if (colorAnim != null) {
            colorAnim.cancel();
            colorAnim = null;
        }

    }

    private ShapeHolder createBall(float x, float y) {
        OvalShape circle = new OvalShape();
        circle.resize(BALL_SIZE, BALL_SIZE);
        ShapeDrawable drawable = new ShapeDrawable(circle);
        ShapeHolder shapeHolder = new ShapeHolder(drawable);
        shapeHolder.setX(x);
        shapeHolder.setY(y);
        return shapeHolder;
    }

    private void addBall(float x, float y, int color) {
        ShapeHolder shapeHolder = createBall(x, y);
        shapeHolder.setColor(color);
        balls.add(shapeHolder);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (ShapeHolder ball : balls) {
            canvas.translate(ball.getX(), ball.getY());
            ball.getShape().draw(canvas);
            canvas.translate(-ball.getX(), -ball.getY());
        }
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }
}
