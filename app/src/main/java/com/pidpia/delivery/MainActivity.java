package com.pidpia.delivery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    Button button;
    User user;
    OrderListView orderListView;
    ListView main_listview;


    TextView main_user_id, main_user_info, main_company_name, main_user_info2;
    Switch main_switch_call, main_switch_gps;

    ImageView main_menu, main_refresh;

    WebClient client;

    DrawerLayout menu_layout;

    FrameLayout policy_confirm_wrap;
    CheckBox policy_confirm_agree1, policy_confirm_agree2, policy_confirm_view;
    Button policy_confirm_ok;

    private BackPressCloseHandler backPressCloseHandler;

    final static String URL_DOMAIN = "http://110.10.189.232:8080/";
    final static String URL_LOGIN = "dv_login.php";
    final static String UR_MAIN_INIT = "dv_main.php";
    final static String URL_ORDER_LIST = "dv_order_list.php";
    final static String URL_ORDER_DETAIL = "dv_order_detail.php";
    final static String URL_ORDER_DATE_SET = "dv_order_date_set.php";
    final static String URL_LOCATION_SET = "dv_order_locatin_set.php";
    final static String URL_ORDER_SET = "dv_order_delivery_set.php";
    final static String URL_SIGN_SET = "dv_delivery_sign1.php";
    final static String URL_ORDER_FEED_LIST = "dv_order_feed_list.php";
    final static String URL_ORDER_FEED_SET = "dv_order_feed_set.php";

    JSONObject login_data;
    String test = "";
    WebClient webClient;


    Geocoder coder;
    LocationManager locManager;
    LocationListener locationListener;
    double latPoint, lngPoint;

    TextView location_target_address;

    boolean location_flag;

    public LocationManager locationManager;


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    try {

                        JSONObject json = new JSONObject(client.recvData);

/*
                        JSONArray array = json.getJSONArray("list");

                        JSONObject status = json.getJSONObject("status");
//                        delivery_title_name.setText(status.getString("title_name"));
                        delivery_brand_name.setText(status.getString("brand_name"));
                        delivery_order_name.setText(status.getString("order_name"));
//                        delivery_start_point.setText(status.getString(""));
                        delivery_end_point.setText(status.getString("end_point"));
                        delivery_request_date.setText(status.getString("request_date"));
                        delivery_memo.setText(status.getString("memo"));


 */
                        main_user_id.setText(json.getString("user_id"));
                        main_user_info.setText(json.getString("user_info1"));
                        main_user_info2.setText(json.getString("user_info2"));
                        main_company_name.setText(json.getString("company_name"));


                    } catch (Exception e) {

                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = new User(this);
        backPressCloseHandler = new BackPressCloseHandler(this);



        //서비스 가동
        if(user.getData("working").equals("1")){
            Intent intent = new Intent(MainActivity.this,GPS_service.class);
            Log.d("GPS_SERVICE","START");
            startService(intent);
        }else{
            Intent intent = new Intent(MainActivity.this,GPS_service.class);
            Log.d("GPS_SERVICE","FINISH");
            stopService(intent);
        }

        button = (Button) findViewById(R.id.main_button);
        main_listview = (ListView) findViewById(R.id.main_listview);
        main_user_id = (TextView) findViewById(R.id.main_user_id);
        main_user_info = (TextView) findViewById(R.id.main_user_info);
        main_user_info2 = (TextView) findViewById(R.id.main_user_info2);

        main_switch_call = (Switch) findViewById(R.id.main_switch_call);
        main_switch_gps = (Switch) findViewById(R.id.main_switch_gps);
        main_menu = (ImageView) findViewById(R.id.main_menu);
        main_refresh = (ImageView) findViewById(R.id.main_refresh);

        main_company_name = (TextView) findViewById(R.id.main_company_name);


        menu_layout = (DrawerLayout) findViewById(R.id.dl_activity_main_drawer);

        policy_confirm_wrap= (FrameLayout)findViewById(R.id.policy_confirm_wrap);
        policy_confirm_agree1 = (CheckBox)findViewById(R.id.policy_confirm_agree1);
        policy_confirm_agree2 = (CheckBox)findViewById(R.id.policy_confirm_agree2);
        policy_confirm_view = (CheckBox)findViewById(R.id.policy_confirm_view);
        LinearLayout layout_tap = (LinearLayout)findViewById(R.id.layout_tap);
        Button bt_logout = (Button)findViewById(R.id.bt_logout);

        TextView use_ok = (TextView)findViewById(R.id.main_use_ok);
        TextView gps_ok = (TextView)findViewById(R.id.main_gps_ok);

        use_ok.setText(user.use_ok);
        gps_ok.setText(user.info_ok);

        main_listview.isStackFromBottom();

        layout_tap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        policy_confirm_ok = (Button)findViewById(R.id.policy_confirm_ok);
        if(user.getBoolean("agree")){

            policy_confirm_wrap.setVisibility(View.GONE);
        }

        policy_confirm_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(policy_confirm_agree1.isChecked()==false||policy_confirm_agree2.isChecked()==false){
                    user.Msg("약관에 동의후 사용 가능합니다.");
                }else {
                    user.setBoolean("agree",policy_confirm_view.isChecked());
                    policy_confirm_wrap.setVisibility(View.GONE);
                }

            }
        });
       /* policy_confirm_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(policy_confirm_agree1.isChecked()==false||policy_confirm_agree2.isChecked()==false){
                    user.Msg("약관에 동의후 사용 가능합니다.");
                }else {
                    user.setBoolean("agree",policy_confirm_view.isChecked());
                    policy_confirm_wrap.setVisibility(View.GONE);
                }

            }
        });*/


        main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menu_layout.isDrawerOpen(Gravity.LEFT)) {
                    menu_layout.closeDrawer(Gravity.LEFT);
                } else {
                    menu_layout.openDrawer(Gravity.LEFT);
                }
            }
        });

        //로그아웃
        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //저장된값 초기화
                //user.Login("", "", true);
                user.setData("working","");
                user.setData("user_id", "");
                user.setData("user_passwd", "");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //GPS 업데이트
        final MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                String location_address = "";
                latPoint = location.getLatitude();
                lngPoint = location.getLongitude();

                Log.d("[LOC]:", latPoint + " " + lngPoint + " - " + location_address);

                user.setData("user_lng",lngPoint+"");
                user.setData("user_lat",latPoint+"");

                if(true){


                    JSONObject obj1 = new JSONObject();
                    try {

                        obj1.put("SSID", user.getData("SSID"));
                        obj1.put("UID",user.getData("UID"));
                        obj1.put("lat",latPoint);
                        obj1.put("lng",lngPoint);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    webClient = new WebClient(URL_DOMAIN + URL_LOCATION_SET, obj1.toString(), mHandler, 200);
                    Log.d("GPS_UP","REFRESH  Lng = "+lngPoint + "  Lat = " + latPoint);
                    //Toast.makeText(MainActivity.this, "REFRESH  Lng = "+lngPoint + "  Lat = " + latPoint, Toast.LENGTH_LONG).show();

                    location_flag=false;

                }

                try {
                    if (coder != null) {
                        List<Address> address = coder.getFromLocation(latPoint, lngPoint, 1);
                        if (address != null && address.size() > 0) {
                            location_address = address.get(0).getAddressLine(0).toString();
                            if (location_target_address != null) {
                                location_target_address.setText(location_address);
                            } else {
                                //Toast.makeText(mContext, location_address, Toast.LENGTH_SHORT).show();
                            }

                            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                return;
                            }
                            //    locManager.removeUpdates(locationListener);
                        } else {
                            //  Toast.makeText(mContext, "Address null.", Toast.LENGTH_SHORT).show();

                            if (location_target_address != null) {
                                location_target_address.setText("위치정보를 찾을 수 없습니다.");
                            } else {
                                Toast.makeText(MainActivity.this, "위치정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (IOException e) {
                    // order_input_address.setText("");
                    if (location_target_address != null) {
                        location_target_address.setText("위치정보를 찾을 수 없습니다.");
                    } else {
                        Toast.makeText(MainActivity.this, "위치정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }

                    locManager.removeUpdates(locationListener);
                    Log.d("location: error", e.toString());
                }
            }
        };

        // user.LocInit();
        orderListView = new OrderListView(this, main_listview, user);

        //user.locManager.requestLocationUpdates(user.locManager.NETWORK_PROVIDER,0,0,user.locationListener);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

//        user.getLocation(null);
        //user.setLocationUpdate();

        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("SSID", user.getData("SSID"));
            obj1.put("UID", user.getData("UID"));
            obj1.put("PID", user.getData("PID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = obj1.toString();
        client = new WebClient(user.URL_DOMAIN + user.UR_MAIN_INIT, data, mHandler, 100);


        //리프레시 버튼
        main_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //리스트뷰 업데이트
                orderListView.Update();
            }
        });
    }

    //백버튼
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1000) {
            // Make sure the request was successful

            orderListView.Update();

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality
            Log.d("[LOC]:", "get permission ok");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
       /* String msg = "New Latitude: " + location.getLatitude()
                + "New Longitude: " + location.getLongitude();

        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();*/
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume(){
        super.onResume();

        user = new User(this);

        if(user.getData("working").equals("1")){
            Intent intent = new Intent(MainActivity.this,GPS_service.class);
            Log.d("GPS_SERVICE","START");
            startService(intent);
        }else{
            Intent intent = new Intent(MainActivity.this,GPS_service.class);
            Log.d("GPS_SERVICE","FINISH");
            stopService(intent);
        }
    }

    //백버튼 종료
    public static class BackPressCloseHandler {
        private long backKeyPressedTime = 0;
        private Toast toast;
        private Activity activity;
        public BackPressCloseHandler(Activity context) {
            this.activity = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000)
            {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return;
            }

            if (System.currentTimeMillis() <= backKeyPressedTime + 2000)
            {
                activity.finish();
                toast.cancel();
            }
        }
        public void showGuide()
        {
            toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT); toast.show();
        }
    }


}
