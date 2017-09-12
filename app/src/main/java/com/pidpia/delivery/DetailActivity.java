package com.pidpia.delivery;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.R.attr.data;

public class DetailActivity extends AppCompatActivity {
    ImageView delivery_back;
    TextView delivery_title_name, delivery_brand_name, delivery_order_name, delivery_start_point, delivery_end_point, delivery_request_date, delivery_memo, delivery_status_icon_text;
    Button delivery_expect_time;
    ListView delivery_order_list;
    LinearLayout delivery_confirm, delivery_complete,delivery_arrive_wrap, delivery_confirm_sign_wrap,delivery_arrive_wrap_line;
    TextView delivery_complete_call, delivery_complete_sign,delivery_arrive_date;

    ImageView delivery_confirm_sign;
    static int pay_type;




    ScrollView scrollview_wrap;

    int year, month, day, hour, minute, second;

    User user;
    WebClient client;





    OrderFeedListview feedListview;

    /****** Sign *******/

    DrawingView dv;
    private Paint mPaint;
    private Canvas mCanvas;
    float downx = 0, downy = 0, upx = 0, upy = 0;
    Path path = new Path();
    File tempFile;

    boolean delivery_date_status=false;



    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    try {

                        JSONObject json = new JSONObject(client.recvData);


                        JSONArray array = json.getJSONArray("list");

                        JSONObject status = json.getJSONObject("status");

                        user.setData("delivery_state", String.valueOf(status.getInt("order_status")));
//                        delivery_title_name.setText(status.getString("title_name"));
                        delivery_brand_name.setText(status.getString("brand111111_name"));
                        delivery_order_name.setText(status.getString("order_name"));
//                        delivery_start_point.setText(status.getString(""));
                        delivery_end_point.setText(status.getString("end_point"));
                        delivery_request_date.setText(status.getString("request_date"));
                        delivery_memo.setText(status.getString("memo"));

                        if(status.getString("expect_date").length()>0) {
                            delivery_date_status=true;
                            delivery_expect_time.setText(status.getString("expect_date").substring(0,status.getString("expect_date").length()-3));
                        }else{

                            delivery_expect_time.setText("설정");
                            delivery_date_status=false;
                        }

                        if(status.getString("arrive_date").length()>0) {
                            delivery_arrive_date.setText(status.getString("arrive_date"));
                            delivery_arrive_wrap.setVisibility(View.VISIBLE);
                        }else{
                            delivery_arrive_wrap.setVisibility(View.GONE);
                        }

                        if(status.getString("img_sign").length()>0) {

                                    new ImageLoad(DetailActivity.this,delivery_confirm_sign,status.getString("img_sign")).start();
                        }





                        user.setData("end_lat", status.getString("end_lat"));
                        user.setData("end_lng", status.getString("end_lng"));


                        final LinearLayout list_size = (LinearLayout)findViewById(R.id.feed_list_real_size_view_wrap);
                        final LinearLayout option_size = (LinearLayout)findViewById(R.id.feed_option_real_size_view_wrap);
                        final LinearLayout feed_option_real_size_view_wrap = (LinearLayout)findViewById(R.id.feed_option_real_size_view_wrap);
                        final LinearLayout feed_option_real_size_set_wrap = (LinearLayout)findViewById(R.id.feed_option_real_size_set_wrap);
                        final LinearLayout feed_list_real_size_view_wrap = (LinearLayout)findViewById(R.id.feed_list_real_size_view_wrap);
                        final LinearLayout feed_list_real_size_set_wrap = (LinearLayout)findViewById(R.id.feed_list_real_size_set_wrap);

                        switch (status.getInt("order_status")) {

                            default:
                                break;

                            case 1:
                            case 2:
                                String PP = "#b17ed3";
                                delivery_status_icon_text.setTextColor(Color.parseColor(PP));
                                delivery_status_icon_text.setBackgroundResource(R.drawable.circle_ready);/*circle에서 따로 래디를 만들어 바꿈*/
                                delivery_status_icon_text.setText("준비중");
                                delivery_confirm.setVisibility(View.VISIBLE);
                                delivery_complete.setVisibility(View.GONE);
                                delivery_confirm_sign_wrap.setVisibility(View.GONE);
                                delivery_arrive_wrap.setVisibility(View.GONE);
                                delivery_arrive_wrap_line.setVisibility(View.GONE);
                                /*list_size.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        feed_list_real_size_view_wrap.setVisibility(View.GONE);
                                        feed_list_real_size_set_wrap.setVisibility(View.VISIBLE);
                                        option_size.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                            }
                                        });
                                    }
                                });

                                option_size.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        feed_option_real_size_view_wrap.setVisibility(View.GONE);
                                        feed_option_real_size_set_wrap.setVisibility(View.VISIBLE);
                                        list_size.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                            }
                                        });
                                    }
                                });*/
                                break;

                            case 3:
                                String GR = "#0BD438";
                                delivery_status_icon_text.setTextColor(Color.parseColor(GR));
                                delivery_status_icon_text.setBackgroundResource(R.drawable.circle_delivery);/*서클 딜리버리를 따로 XML로 만들어 바꿈*/
                                delivery_status_icon_text.setText("배송중");
                                delivery_confirm.setVisibility(View.GONE);
                                delivery_complete.setVisibility(View.VISIBLE);
                                /*Drawable alpha = delivery_complete.getBackground();
                                alpha.setAlpha(80);*/
                                delivery_confirm_sign_wrap.setVisibility(View.GONE);
                                delivery_arrive_wrap.setVisibility(View.GONE);
                                delivery_arrive_wrap_line.setVisibility(View.GONE);
                              /*  list_size.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });
                                option_size.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });*/
                                break;

                            case 4:
                                String DG = "#666666";
                                delivery_status_icon_text.setText("완료");
                                delivery_status_icon_text.setTextColor(Color.parseColor(DG));
                                delivery_status_icon_text.setBackgroundResource(R.drawable.circle_end);/*써클 엔드를 따로 만들어 바꿈*/
                           /*     list_size.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });
                                option_size.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });*/

                            case 9:
                                String DG1 = "#666666";
                                delivery_status_icon_text.setText("완료");
                                delivery_status_icon_text.setTextColor(Color.parseColor(DG1));
                                delivery_status_icon_text.setBackgroundResource(R.drawable.circle_end);/*써클 엔드를 따로 만들어 바꿈*/
                           /*     list_size.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });
                                option_size.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });*/
                            case 5:
                                delivery_arrive_wrap_line.setVisibility(View.VISIBLE);
                                delivery_confirm_sign_wrap.setVisibility(View.VISIBLE);
                                delivery_confirm.setVisibility(View.GONE);
                                delivery_complete.setVisibility(View.GONE);
                     /*           list_size.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });
                                option_size.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });
*/

                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                params.setMargins(0,0,0,0);
                                scrollview_wrap.setLayoutParams(params);

                                break;

                        }

                        client = new WebClient("http://110.10.189.232/Service_FCM.php","", mHandler, 0);

                    } catch (Exception e) {

                    }
                    break;
                case 200:

