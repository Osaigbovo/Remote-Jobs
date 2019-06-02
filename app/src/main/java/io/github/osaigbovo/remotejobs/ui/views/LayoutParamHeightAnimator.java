package io.github.osaigbovo.remotejobs.ui.views;

import android.animation.ValueAnimator;
import android.view.View;

/*
 * https://messedcode.com/dave/android-java-swipe-collapse-animation
 * */
public class LayoutParamHeightAnimator extends ValueAnimator {

    private LayoutParamHeightAnimator(final View target, int... values) {
        setIntValues(values);

        addUpdateListener(valueAnimator -> {
            target.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
            target.requestLayout();
        });
    }

    public static LayoutParamHeightAnimator collapse(View target) {
        return new LayoutParamHeightAnimator(target, target.getHeight(), 0);
    }

}