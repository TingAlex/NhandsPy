package com.tingalex.picsdemo.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tingalex.picsdemo.Fragment.DetailsBottomBuyerFragment;
import com.tingalex.picsdemo.Fragment.DetailsBottomOwnerFragment;
import com.tingalex.picsdemo.R;
import com.tingalex.picsdemo.db.Good;
import com.tingalex.picsdemo.db.Users;
import com.tingalex.picsdemo.global.MyApplication;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class DetailsActivity extends AppCompatActivity {
    public static final int UPDATE = 1;
    private MyApplication myApplication;
    private Context context;
    private Good good;
    private TextView title, belongto, description, packageCost, price, category;
    private ImageView pictureFromWeb;
    private Bundle bundle;
    private Users user;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    title.setText(title.getText().toString() + good.getTitle());
                    belongto.setText(belongto.getText().toString() + user.getEmail());
                    description.setText(description.getText().toString() + good.getDescription());
                    if (good.getContainPackageCost() == null || good.getContainPackageCost() == true) {
                        packageCost.setText(packageCost.getText().toString() + "是");
                    } else {
                        packageCost.setText(packageCost.getText().toString() + "否");
                    }
                    price.setText(price.getText().toString() + good.getPrice());
                    category.setText(category.getText().toString() + good.getCategroy());
                    Glide.with(context).load(good.getPicurls().get(0)).into(pictureFromWeb);
//                    Log.i("bmob", "handleMessage:good.getBelongs().getObjectId() " + good.getBelongs().getObjectId());
//                    Log.i("bmob", "handleMessage:myApplication.getBmobId() " + myApplication.getBmobId());
                    if (good.getBelongs().getObjectId().equals(myApplication.getBmobId())) {
                        replaceFragment(new DetailsBottomOwnerFragment());
                    } else {
                        replaceFragment(new DetailsBottomBuyerFragment());
                    }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        context = getApplicationContext();
        myApplication = (MyApplication) getApplication();

        title = findViewById(R.id.titleView);
        belongto = findViewById(R.id.belongtoView);
        description = findViewById(R.id.descriptionView);
        packageCost = findViewById(R.id.packageCostView);
        price = findViewById(R.id.priceView);
        category = findViewById(R.id.categoryView);
        pictureFromWeb = findViewById(R.id.pictureFromWeb);

        Intent intent = getIntent();
        String goodUid = intent.getStringExtra("bmobId");
        bundle = new Bundle();
        bundle.putString("userBmobId", myApplication.getBmobId());
        bundle.putDouble("userCredit",myApplication.getCredit());
        getDetails(goodUid);

    }

    private void getDetails(final String uid) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                BmobQuery<Good> bmobQuery = new BmobQuery("Good");
                bmobQuery.include("belongs");
                bmobQuery.getObject(uid, new QueryListener<Good>() {
                    @Override
                    public void done(Good goods, BmobException e) {
                        if (e == null) {
                            good = goods;
                            user = good.getBelongs();
                            bundle.putString("bmobId", good.getObjectId());
                            bundle.putString("sellerId",good.getBelongs().getObjectId());
                            bundle.putDouble("goodCharge",good.getPrice());
                            Log.i("bmob", "done: get good details of " + good.getObjectId());
                            Message message = new Message();
                            message.what = UPDATE;
                            handler.sendMessage(message);
                        } else {
                            Toast.makeText(context, "Network connection failed, try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();

    }

    private void replaceFragment(Fragment fragment) {
        Log.i("bmob", "replaceFragment: details activity");
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.detailsBottom, fragment);
        transaction.commit();
    }

}
