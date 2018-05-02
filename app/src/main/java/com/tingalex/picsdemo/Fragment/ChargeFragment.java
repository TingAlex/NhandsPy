package com.tingalex.picsdemo.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tingalex.picsdemo.R;
import com.tingalex.picsdemo.db.Users;
import com.tingalex.picsdemo.global.MyApplication;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by oureda on 2018/4/30.
 */

public class ChargeFragment extends Fragment {
    private MyApplication myApplication;
    //Message alert to render UI in handler
    public static final int UPDATE_USER = 1;
    private TextView creditView;
    private EditText needCredit;
    private Double credit;
    private Button pushCreditButton;
    private Button pullCreditButton;

    //After get user info, render UI
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_USER:
                    Log.i("bmob", "handleMessage: update to credit: " + credit);
                    creditView.setText("余额：" + credit.toString());
                    myApplication.setCredit(credit);
                    TextView navCreditView = getActivity().findViewById(R.id.nav_credit);
                    navCreditView.setText("余额：" + credit.toString());
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charge, container, false);
        Log.i("bmob", "onCreateView: get into ChargeFragment");
        myApplication = (MyApplication) getActivity().getApplication();
        Bmob.initialize(getActivity(), "195f864122ce10a6d3197a984d4c6370");

        creditView = view.findViewById(R.id.credit);
        needCredit = view.findViewById(R.id.needCredit);
        pushCreditButton = view.findViewById(R.id.pushCredit);
        pullCreditButton = view.findViewById(R.id.pullCredit);

        credit = myApplication.getCredit();
        creditView.setText("余额：" + credit.toString());

        pushCreditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double tempNeedCredit = Double.valueOf(needCredit.getText().toString());
                pushCredit(tempNeedCredit);
            }
        });
        pullCreditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double tempNeedCredit = Double.valueOf(needCredit.getText().toString());
                pullCredit(tempNeedCredit);
            }
        });


        return view;
    }

    private void pushCredit(Double needCredit) {
        Log.i("Bmob", "pushCredit: get needCredit: " + needCredit);
        credit = credit + needCredit;
        updateCredit();
    }

    private void pullCredit(Double needCredit) {
        Log.i("Bmob", "pullCredit: get needCredit: " + needCredit);
        if (credit - needCredit >= 0.0) {
            credit=credit-needCredit;
            updateCredit();
        } else {
            Toast.makeText(getActivity(), "你哪有那么多钱！", Toast.LENGTH_LONG).show();
        }

    }

    private void updateCredit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("bmob", "run: get upadteCredit run.");
                String bmobId = myApplication.getBmobId();
                Users user = new Users();
                user.setCredit(credit);
                user.update(bmobId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(getActivity(), "操作成功！", Toast.LENGTH_LONG).show();
                            Message message = new Message();
                            message.what = UPDATE_USER;
                            handler.sendMessage(message);
                        } else {
                            Toast.makeText(getActivity(), "操作失败！", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        }).start();
    }
}
