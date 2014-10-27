package com.luciofm.droidcon.ifican.fragment;



import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.luciofm.droidcon.ifican.R;
import com.luciofm.droidcon.ifican.anim.XFractionProperty;
import com.luciofm.droidcon.ifican.anim.YFractionProperty;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class WorkFragment extends BaseFragment {

    @InjectView(R.id.imageMuambator1)
    ImageView imageMuambator1;
    @InjectView(R.id.imageMuambator2)
    ImageView imageMuambator2;

    @InjectView(R.id.imageWhi1)
    ImageView imageWhi1;
    @InjectView(R.id.imageWhi2)
    ImageView imageWhi2;

    public WorkFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_work;
    }

    @Override
    public String getMessage() {
        return "Work on We Heart It and Muambator - explain WHI and Muambator.";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        ButterKnife.inject(this, v);

        imageMuambator1.animate().alpha(1f).setDuration(200).start();
        imageWhi1.animate().alpha(1f).setDuration(200).start();

        v.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isAdded())
                    return;
                imageMuambator1.animate().alpha(0f).setDuration(1000).start();
                imageMuambator2.animate().alpha(1f).setDuration(1000).start();
                imageWhi1.animate().alpha(0f).setDuration(1000).start();
                imageWhi2.animate().alpha(1f).setDuration(1000).start();
            }
        }, 200);

        currentStep = 1;
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
        return enter ? null :
                ObjectAnimator.ofFloat(null, new YFractionProperty(), 0f, -1f);
    }
}