                    try {

                        JSONObject json = new JSONObject(client.recvData);

                        if (json.getBoolean("status")) {
                            user.Msg(json.getString("msg"));
                            delivery_expect_time.setText(json.getString("date"));

                            delivery_date_status=true;

                        } else {
                            user.Msg(json.getString("msg"));

                        }


                    } catch (Exception e) {

                    }
                    break;

                case 300:


                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("SSID", user.getData("SSID"));
                        obj1.put("UID", user.getData("UID"));
                        obj1.put("PID", user.getData("PID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = obj1.toString();
                    client = new WebClient(user.URL_DOMAIN + user.URL_ORDER_DETAIL, data, mHandler, 100);

                    break;

                case 500:
                case 600:
                    JSONObject obj2 = new JSONObject();
                    try {
                        obj2.put("SSID", user.getData("SSID"));
                        obj2.put("UID", user.getData("UID"));
                        obj2.put("PID", user.getData("PID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data2 = obj2.toString();
                    client = new WebClient(user.URL_DOMAIN + user.URL_ORDER_DETAIL, data2, mHandler, 100);

                    user.Msg(upload_msg);
                    popupWindowSign.dismiss();
                    break;

                case 0:


                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        user = new User(this);
        delivery_order_list = (ListView) findViewById(R.id.delivery_order_list);
        feedListview= new OrderFeedListview(this,delivery_order_list,user);
        delivery_back = (ImageView) findViewById(R.id.delivery_back);
        //delivery_status_icon = (ImageView) findViewById(R.id.delivery_status_icon);

        delivery_title_name = (TextView) findViewById(R.id.delivery_title_name);
        delivery_brand_name = (TextView) findViewById(R.id.delivery_brand_name);
        delivery_order_name = (TextView) findViewById(R.id.delivery_order_name);
        delivery_status_icon_text = (TextView) findViewById(R.id.delivery_status_icon_text1);

        //배송상세 현재 위치 텍스트뷰 삭제
        //delivery_start_point = (TextView) findViewById(R.id.delivery_start_point);

        delivery_end_point = (TextView) findViewById(R.id.delivery_end_point);
        delivery_request_date = (TextView) findViewById(R.id.delivery_request_date);
        delivery_memo = (TextView) findViewById(R.id.delivery_memo);
        delivery_arrive_date = (TextView) findViewById(R.id.delivery_arrive_date);
        delivery_expect_time = (Button) findViewById(R.id.delivery_expect_time);

        LinearLayout real = (LinearLayout)findViewById(R.id.feed_list_real_size_view_wrap);
        Intent intent = getIntent();
        final int state = intent.getIntExtra("state",0);
        pay_type = intent.getIntExtra("order_pay_type",0);

        if(state==4) {
            //도착시간 설정
            delivery_expect_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DetailActivity.this, "배송완료 된 배송입니다.", Toast.LENGTH_SHORT).show();
                }
            });


        }else {
            //도착시간 설정
            delivery_expect_time.setOnClickListener(new View.OnClickListener() {
                View layout;

                @Override
                public void onClick(View v) {


                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(DetailActivity.this.LAYOUT_INFLATER_SERVICE);
                    layout = layoutInflater.inflate(R.layout.popup_datetime, null);

                    final PopupWindow popupWindow = new PopupWindow(DetailActivity.this);
                    popupWindow.setContentView(layout);
                    popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                    popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
                    popupWindow.setFocusable(true);

                    popupWindow.setAnimationStyle(-1);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    popupWindow.setOutsideTouchable(true);
                    //popupWindow.setFocusable(true);

                    popupWindow.showAtLocation(layout, Gravity.CENTER, 0, -100);
                    ((TextView) layout.findViewById(R.id.popup_datetime_time)).setText(String.format("%02d", hour) + ":" + String.format("%02d", minute));
                    ((TextView) layout.findViewById(R.id.popup_datetime_date)).setText(year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day));


                    (layout.findViewById(R.id.popup_datetime_date)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            new DatePickerDialog(DetailActivity.this, dateSetListener1, year, month, day).show();

                        }
                    });


                    (layout.findViewById(R.id.popup_datetime_time)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            new TimePickerDialog(DetailActivity.this, timeSetListener, hour, minute, false).show();

                        }
                    });


