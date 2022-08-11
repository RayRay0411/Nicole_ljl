package com.app.book.ui.mine;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.book.R;
import com.app.book.model.User;
import com.app.book.ui.login.LoginActivity;
import com.app.book.ui.order.OrderActivity;
import com.app.book.util.Constant;
import com.app.book.util.GlideImageLoader;
import com.app.book.util.PreferencesService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*个人中心页面*/
public class MineFragment extends Fragment implements View.OnClickListener{

    private ImageView avatar;/*默认头像*/
    private TextView nickname;/*用户名*/

    private User user;

    private LinearLayout mine_info, mine_order;

    private Button logout;

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_mine, container, false);
        avatar = root.findViewById(R.id.avatar);
        nickname = root.findViewById(R.id.nickname);
        nickname.setOnClickListener(this);
        avatar.setOnClickListener(this);

        mine_info = root.findViewById(R.id.mine_info);
        mine_order = root.findViewById(R.id.mine_order);
        logout = root.findViewById(R.id.logout);

        mine_info.setOnClickListener(this);
        mine_order.setOnClickListener(this);
        logout.setOnClickListener(this);

        return root;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nickname:
                if(user != null){
                    break;
                }
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.mine_info:
                Intent info = new Intent(getActivity(), UserActivity.class);
                startActivity(info);
                break;

            case R.id.mine_order:
                Intent order = new Intent(getActivity(), OrderActivity.class);
                startActivity(order);
                break;

            case R.id.logout:
                PreferencesService preferencesService = new PreferencesService(getActivity());
                preferencesService.remove("token");
                user = null;
                nickname.setText("未登录，点击登录");
                avatar.setImageResource(R.mipmap.avatar);
                logout.setVisibility(View.GONE);
                break;
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
                    nickname.setText(user.getNickname());
                    new GlideImageLoader().displayImage(getContext(), user.getAvatarUrl(), (ImageView)root.findViewById(R.id.avatar));
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        PreferencesService preferencesService = new PreferencesService(getActivity());
        String token = preferencesService.get("token");
        if(token != null && !"".equals(token)){
            logout.setVisibility(View.VISIBLE);
        }
        this.getMember();
    }

    private void getMember(){
        PreferencesService preferencesService = new PreferencesService(this.getActivity());
        String token = preferencesService.get("token");
        if(token == null || "".equals(token)){
            return;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/api/member/info")
                .addHeader("token", token)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("/api/member/info", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("/api/member/info", "onResponse: " + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int code = json.getInt(Constant.RESP_CODE);
                    if(code == 0){

                        JSONObject memberJson = json.getJSONObject("member");
                        user = new User();
                        user.setId(memberJson.getInt("id"));
                        user.setNickname(memberJson.getString("nickname"));
                        user.setAvatarUrl(memberJson.getString("avatarUrl"));
                        handler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
