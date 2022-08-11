package com.app.book.ui.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.book.R;
import com.app.book.model.Order;
import com.app.book.model.OrderBook;
import com.app.book.util.Constant;
import com.app.book.util.GlideImageLoader;
import com.app.book.util.PreferencesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView order;

    private List<Order> orderList = new ArrayList<>();

    private OrderAdapter orderAdapter = new OrderAdapter();

    private TextView statusAll, statusBorrowing, statusReturned;

    private Integer status = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        order = findViewById(R.id.order);
        order.setAdapter(orderAdapter);
        getOrder();

        statusAll = findViewById(R.id.statusAll);
        statusAll.setOnClickListener(this);
        statusBorrowing = findViewById(R.id.statusBorrowing);
        statusBorrowing.setOnClickListener(this);
        statusReturned = findViewById(R.id.statusReturned);
        statusReturned.setOnClickListener(this);

    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    orderAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void setSelected(TextView tv){
        statusAll.setTextColor(getResources().getColor(R.color.black));
        statusBorrowing.setTextColor(getResources().getColor(R.color.black));
        statusReturned.setTextColor(getResources().getColor(R.color.black));
        tv.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.statusAll:
                setSelected(statusAll);
                status = -1;
                getOrder();
                break;
            case R.id.statusBorrowing:
                setSelected(statusBorrowing);
                status = 1;
                getOrder();
                break;
            case R.id.statusReturned:
                setSelected(statusReturned);
                status = 2;
                getOrder();
                break;
        }
    }

    class OrderAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return orderList.size();
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
            View v = LayoutInflater.from(OrderActivity.this).inflate(R.layout.order_item, null);
            final Order order = orderList.get(i);

            TextView num = v.findViewById(R.id.num);
            num.setText(order.getOrderNumber());
            TextView date = v.findViewById(R.id.date);
            date.setText(order.getStartDate() + "~" + order.getEndDate());
            LinearLayout bookLayout = v.findViewById(R.id.book);
            List<OrderBook> orderBookList = order.getOrderBookList();
            for (int j = 0; j < orderBookList.size(); j++){
                OrderBook orderBook = orderBookList.get(j);
                View book = LayoutInflater.from(OrderActivity.this).inflate(R.layout.order_book_item, null);
                TextView name = book.findViewById(R.id.name);
                name.setText(orderBook.getBookName());
                new GlideImageLoader().displayImage(OrderActivity.this, orderBook.getPicUrl(), (ImageView)book.findViewById(R.id.pic));

                bookLayout.addView(book);
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            return v;
        }
    }

    private void getOrder(){
        orderList.clear();
        PreferencesService preferencesService = new PreferencesService(this);
        String token = preferencesService.get("token");
        if(token == null || "".equals(token)){
            return;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/api/order/list?status="+status)
                .addHeader("token", token)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("/api/order/list", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("/api/order/list", "onResponse: " + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int code = json.getInt(Constant.RESP_CODE);
                    if(code == 0){
                        JSONArray orderArr = json.getJSONArray("orderList");
                        List<String> picUrls = new ArrayList<String>();
                        for(int i = 0; i < orderArr.length(); i++){
                            JSONObject orderJson = orderArr.getJSONObject(i);
                            Order order = new Order();
                            order.setId(orderJson.getInt("id"));
                            order.setOrderNumber(orderJson.getString("orderNumber"));
                            order.setStartDate(orderJson.getString("startDate"));
                            order.setEndDate(orderJson.getString("endDate"));
                            order.setOrderStatus(orderJson.getInt("orderStatus"));
                            JSONArray bookArr = orderJson.getJSONArray("orderBookList");
                            List<OrderBook> orderBookList = new ArrayList<>();
                            for(int j = 0; j < bookArr.length(); j++){
                                JSONObject orderBookJson = bookArr.getJSONObject(j);
                                OrderBook orderBook = new OrderBook();
                                orderBook.setPicUrl(orderBookJson.getString("picUrl"));
                                orderBook.setBookName(orderBookJson.getString("bookName"));
                                orderBookList.add(orderBook);
                            }
                            order.setOrderBookList(orderBookList);
                            orderList.add(order);
                        }
                        handler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //返回按钮点击事件
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
