package com.luciofm.droidcon.ifican.anim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by luciofm on 10/27/14.
 */
public class Pop extends Visibility {
    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        view.setScaleX(0f);
        view.setScaleY(0f);

        PropertyValuesHolder[] pvh = new PropertyValuesHolder[2];
        pvh[0] = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f);
        pvh[1] = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, pvh);
        anim.setInterpolator(new OvershootInterpolator());

        return anim;
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        PropertyValuesHolder[] pvh = new PropertyValuesHolder[2];
        pvh[0] = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f);
        pvh[1] = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, pvh);
        anim.setInterpolator(new AnticipateInterpolator());

        return anim;
    }
}
