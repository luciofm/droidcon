package com.luciofm.droidcon.ifican.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luciofm.droidcon.ifican.R;
import com.luciofm.droidcon.ifican.util.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuideUserFragment extends BaseFragment {

    @InjectView(R.id.container)
    ViewGroup container;
    @InjectView(R.id.container2)
    ViewGroup container2;

    @InjectView(R.id.text1)
    TextView text1;

    @InjectView(R.id.gif1)
    GifImageView gif1;

    public GuideUserFragment() {
    }


    @Override
    public int getLayout() {
        return R.layout.fragment_guide_user;
    }

    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        ButterKnife.inject(this, v);
        currentStep = 1;

        return v;
    }

    @Override
    public void onNextPressed() {
        switch (++currentStep) {
            case 2:
                text1.animate().scaleY(0.7f).scaleX(0.7f);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) text1.getLayoutParams();
                params.bottomMargin = Utils.dpToPx(getActivity(), 20) * -1;
                text1.setLayoutParams(params);

                container2.setVisibility(View.VISIBLE);
                Utils.startGifDelayed(gif1, 600);
                break;
            default:
                super.onNextPressed();
        }
    }

    @OnClick(R.id.container)
    public void onClick() {
        onNextPressed();
    }
}
