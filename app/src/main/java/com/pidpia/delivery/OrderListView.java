package com.pidpia.delivery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okio.Timeout;

import static android.content.Context.MODE_PRIVATE;
import static android.net.sip.SipErrorCode.TIME_OUT;

/**
 * Created by jenorain on 2016-12-16.
 */

public class OrderListView {

    Context context;
    User user;
    ArrayList<DataOrder> order_data;
    OrderDataAdapter order_adpater;

    WebClient client;

    ListView mListView;
    ProgressDialog progress;


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 100:

                    order_data.clear();

                    try {
                        JSONArray ja = new JSONArray(client.recvData);

                        int count = 0;
                        for (int i = 0; i < ja.length(); i++) {
                            final JSONObject data = ja.getJSONObject(i);

                            //order status가 1일때는 미등록
                            if(data.getString("order_status").equals("1")){
                            }
                            //1이 아닐때는 등록
                            else {
                                order_data.add(new DataOrder(data.getString("order_type"), data.getString("order_title"), data.getString("order_status"), data.getString("brand"), data.getString("order_name"), data.getString("order_address"), data.getString("order_reqeust_date"), data.getString("order_code"), data.getString("order_status_msg"), data.getInt("order_pay_type")));

                                if (data.getString("order_status").equals("3")) {
                                    count++;
                                }
                            }
                        }
                        // count가 0보다 크면 working =1(service on) , 0이면 working=0 (service off)
                        if(count>0)
                        {
                            user.setData("working","1");

                            Log.d("Working_c", "배송중 = " + String.valueOf(count)+ "     "+user.getData("working"));
                        }else if(count==0)
                        {
                            user.setData("working","0");
                            Log.d("Working_c", "배송중 = " + String.valueOf(count)+ "     "+user.getData("working"));
                        }


                        //                      mListView.setAdapter(order_adpater);
//                        mListView.notify();

                        order_adpater.notifyDataSetChanged();
                    } catch (Exception e) {

                    }

