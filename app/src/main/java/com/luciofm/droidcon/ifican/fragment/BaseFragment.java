package com.luciofm.droidcon.ifican.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luciofm.droidcon.ifican.activity.BaseActivity;
import com.luciofm.droidcon.ifican.activity.MainActivity;
import com.luciofm.droidcon.ifican.anim.XFractionProperty;

import butterknife.ButterKnife;

/**
 * Created by luciofm on 5/23/14.
 */
public abstract class BaseFragment extends Fragment {

    protected int currentStep = 1;

    public abstract int getLayout();

    public abstract String getMessage();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        currentStep = 1;
        return inflater.inflate(getLayout(), parent, false);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    public void onNextPressed() {
        ((MainActivity) getActivity()).nextFragment();
    }

    public void onPrevPressed() {
        getActivity().onBackPressed();
    }
}
