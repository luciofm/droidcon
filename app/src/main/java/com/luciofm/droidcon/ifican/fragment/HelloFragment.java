package com.luciofm.droidcon.ifican.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luciofm.droidcon.ifican.R;
import com.luciofm.droidcon.ifican.activity.MainActivity;
import com.luciofm.droidcon.ifican.anim.XFractionProperty;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelloFragment extends BaseFragment {

    public HelloFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_hello;
    }

    @Override
    public String getMessage() {
        return "First, hello and thank you everyone…";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, v);
        currentStep = 1;
        return v;
    }

    @Override
    public void onNextPressed() {
        ((MainActivity) getActivity()).nextFragment();
    }

    @OnClick(R.id.container)
    public void onClick() {
        onNextPressed();
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (transit == 0 | enter) {
            return null;
        }

        //Target will be filled in by the framework
        return ObjectAnimator.ofFloat(null, new XFractionProperty(), 0f, -1f);
    }

}
