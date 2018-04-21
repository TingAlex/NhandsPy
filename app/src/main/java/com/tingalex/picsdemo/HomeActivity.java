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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tingalex.picsdemo.db.Good;
import com.tingalex.picsdemo.db.Users;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    public static final int UPDATE_USER = 1;
    private Users user;
    private String uid;
    private DrawerLayout drawerLayout;
    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            drawerLayout.closeDrawers();
            return true;
        }
    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(HomeActivity.this, ShareActivity.class);
            startActivity(intent);
        }
    };
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_USER:
                    Log.i("bmob", "handleMessage: recievie "+user.getName());
                    userNameView.setText(user.getName());
                    userEmailView.setText(user.getEmail());
                    if (user.getHeadpic() != null && !user.getHeadpic().equals("")) {
                        Glide.with(context).load(user.getHeadpic()).into(headpicView);
                    }
            }
        }
    };

    private GoodsInMainAdapter adapter;
    private RecyclerView recyclerView;
    private List<Good> goodList;
    private Context context;
    private TextView userEmailView;
    private TextView userNameView;
    private NavigationView navigationView;
    private View headerLayout;
    private CircleImageView headpicView;


    private GoodsInMainAdapter.onItemClickListener clickListener = new GoodsInMainAdapter.onItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
//            final GoodsInMainAdapter.ViewHolder holder = new GoodsInMainAdapter.ViewHolder(view);
            Good good = goodList.get(position);
            Intent intent = new Intent(HomeActivity.this, DetailsActivity.class);
            intent.putExtra("uid", good.getUid());
//            Toast.makeText(HomeActivity.this, "click good: " + good.getUid(), Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "195f864122ce10a6d3197a984d4c6370");
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }
        navigationView.setCheckedItem(R.id.nav_main);
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);
        FloatingActionButton floatingActionButton = findViewById(R.id.addGood);
        floatingActionButton.setOnClickListener(onClickListener);

        headerLayout=navigationView.getHeaderView(0);

        userEmailView = headerLayout.findViewById(R.id.nav_email);
        userNameView = headerLayout.findViewById(R.id.nav_name);
        headpicView = headerLayout.findViewById(R.id.icon_head);

        goodList = new List<Good>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<Good> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] ts) {
                return null;
            }

            @Override
            public boolean add(Good good) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends Good> collection) {
                return false;
            }

            @Override
            public boolean addAll(int i, @NonNull Collection<? extends Good> collection) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public Good get(int i) {
                return null;
            }

            @Override
            public Good set(int i, Good good) {
                return null;
            }

            @Override
            public void add(int i, Good good) {

            }

            @Override
            public Good remove(int i) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @NonNull
            @Override
            public ListIterator<Good> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<Good> listIterator(int i) {
                return null;
            }

            @NonNull
            @Override
            public List<Good> subList(int i, int i1) {
                return null;
            }
        };
        context = this.getApplicationContext();
        adapter = new GoodsInMainAdapter(context, goodList);
        recyclerView = findViewById(R.id.goodsView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        uid = preferences.getString("uid", "");
        getUserInfo();
        update();

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
                            Log.i("bmob", "done:homepage "+user.getName());
                            Log.i("bmob", "done:homepage "+user.getEmail());
                            Message message = new Message();
                            message.what = UPDATE_USER;
                            handler.sendMessage(message);
                        }
                    }
                });
            }
        }).start();

    }

    private void update() {
        BmobQuery<Good> bmobQuery = new BmobQuery("Good");
        bmobQuery.addQueryKeys("uid,title,picurls");
        bmobQuery.order("-updatedAt");
        bmobQuery.findObjects(new FindListener<Good>() {
            @Override
            public void done(List<Good> objects, BmobException e) {
                if (objects != null) {
                    goodList = objects;
                    adapter = new GoodsInMainAdapter(context, goodList);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(clickListener);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        update();
//        getUserInfo();
    }

    @Override
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
