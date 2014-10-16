package com.luciofm.droidcon.ifican.fragment;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luciofm.droidcon.ifican.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class WorkFragment extends BaseFragment {

    public WorkFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_work;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, v);


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

}
