package com.app.book.ui.book;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.book.MainActivity;
import com.app.book.R;
import com.app.book.model.Book;
import com.app.book.ui.order.ConfirmActivity;
import com.app.book.util.Constant;
import com.app.book.util.GlideImageLoader;
import com.app.book.util.PreferencesService;
import com.youth.banner.Banner;

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

/**
 * 详情
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Book book = new Book();

    private TextView name, author,stock, describe, num;

    private Button buy, addCart;

    private ImageView cart;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //返回按钮点击事件
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = findViewById(R.id.name);
        author = findViewById(R.id.author);
        stock = findViewById(R.id.stock);
        int id = getIntent().getIntExtra("id", 0);

        buy = findViewById(R.id.buy);
        addCart = findViewById(R.id.addCart);

        buy.setOnClickListener(this);
        addCart.setOnClickListener(this);

        cart = findViewById(R.id.cart);
        cart.setOnClickListener(this);

        num = findViewById(R.id.num);

        describe = findViewById(R.id.describe);
        this.getBook(id);
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    /*自定义的图片加载器*/
                    new GlideImageLoader().displayImage(DetailActivity.this, book.getPicUrl(), (ImageView)findViewById(R.id.pic));
                    name.setText(book.getBookName());
                    author.setText(book.getAuthor());
                    stock.setText(book.getStock());
                    describe.setText(book.getDescribe());
                    break;
            }
        }
    };

    private void getBook(int id){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/api/book/detail?id="+id)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("/api/book/detail", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("/api/book/list", "onResponse: " + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int code = json.getInt(Constant.RESP_CODE);
                    if(code == 0){
                        JSONObject bookJson = json.getJSONObject("book");
                        book.setId(bookJson.getInt("id"));
                        book.setBookName(bookJson.getString("bookName"));
                        book.setPicUrl(bookJson.getString("picUrl"));
                        book.setAuthor(bookJson.getString("author"));
                        book.setStock(bookJson.getString("stock"));
                        book.setDescribe(bookJson.getString("describe"));
                        handler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getCartNum(){
        PreferencesService preferencesService = new PreferencesService(DetailActivity.this);
        String token = preferencesService.get("token");
        if(token == null || "".equals(token)){
            return;
        }
        String userId = preferencesService.get("userId");
        String cartList = preferencesService.get("cartList" + userId);

        if(cartList != null && !"".equals(cartList)){
            try {
                JSONArray json = new JSONArray(cartList);
                num.setText("(" + json.length() + ")");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buy:
                Intent intent = new Intent(DetailActivity.this, ConfirmActivity.class);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("bookId", book.getId());
                    jsonObject.put("bookName", book.getBookName());
                    jsonObject.put("author", book.getAuthor());
                    jsonObject.put("stock", book.getStock());
                    jsonObject.put("picUrl", book.getPicUrl());
                    jsonObject.put("num", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                intent.putExtra("payBook", jsonArray.toString());
                startActivity(intent);

                break;
            case R.id.cart:
                Intent cart = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(cart);
                break;

            case R.id.addCart:
                PreferencesService preferencesService = new PreferencesService(DetailActivity.this);
                String token = preferencesService.get("token");
                if(token == null || "".equals(token)){
                    return;
                }
                String userId = preferencesService.get("userId");
                String cartList = preferencesService.get("cartList" + userId);
                JSONArray json = null;
                boolean has = false;
                if(cartList != null && !"".equals(cartList)){
                    try {
                        json = new JSONArray(cartList);
                        for(int i = 0; i < json.length(); i++){
                            if(json.getJSONObject(i).getInt("bookId") == book.getId()){
                                has = true;
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    json = new JSONArray();
                }
                if(!has){
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("bookId", book.getId());
                        obj.put("bookName", book.getBookName());
                        obj.put("author", book.getAuthor());
                        obj.put("stock", book.getStock());
                        obj.put("picUrl", book.getPicUrl());
                        obj.put("num", 1);
                        json.put(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    preferencesService.save("cartList" + userId, json.toString());
                }
                Toast.makeText(DetailActivity.this, "加入书架成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
