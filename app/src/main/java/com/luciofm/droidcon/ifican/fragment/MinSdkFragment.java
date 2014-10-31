package com.luciofm.droidcon.ifican.fragment;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.luciofm.droidcon.ifican.R;
import com.luciofm.droidcon.ifican.util.IOUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MinSdkFragment extends BaseFragment {

    @InjectView(R.id.text1)
    TextView text1;

    Spanned code1;

    @Override
    public int getLayout() {
        return R.layout.fragment_minsdk;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, v);

        code1 = Html.fromHtml(IOUtils.readFile(getActivity(), "source/build.gradle.html"));
        text1.setText(code1);

        return v;
    }

    @Override
    public void onNextPressed() {
        switch (++currentStep) {
            case 2:
                getActivity().finish();
                break;
        }
    }

    @OnClick(R.id.container)
    public void onClick() {
        onNextPressed();
    }
}
