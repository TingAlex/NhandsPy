package com.tingalex.picsdemo.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tingalex.picsdemo.R;
import com.tingalex.picsdemo.db.Good;
import com.tingalex.picsdemo.db.Users;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

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

        likeButton = view.findViewById(R.id.likeButton);
        buyButton = view.findViewById(R.id.buyButton);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goodId = getArguments().getString("bmobId");
                String userId = getArguments().getString("userBmobId");
                Log.i("bmob", "onClick: like button get good bombId: " + goodId);
                Users user = new Users();
                user.setObjectId(userId);
                BmobRelation relation = new BmobRelation();
                relation.add(user);
                Good good = new Good();
                good.setObjectId(goodId);
                good.setLikes(relation);
                good.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Log.i("bmob", "onClick: add to like collection ");
                            Toast.makeText(getActivity(), "已添加到我的喜欢！", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("bmob", "onClick: network issue ");
                            Toast.makeText(getActivity(), "network connection issue", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String goodId = getArguments().getString("bmobId");
                final Double userCredit = getArguments().getDouble("userCredit");
                final String userId = getArguments().getString("userBmobId");
                final Double goodCharge = getArguments().getDouble("goodCharge");
                final String sellerId = getArguments().getString("sellerId");
                if (userCredit < goodCharge) {
                    Log.i("bmob", "onClick: money not enough");
                    Toast.makeText(getActivity(), "钱不够啊童鞋！", Toast.LENGTH_SHORT).show();
                } else {
                    BmobQuery<Good> bmobQuery = new BmobQuery("Good");
                    bmobQuery.include("belongs");
                    bmobQuery.getObject(goodId, new QueryListener<Good>() {
                        @Override
                        public void done(Good goods, BmobException e) {
                            if (e == null) {
                                if (goods.getTradeState().equals("onSell")) {
                                    Good good = new Good();
                                    final Users buyer = new Users();
                                    buyer.setObjectId(userId);
                                    good.setSells(buyer);
                                    good.setTradeState("Paid");
                                    buyer.setCredit(userCredit - goodCharge);

                                    buyer.update(userId, new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Log.i("bmob", "paid money!");
                                            } else {
                                                Log.i("bmob", "onClick: network issue ");
                                                Toast.makeText(getActivity(), "network connection issue", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    final Users seller = new Users();
                                    seller.setObjectId(sellerId);
                                    BmobQuery<Users> query = new BmobQuery<>();
                                    query.getObject(sellerId, new QueryListener<Users>() {
                                        @Override
                                        public void done(Users users, BmobException e) {
                                            if (e == null) {
                                                final Double sellerCredit = users.getCredit() + goodCharge;
                                                seller.setCredit(sellerCredit);
                                                seller.update(sellerId, new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                        if (e == null) {
                                                            Log.i("bmob", "recieved money!");
                                                        } else {
                                                            Log.i("bmob", "onClick: network issue ");
                                                            Toast.makeText(getActivity(), "network connection issue", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.i("bmob", "onClick: network issue ");
                                                Toast.makeText(getActivity(), "network connection issue", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    good.update(goodId, new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Log.i("bmob", "brought!");
                                                Toast.makeText(getActivity(), "买到啦！", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.i("bmob", "onClick: network issue ");
                                                Toast.makeText(getActivity(), "network connection issue", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), "对不起，刚已经卖掉了", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Network connection failed, try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                Log.i("bmob", "onClick: buy button get good bombId: " + goodId);

            }
        });
        return view;
    }

}
