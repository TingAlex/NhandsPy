package com.tingalex.picsdemo.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tingalex.picsdemo.Activity.DetailsActivity;
import com.tingalex.picsdemo.Adapter.GoodsInMainAdapter;
import com.tingalex.picsdemo.R;
import com.tingalex.picsdemo.db.Good;
import com.tingalex.picsdemo.db.Users;
import com.tingalex.picsdemo.global.MyApplication;

import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by oureda on 2018/4/30.
 */

public class OnshowFragment extends Fragment {

    //RecyclerView Part
    private MyApplication myApplication;
    private String userId;
    private GoodsInMainAdapter adapter;
    private RecyclerView recyclerView;
    private List<Good> goodList;
    private Context context;

    //SwipeRefresh Part
    private SwipeRefreshLayout swipeRefreshLayout;

    //Swipe to refresh the goods recycler view
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            updateGoods();
        }
    };

    //Bind Click Action to every cell in recycler view. Will enter the Details Activity.
    private GoodsInMainAdapter.onItemClickListener clickListener = new GoodsInMainAdapter.onItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
//            final GoodsInMainAdapter.ViewHolder holder = new GoodsInMainAdapter.ViewHolder(view);
            Good good = goodList.get(position);
            Intent intent = new Intent(getActivity(), DetailsActivity.class);
            intent.putExtra("bmobId", good.getObjectId());
            startActivity(intent);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onshow, container, false);
        context = getActivity();
        myApplication = (MyApplication) getActivity().getApplication();
        userId = myApplication.getBmobId();

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        recyclerView = view.findViewById(R.id.goodsView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        goodList = new LinkedList<>();
        adapter = new GoodsInMainAdapter(context, goodList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(clickListener);
        updateGoods();

        return view;
    }

    private void updateGoods() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Users user = new Users();
                user.setObjectId(userId);
                BmobQuery<Good> bmobQuery = new BmobQuery("Good");
//                bmobQuery.addQueryKeys("title,picurls");
                bmobQuery.include("belongs");
                bmobQuery.addWhereEqualTo("tradeState","onSell");
                bmobQuery.addWhereEqualTo("belongs", user);
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
}
