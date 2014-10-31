package com.luciofm.droidcon.ifican.fragment;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luciofm.droidcon.ifican.R;
import com.luciofm.droidcon.ifican.anim.XFractionProperty;
import com.luciofm.droidcon.ifican.anim.YFractionProperty;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CodeFragment extends BaseFragment {


    public CodeFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayout() {
        return R.layout.fragment_code;
    }

    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onNextPressed() {
        super.onNextPressed();
    }

    @OnClick(R.id.container)
    public void onClick() {
        onNextPressed();
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (transit == 0) {
            return null;
        }

        //Target will be filled in by the framework
        return enter ? ObjectAnimator.ofFloat(null, new YFractionProperty(), 1f, 0f)
                     : ObjectAnimator.ofFloat(null, new XFractionProperty(), 0f, -1f);
    }
}
