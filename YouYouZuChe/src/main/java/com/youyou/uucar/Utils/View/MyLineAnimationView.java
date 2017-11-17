package com.youyou.uucar.Utils.View;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;

import com.youyou.uucar.Utils.DisplayUtil;
import com.youyou.uucar.Utils.empty.EmptyAnimatorListener;

import java.util.ArrayList;

/**
 * Created by taurusxi on 14/10/14.
 */
public class MyLineAnimationView extends View implements ValueAnimator.AnimatorUpdateListener {
    private static final int INIT_COLOR = 0xffffffff;
    public final ArrayList<ShapeHolder> balls = new ArrayList<ShapeHolder>();
    AnimatorSet bounceAnim = null;
    private final int x = (int) (58 * DisplayUtil.density + 0.5f);
    private final int y = (int) (25 * DisplayUtil.density + 0.5f);

    public MyLineAnimationView(Context context) {
        super(context);
        addBall(x, y, INIT_COLOR);
        addBall(x + (int) (DisplayUtil.density * 2 + 0.5f), y + (int) (DisplayUtil.density * 3 + 0.5f), INIT_COLOR);
        addBall(x + (int) (DisplayUtil.density * 3 + 0.5f), y + (int) (DisplayUtil.density * 6 + 0.5f), INIT_COLOR);
        balls.get(0).setAlpha(0f);
        balls.get(1).setAlpha(0f);
        balls.get(2).setAlpha(0f);
    }

    private void createAnimation() {
        if (bounceAnim == null) {
            final ShapeHolder ball = balls.get(0);
            PropertyValuesHolder pvhW = PropertyValuesHolder.ofFloat("width", ball.getWidth(),
                    DisplayUtil.density * 9 + 0.5f);
            ObjectAnimator whxyBouncer = ObjectAnimator.ofPropertyValuesHolder(ball, pvhW).setDuration(300);
            whxyBouncer.addUpdateListener(this);
            whxyBouncer.addListener(new EmptyAnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ball.setAlpha(1f);
                }
            });
            whxyBouncer.setRepeatMode(ValueAnimator.INFINITE);

            final ShapeHolder ball1 = balls.get(1);
            PropertyValuesHolder pvhW1 = PropertyValuesHolder.ofFloat("width", ball.getWidth(),
                    DisplayUtil.density * 9 + 0.5f);
            ObjectAnimator whxyBouncer1 = ObjectAnimator.ofPropertyValuesHolder(ball1, pvhW1).setDuration(300);
            whxyBouncer1.addUpdateListener(this);
            whxyBouncer1.addListener(new EmptyAnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ball1.setAlpha(1f);
                }
            });
            whxyBouncer1.setRepeatMode(ValueAnimator.INFINITE);

            final ShapeHolder ball2 = balls.get(2);
            PropertyValuesHolder pvhW2 = PropertyValuesHolder.ofFloat("width", ball.getWidth(),
                    DisplayUtil.density * 7 + 0.5f);
            ObjectAnimator whxyBouncer2 = ObjectAnimator.ofPropertyValuesHolder(ball2, pvhW2).setDuration(300);
            whxyBouncer2.addUpdateListener(this);
            whxyBouncer2.setRepeatMode(ValueAnimator.INFINITE);
            whxyBouncer2.addListener(new EmptyAnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ball2.setAlpha(1f);
                }
            });
            PropertyValuesHolder pvhWResert = PropertyValuesHolder.ofFloat("width", DisplayUtil.density * 9 + 0.5f, ball.getWidth());
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f);
            ObjectAnimator whxyBouncerResert = ObjectAnimator.ofPropertyValuesHolder(ball, pvhWResert, alpha).setDuration(300);
            whxyBouncerResert.addUpdateListener(this);
            whxyBouncerResert.setRepeatMode(ValueAnimator.INFINITE);

            PropertyValuesHolder pvhWResert1 = PropertyValuesHolder.ofFloat("width", DisplayUtil.density * 9 + 0.5f, ball.getWidth());
            PropertyValuesHolder alpha1 = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f);
            ObjectAnimator whxyBouncerResert1 = ObjectAnimator.ofPropertyValuesHolder(ball1, pvhWResert1, alpha1).setDuration(300);
            whxyBouncerResert1.addUpdateListener(this);
            whxyBouncerResert1.setRepeatMode(ValueAnimator.INFINITE);

            PropertyValuesHolder pvhWResert2 = PropertyValuesHolder.ofFloat("width", DisplayUtil.density * 7 + 0.5f, ball.getWidth());
            PropertyValuesHolder alpha2 = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f);
            ObjectAnimator whxyBouncerResert2 = ObjectAnimator.ofPropertyValuesHolder(ball2, pvhWResert2, alpha2).setDuration(300);
            whxyBouncerResert2.addUpdateListener(this);
            whxyBouncerResert2.setRepeatMode(ValueAnimator.INFINITE);

            bounceAnim = new AnimatorSet();
            bounceAnim.play(whxyBouncer);
            bounceAnim.play(whxyBouncer1).after(whxyBouncer);
            bounceAnim.play(whxyBouncer2).after(whxyBouncer1);
            bounceAnim.play(whxyBouncerResert).after(whxyBouncer2);
            bounceAnim.play(whxyBouncerResert1).after(whxyBouncerResert);
            bounceAnim.play(whxyBouncerResert2).after(whxyBouncerResert1);
            bounceAnim.addListener(new EmptyAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (bounceAnim != null)
                        bounceAnim.start();
                }
            });
        }
    }

    public void startAnimation() {
        createAnimation();
        bounceAnim.start();
    }

    public void clearLineAnimation() {
        if (bounceAnim != null) {
            bounceAnim.cancel();
            bounceAnim = null;
            balls.get(0).setAlpha(0f);
            balls.get(1).setAlpha(0f);
            balls.get(2).setAlpha(0f);
        }
    }

    private ShapeHolder createBall(float x, float y) {
        RectShape circle = new RectShape();
        final float size = 1 * DisplayUtil.density + 0.5f;
        circle.resize(size, size);
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
