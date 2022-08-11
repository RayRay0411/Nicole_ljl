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
import com.app.book.util.PreferencesService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 登录
 */
public class LoginActivity extends AppCompatActivity {

    private EditText loginNameEdit, passwordEdit;

    private Button loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginNameEdit = findViewById(R.id.loginName);
        passwordEdit = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
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


    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    //banner设置方法全部调用完毕时最后调用
                    finish();
                    break;
            }
        }
    };

   /* OkHttpClient 保持单例，用同一个 OkHttpClient 实例来执行你的所有请求，因为每一个 OkHttpClient 实例都拥有自己的连接池和线程池，重用这些资源可以减少延时和节省资源*/
    /**
     * 调用登录接口
     */
    private void login(){
        String loginName = loginNameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/api/login?loginName=" + loginName +"&password=" + password)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("/api/login", "onFailure: ");
            }
            /*每一个Call（其实现是RealCall）只能执行一次，否则会报异常*/
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("/api/login", "onResponse: " + result);
                try {
                    /*json就是通过API发送一个请求，然后会收到json数据
                     * 得到服务器端返回json数据，并做处理
                     * */
                    JSONObject json = new JSONObject(result);
                    int code = json.getInt(Constant.RESP_CODE);
                    if(code == 0){
                        JSONObject userJson = json.getJSONObject("member");
                        String token = json.getString("token");
                        PreferencesService preferencesService = new PreferencesService(LoginActivity.this);
                        preferencesService.save("token", token);
                        handler.sendEmptyMessage(1);
                    }
                    /*当try语句中出现异常时，会执行catch中的语句*/
                    /*java运行时系统会自动将catch括号中的Exception e 初始化，也就是实例化Exception类型的对象，
                    e是此对象引用名称，然后e（引用）会自动调用异常类中指定的方法*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }/*在命令行打印异常信息在程序中出错的位置及原因*/

            }
        });
    }


}
