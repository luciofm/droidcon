package com.luciofm.droidcon.ifican.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.Fade;
import android.transition.MoveImage;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.luciofm.droidcon.ifican.R;
import com.luciofm.droidcon.ifican.activity.MainActivity;
import com.luciofm.droidcon.ifican.anim.AnimUtils;
import com.luciofm.droidcon.ifican.util.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyselfFragment extends BaseFragment {

    @InjectView(R.id.container)
    ViewGroup container;

    @InjectView(R.id.image)
    ImageView image;

    @InjectView(R.id.text1)
    TextView text1;
    @InjectView(R.id.text2)
    TextView text2;
    @InjectView(R.id.text3)
    TextView text3;

    Bitmap originalBitmap;

    public MyselfFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.me);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_myself;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, v);
        currentStep = 1;

        v.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isResumed())
                    return;

                image.setVisibility(View.VISIBLE);
                image.animate().alpha(1f).setDuration(300);
                ObjectAnimator pixelate = ObjectAnimator.ofInt(MyselfFragment.this, "pixelateFactor", 100, 0);
                pixelate.setDuration(1200);
                pixelate.setInterpolator(new DecelerateInterpolator());
                pixelate.start();
            }
        }, 600);
        return v;
    }

    @Override
    public void onNextPressed() {
        switch (++currentStep) {
            case 2:
                image.animate().scaleX(0.8f).scaleY(0.8f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        text1.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case 3:
                image.animate().scaleX(0.7f).scaleY(0.7f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        text2.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case 4:
                image.animate().scaleX(0.6f).scaleY(0.6f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        text3.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case 5:
                ((MainActivity) getActivity()).nextFragment();
        }
    }

    @OnClick(R.id.container)
    public void onClick() {
        onNextPressed();
    }

    final private static float PROGRESS_TO_PIXELIZATION_FACTOR = 1000.0f;

    public void setPixelateFactor(int number) {
        float factor = number / PROGRESS_TO_PIXELIZATION_FACTOR;

        PixelizeImageAsyncTask asyncPixelateTask = new PixelizeImageAsyncTask();
        asyncPixelateTask.execute(factor, originalBitmap);
    }

    /**
     * Implementation of the AsyncTask class showing how to run the
     * pixelization algorithm in the background, and retrieving the
     * pixelated image from the resulting operation.
     */
    private class PixelizeImageAsyncTask extends AsyncTask<Object, Void, BitmapDrawable> {

        @Override
        protected BitmapDrawable doInBackground(Object... params) {
            if (!isResumed())
                return null;

            float pixelizationFactor = (Float)params[0];
            Bitmap originalBitmap = (Bitmap)params[1];
            return AnimUtils.builtInPixelization(getActivity(), pixelizationFactor, originalBitmap);
        }

        @Override
        protected void onPostExecute(BitmapDrawable result) {
            if (isResumed() && result != null)
                image.setImageDrawable(result);
        }
    }
}
