package com.tingalex.picsdemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tingalex.picsdemo.db.Good;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class DetailsActivity extends AppCompatActivity {
    private TextView title, belongto, description, packageCost, price, category;
    private ImageView pictureFromWeb;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        title=findViewById(R.id.titleView);
        belongto=findViewById(R.id.belongtoView);
        description=findViewById(R.id.descriptionView);
        packageCost=findViewById(R.id.packageCostView);
        price=findViewById(R.id.priceView);
        category=findViewById(R.id.categoryView);
        pictureFromWeb=findViewById(R.id.pictureFromWeb);

        Intent intent = getIntent();
        String goodUid = intent.getStringExtra("uid");
        context = getApplicationContext();

        getDetails(context, goodUid);


    }

    private void getDetails(final Context context, String uid) {
        BmobQuery<Good> bmobQuery = new BmobQuery("Good");
        bmobQuery.addWhereEqualTo("uid", uid);
        bmobQuery.findObjects(new FindListener<Good>() {
            @Override
            public void done(List<Good> objects, BmobException e) {
                Good good = objects.get(0);
                if (good != null) {
                    title.setText(title.getText().toString() + good.getTitle());
                    belongto.setText(belongto.getText().toString() + good.getBelongto());
                    description.setText(description.getText().toString() + good.getDescription());
                    if (good.getContainPackageCost()) {
                        packageCost.setText(packageCost.getText().toString() + "是");
                    } else {
                        packageCost.setText(packageCost.getText().toString() + "否");
                    }
                    price.setText(price.getText().toString() + good.getPrice());
                    category.setText(category.getText().toString() + good.getCategroy());
                    Glide.with(context).load(good.getPicurls().get(0)).into(pictureFromWeb);
                }
            }
        });
    }
}
