package com.tingalex.picsdemo.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tingalex.picsdemo.R;
import com.tingalex.picsdemo.db.Good;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class DetailsActivity extends AppCompatActivity {
    public static final int UPDATE = 1;
    private Context context;
    private Good good;
    private TextView title, belongto, description, packageCost, price, category;
    private ImageView pictureFromWeb;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    title.setText(title.getText().toString() + good.getTitle());
                    belongto.setText(belongto.getText().toString() + good.getBelongto());
                    description.setText(description.getText().toString() + good.getDescription());
                    if (good.getContainPackageCost() == null || good.getContainPackageCost() == true) {
                        packageCost.setText(packageCost.getText().toString() + "是");
                    } else {
                        packageCost.setText(packageCost.getText().toString() + "否");
                    }
                    price.setText(price.getText().toString() + good.getPrice());
                    category.setText(category.getText().toString() + good.getCategroy());
                    Glide.with(context).load(good.getPicurls().get(0)).into(pictureFromWeb);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        context = getApplicationContext();

        title = findViewById(R.id.titleView);
        belongto = findViewById(R.id.belongtoView);
        description = findViewById(R.id.descriptionView);
        packageCost = findViewById(R.id.packageCostView);
        price = findViewById(R.id.priceView);
        category = findViewById(R.id.categoryView);
        pictureFromWeb = findViewById(R.id.pictureFromWeb);

        Intent intent = getIntent();
        String goodUid = intent.getStringExtra("uid");

        getDetails(goodUid);

    }

    private void getDetails(final String uid) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                BmobQuery<Good> bmobQuery = new BmobQuery("Good");
                bmobQuery.addWhereEqualTo("uid", uid);
                bmobQuery.findObjects(new FindListener<Good>() {
                    @Override
                    public void done(List<Good> objects, BmobException e) {
                        if (objects != null) {
                            good = objects.get(0);
                            Log.i("bmob", "done: get good details of " + good.getUid());
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
}
