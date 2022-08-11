package com.app.book.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.book.R;
import com.app.book.SearchActivity;
import com.app.book.model.Book;
import com.app.book.ui.book.DetailActivity;
import com.app.book.util.Constant;
import com.app.book.util.GlideImageLoader;
import com.youth.banner.Banner;

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

public class HomeFragment extends Fragment {

    private Banner banner;

    private GridView gridView;

    private BookAdapter bookAdapter = new BookAdapter();

    private List<Book> bookList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);


        banner = root.findViewById(R.id.banner);/*放图片地址的集合*/

        this.getBanner();

        gridView = root.findViewById(R.id.book);
        gridView.setAdapter(bookAdapter);

        root.findViewById(R.id.tv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        this.getBook();
        return root;
    }

    class BookAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return bookList.size();
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
            View v = LayoutInflater.from(getContext()).inflate(R.layout.book_item, null);
            final Book book = bookList.get(i);
            new GlideImageLoader().displayImage(getContext(), book.getPicUrl(), (ImageView)v.findViewById(R.id.pic));
            TextView name = v.findViewById(R.id.name);
            name.setText(book.getBookName());
            TextView author = v.findViewById(R.id.author);
            author.setText(book.getAuthor());
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("id", book.getId());
                    getActivity().startActivity(intent);
                }
            });
            return v;
        }
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    //banner设置方法全部调用完毕时最后调用
                    banner.start();/*必须最后调用的方法，启动轮播图*/
                    break;
                case 2:
                    bookAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    /**
     * 获取
     */
    private void getBanner(){
        Log.e("请求", "开始---/api/advert/list---");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/api/advert/list")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("/api/advert/list", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("/api/advert/list", "onResponse: " + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int code = json.getInt(Constant.RESP_CODE);
                    if(code == 0){
                        JSONArray advertList = json.getJSONArray("advertList");
                        List<String> picUrls = new ArrayList<String>();
                        for(int i = 0; i < advertList.length(); i++){
                            JSONObject advert = advertList.getJSONObject(i);
                            picUrls.add(advert.getString("picUrl"));
                            //设置图片加载器
                            banner.setImageLoader(new GlideImageLoader());
                            //设置图片集合
                            banner.setImages(picUrls);
                            handler.sendEmptyMessage(1);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void getBook(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/api/book/list")
                .build();

        Call call = client.newCall(request);
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
                    JSONObject json = new JSONObject(result);
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