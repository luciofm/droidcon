package com.luciofm.droidcon.ifican.fragment;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.transition.ChangeBounds;
import android.transition.CircularPropagation;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.luciofm.droidcon.ifican.R;
import com.luciofm.droidcon.ifican.anim.AnimUtils;
import com.luciofm.droidcon.ifican.anim.Pop;
import com.luciofm.droidcon.ifican.anim.YFractionProperty;
import com.luciofm.droidcon.ifican.util.IOUtils;
import com.luciofm.droidcon.ifican.util.Utils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

/**
 * A simple {@link android.app.Fragment} subclass.
 *
 */
public class MorphingButtonCodeFragment extends BaseFragment {

    private static final String TAG = "MorphingButtonCodeFragment";

    @InjectView(R.id.container)
    ViewGroup root;
    @InjectView(R.id.container2)
    ViewGroup container2;
    @InjectView(R.id.container3)
    ViewGroup container3;

    @InjectView(R.id.reg_container)
    View reg_container;
    @InjectView(R.id.login_container)
    View login_container;
    @InjectView(R.id.buttonReg1)
    Button buttonReg;
    @InjectView(R.id.buttonLog1)
    Button buttonLog;

    @InjectViews({R.id.editReg1, R.id.editReg2, R.id.editReg3, R.id.buttonReg1})
    List<View> register;

    @InjectViews({R.id.editLog1, R.id.editLog2, R.id.buttonLog1})
    List<View> login;

    @InjectView(R.id.text1)
    TextView text1;
    @InjectView(R.id.text2)
    TextSwitcher text2;

    Spanned code1;
    Spanned code2;

    private int currentStep;

    private boolean registerOpened = false;
    private boolean loginOpened = false;

    public MorphingButtonCodeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, v);

        code1 = Html.fromHtml(IOUtils.readFile(getActivity(), "source/morph.xml.html"));
        code2 = Html.fromHtml(IOUtils.readFile(getActivity(), "source/delayed.java.html"));
        text2.setInAnimation(getActivity(), android.R.anim.slide_in_left);
        text2.setOutAnimation(getActivity(), android.R.anim.slide_out_right);
        text2.setText(code1);

        currentStep = 1;

        return v;
    }

    @Override
    public void onNextPressed() {
        switch (++currentStep) {
            case 2:
                text1.animate().scaleX(0.6f).scaleY(0.6f);
                AnimUtils.beginDelayedTransition(root);
                container2.setVisibility(View.VISIBLE);
                break;
            case 3:
                Utils.dispatchTouch(reg_container);
                break;
            case 4:
                Utils.dispatchTouch(buttonReg);
                break;
            case 5:
                Utils.dispatchTouch(login_container);
                break;
            case 6:
                Utils.dispatchTouch(buttonLog);
                break;
            case 7:
                Utils.dispatchTouch(reg_container);
                break;
            case 8:
                AnimUtils.beginDelayedTransition(root);
                container2.setVisibility(View.GONE);
                text2.setVisibility(View.VISIBLE);
                break;
            case 9:
                text2.setText(code2);
                break;
            default:
                super.onNextPressed();
        }
    }

    @Override
    public void onPrevPressed() {
        if (--currentStep > 0) {
            if (currentStep == 3) {
                text2.setText(code1);
            } else if (currentStep > 1) {
                Log.d("IfICan", "currentStep: " + currentStep);
                AnimUtils.beginDelayedTransition(container3);
                ButterKnife.apply(register, new ButterKnife.Action<View>() {
                    @Override
                    public void apply(View view, int i) {
                        view.setVisibility(View.GONE);
                    }
                });
                ButterKnife.apply(login, new ButterKnife.Action<View>() {
                    @Override
                    public void apply(View view, int i) {
                        view.setVisibility(View.GONE);
                    }
                });
                container2.setVisibility(View.VISIBLE);
                text2.setVisibility(View.GONE);
                currentStep = 1;
            } else {
                text1.animate().scaleX(1f).scaleY(1f);
                AnimUtils.beginDelayedTransition(root);
                container2.setVisibility(View.GONE);
            }
            return;
        }
        super.onPrevPressed();
    }

    @OnClick(R.id.container)
    public void onClick() {
        onNextPressed();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @OnClick(R.id.textRegister)
    public void onRegClick(final View v) {
        if (loginOpened)
            onButtonLogClick();

        registerOpened = true;
        reg_container.setClickable(false);

        AnimUtils.beginDelayedTransition(container3);
        ButterKnife.apply(register, new ButterKnife.Action<View>() {
            @Override
            public void apply(View view, int i) {
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @OnClick(R.id.buttonReg1)
    public void onButtonRegClick() {
        registerOpened = false;
        reg_container.setClickable(true);

        AnimUtils.beginDelayedTransition(container3);
        ButterKnife.apply(register, new ButterKnife.Action<View>() {
            @Override
            public void apply(View view, int i) {
                view.setVisibility(View.GONE);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @OnClick(R.id.textLogin)
    public void onLogClick() {
        if (registerOpened)
            onButtonRegClick();
        login_container.setClickable(false);
        loginOpened = true;
        AnimUtils.beginDelayedTransition(container3);
        ButterKnife.apply(login, new ButterKnife.Action<View>() {
            @Override
            public void apply(View view, int i) {
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @OnClick(R.id.buttonLog1)
    public void onButtonLogClick() {
        loginOpened = false;
        login_container.setClickable(true);
        AnimUtils.beginDelayedTransition(container3);
        ButterKnife.apply(login, new ButterKnife.Action<View>() {
            @Override
            public void apply(View view, int i) {
                view.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_morphing_button_code;
    }

    @Override
    public String getMessage() {
        return null;
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
