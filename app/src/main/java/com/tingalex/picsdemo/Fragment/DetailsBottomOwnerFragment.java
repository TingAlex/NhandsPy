package com.tingalex.picsdemo.Fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tingalex.picsdemo.Activity.EditActivity;
import com.tingalex.picsdemo.R;
import com.tingalex.picsdemo.db.Good;
import com.tingalex.picsdemo.db.Users;

import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsBottomOwnerFragment extends Fragment {

    private Button editButton;
    private Button deleteButton;
    private LocalBroadcastManager localBroadcastManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_bottom_owner, container, false);

        editButton = view.findViewById(R.id.editButton);
        deleteButton = view.findViewById(R.id.deleteButton);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goodId = getArguments().getString("bmobId");
                Log.i("bmob", "onClick: delete button get good bombId: " + goodId);
                Good good = new Good();
                good.setObjectId(goodId);
                good.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Log.i("bmob", "onClick: delete ");
                            Toast.makeText(getActivity(), "已成功删除！", Toast.LENGTH_SHORT).show();
                            localBroadcastManager=LocalBroadcastManager.getInstance(getActivity());
                            Intent intent=new Intent("com.example.broadcasttest.LOCAL_BROADCAST");
                            localBroadcastManager.sendBroadcast(intent);
                            getActivity().finish();
                        } else {
                            Log.i("bmob", "onClick: network issue ");
                            Toast.makeText(getActivity(), "network connection issue", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
//                Intent intent = new Intent("com.example.broadcasttest.LOCAL_BROADCAST");
//                localBroadcastManager.sendBroadcast(intent);
//                getActivity().finish();
//            }
//        });
        return view;
    }

}
