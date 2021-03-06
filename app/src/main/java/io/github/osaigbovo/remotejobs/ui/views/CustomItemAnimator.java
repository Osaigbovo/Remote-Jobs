package io.github.osaigbovo.remotejobs.ui.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

/*
 * https://messedcode.com/dave/android-java-swipe-collapse-animation
 * */
public class CustomItemAnimator extends DefaultItemAnimator {

    private static final Interpolator COLLAPSE_INTERPOLATOR = new AccelerateInterpolator(3f);
    private static final int COLLAPSE_ANIM_DURATION = 600;

    private onAnimationEndListener animationEndListener;

    public CustomItemAnimator() {

    }

    public CustomItemAnimator(onAnimationEndListener listener) {
        this.animationEndListener = listener;
    }

    @Override
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder,
                                 @NonNull final RecyclerView.ViewHolder newHolder,
                                 @NonNull ItemHolderInfo preInfo, @NonNull ItemHolderInfo postInfo) {
        final View itemView = newHolder.itemView;
        AnimatorSet set = new AnimatorSet();

        LayoutParamHeightAnimator animHeight = LayoutParamHeightAnimator.collapse(itemView);
        animHeight.setDuration(COLLAPSE_ANIM_DURATION).setInterpolator(COLLAPSE_INTERPOLATOR);

        set.play(animHeight);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                dispatchChangeStarting(newHolder, false);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                dispatchChangeFinished(newHolder, false);
                if (animationEndListener != null) {
                    animationEndListener.onChangeEnd(newHolder);
                }
            }
        });
        set.start();
        return false;
    }

    public interface onAnimationEndListener {
        void onChangeEnd(RecyclerView.ViewHolder newHolder);
    }
}