                    break;


            }

        }
    };


    OrderListView(Context _context, ListView listView, User _user) {
        context = _context;
        user = _user;

        mListView = listView;
        order_data = new ArrayList<DataOrder>();
        order_adpater = new OrderDataAdapter(context, order_data);
        mListView.setAdapter(order_adpater);


        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("SSID", user.getData("SSID"));
            obj1.put("UID", user.getData("UID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = obj1.toString();
        client = new WebClient(user.URL_DOMAIN + user.URL_ORDER_LIST, data, mHandler, 100);


    }


    public void Update(){

        //로딩  프로그래스

        progress = new ProgressDialog(context);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("로딩중입니다..");

        progress.show();

        // 0.3초뒤 프로그래스 종료
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progress.dismiss();
            }
        },500);

        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("SSID", user.getData("SSID"));
            obj1.put("UID", user.getData("UID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = obj1.toString();
        client = new WebClient(user.URL_DOMAIN + user.URL_ORDER_LIST, data, mHandler, 100);
    }

    private class OrderDataAdapter extends ArrayAdapter<DataOrder> {
        private LayoutInflater mInflater;
        private Context context;

        public OrderDataAdapter(Context _context, ArrayList<DataOrder> object) {
            super(_context, 0, object);
            mInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            context = _context;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            View view = null;
            if (v == null) {

                view = mInflater.inflate(R.layout.custom_order_list, null);
            } else {
                view = v;
            }

            final DataOrder data = this.getItem(position);
            if (data != null) {

                LinearLayout list_title_bar;
                TextView list_title_bar_msg, list_title_bar_option, list_order_brand_name, list_order_order_name, list_order_order_address, list_order_order_request, list_order_icon_text;
                FrameLayout list_order_order_row;
                ImageView list_order_icon;

                //view 선언
                String PP = "#b17ed3";
                String GR = "#0BD438";
                String DG = "#666666";
                list_title_bar = (LinearLayout) view.findViewById(R.id.list_title_bar);

                list_title_bar_msg = (TextView) view.findViewById(R.id.list_title_bar_msg);
                list_title_bar_option = (TextView) view.findViewById(R.id.list_title_bar_option);
                list_order_brand_name = (TextView) view.findViewById(R.id.list_order_brand_name);
                list_order_order_name = (TextView) view.findViewById(R.id.list_order_order_name);
                list_order_order_address = (TextView) view.findViewById(R.id.list_order_order_address);
                list_order_order_request = (TextView) view.findViewById(R.id.list_order_order_request);
                list_order_icon_text = (TextView) view.findViewById(R.id.list_order_icon_text);

                list_order_order_row = (FrameLayout) view.findViewById(R.id.list_order_order_row);

                list_order_icon = (ImageView) view.findViewById(R.id.list_order_icon);
                list_order_brand_name.setText(data.brand);
                list_order_order_name.setText(data.order_name);
                list_order_order_address.setText(data.order_address);
                list_order_order_request.setText(data.order_reqeust_date);

                //진행중인 배송 갯수 알려주는 타이틀 바
                if (data.order_title.length() == 0) {
                    list_title_bar.setVisibility(View.GONE);
                } else {
                    list_title_bar.setVisibility(View.VISIBLE);
                    list_title_bar_msg.setText(data.order_title);
                }

                list_order_icon_text.setText("오늘");
                list_order_icon_text.setBackgroundResource(R.drawable.circle);
                list_order_icon_text.setText(data.order_status_msg);

                //리스트 배송상태 아이콘
                switch (Integer.parseInt(data.order_status)) {
                    default:
                        break;
                    case 1:

                        list_order_icon_text.setTextColor(Color.parseColor(PP));
                        list_order_icon_text.setBackgroundResource(R.drawable.circle_ready);
                        break;
                    case 2:

                        list_order_icon_text.setTextColor(Color.parseColor(PP));
                        list_order_icon_text.setBackgroundResource(R.drawable.circle_ready);
                        break;
                    case 3:

                        list_order_icon_text.setTextColor(Color.parseColor(GR));
                        list_order_icon_text.setBackgroundResource(R.drawable.circle_delivery);

                        break;
                    case 4:

                        list_order_icon_text.setTextColor(Color.parseColor(DG));
                        list_order_icon_text.setBackgroundResource(R.drawable.circle_end);
                        break;

                    case 9:

                        list_order_icon_text.setTextColor(Color.parseColor(DG));
                        list_order_icon_text.setBackgroundResource(R.drawable.circle_end);
                        break;


                }

                //아이템 선택 시 detailactivity 로 이동
                list_order_order_row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //PIDd = 사료 주문 넘버
                       // Toast.makeText(context, data.order_code, Toast.LENGTH_SHORT).show();
                        user.setData("PID", data.order_code);
                        Intent intent = new Intent((Activity) context, DetailActivity.class);
                        intent.putExtra("state",Integer.parseInt(data.order_status));
                        intent.putExtra("order_pay_type",data.order_pay_type);
//                        context.startActivity(intent);

                        ((Activity) context).startActivityForResult(intent,1000);
                    }
                });

            }
            return view;
        }
    }

    public class DataOrder {
        String order_type, order_title, order_status, brand, order_name, order_address, order_reqeust_date, order_code,order_status_msg;
        int order_pay_type;

        DataOrder(String order_type, String order_title, String order_status, String brand, String order_name, String order_address, String order_reqeust_date, String order_code, String order_status_msg, int order_pay_type) {
            this.order_type = order_type;
            this.order_title = order_title;
            this.order_status = order_status;
            this.brand = brand;
            this.order_name = order_name;
            this.order_address = order_address;
            this.order_reqeust_date = order_reqeust_date;
            this.order_code = order_code;
            this.order_status_msg= order_status_msg;
            this.order_pay_type = order_pay_type;
        }
    }
}
