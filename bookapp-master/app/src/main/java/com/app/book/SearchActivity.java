package com.app.book;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.ImageDecoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.book.adapter.MyRvListAdapter;
import com.app.book.model.Book;
import com.app.book.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etSearch;
    private View tvSearch;
    private RecyclerView rvList;
    private List<Book> bookList = new ArrayList<>();
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {

                case 2:
                    adapter.setData(bookList);
                    break;
            }
        }
    };
    private MyRvListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        View ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        etSearch = findViewById(R.id.et_search);
        tvSearch = findViewById(R.id.tv_search);
        tvSearch.setOnClickListener(this);
        rvList = findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRvListAdapter();
        rvList.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_search:
                String name = etSearch.getText().toString().trim();
                if (TextUtils.isEmpty(name)){
                    Toast.makeText(this, "请输入书名", Toast.LENGTH_SHORT).show();
                    return;
                }

                getBook(name);

                break;
        }
    }

    /*1、创建OkHttpClient客户端；
    2、创建Request对象；
    3、创建Call对象，并通过newCall方法把OkHttpClient客户端和Request对象串联起来
    4、调用Call的同步或者异步方法发起网络请求：call.execute()、call.enqueue()。*/


    private void getBook(String name){
        OkHttpClient client = new OkHttpClient();/*创建OkHttpClient客户端*/
        Request request = new Request.Builder()  /*返回一个Builder对象*/
        /*初始化一个常用的数据请求要用到的一些参数，比如指定URL网络连接地址，请求方法如get、post等*/
                .url(Constant.BASE_URL + "/api/book/list?bookName="+name)
                .build();

        /*Call对象代表一个实际的一个网络请求，他是OkHttpClient客户端和Request对象的纽带和桥梁，他通过newCall方法把二者串联起来*/
        Call call = client.newCall(request);
        /*同步请求：call.execute();
        异步请求：call.enqueue();*/
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("/api/book/list", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("/api/book/list", "onResponse: " + result);
                try {
                    JSONObject json = new JSONObject(result);/*result是网络请求返回来的json字符串*/
                    int code = json.getInt(Constant.RESP_CODE);
                    if(code == 0){
                        JSONArray bookArr = json.getJSONArray("bookList");
                        List<String> picUrls = new ArrayList<String>();
                        for(int i = 0; i < bookArr.length(); i++){
                            JSONObject bookJson = bookArr.getJSONObject(i);
                            Book book = new Book();
                            book.setId(bookJson.getInt("id"));
                            book.setBookName(bookJson.getString("bookName"));
                            book.setPicUrl(bookJson.getString("picUrl"));
                            book.setAuthor(bookJson.getString("author"));
                            bookList.add(book);
                            handler.sendEmptyMessage(2);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}