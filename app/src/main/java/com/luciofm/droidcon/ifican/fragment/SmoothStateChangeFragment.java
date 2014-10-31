package com.luciofm.droidcon.ifican.fragment;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luciofm.droidcon.ifican.R;
import com.luciofm.droidcon.ifican.anim.TextTransform;
import com.luciofm.droidcon.ifican.util.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class SmoothStateChangeFragment extends BaseFragment {

    @InjectView(R.id.container)
    LinearLayout container;
    @InjectView(R.id.container2)
    LinearLayout container2;
    @InjectView(R.id.text1)
    TextView text1;

    @InjectView(R.id.gif1)
    GifImageView gif1;
    @InjectView(R.id.gif2)
    GifImageView gif2;

    int grey_light;
    int pink;

    public SmoothStateChangeFragment() {
    }


    @Override
    public int getLayout() {
        return R.layout.fragment_smooth_state_change;
    }

    @Override
    public String getMessage() {
        return "Never hide/show/mode/change state of elements without animation...";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup parent, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        ButterKnife.inject(this, v);
        currentStep = 1;

        grey_light = getResources().getColor(R.color.grey_light);
        pink = getResources().getColor(R.color.pink);

        Utils.stopGif(gif1, gif2);
        Utils.resetGif(gif1, gif2);

        return v;
    }

    @OnClick(R.id.container)
    public void onClick() {
        onNextPressed();
    }

    @Override
    public void onNextPressed() {
        switch (++currentStep) {
            case 2:
                TransitionSet set = new TransitionSet();
                set.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
                set.addTransition(new TextTransform()).addTransition(new Fade(Fade.IN));
                TransitionManager.beginDelayedTransition(container, set);

                container.setGravity(Gravity.TOP);

                text1.setTextSize(60f);
                text1.setTextColor(grey_light);

                ObjectAnimator background = ObjectAnimator.ofObject(container, "backgroundColor",
                        new ArgbEvaluator(), grey_light, pink);
                background.start();
                break;
            case 3:
                container2.setVisibility(View.VISIBLE);
                gif1.setVisibility(View.VISIBLE);
                Utils.startGifDelayed(gif1);
                break;
            case 4:
                gif2.setVisibility(View.VISIBLE);
                Utils.stopGif(gif1);
                Utils.startGifDelayed(gif2);
                break;
            case 5:
                Utils.startGif(gif1);
                break;
            default:
                super.onNextPressed();
        }
    }

    @Override
    public void onPrevPressed() {
        if (--currentStep > 0) {
            switch (currentStep) {
                case 1:
                    super.onPrevPressed();
                case 2:
                    Utils.stopGif(gif1);
                    gif1.setVisibility(View.GONE);
                    container2.setVisibility(View.GONE);
                    break;
                case 3:
                    Utils.stopGif(gif2);
                    Utils.startGifDelayed(gif1);
                    gif2.setVisibility(View.GONE);
            }
            return;
        }
        super.onPrevPressed();
    }

    @OnClick({R.id.gif1, R.id.gif2})
    public void onGifClick(GifImageView view) {
        GifDrawable drawable = (GifDrawable) view.getDrawable();
        if (drawable.isPlaying())
            drawable.stop();
        else
            drawable.start();
    }
}
