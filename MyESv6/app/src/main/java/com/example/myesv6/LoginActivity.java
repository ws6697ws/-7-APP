package com.example.myesv6;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private LinearLayout all;
    private EditText userInput;
    private EditText passwordInput;
    private CircleView snap;
    private Button login;
    private APIManager apiManager;
    private static Context context;
    private Handler ToastHandler = new Handler() {
        public void handleMessage(Message message) {
            switch(message.what) {
                case 0:
                    Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, MainActivity.class));
                    LoginActivity.this.finish();
                    break;
                case 1:
                    Toast.makeText(context,"密码错误", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(context,"网络错误", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        all = (LinearLayout)findViewById(R.id.all);
        userInput = (EditText) all.findViewById(R.id.userInput);
        passwordInput = (EditText) all.findViewById(R.id.passwordInput);
        context = this;
        apiManager = new APIManager(this);
        login = (Button)all.findViewById(R.id.login);
        snap = (CircleView)all.findViewById(R.id.myImage);
        snap.setImageResource(R.drawable.boy);

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String tempUser = userInput.getText().toString();
                String tempPass = passwordInput.getText().toString();
                Toast.makeText(context, "登录中", Toast.LENGTH_SHORT).show();
                new Thread(new LoginRun(tempUser, tempPass)) {
                }.start();
            }

        });
    }

    class LoginRun implements Runnable {
        String user, password;
        LoginRun(String u, String p) {
            user = u;
            password = p;
        }
        @Override
        public void run() {
            try {
                boolean result = apiManager.login(user, password);
                if (result) {

                    Message msg = new Message();
                    msg.what = 0;
                    ToastHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = 1;
                    ToastHandler.sendMessage(msg);
                }
            } catch (RequestErrorException e) {
                Message msg = new Message();
                msg.what = 2;
                ToastHandler.sendMessage(msg);
                e.printStackTrace();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
