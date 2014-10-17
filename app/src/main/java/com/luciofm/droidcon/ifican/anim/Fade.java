package com.luciofm.droidcon.ifican.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by luciofm on 10/16/14.
 */
public class Fade extends android.transition.Fade {

    long startDelay = 0;

    public Fade() {
        super();
    }

    public Fade(int fadingMode) {
        super(fadingMode);
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        Animator anim = super.onAppear(sceneRoot, startValues, startVisibility, endValues, endVisibility);
        if (anim != null) {
            anim.setStartDelay(startDelay);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }
            });
        }
        return anim;
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        return super.onDisappear(sceneRoot, startValues, startVisibility, endValues, endVisibility);
    }

    @Override
    public Transition setStartDelay(long startDelay) {
        this.startDelay = startDelay;
        return super.setStartDelay(startDelay);
    }

    @Override
    public long getStartDelay() {
        return this.startDelay;
    }
}
