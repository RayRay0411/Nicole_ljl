package com.app.book.ui.mine;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.book.MainActivity;
import com.app.book.R;
import com.app.book.components.AvatarImageView;
import com.app.book.model.User;
import com.app.book.util.Constant;
import com.app.book.util.GlideImageLoader;
import com.app.book.util.PreferencesService;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserActivity extends AppCompatActivity {

    private EditText nicknameEdit, realNameEdit, mobileEdit;

    private User user;

    private Button saveBtn;

    private AvatarImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nicknameEdit = findViewById(R.id.nickname);
        realNameEdit = findViewById(R.id.realName);
        mobileEdit = findViewById(R.id.mobile);

        saveBtn = findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMember();
            }
        });

        avatar = findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });

        this.getMember();
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    nicknameEdit.setText(user.getNickname());
                    realNameEdit.setText(user.getRealName());
                    mobileEdit.setText(user.getMobile());
                    new GlideImageLoader().displayImage(UserActivity.this, user.getAvatarUrl(), (ImageView)findViewById(R.id.avatar));
                    break;
                case 2:
                    String info = msg.getData().getString("msg");
                    Toast.makeText(UserActivity.this, info, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //返回按钮点击事件
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMember(){
        PreferencesService preferencesService = new PreferencesService(this);
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
                        user.setRealName(memberJson.getString("realName"));
                        user.setMobile(memberJson.getString("mobile"));
                        user.setAvatarUrl(memberJson.getString("avatarUrl"));
                        handler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void saveMember(){
        String nickname = nicknameEdit.getText().toString();
        String mobile = mobileEdit.getText().toString();
        String realName = realNameEdit.getText().toString();
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("id", user.getId());
            json.put("nickname", nickname);
            json.put("realName", realName);
            json.put("mobile", mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = FormBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));

        PreferencesService preferencesService = new PreferencesService(this);
        String token = preferencesService.get("token");

        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/api/member/update")
                .addHeader("token", token)
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("/api/member/update", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("/api/member/update", "onResponse: " + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int code = json.getInt(Constant.RESP_CODE);
                    if(code == 0){
                        Message msg = new Message();
                        msg.what = 2;
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", "修改成功");
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 上传图片
     * @param file
     */
    private void uploadImg(File file){
        //2.创建RequestBody,Request需要创建一个
        RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/png"));

        //3.构建MultipartBody
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "avatar.png", fileBody)
                .build();

        //4.构建请求
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/api/fileupload/upload")
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("/api/fileupload/upload", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("/api/fileupload/upload", "onResponse: " + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int code = json.getInt(Constant.RESP_CODE);
                    if(code == 0){
                        user.setAvatarUrl(json.getString("url"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showUserImage(user.getAvatarUrl());
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void showUserImage(String avatarUrl) {
        Log.d("UserActivity = ", "showUserImage: 显示的图片地址 = "+avatarUrl);
        new GlideImageLoader().displayImage(UserActivity.this, avatarUrl, (ImageView)findViewById(R.id.avatar));

        updateUserInfo();
    }

    private void updateUserInfo() {


            OkHttpClient client = new OkHttpClient();

            JSONObject json = new JSONObject();
            try {
                json.put("id", user.getId());
                json.put("avatarUrl", user.getAvatarUrl());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody requestBody = FormBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));

            PreferencesService preferencesService = new PreferencesService(this);
            String token = preferencesService.get("token");

            Request request = new Request.Builder()
                    .url(Constant.BASE_URL + "/api/member/update")
                    .addHeader("token", token)
                    .post(requestBody)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("/api/member/update", "onFailure: ");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Log.i("/api/member/update", "onResponse: " + result);
                    try {
                        JSONObject json = new JSONObject(result);
                        int code = json.getInt(Constant.RESP_CODE);
                        if(code == 0){
                            Message msg = new Message();
                            msg.what = 2;
                            Bundle bundle = new Bundle();
                            bundle.putString("msg", "修改成功");
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

    }

    /**
     * 打开相册
     */
    private void choosePhoto() {
        //这是打开系统默认的相册(就是你系统怎么分类,就怎么显示,首先展示分类列表)
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, 2);
    }

    /**
     * startActivityForResult执行后的回调方法，接收返回的图片
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode != Activity.RESULT_CANCELED) {
            String state = Environment.getExternalStorageState();
            if (!state.equals(Environment.MEDIA_MOUNTED)) return;

        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK && null != data) {
            try {
                Uri selectedImage = data.getData();//获取路径
                photoClip(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (requestCode == 3 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();

            if (bundle != null) {
                //在这里获得了剪裁后的Bitmap对象，可以用于上传
                Bitmap image = bundle.getParcelable("data");
                //设置到ImageView上
                avatar.setImageBitmap(image);
                //也可以进行一些保存、压缩等操作后上传
                String path = saveImage("头像", image);
                uploadImg(new File(path));
            }
        }
    }

    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }



    /**
     * 获得读写权限
     */
    private void requestStoragePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else {
            choosePhoto();
        }

//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//
//            dialog.setCancelable(false);
//
//            dialog.setTitle("权限请求");
//
//            dialog.setMessage("请赋予 存储 权限以便执行下一步操作");
//
//            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                @Override
//
//                public void onClick(DialogInterface dialog, int which) {
//                    Toast.makeText(UserActivity.this, "用户取消了权限赋予", Toast.LENGTH_SHORT).show();
//
//                }
//
//            });
//
//            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                @Override
//
//                public void onClick(DialogInterface dialog, int which) {
//                    ActivityCompat.requestPermissions(UserActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//
//                }
//
//            });
//
//            dialog.show();
//
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//
//        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
//                Toast.makeText(this, "storage permission request was denied....", Toast.LENGTH_SHORT).show();
                // 用户拒绝且不再提示
                choosePhoto();

            }


        }
    }

    //裁剪
    private void photoClip(Uri uri) {
        // 调用系统中自带的图片剪裁
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        intent.setDataAndType(uri, "image/*");
//        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
//        intent.putExtra("crop", "true");
//        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", 150);
//        intent.putExtra("outputY", 150);
//        intent.putExtra("return-data", true);
//        startActivityForResult(intent, 3);
        String filePath = getRealFilePath(this, uri);
        Log.d("imagepath = ", "photoClip: 获取的图片路径是 = "+filePath);
        File file = compressPhoto(new File(filePath), 3);
        uploadImg(file);
    }


    private  File compressPhoto(File file, int scale) {
        String photo_path = this.getExternalCacheDir() + File.separator + "bookapp" + File.separator + "photos";
        File dir = new File(photo_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File targetFile = new File(dir, "photo.png");
        int degree = imgDegree(file.getAbsolutePath());
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//        Bitmap bitmap = null;
//        try {
//            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        bitmap = rotateImage(bitmap, degree);
        Bitmap resultBitmap = Bitmap.createBitmap(bitmap.getWidth() / scale, bitmap.getHeight() / scale, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        RectF rectF = new RectF(0, 0, resultBitmap.getWidth(), resultBitmap.getHeight());
        canvas.drawBitmap(bitmap, null, rectF, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        try {
            FileOutputStream fos = new FileOutputStream(targetFile);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return targetFile;
    }

    public static int imgDegree(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }

        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e) {
            //相应异常处理
        }
        return degree;
    }

    public static Bitmap rotateImage(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bmp;
    }


    //保存裁剪头像
    public String saveImage(String name, Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory().getPath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
