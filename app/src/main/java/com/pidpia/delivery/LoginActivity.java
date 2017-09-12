package com.pidpia.delivery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    User user;
    ImageView button;
    protected MainActivity.BackPressCloseHandler backPressCloseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = new User(this);

        button = (ImageView) findViewById(R.id.login_login_btn);

/*        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
*/


        backPressCloseHandler = new MainActivity.BackPressCloseHandler(this);


        //튜토리얼 이동
        ImageView img_tutorial = (ImageView)findViewById(R.id.img_tutorial);
        img_tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, TutorialActivity.class);
                startActivity(intent);
                finish();
            }
        });

        final String user_ids = user.getData("user_id");
        final String user_passwds = user.getData("user_passwd");



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //user.Login("test","1234");
                //클릭시 팝업 윈도우 생성


                PopupWindow popup = new PopupWindow(v, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //팝업으로 띄울 커스텀뷰를 설정하고
                View view = inflater.inflate(R.layout.activity_login_popup, null);
                popup.setContentView(view);
                popup.setAnimationStyle(-1);
                popup.showAtLocation(view, Gravity.CENTER, 0, -100);

//                popup.setBackgroundDrawable(new ColorDrawable(0xb0000000));

                Button login_btn = (Button) view.findViewById(R.id.login_login_btn);

                final EditText user_id = (EditText) view.findViewById(R.id.login_user_id);
                final EditText user_passwd = (EditText) view.findViewById(R.id.login_user_passwd);
                final CheckBox login_Save = (CheckBox) view.findViewById(R.id.login_save_login);

                if (user_ids.length() > 0) user_id.setText(user_ids);
                if (user_passwds.length() > 0) user_passwd.setText(user_passwds);
                if (user_ids.length() > 0 && user_passwds.length() > 0) {
                    login_Save.setChecked(true);
                } else {
                    login_Save.setChecked(false);
                }

                login_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String user_id_str = user_id.getText().toString().trim();
                        final String user_passwd_str = user_passwd.getText().toString().trim();

                        if (user_id_str.length() > 0 && user_passwd_str.length() > 0) {
                            user.Login(user_id_str, user_passwd_str, login_Save.isChecked());
                        } else {
                            user.Msg("아이디와 비밀번호를 입력해주세요.");
                            user.DMsg(user_id_str + "/" + user_passwd_str);
                        }
                    }
                });
                //팝업의 크기 설정
                //  popup.setWindowLayoutMode(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                //팝업 뷰 터치 되도록
                popup.setTouchable(true);
                //팝업 뷰 포커스도 주고
                popup.setFocusable(true);
                //팝업 뷰 이외에도 터치되게 (터치시 팝업 닫기 위한 코드)
                //           popup.setOutsideTouchable(true);
//                popup.setBackgroundDrawable(new BitmapDrawable());
                //인자로 넘겨준 v 아래로 보여주기
                popup.showAsDropDown(v);

                popup.update();
                popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);




            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.img_main_bg);

        //LinearLayout linearLayout  = (LinearLayout) findViewById(R.id.ll_main_bg);

        ScaleAnimation fade_in = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(5000);     // animation duration in milliseconds
        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        imageView.startAnimation(fade_in);

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();


  Log.d("FCM",user.getData("FCM"));



    }
    //백버튼
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }



}
