package com.luciofm.droidcon.ifican.anim;

import android.animation.Animator;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.ViewGroup;


public class CustomTransition extends Transition {
    @Override
    public void captureStartValues(TransitionValues transitionValues) {
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        return super.createAnimator(sceneRoot, startValues, endValues);
    }
}
