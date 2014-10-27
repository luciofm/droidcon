package com.luciofm.droidcon.ifican.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luciofm.droidcon.ifican.R;

import butterknife.ButterKnife;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class SmoothTransitionsFragment extends BaseFragment {


    public SmoothTransitionsFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayout() {
        return R.layout.fragment_why;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        ButterKnife.inject(this, v);
        currentStep = 1;
        return v;
    }
}
