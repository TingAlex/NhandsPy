package com.tingalex.picsdemo.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tingalex.picsdemo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsBottomBuyerFragment extends Fragment {

    private Button likeButton;
    private Button buyButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_bottom_buyer, container, false);

        likeButton=view.findViewById(R.id.likeButton);
        buyButton=view.findViewById(R.id.buyButton);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goodId=getArguments().getString("bmobId");
                Log.i("bmob", "onClick: like button get good bombId: "+goodId);
            }
        });
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goodId=getArguments().getString("bmobId");
                Log.i("bmob", "onClick: buy button get good bombId: "+goodId);
            }
        });
        return view;
    }

}
