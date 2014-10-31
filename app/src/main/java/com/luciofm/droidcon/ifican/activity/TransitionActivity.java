package com.luciofm.droidcon.ifican.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.graphics.ColorMatrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.luciofm.droidcon.ifican.IfICan;
import com.luciofm.droidcon.ifican.R;
import com.luciofm.droidcon.ifican.anim.LayerEnablingAnimatorListener;
import com.luciofm.droidcon.ifican.anim.SimpleAnimatorListener;
import com.luciofm.droidcon.ifican.model.Dog;
import com.luciofm.droidcon.ifican.model.ViewInfo;
import com.luciofm.droidcon.ifican.util.ActivityFinishEvent;
import com.luciofm.droidcon.ifican.util.IOUtils;
import com.luciofm.droidcon.ifican.util.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by luciofm on 5/25/14.
 */
public class TransitionActivity extends BaseActivity {

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final TimeInterpolator interpolator = new AnticipateOvershootInterpolator();
    private static final int ANIM_DURATION = 800;

    @InjectView(R.id.toplevel)
    ViewGroup topLevel;
    @InjectView(R.id.thumb)
    ImageView image;
    @InjectView(R.id.text1)
    TextView text1;
    @InjectView(R.id.text2)
    TextView text2;
    @InjectView(R.id.text3)
    TextView text3;
    @InjectView(R.id.text4)
    TextView text4;

    Dog dog;
    ViewInfo info;

    int currentStep = 1;

    private BitmapDrawable bitmapDrawable;
    private ColorMatrix colorizerMatrix = new ColorMatrix();
    ColorDrawable background;
    int leftDelta;
    int topDelta;
    float widthScale;
    float heightScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        ButterKnife.inject(this);

        Bundle bundle = getIntent().getExtras();
        info = bundle.getParcelable("INFO");
        dog = bundle.getParcelable("DOG");

        background = new ColorDrawable(getResources().getColor(R.color.pink));
        background.setAlpha(0);
        topLevel.setBackgroundDrawable(background);
        image.setImageResource(dog.getResource());

        text1.setText(Html.fromHtml(IOUtils.readFile(this, "source/itemclick.java.html")));
        text2.setText(Html.fromHtml(IOUtils.readFile(this, "source/predraw.java.html")));
        text3.setText(Html.fromHtml(IOUtils.readFile(this, "source/init.java.html")));
        text4.setText(Html.fromHtml(IOUtils.readFile(this, "source/anim1.java.html")));

        if (savedInstanceState == null){
            image.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    image.getViewTreeObserver().removeOnPreDrawListener(this);

                    int[] screenLocation = new int[2];
                    image.getLocationOnScreen(screenLocation);
                    leftDelta = info.left - screenLocation[0];
                    topDelta = info.top - screenLocation[1];

                    // Scale factors to make the large version the same size as the thumbnail
                    widthScale = (float) info.width / image.getWidth();
                    heightScale = (float) info.height / image.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }

    private void runEnterAnimation() {
        final long duration = Utils.calcDuration(info.position);

        Log.d("IfICan", "Duration: " + duration);

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        image.setPivotX(0);
        image.setPivotY(0);
        image.setScaleX(widthScale);
        image.setScaleY(heightScale);
        image.setTranslationX(leftDelta);
        image.setTranslationY(topDelta);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                image.animate().setDuration(duration)
                        .scaleX(1).scaleY(1)
                        .translationX(0).translationY(0)
                        .setInterpolator(interpolator)
                        .setListener(new LayerEnablingAnimatorListener(image) {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                text1.setVisibility(View.VISIBLE);
                                text1.setAlpha(0);
                                text1.setTranslationY(text1.getHeight());
                                text1.animate().setDuration(duration / 2)
                                        .translationY(0).alpha(1)
                                        .setInterpolator(sDecelerator);
                                super.onAnimationEnd(animation);
                            }
                        });
                ObjectAnimator bgAnim = ObjectAnimator.ofInt(background, "alpha", 0, 255);
                bgAnim.setDuration(duration * 2);
                bgAnim.start();
            }
        }, 100);
    }

    public void onNextPressed() {
        switch (++currentStep) {
            case 2:
                onText1Click();
                break;
            case 3:
                onText2Click();
                break;
            case 4:
                onText3Click();
                break;
            case 5:
                onText4Click();
                break;
        }
    }

    @OnClick(R.id.text1)
    public void onText1Click() {
        final long duration = (long) (ANIM_DURATION);

        animateText(text1, text2);
    }

    @OnClick(R.id.text2)
    public void onText2Click() {
        final long duration = (long) (ANIM_DURATION);

        animateText(text2, text3);
    }

    @OnClick(R.id.text3)
    public void onText3Click() {
        animateText(text3, text4);
    }


    private void animateText(final View oldView, final View newView) {
        final long duration = (long) (ANIM_DURATION);

        newView.setAlpha(0);
        newView.setVisibility(View.VISIBLE);

        oldView.animate().translationY(oldView.getHeight())
                .setDuration(duration / 2).alpha(0)
                .setInterpolator(sDecelerator)
                .setListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        newView.setTranslationY(newView.getHeight());
                        newView.animate().setDuration(duration / 2)
                                .translationY(0).alpha(1)
                                .setInterpolator(sDecelerator);
                        oldView.setVisibility(View.GONE);
                    }
                });
    }

    @OnClick(R.id.text4)
    public void onText4Click() {
        setResult(RESULT_OK);
        animateOut(text4);
    }

    public void animateOut(View text) {
        final long duration = Utils.calcDuration(info.position);

        // First, slide/fade text out of the way
        text.animate().translationY(text.getHeight())
                .alpha(0).setDuration(ANIM_DURATION / 2)
                .setInterpolator(sAccelerator)
                .setListener(new SimpleAnimatorListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        image.animate().setDuration(duration)
                                .scaleX(widthScale)
                                .scaleY(heightScale)
                                .translationX(leftDelta)
                                .translationY(topDelta)
                                .setInterpolator(interpolator)
                                .setListener(new LayerEnablingAnimatorListener(image) {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        finish();
                                    }
                                });
                        // Fade out background
                        ObjectAnimator bgAnim = ObjectAnimator.ofInt(background, "alpha", 0);
                        bgAnim.setDuration(duration);
                        bgAnim.start();
                        IfICan.getBusInstance().post(new ActivityFinishEvent());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (text1.getVisibility() == View.VISIBLE)
            animateOut(text1);
        else if (text2.getVisibility() == View.VISIBLE)
            animateOut(text2);
        else if (text3.getVisibility() == View.VISIBLE)
            animateOut(text3);
        else
            animateOut(text4);
    }

    @Override
    public void finish() {
        super.finish();

        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("IFICAN", "onKeyDown: " + keyCode + " - event: " + event);
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case IfICan.BUTTON_NEXT:
            case 28:
            case 229:
            case 0x74:
                onNextPressed();
                return true;
            case IfICan.BUTTON_PREV:
            case 0x79:
            case 57:
                onBackPressed();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
