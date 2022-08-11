package com.app.book.ui.classify;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.book.R;
import com.app.book.model.Classify;
import com.app.book.model.Book;
import com.app.book.ui.book.DetailActivity;
import com.app.book.util.Constant;
import com.app.book.util.GlideImageLoader;


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

public class ClassifyFragment extends Fragment {

    private List<Classify> classifyList = new ArrayList<>();
    private List<Book> bookList = new ArrayList<>();

    private ListView listView;

    private GridView gridView;

    private BookAdapter bookAdapter = new BookAdapter();

    private ClassifyAdapter classifyAdapter = new ClassifyAdapter();

    private int categoryIndex = 0;

    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_classify, container, false);

        Classify all = new Classify();
        all.setCategoryName("全部");
        all.setId(-1);
        classifyList.add(all);
        listView = root.findViewById(R.id.category);
        gridView = root.findViewById(R.id.book);

        listView.setAdapter(classifyAdapter);
        gridView.setAdapter(bookAdapter);

        this.getCategory();
        this.getBook();

        return root;
    }

    class ClassifyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return classifyList.size();
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
        public View getView(int i, View view, final ViewGroup viewGroup) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.classify_item, null);
            Classify classify = classifyList.get(i);
            final TextView name = v.findViewById(R.id.name);
            name.setText(classify.getCategoryName());
            final int index = i;
            if(i == categoryIndex){
                name.setTextColor(Color.rgb(216, 27, 96));
            }else{
                name.setTextColor(Color.rgb(44, 44, 44));
            }
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryIndex = index;
                    getBook();
                    handler.sendEmptyMessage(1);
                }
            });
            return v;
        }
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
            View v = LayoutInflater.from(getContext()).inflate(R.layout.classify_book_item, null);
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
                    classifyAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    bookAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void getCategory(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/api/category/list")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("/api/category/list", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("/api/category/list", "onResponse: " + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int code = json.getInt(Constant.RESP_CODE);
                    if(code == 0){
                        JSONArray categoryArr = json.getJSONArray("categoryList");
                        for(int i = 0; i < categoryArr.length(); i++){
                            /*i=0，定义循环变量i，从0开始；arr.Length是指数组的元素个数，当i<arr.Length成立时执行循环体内的语句；
                            i++，指一个循环结束后，i自加1。*/
                            JSONObject categoryJson = categoryArr.getJSONObject(i);
                            Classify category = new Classify();
                            category.setId(categoryJson.getInt("id"));
                            category.setCategoryName(categoryJson.getString("categoryName"));
                            classifyList.add(category);
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
        bookList.clear();
        Integer categoryId = classifyList.get(this.categoryIndex).getId();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/api/book/list?categoryId="+categoryId)
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
