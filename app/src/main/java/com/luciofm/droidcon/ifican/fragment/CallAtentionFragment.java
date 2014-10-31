package com.luciofm.droidcon.ifican.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luciofm.droidcon.ifican.R;
import com.luciofm.droidcon.ifican.anim.AnimUtils;
import com.luciofm.droidcon.ifican.anim.XFractionProperty;
import com.luciofm.droidcon.ifican.util.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifImageView;

public class CallAtentionFragment extends BaseFragment {

    private static final long HEARTBEAT_ANIM_DELAY = 3000;

    @InjectView(R.id.container)
    ViewGroup container;
    @InjectView(R.id.container2)
    ViewGroup container2;
    @InjectView(R.id.container3)
    ViewGroup container3;
    @InjectView(R.id.info)
    View info;
    @InjectView(R.id.text1)
    TextView text1;

    @InjectView(R.id.gif1)
    GifImageView gif1;
    @InjectView(R.id.gif2)
    GifImageView gif2;
    @InjectView(R.id.gif3)
    GifImageView gif3;

    public CallAtentionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_call_atention;
    }

    @Override
    public String getMessage() {
        return "First, hello and thank you everyone…";
    }

    Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        ButterKnife.inject(this, v);
        currentStep = 1;

        setOutlines(info);
        AnimUtils.setupResizeTouchListener(info);

        AnimUtils.popOutViewDelayed(info, 5000);
        handler.removeCallbacks(heartbeatRunnable);
        handler.postDelayed(heartbeatRunnable, HEARTBEAT_ANIM_DELAY);

        Utils.stopGif(gif1, gif2, gif3);
        Utils.resetGif(gif1, gif2, gif3);

        return v;
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacks(heartbeatRunnable);
        super.onDestroyView();
    }

    Runnable heartbeatRunnable = new Runnable() {
        @Override
        public void run() {
            AnimUtils.animateHeartBeat(info);
            handler.postDelayed(this, HEARTBEAT_ANIM_DELAY);
        }
    };

    private void setOutlines(View v) {
        v.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int size = getResources().getDimensionPixelSize(R.dimen.floating_button_size);
                outline.setOval(0, 0, size, size);
            }
        });

        v.animate().alpha(1.0f);
    }

    @Override
    public void onNextPressed() {
        switch (++currentStep) {
            case 2:
                Utils.dispatchTouch(info, 300);
                break;
            case 3:
                container2.animate().scaleY(0.5f).scaleX(0.5f);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) container2.getLayoutParams();
                params.topMargin = Utils.dpToPx(getActivity(), 20) * -1;
                container2.setLayoutParams(params);

                container3.setVisibility(View.VISIBLE);
                Utils.startGifDelayed(gif1, 600);
                break;
            case 4:
                gif2.setVisibility(View.VISIBLE);
                Utils.stopGif(gif1);
                Utils.startGifDelayed(gif2);
                break;
            case 5:
                gif3.setVisibility(View.VISIBLE);
                Utils.stopGif(gif2);
                Utils.startGifDelayed(gif3);
                break;
            default:
                super.onNextPressed();
        }
    }

    @Override
    public void onPrevPressed() {
        if (--currentStep == 1) {
            Utils.dispatchTouch(info, 300);
            return;
        }
        super.onPrevPressed();
    }

    @OnClick(R.id.info)
    public void onInfoClick(View v) {
        toggleCodeView(v);
    }

    private void toggleCodeView(View view) {
        int cx = text1.getWidth();
        int cy = 0;
        float radius = text1.getWidth();

        if (text1.getVisibility() == View.INVISIBLE) {
            text1.setVisibility(View.VISIBLE);
            ViewAnimationUtils.createCircularReveal(text1, cx, cy, 0, radius).start();
        } else {
            Animator reveal = ViewAnimationUtils.createCircularReveal(
                    text1, cx, cy, radius, 0);
            reveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    text1.setVisibility(View.INVISIBLE);
                }
            });
            reveal.start();
        }
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
