package com.luciofm.droidcon.ifican.fragment;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luciofm.droidcon.ifican.R;
import com.luciofm.droidcon.ifican.activity.MainActivity;
import com.luciofm.droidcon.ifican.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class IfICanFragment extends BaseFragment {

    @InjectView(R.id.text2)
    TextView text2;

    int currentStep;

    public IfICanFragment() {
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_if_ican;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, v);
        currentStep = 1;
        return v;
    }

    @Override
    public void onNextPressed() {
        // Simple animateLayoutChanges transition...
        if (++currentStep == 2)
            text2.setVisibility(View.VISIBLE);
        else {
            ((MainActivity) getActivity()).nextFragment();
        }
    }

    @OnClick(R.id.container)
    public void onClick() {
        onNextPressed();
    }
}
