package com.luciofm.droidcon.ifican.fragment;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import com.luciofm.droidcon.ifican.R;
import com.luciofm.droidcon.ifican.anim.AlphaSpan;
import com.luciofm.droidcon.ifican.anim.LayerEnablingAnimatorListener;
import com.luciofm.droidcon.ifican.anim.SimpleAnimatorListener;
import com.luciofm.droidcon.ifican.anim.TextSizeSpan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A simple {@link android.app.Fragment} subclass.
 *
 */
public class QuestionsFragment extends BaseFragment {

    @InjectView(R.id.container)
    ViewGroup container;
    @InjectView(R.id.container2)
    ViewGroup container2;
    @InjectView(R.id.text1)
    TextView text1;
    @InjectView(R.id.text2)
    TextView text2;

    AnimatorSet fireworks;

    int pink;
    int grey_light;

    private SpannableString title = new SpannableString("QUESTIONS??");

    public QuestionsFragment() {
    }


    @Override
    public int getLayout() {
        return R.layout.fragment_questions;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, v);

        grey_light = getResources().getColor(R.color.grey_light);
        pink = getResources().getColor(R.color.pink);

        container2.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateTitle();
            }
        }, 800);

        return v;
    }

    @Override
    public void onNextPressed() {
        switch (++currentStep) {
            case 2:
                if (fireworks != null)
                    fireworks.cancel();
                animateOut();
                break;
            default:
                super.onNextPressed();
        }
    }

    @OnClick(R.id.container)
    public void onClick() {
        onNextPressed();
    }

    private void animateTitle() {
        buildFireworksAnimation(0, title.length() - 1);
    }

    private static final int FIREWORK_ANIM_DURATION = 1200;
    private static final int FIREWORK_ANIM_DELAY = 120;
    private int currentDelay = 0;

    private void buildFireworksAnimation(int start, int end) {

        float textSize = text1.getTextSize();

        ArrayList<TextSizeSpan> tmp = new ArrayList<>();
        for(int index = start ; index <= end ; index++) {
            TextSizeSpan span = new TextSizeSpan(0);
            tmp.add(span);
            title.setSpan(span, index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        ArrayList<TextSizeSpan> spans = new ArrayList<>();
        int middle = end / 2;
        spans.add(tmp.get(middle));
        for (int i = 1; i <= middle; i++) {
            spans.add(tmp.get(middle - i));
            spans.add(tmp.get(middle + i));
        }

        currentDelay = 0;
        List<Animator> animators = new ArrayList<>();
        for (TextSizeSpan span : spans) {
            ObjectAnimator anim = ObjectAnimator.ofInt(span, SIZE_SPAN_PROPERTY, 0, (int) textSize);
            anim.setInterpolator(new BounceInterpolator());
            anim.setDuration(FIREWORK_ANIM_DURATION);
            anim.setStartDelay(currentDelay);
            animators.add(anim);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (isResumed())
                        text1.setText(title);
                }
            });
            currentDelay += FIREWORK_ANIM_DELAY;
        }

        fireworks = new AnimatorSet();
        fireworks.playTogether(animators);
        fireworks.start();
    }

    private static final Property<TextSizeSpan, Integer> SIZE_SPAN_PROPERTY =
            new Property<TextSizeSpan, Integer>(Integer.class, "SIZE_SPAN_PROPERTY") {
                @Override
                public void set(TextSizeSpan span, Integer value) {
                    span.setSize(value);
                }

                @Override
                public Integer get(TextSizeSpan span) {
                    return span.getSize();
                }
            };

    private static final Property<AlphaSpan, Integer> ALPHA_SPAN_PROPERTY =
            new Property<AlphaSpan, Integer>(Integer.class, "ALPHA_SPAN_PROPERTY") {

                @Override
                public void set(AlphaSpan span, Integer value) {
                    span.setAlpha(value);
                }

                @Override
                public Integer get(AlphaSpan span) {
                    return span.getAlpha();
                }
            };

    private void animateOut() {
        text1.setVisibility(View.GONE);
        container2.setVisibility(View.VISIBLE);

        container2.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                container2.getViewTreeObserver().removeOnPreDrawListener(this);
                ViewPropertyAnimator animator = animateViewsOut();
                animator.setListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        container2.setVisibility(View.GONE);
                    }
                });
                animator.start();

                text2.setVisibility(View.VISIBLE);
                text2.setAlpha(0f);
                text2.animate().alpha(1f).setDuration(300).setStartDelay(600).start();

                ObjectAnimator background = ObjectAnimator.ofObject(container, "backgroundColor",
                                                                    new ArgbEvaluator(), grey_light,
                                                                    pink);
                background.setDuration(300);
                background.setStartDelay(600);
                background.start();

                return false;
            }
        });
    }

    public static final int ANIM_DELAY = 30;
    public static final int MULTIPLIER = 1;
    public static final int ANIM_DURATION = 900;
    AccelerateInterpolator accelerate = new AccelerateInterpolator();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public ViewPropertyAnimator animateViewsOut() {
        ArrayList<View> views = new ArrayList<>();
        ViewPropertyAnimator anim = null;
        for (int i = 0; i < container2.getChildCount(); i++) {
            View v = container2.getChildAt(i);
            views.add(v);
        }

        Collections.shuffle(views);

        for (int i = 0; i < views.size(); i++) {
            View v = views.get(i);
            v.setPivotY(v.getHeight() / 2);
            v.setPivotX(v.getWidth() / 2);
            anim = v.animate();
            anim.alpha(0f).scaleY(12f).scaleX(12f).setInterpolator(accelerate)
                    .setStartDelay(i * ANIM_DELAY * MULTIPLIER)
                    .setDuration((ANIM_DURATION - (i * ANIM_DELAY)) * MULTIPLIER)
                    .setListener(new LayerEnablingAnimatorListener(v));

            container.getOverlay().add(v);
        }
        return anim;
    }
}
