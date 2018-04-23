package com.tingalex.picsdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tingalex.picsdemo.db.Good;
import com.tingalex.picsdemo.db.Users;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    //Message alert to render UI in handler
    public static final int UPDATE_USER = 1;
    private Users user;
    //from SharedPreference
    private String uid;
    //Toolbar to replace ActionBar.
    private Toolbar toolbar;
    //FAB
    private FloatingActionButton floatingActionButton;
    //RecyclerView Part
    private GoodsInMainAdapter adapter;
    private RecyclerView recyclerView;
    private List<Good> goodList;
    private Context context;
    //SwipeRefresh Part
    private SwipeRefreshLayout swipeRefreshLayout;
    //Drawer Part
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headerLayout;
    private CircleImageView headpicView;
    private TextView userEmailView;
    private TextView userNameView;
    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                //Drawer中的Navigation中的"我的跑路"部分
                case R.id.nav_quit:
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();
                    startActivity(intent);
                    finish();
                    break;
            }
            drawerLayout.closeDrawers();
            return true;
        }
    };
    //Click the Floating Action Bar to create new publish action
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(HomeActivity.this, ShareActivity.class);
            startActivity(intent);
        }
    };
    //Swipe to refresh the goods recycler view
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            updateGoods();
        }
    };
    //After get user info, render UI
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_USER:
                    Log.i("bmob", "handleMessage: recievie " + user.getName());
                    userNameView.setText(user.getName());
                    userEmailView.setText(user.getEmail());
                    Log.i("bmob", "handleMessage: user headpic " + user.getHeadpic());
                    if (user.getHeadpic() != null && !user.getHeadpic().equals("")) {
                        Glide.with(context).load(user.getHeadpic()).into(headpicView);
                    }
            }
        }
    };
    //Bind Click Action to every cell in recycler view. Will enter the Details Activity.
    private GoodsInMainAdapter.onItemClickListener clickListener = new GoodsInMainAdapter.onItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
//            final GoodsInMainAdapter.ViewHolder holder = new GoodsInMainAdapter.ViewHolder(view);
            Good good = goodList.get(position);
            Intent intent = new Intent(HomeActivity.this, DetailsActivity.class);
            intent.putExtra("uid", good.getUid());
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "195f864122ce10a6d3197a984d4c6370");
        setContentView(R.layout.activity_home);

        context = this.getApplicationContext();

        toolbar = findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }

        floatingActionButton = findViewById(R.id.addGood);
        floatingActionButton.setOnClickListener(onClickListener);

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        //Set default chosen item."探索发现"
        navigationView.setCheckedItem(R.id.nav_main);
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);

        headerLayout = navigationView.getHeaderView(0);
        userEmailView = headerLayout.findViewById(R.id.nav_email);
        userNameView = headerLayout.findViewById(R.id.nav_name);
        headpicView = headerLayout.findViewById(R.id.icon_head);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        recyclerView = findViewById(R.id.goodsView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        goodList = new LinkedList<>();
        adapter = new GoodsInMainAdapter(context, goodList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(clickListener);

        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        uid = preferences.getString("uid", "");
        Log.i("bmob", "onCreate: get user uid : " + uid);

        getUserInfo();
        updateGoods();

    }

    private void getUserInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BmobQuery<Users> bmobQuery = new BmobQuery("Users");
                bmobQuery.addWhereEqualTo("uid", uid);
                bmobQuery.findObjects(new FindListener<Users>() {
                    @Override
                    public void done(List<Users> objects, BmobException e) {
                        if (objects != null) {
                            Log.i("bmob", "done:homepage get user!");
                            user = objects.get(0);
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

    private void updateGoods() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BmobQuery<Good> bmobQuery = new BmobQuery("Good");
                bmobQuery.addQueryKeys("uid,title,picurls");
                bmobQuery.order("-updatedAt");
                bmobQuery.findObjects(new FindListener<Good>() {
                    @Override
                    public void done(List<Good> objects, BmobException e) {
                        if (objects != null) {
                            goodList.clear();
                            goodList.addAll(objects);
                            Log.i("bmob", "done: query good finish!");
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            Toast.makeText(context, "Network connection failed, try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
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
}
