package com.app.book.ui.order;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.book.R;
import com.app.book.util.Constant;
import com.app.book.util.GlideImageLoader;
import com.app.book.util.PreferencesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConfirmActivity extends AppCompatActivity {

    private LinearLayout bookLayout;

    private TextView amount;

    private Button payBtn;

    private EditText remarkEdt;

    private JSONObject order = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bookLayout = findViewById(R.id.book);

        amount = findViewById(R.id.amount);
        remarkEdt = findViewById(R.id.remark);

        payBtn = findViewById(R.id.pay);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay();
            }
        });

        String payBook = getIntent().getStringExtra("payBook");
        Log.e("payBook", payBook);
        try {
            JSONArray jsonArray = new JSONArray(payBook);
            int total = 0;
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                View book = LayoutInflater.from(this).inflate(R.layout.order_book_item, null);
                TextView name = book.findViewById(R.id.name);
                name.setText(jsonObject.getString("bookName"));
                new GlideImageLoader().displayImage(this, jsonObject.getString("picUrl"), (ImageView)book.findViewById(R.id.pic));
                total += 1;
                bookLayout.addView(book);
            }
            amount.setText(String.valueOf(total + "本"));

            order.put("totalAmount", 0);
            order.put("orderBookList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    Intent intent = new Intent(ConfirmActivity.this, OrderActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 2:
                    String info = msg.getData().getString("msg");
                    Toast.makeText(ConfirmActivity.this, info, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void pay(){

        try {
            order.put("remark", remarkEdt.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = FormBody.create(order.toString(), MediaType.parse("application/json; charset=utf-8"));

        PreferencesService preferencesService = new PreferencesService(this);
        String token = preferencesService.get("token");

        Log.i("/api/order/create", order.toString());

        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/api/order/create")
                .addHeader("token", token)
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("/api/order/create", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("/api/order/create", "onResponse: " + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int code = json.getInt(Constant.RESP_CODE);
                    if(code == 0){

                        handler.sendEmptyMessage(1);
                    }else{
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", json.getString("msg"));
                        msg.setData(bundle);
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
