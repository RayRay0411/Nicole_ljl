package com.app.book.ui.cart;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.book.R;
import com.app.book.model.Cart;
import com.app.book.ui.book.DetailActivity;
import com.app.book.ui.order.ConfirmActivity;
import com.app.book.util.Constant;
import com.app.book.util.GlideImageLoader;
import com.app.book.util.PreferencesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CartFragment extends Fragment {

    private ListView listView;

    private List<Cart> cartList = new ArrayList<>();

    private CartAdapter cartAdapter = new CartAdapter();

    private TextView amountTxt;

    private Button payBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        listView = root.findViewById(R.id.cart);
        listView.setAdapter(cartAdapter);

        amountTxt = root.findViewById(R.id.amount);

        payBtn = root.findViewById(R.id.pay);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ConfirmActivity.class);
                JSONArray jsonArray = new JSONArray();
                for(int i = 0; i < cartList.size(); i++){
                    if(!cartList.get(i).getChecked()){
                        continue;
                    }
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("bookId", cartList.get(i).getBookId());
                        jsonObject.put("bookName", cartList.get(i).getBookName());
                        jsonObject.put("price", cartList.get(i).getPrice());
                        jsonObject.put("picUrl", cartList.get(i).getPicUrl());
                        jsonObject.put("num", cartList.get(i).getNum());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonArray.put(jsonObject);
                }
                intent.putExtra("payBook", jsonArray.toString());
                startActivity(intent);
            }
        });

        return root;
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    cartAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    class CartAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cartList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.cart_item, null);
            final Cart cart = cartList.get(i);
            new GlideImageLoader().displayImage(getContext(), cart.getPicUrl(), (ImageView)v.findViewById(R.id.pic));
            TextView name = v.findViewById(R.id.name);
            name.setText(cart.getBookName());
            CheckBox cb = v.findViewById(R.id.cb);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    cart.setChecked(b);
                    setTotal();
                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("id", cart.getBookId());
                    getActivity().startActivity(intent);
                }
            });
            ImageView remove = v.findViewById(R.id.remove);
            final int index = i;
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("cartList", cartList.size() + "");
                    cartList.remove(index);
                    Log.e("cartList", cartList.size() + "");
                    PreferencesService preferencesService = new PreferencesService(getActivity());
                    String userId = preferencesService.get("userId");
                    JSONArray jsonArr = new JSONArray();
                    for (int i = 0; i < cartList.size(); i++){
                        Cart cart = cartList.get(i);
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("bookId", cart.getBookId());
                            obj.put("bookName", cart.getBookName());
                            obj.put("author", cart.getAuthor());
                            obj.put("picUrl", cart.getPicUrl());
                            obj.put("num", 1);
                            jsonArr.put(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    preferencesService.save("cartList" + userId, jsonArr.toString());
                    cartAdapter.notifyDataSetChanged();
                }
            });
            return v;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getCart();
    }

    private void getCart(){
        PreferencesService preferencesService = new PreferencesService(this.getActivity());
        String userId = preferencesService.get("userId");
        String cartStr = preferencesService.get("cartList" + userId);
        try {
            JSONArray json = new JSONArray(cartStr);
            for(int i = 0; i < json.length(); i++){
                Cart cart = new Cart();
                cart.setBookId(json.getJSONObject(i).getInt("bookId"));
                cart.setBookName(json.getJSONObject(i).getString("bookName"));
                cart.setPicUrl(json.getJSONObject(i).getString("picUrl"));
                cart.setAuthor(json.getJSONObject(i).getString("author"));
                cartList.add(cart);
            }
            cartAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
        }

    }

    private void setTotal(){
        int total = 0;
        for (int i = 0; i < cartList.size(); i++){
            if(cartList.get(i).getChecked()){
                total += 1;
            }
        }
        amountTxt.setText(String.valueOf(total) + "本");
    }

}