                    (layout.findViewById(R.id.popup_datetime_cancel)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });

                    (layout.findViewById(R.id.popup_datetime_ok)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            long unixtime = user.getUnixtime(year + "-" + (month + 1) + "-" + day + " " + String.format("%02d", hour) + ":" + String.format("%02d", minute), "yyyy-MM-dd HH:mm");

                            //   user.Msg(unixtime + "");
                            Log.d("UTIME", unixtime + "");


                            JSONObject obj1 = new JSONObject();
                            try {
                                obj1.put("SSID", user.getData("SSID"));
                                obj1.put("UID", user.getData("UID"));
                                obj1.put("PID", user.getData("PID"));
                                obj1.put("DT_TIME", unixtime);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String data = obj1.toString();
                            client = new WebClient(user.URL_DOMAIN + user.URL_ORDER_DATE_SET, data, mHandler, 200);
                            popupWindow.dismiss();

                        }
                    });


                }

                public DatePickerDialog.OnDateSetListener dateSetListener1 = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year2, int monthOfYear2, int dayOfMonth2) {
                        year = year2;
                        month = monthOfYear2;
                        day = dayOfMonth2;

                        ((TextView) layout.findViewById(R.id.popup_datetime_date)).setText(year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day));

                    }
                };

                //시간 피커
                public TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
                        hour = hourOfDay;
                        minute = minutes;
                        ((TextView) layout.findViewById(R.id.popup_datetime_time)).setText(String.format("%02d", hour) + ":" + String.format("%02d", minute));
                    }
                };

            });
        }

        if(state==2){

        }
        delivery_confirm = (LinearLayout) findViewById(R.id.delivery_confirm);

        delivery_confirm_sign = (ImageView) findViewById(R.id.delivery_confirm_sign);

        delivery_confirm_sign_wrap = (LinearLayout) findViewById(R.id.delivery_confirm_sign_wrap);
        delivery_arrive_wrap = (LinearLayout) findViewById(R.id.delivery_arrive_wrap);
        delivery_arrive_wrap_line = (LinearLayout) findViewById(R.id.delivery_arrive_wrap_line);

        delivery_complete = (LinearLayout) findViewById(R.id.delivery_complete);
        delivery_complete_call = (TextView) findViewById(R.id.delivery_complete_call);
        delivery_complete_sign = (TextView) findViewById(R.id.delivery_complete_sign);
        delivery_complete.setVisibility(View.GONE);
        scrollview_wrap  = (ScrollView )findViewById(R.id.scrollview_wrap);






        /*
        delivery_order_list.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                delivery_order_list.requestDisallowInterceptTouchEvent(true);

                return false;

            }

        });
        */

        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = 0;
        minute = 0;
        second = 0;


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(12);

        //상단 뒤로가기버튼
        ImageView back = (ImageView)findViewById(R.id.delivery_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });



        //location 매니저 선언
        final LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);


        //배송상세 현재 위치 삭제
        /*delivery_start_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.Msg("현재 위치를 검색합니다.");
                user.getLocation(delivery_start_point);
            }
        });
*/

        //지도 호출
        delivery_end_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, MapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        user.getLocation(delivery_start_point);


        //배송 출발 버튼
        delivery_confirm.setOnClickListener(new View.OnClickListener() {
            View layout;

            @Override
            public void onClick(View v) {

                TextView feed_list_real_size = (TextView) findViewById(R.id.feed_list_real_size);
                TextView feed_option_real_sezi = (TextView) findViewById(R.id.feed_option_real_size);
                //GPS가 켜져있지 않을때
                if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                    builder.setMessage("GPS가 꺼져있습니다. GPS를 켜주세요")
                            .setCancelable(false)
                            .setPositiveButton("GPS 켜기", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(gps);
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
                //사료 실제 공급량 기입안되면 배송출발 X
                else if(feed_list_real_size.getText().toString().equals(" kg / 준비중")){
                    Toast.makeText(DetailActivity.this, "메인사료의 실공급량을 입력해주세요", Toast.LENGTH_SHORT).show();
                }

                else if(user.getBoolean("feed_option")){
                    if (feed_option_real_sezi.getText().toString().equals(" kg / 준비중")) {
                        Toast.makeText(DetailActivity.this, "배합사료의 실공급량을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }else {

                        //배송출발 수행기능
                        if (delivery_date_status) {

                            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(DetailActivity.this.LAYOUT_INFLATER_SERVICE);
                            layout = layoutInflater.inflate(R.layout.popup_confirm_delivery, null);

                            final PopupWindow popupWindow = new PopupWindow(DetailActivity.this);
                            popupWindow.setContentView(layout);
                            popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                            popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
                            popupWindow.setFocusable(true);

                            popupWindow.setAnimationStyle(-1);
                            popupWindow.setBackgroundDrawable(new BitmapDrawable());
                            popupWindow.setOutsideTouchable(true);
                            //popupWindow.setFocusable(true);

                            popupWindow.showAtLocation(layout, Gravity.CENTER, 0, -100);

                            (layout.findViewById(R.id.popup_confirm_delivery_ok)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String time = sdfNow.format(new Date(System.currentTimeMillis()));

                                    JSONObject obj1 = new JSONObject();
                                    try {
                                        obj1.put("SSID", user.getData("SSID"));
                                        obj1.put("UID", user.getData("UID"));
                                        obj1.put("PID", user.getData("PID"));
                                        obj1.put("STATUS", 3);
                                        obj1.put("time", time);
                                        obj1.put("type", "y");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String data = obj1.toString();
                                    Log.d("DELIVERY_START", user.getData("UID") + " " + user.getData("PID") + " " + time);
                                    client = new WebClient(user.URL_DOMAIN + user.URL_ORDER_SET, data, mHandler, 300);


                                    popupWindow.dismiss();

                                    user.setData("delivery_state","3");
                                    //배송출발 버튼 후 수정 불가하도록 레이아웃 클릭기능 삭제
                                    LinearLayout feed_option_real_size_view_wrap = (LinearLayout)findViewById(R.id.feed_option_real_size_view_wrap);
                                    LinearLayout feed_option_real_size_set_wrap = (LinearLayout)findViewById(R.id.feed_option_real_size_set_wrap);
                                    LinearLayout feed_list_real_size_view_wrap = (LinearLayout)findViewById(R.id.feed_list_real_size_view_wrap);
                                    LinearLayout feed_list_real_size_set_wrap = (LinearLayout)findViewById(R.id.feed_list_real_size_set_wrap);
                                    feed_list_real_size_view_wrap.setVisibility(View.VISIBLE);
                                    feed_list_real_size_set_wrap.setVisibility(View.GONE);
                                    feed_option_real_size_view_wrap.setVisibility(View.VISIBLE);
                                    feed_option_real_size_set_wrap.setVisibility(View.GONE);
                                    LinearLayout list_size = (LinearLayout)findViewById(R.id.feed_list_real_size_view_wrap);
                                    LinearLayout option_size = (LinearLayout)findViewById(R.id.feed_option_real_size_view_wrap);
                                    list_size.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    });
                                    option_size.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    });

                                }
                            });


                            (layout.findViewById(R.id.popup_confirm_delivery_cancel)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupWindow.dismiss();
                                }
                            });

                        } else {
                            user.Msg("도착예상시간을 먼저 입력해주세요.");
                        }

                    }
                }

                else {

                    //배송출발 수행기능
                    if (delivery_date_status) {
                        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(DetailActivity.this.LAYOUT_INFLATER_SERVICE);
                        layout = layoutInflater.inflate(R.layout.popup_confirm_delivery, null);

                        final PopupWindow popupWindow = new PopupWindow(DetailActivity.this);
                        popupWindow.setContentView(layout);
                        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                        popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
                        popupWindow.setFocusable(true);

                        popupWindow.setAnimationStyle(-1);
                        popupWindow.setBackgroundDrawable(new BitmapDrawable());
                        popupWindow.setOutsideTouchable(true);
                        //popupWindow.setFocusable(true);

                        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, -100);

                        (layout.findViewById(R.id.popup_confirm_delivery_ok)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String time = sdfNow.format(new Date(System.currentTimeMillis()));

                                JSONObject obj1 = new JSONObject();
                                try {
                                    obj1.put("SSID", user.getData("SSID"));
                                    obj1.put("UID", user.getData("UID"));
                                    obj1.put("PID", user.getData("PID"));
                                    obj1.put("STATUS", 3);
                                    obj1.put("time", time);
                                    obj1.put("type", "a");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String data = obj1.toString();
                                Log.d("DELIVERY_START", user.getData("UID") + " " + user.getData("PID") + " " + time);
                                client = new WebClient(user.URL_DOMAIN + user.URL_ORDER_SET, data, mHandler, 300);
                                popupWindow.dismiss();
                            }
                        });


                        (layout.findViewById(R.id.popup_confirm_delivery_cancel)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });

                    } else {
                        user.Msg("도착예상시간을 먼저 입력해주세요.");
                    }

                }

            }
        });

        delivery_complete_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:010-1234-1234"));
                startActivity(intent);
            }
        });

        delivery_complete_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         //       user.SignView(v);
                Sign();

            }
        });


        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("SSID", user.getData("SSID"));
            obj1.put("UID", user.getData("UID"));
            obj1.put("PID", user.getData("PID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = obj1.toString();
        client = new WebClient(user.URL_DOMAIN + user.URL_ORDER_DETAIL, data, mHandler, 100);


    }



    View layout_sign;
    LinearLayout sign_pad;

    PopupWindow popupWindowSign;
    String upload_msg;


    void Sign(){

        dv = new  DrawingView(DetailActivity.this);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(DetailActivity.this.LAYOUT_INFLATER_SERVICE);
        layout_sign = layoutInflater.inflate(R.layout.activity_main_sign, null);

        popupWindowSign = new PopupWindow(DetailActivity.this);
        popupWindowSign.setContentView(layout_sign);
        popupWindowSign.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindowSign.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindowSign.setFocusable(true);

        popupWindowSign.setAnimationStyle(-1);
        popupWindowSign.setBackgroundDrawable(new BitmapDrawable());
        popupWindowSign.setOutsideTouchable(true);
        //popupWindow.setFocusable(true);


        popupWindowSign.showAtLocation(layout_sign, Gravity.CENTER, 0, -100);

        sign_pad= ((LinearLayout)layout_sign.findViewById(R.id.sign_pad));

        sign_pad.addView(dv);
        (layout_sign.findViewById(R.id.sign_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowSign.dismiss();
            }
        });
        ((TextView)layout_sign.findViewById(R.id.sign_arrive_date)).setText(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));

        (layout_sign.findViewById(R.id.sign_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                Bitmap b = Bitmap.createBitmap( sign_pad.getWidth(), sign_pad.getHeight(), Bitmap.Config.RGB_565);
                if (b != null) {
                    try {
                          tempFile = File.createTempFile("photo", ".jpg", getCacheDir());
                        tempFile.setWritable(true, false);

                        Canvas c = new Canvas(b);
                        sign_pad.draw(c);

                        FileOutputStream fos = new FileOutputStream(tempFile);


                        if (fos != null) {
                            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();
                        }

                        Log.d("FILE", tempFile.length() + "");


                        if(tempFile.length() < 3000){
                            Toast.makeText(DetailActivity.this, "사인을 인식할수 없습니다. 사인을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }else{
                            new HttpAsyncTaskFile().execute(tempFile);
                        }

                    } catch (Exception e) {
                        Log.e("testSaveView", "Exception: " + e.toString());
                    }
                }
            }
        });

    }





    private class HttpAsyncTaskFile extends AsyncTask<File, Void, String> {
        @Override
        protected String doInBackground(File... files) {

            try {

                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdfNow.format(new Date(System.currentTimeMillis()));

                MultipartUtility multipart = null;
                multipart = new MultipartUtility(user.URL_DOMAIN+user.URL_SIGN_SET, "utf8");
                multipart.addFormField("UID",user.getData("UID"));
                multipart.addFormField("PID",user.getData("PID"));
                multipart.addFormField("time",time);
                multipart.addFormField("type","a");
                if(pay_type==0) {
                    multipart.addFormField("status", "4");
                }else if(pay_type==2){
                    multipart.addFormField("status", "9");
                }

                if(files[0].exists()) {
                    multipart.addFilePart("file_sign", new File(files[0].getAbsolutePath()));
                }
                List<String> response = multipart.finish();


                for (String line : response) {

                    Log.d("RSP", "Upload Files Response:::" + line);

                    try {

                        JSONObject json = new JSONObject(line);

                        if (json.getBoolean("status")) {
                            upload_msg=json.getString("msg");
                            mHandler.sendEmptyMessage(500);

                        } else {
                            upload_msg=json.getString("msg");
                            mHandler.sendEmptyMessage(600);
                        }


                    } catch (Exception e) {

                            e.printStackTrace();
                    }


                 }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(context, "Data Sent!", Toast.LENGTH_LONG).show();
            //final upload
        }
    }





    public class DrawingView extends View {

        public int width;
        public int height;
        private Bitmap mBitmap;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context = c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
            canvas.drawPath(circlePath, circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }

}
