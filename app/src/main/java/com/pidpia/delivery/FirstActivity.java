package com.pidpia.delivery;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class FirstActivity extends AppCompatActivity {

    Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        final Handler mHandler;
        final User user = new User(this);

        final String user_ids = user.getData("user_id");
        final String user_passwds = user.getData("user_passwd");


        mHandler = new Handler(){
            public void handleMessage(Message msg){
                if (user_ids.length() > 0 && user_passwds.length() > 0) {
                    user.Login(user_ids, user_passwds, true);
                }else{
                    Intent intent = new Intent(FirstActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        mHandler.sendEmptyMessageDelayed(0,2000);

       /* final User user = new User(this);

        final String user_ids = user.getData("user_id");
        final String user_passwds = user.getData("user_passwd");


        mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ImageView back = (ImageView)findViewById(R.id.first_img);
                            back.setImageResource(R.drawable.firstackground);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (user_ids.length() > 0 && user_passwds.length() > 0) {
                            user.Login(user_ids, user_passwds, true);
                        }
                        else {
                            Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                });
            }

        }).start();*/
    }
}
