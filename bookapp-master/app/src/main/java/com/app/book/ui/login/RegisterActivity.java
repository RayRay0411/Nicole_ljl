package com.app.book.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.book.R;
import com.app.book.util.Constant;

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

/**
 * 注册
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText loginNameEdit, passwordEdit, realNameEdit, mobileEdit, nicknameEdit;

    private Button loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loginNameEdit = findViewById(R.id.loginName);
        passwordEdit = findViewById(R.id.password);
        mobileEdit = findViewById(R.id.mobile);
        realNameEdit = findViewById(R.id.realName);
        nicknameEdit = findViewById(R.id.nickname);
        loginButton = findViewById(R.id.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:

                    break;
            }
        }
    };

    /**
     * 调用注册接口
     */
    private void register(){
        String loginName = loginNameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String mobile = mobileEdit.getText().toString();
        String realName = realNameEdit.getText().toString();
        String nickname = nicknameEdit.getText().toString();
        OkHttpClient client = new OkHttpClient();

        /*使用JSONObject创建一个JSON对象，其中包含来自java中数据库表的所有值*/
        JSONObject json = new JSONObject();
        try {
            json.put("loginName", loginName);
            json.put("mobile", mobile);
            json.put("realName", realName);
            json.put("password", password);
            json.put("nickname", nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = FormBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/api/register")
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            /*onFailure回调的异常捕获*/
            /*服务器已经收到了请求，但是在做出响应的时候超时了，就会导致OKhttp回调onfailure方法，
            此时，应该重新请求验证服务器是否做出更新。如果已经做出更新则为成功。*/
            public void onFailure(Call call, IOException e) {
                Log.i("/api/register", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("/api/register", "onResponse: " + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int code = json.getInt(Constant.RESP_CODE);
                    if(code == 0){
                        finish();
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
