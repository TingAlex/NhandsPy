package com.tingalex.picsdemo.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tingalex.picsdemo.R;

/**
 * Created by oureda on 2018/4/30.
 */

public class BroughtFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brought, container, false);
        Log.i("bmob", "onCreateView: get into BroughtFragment");
        return view;
    }
}
