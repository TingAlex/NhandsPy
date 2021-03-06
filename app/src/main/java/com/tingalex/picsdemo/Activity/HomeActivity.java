package com.tingalex.picsdemo.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tingalex.picsdemo.Fragment.BroughtFragment;
import com.tingalex.picsdemo.Fragment.ChargeFragment;
import com.tingalex.picsdemo.Fragment.ExploreFragment;
import com.tingalex.picsdemo.Fragment.LikeFragment;
import com.tingalex.picsdemo.Fragment.OnshowFragment;
import com.tingalex.picsdemo.Fragment.PersonFragment;
import com.tingalex.picsdemo.R;
import com.tingalex.picsdemo.Fragment.SoldFragment;
import com.tingalex.picsdemo.db.Users;
import com.tingalex.picsdemo.global.MyApplication;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    private IntentFilter intentFilter;
    private UserChargeReceiver chargeReceiver;
    private LocalBroadcastManager localBroadcastManager;
    //Message alert to render UI in handler
    public static final int UPDATE_USER = 1;
    private Context context;
    protected MyApplication myApplication;
    private Users user;
    //from SharedPreference
    //Toolbar to replace ActionBar.
    private Toolbar toolbar;
    //Drawer Part
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headerLayout;
    private CircleImageView headpicView;
    private TextView userEmailView;
    private TextView userNameView;
    private TextView userCreditView;

    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.nav_explore:
                    replaceFragment(new ExploreFragment());
                    break;
                case R.id.nav_charge:
                    replaceFragment(new ChargeFragment());
                    break;
                case R.id.nav_onshow:
                    replaceFragment(new OnshowFragment());
                    break;
                case R.id.nav_sold:
                    replaceFragment(new SoldFragment());
                    break;
                case R.id.nav_brought:
                    replaceFragment(new BroughtFragment());
                    break;
                case R.id.nav_like:
                    replaceFragment(new LikeFragment());
                    break;
                case R.id.nav_person:
                    replaceFragment(new PersonFragment());
                    break;
                //Drawer中的Navigation中的"我的跑路"部分
                case R.id.nav_quit:
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    myApplication.clearAllInfo();
                    startActivity(intent);
                    finish();
                    break;
            }
            drawerLayout.closeDrawers();
            return true;
        }
    };

    //After get user info, render UI
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_USER:
                    Log.i("bmob", "handleMessage: recievie " + user.getName());
                    userNameView.setText(user.getName());
                    myApplication.setName(user.getName());
                    userEmailView.setText(user.getEmail());
                    myApplication.setEmail(user.getEmail());
                    if (user.getCredit() != null) {
                        userCreditView.setText("余额：" + user.getCredit().toString());
                        myApplication.setCredit(user.getCredit());
                    }
                    Log.i("bmob", "handleMessage: user headpic " + user.getHeadpic());
                    if (user.getHeadpic() != null && !user.getHeadpic().equals("")) {
                        Glide.with(context).load(user.getHeadpic()).into(headpicView);
                        myApplication.setHeadpic(user.getHeadpic());
                    }
            }
        }
    };

    public class UserChargeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(final Context context, Intent intent) {
            Toast.makeText(context, "Toast from user charge receiver", Toast.LENGTH_SHORT).show();
            Log.i("bmob", "onReceive: receive broadcast");
            getUserInfo(myApplication.getBmobId());
            // Post the UI updating code to our Handler
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(context, "Toast from broadcast receiver", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "195f864122ce10a6d3197a984d4c6370");
        setContentView(R.layout.activity_home);
        myApplication = (MyApplication) getApplication();

        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        chargeReceiver=new UserChargeReceiver();
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.tingalex.picsdemo.LOCAL_BROADCAST");
        localBroadcastManager.registerReceiver(chargeReceiver,intentFilter);

        context = getApplicationContext();
        toolbar = findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        //Set default chosen item."探索发现"
        navigationView.setCheckedItem(R.id.nav_explore);
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);

        headerLayout = navigationView.getHeaderView(0);
        userEmailView = headerLayout.findViewById(R.id.nav_email);
        userNameView = headerLayout.findViewById(R.id.nav_name);
        headpicView = headerLayout.findViewById(R.id.icon_head);
        userCreditView = headerLayout.findViewById(R.id.nav_credit);

        String userUid = myApplication.getBmobId();
        Log.i("bmob", "onCreate: get user bmobId : " + userUid);
        getUserInfo(userUid);
        replaceFragment(new ExploreFragment());



    }

    public void getUserInfo(final String uid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BmobQuery<Users> bmobQuery = new BmobQuery("Users");
                bmobQuery.getObject(uid, new QueryListener<Users>() {
                    @Override
                    public void done(Users users, BmobException e) {
                        if (e == null) {
                            user = users;
                            Message message = new Message();
                            message.what = UPDATE_USER;
                            handler.sendMessage(message);
                        } else {
                            Toast.makeText(context, "Network connection failed, try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(chargeReceiver);
    }
    @Override
    //Action after you click the Hamburger icon on the top left of toolbar: Open the Drawer
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //这里的android前缀绝对不能丢！！
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        Log.i("bmob", "replaceFragment: Home activity");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.change_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
