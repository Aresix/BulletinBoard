package com.example.bulletinboard.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bulletinboard.utils.CaptchaUtil;
import com.example.bulletinboard.R;

import org.json.JSONObject;


import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username;
    private EditText password;
    private EditText captcha;
    private FrameLayout hint;
    private TextView hintText;
    private Button loginButton;
    private Button usernameClearButton;
    private Button passwordEyeButton;
    private Button passwordClearButton;
    private Button captchaClearButton;
    private ImageButton captchaButton;
    private TextWatcher usernameWatcher;
    private TextWatcher passwordWatcher;
    private TextWatcher captchaWatcher;
    private CaptchaUtil captchaUtil;
    private Handler handler;
    private String url = "https://vcapi.lvdaqian.cn/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initWatcher(); // 初始化文本框的监听器
        username = (EditText) findViewById(R.id.username);
        username.addTextChangedListener(usernameWatcher);
        password = (EditText) findViewById(R.id.password);
        password.addTextChangedListener(passwordWatcher);
        captcha = (EditText) findViewById(R.id.captcha);
        captcha = (EditText) findViewById(R.id.captcha);
        captcha.addTextChangedListener(captchaWatcher);
        hint = (FrameLayout) findViewById(R.id.hint);
        hintText = (TextView) findViewById(R.id.hintText);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        usernameClearButton = (Button) findViewById(R.id.usernameClearButton);
        usernameClearButton.setOnClickListener(this);
        passwordEyeButton = (Button) findViewById(R.id.passwordEyeButton);
        passwordEyeButton.setOnClickListener(this);
        passwordClearButton = (Button) findViewById(R.id.passwordClearButton);
        passwordClearButton.setOnClickListener(this);
        captchaClearButton = (Button) findViewById(R.id.captchaClearButton);
        captchaClearButton.setOnClickListener(this);
        captchaButton = (ImageButton) findViewById(R.id.captchaButton);
        captchaButton.setOnClickListener(this);
        // 初始化验证码
        captchaUtil = CaptchaUtil.getInstance();
        Bitmap bitmap = captchaUtil.createBitmap();
        captchaButton.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginButton: // 登录
                if (login() == true) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    Intent intent;
                    if (getIntent().getStringExtra("id") != null){
                        intent = new Intent(LoginActivity.this, TextActivity.class); // 成功登陆后，跳转至正文页面
                        intent.replaceExtras(getIntent()); // 将intent中的信息传递给正文活动
                        // 等待网络请求进程结束
                        handler = new Handler(){
                            public void handleMessage(Message msg){
                                switch (msg.what){
                                    case 0:
                                        startActivity(intent);
                                        finish();
                                        break;
                                }
                                super.handleMessage(msg);
                            }
                        };
                    }
                    else{
                        finish();
                    }
                }
                break;
            case R.id.usernameClearButton: // 删除用户名和密码整行
                username.setText("");
                password.setText("");
                break;
            case R.id.passwordClearButton: // 删除密码整行
                password.setText("");
            case R.id.captchaClearButton: // 删除验证码整行
                captcha.setText("");
            // 密码是否可见通过检查InputType的类型，设置InputType实现
            case R.id.passwordEyeButton:
                if (password.getInputType() == (InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                    passwordEyeButton.setBackgroundResource(R.mipmap.visible); // 切换图形
                    password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_NORMAL);
                }
                else{
                    passwordEyeButton.setBackgroundResource(R.mipmap.invisible);
                    password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setTypeface(Typeface.SANS_SERIF); // 密码格式自动改字体为MONOSPACE，需要改回来
                }
                password.setSelection(password.getText().toString().length()); // 重新赋值
                break;
            case R.id.captchaButton: // 刷新验证码
                Bitmap bitmap = captchaUtil.createBitmap();
                captchaButton.setImageBitmap(bitmap);
                break;
            default:
        }
    }

    // 当文本框内有数据时，删除按钮可见，否则不可见
    private void initWatcher(){
        usernameWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0){
                    usernameClearButton.setVisibility(View.VISIBLE);
                }
                else{
                    usernameClearButton.setVisibility(View.INVISIBLE);
                }
            }
        };

        passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0){
                    passwordClearButton.setVisibility(View.VISIBLE);
                }
                else{
                    passwordClearButton.setVisibility(View.INVISIBLE);
                }
            }
        };

        captchaWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0){
                    captchaClearButton.setVisibility(View.VISIBLE);
                }
                else{
                    captchaClearButton.setVisibility(View.INVISIBLE);
                }
            }
        };
    }

    private boolean login(){
        String captchaCode = captcha.getText().toString().trim();
        String name = username.getText().toString().trim();
        String pwd = password.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)){
            hintText.setText("用户名或密码不能为空");
            hint.setVisibility(View.VISIBLE);
            return false;
        }
        if (TextUtils.isEmpty(captchaCode)){
            hintText.setText("请输入验证码");
            hint.setVisibility(View.VISIBLE);
            Bitmap bitmap = captchaUtil.createBitmap();
            captchaButton.setImageBitmap(bitmap);
            return false;
        }
        if (!captchaCode.equalsIgnoreCase(captchaUtil.getCode())){
            hintText.setText("验证码错误");
            hint.setVisibility(View.VISIBLE);
            Bitmap bitmap = captchaUtil.createBitmap(); // 刷新验证码
            captchaButton.setImageBitmap(bitmap);
            return false;
        }

        final OkHttpClient client = new OkHttpClient();
        final MediaType JSON = MediaType.parse("application/json");
        RequestBody body = new FormBody.Builder()
                .add("username", name) // 用户名
                .add("password", pwd) // 密码
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        String jsonStr = response.body().string();
                        Log.e("tokens", jsonStr);
                        JSONObject jsonResponse = new JSONObject(jsonStr);
                        // tokens放入sharedPreference中存储
                        if (jsonResponse != null){
                            SharedPreferences sp = getSharedPreferences("loginToken", 0);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("code", jsonResponse.getString("code"));
                            editor.putString("message", jsonResponse.getString("message"));
                            editor.putString("token", jsonResponse.getString("token"));
                            editor.commit();
                        }
                        handler.sendEmptyMessage(0); // 进程执行完毕后发送同步指令
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        return true;
    }
}