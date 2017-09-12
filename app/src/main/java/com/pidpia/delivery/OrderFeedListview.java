package com.pidpia.delivery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by jenorain on 2017-02-04.
 */

public class OrderFeedListview {

    Context context;
    User user;
    ArrayList<DataOrder> order_data;
    OrderDataAdapter order_adpater;

    WebClient client;

    ListView mListView;

    WebClient client2;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                //데이터 가져오기
                case 100:
                    order_data.clear();
                    try {
                        JSONArray ja = new JSONArray(client.recvData);

                        Log.d("recv", client.recvData);
                        for (int i = 0; i < ja.length(); i++) {
                            final JSONObject data = ja.getJSONObject(i);
                            //  order_data.add(new DataOrder(data.getString("order_type"),data.getString("order_title"),data.getString("order_status"),data.getString("brand"),data.getString("order_name"),data.getString("order_address"),data.getString("order_reqeust_date"), data.getString("order_code")));
                            Log.d("recv" + i, data.getString("feed_idx"));
                            order_data.add(new DataOrder(
                                    data.getString("feed_idx")
                                    , data.getString("feed_title")
                                    , data.getString("feed_company_name")
                                    , data.getString("feed_name")
                                    , data.getString("feed_type")
                                    , data.getString("feed_section")
                                    , data.getString("feed_size")
                                    , data.getString("feed_real_size")
                                    , data.getString("feed_price")
                                    , data.getString("feed_price_exp")
                                    , data.getString("feed_option_status")
                                    , data.getString("feed_option_company_name")
                                    , data.getString("feed_option_feed_name")
                                    , data.getString("feed_option_feed_type")
                                    , data.getString("feed_option_feed_section")
                                    , data.getString("feed_option_size")
                                    , data.getString("feed_option_real_size")
                                    , data.getString("feed_option_price")
                                    , data.getString("feed_option_price_exp")
                                    , data.getString("feed_nutrient")
                            ));
                        }
                        //                      mListView.setAdapter(order_adpater);
//                        mListView.notify();
                        order_adpater.notifyDataSetChanged();

                        user.setListViewHeightBasedOnChildren(mListView);

                    } catch (Exception e) {
                        e.getStackTrace();
                    }


                    break;
                case 200:
                    try {
                        if (client2.recvData != null) {
                            JSONObject ja = new JSONObject(client2.recvData);
                            if (ja.getBoolean("status")) {
                                Refresh();
                            } else {
                                user.Msg(ja.getString("msg"));
                            }

                            Log.d("recv", client2.recvData);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

            }
        }
    };


    OrderFeedListview(Context _context, ListView listView, User _user) {
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
            obj1.put("PID", user.getData("PID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = obj1.toString();
        Log.d("LIST_EEE","상세 업데이트    " + data);
        client = new WebClient(user.URL_DOMAIN + user.URL_ORDER_FEED_LIST, data, mHandler, 100);


    }


    public void Refresh() {


        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("SSID", user.getData("SSID"));
            obj1.put("UID", user.getData("UID"));
            obj1.put("PID", user.getData("PID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = obj1.toString();
        client = new WebClient(user.URL_DOMAIN + user.URL_ORDER_FEED_LIST, data, mHandler, 100);


    }

    //스트링에 콤마넣기
    public String comma(String data){
        int result = Integer.parseInt(data);
        return new java.text.DecimalFormat("#,###").format(result);
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

                view = mInflater.inflate(R.layout.custom_order_feed_list, null);
            } else {
                view = v;
            }

            final DataOrder data = this.getItem(position);
            if (data != null) {

                final LinearLayout feed_list_wrap, feed_list_real_size_view_wrap, feed_list_real_size_set_wrap, feed_option_wrap, feed_option_real_size_view_wrap, feed_option_real_size_set_wrap;
                TextView feed_list_title, feed_list_company_name, feed_list_feed_name, feed_list_type, feed_list_section, feed_list_size, feed_list_real_size, feed_list_price, feed_list_price_exp, feed_option_title, feed_option_company_name, feed_option_feed_name, feed_option_type, feed_option_section, feed_option_size, feed_option_real_size, feed_option_price, feed_option_price_exp, feed_nutrient;
                final EditText feed_list_real_size_set_input, feed_option_real_size_set_input;
                Button feed_list_real_size_set_input_button, feed_option_real_size_set_input_button;

                feed_list_wrap = (LinearLayout) view.findViewById(R.id.feed_list_wrap);
                feed_list_real_size_view_wrap = (LinearLayout) view.findViewById(R.id.feed_list_real_size_view_wrap);
                feed_list_real_size_set_wrap = (LinearLayout) view.findViewById(R.id.feed_list_real_size_set_wrap);
                feed_option_wrap = (LinearLayout) view.findViewById(R.id.feed_option_wrap);
                feed_option_real_size_view_wrap = (LinearLayout) view.findViewById(R.id.feed_option_real_size_view_wrap);
                feed_option_real_size_set_wrap = (LinearLayout) view.findViewById(R.id.feed_option_real_size_set_wrap);

                feed_list_title = (TextView) view.findViewById(R.id.feed_list_title);
                feed_list_company_name = (TextView) view.findViewById(R.id.feed_list_company_name);
                feed_list_feed_name = (TextView) view.findViewById(R.id.feed_list_feed_name);
                feed_list_type = (TextView) view.findViewById(R.id.feed_list_type);
                feed_list_section = (TextView) view.findViewById(R.id.feed_list_section);
                feed_list_size = (TextView) view.findViewById(R.id.feed_list_size);
                feed_list_real_size = (TextView) view.findViewById(R.id.feed_list_real_size);
                feed_list_price = (TextView) view.findViewById(R.id.feed_list_price);
               // feed_list_price_exp = (TextView) view.findViewById(R.id.feed_list_price_exp);
                feed_option_title = (TextView) view.findViewById(R.id.feed_option_title);
                feed_option_company_name = (TextView) view.findViewById(R.id.feed_option_company_name);
                feed_option_feed_name = (TextView) view.findViewById(R.id.feed_option_feed_name);
                feed_option_type = (TextView) view.findViewById(R.id.feed_option_type);
                feed_option_section = (TextView) view.findViewById(R.id.feed_option_section);
                feed_option_size = (TextView) view.findViewById(R.id.feed_option_size);
                feed_option_real_size = (TextView) view.findViewById(R.id.feed_option_real_size);
                feed_option_price = (TextView) view.findViewById(R.id.feed_option_price);
               // feed_option_price_exp = (TextView) view.findViewById(R.id.feed_option_price_exp);
                feed_nutrient = (TextView) view.findViewById(R.id.feed_nutrient);

                feed_list_real_size_set_input = (EditText) view.findViewById(R.id.feed_list_real_size_set_input);
                feed_option_real_size_set_input = (EditText) view.findViewById(R.id.feed_option_real_size_set_input);

                feed_list_real_size_set_input_button = (Button) view.findViewById(R.id.feed_list_real_size_set_input_button);
                feed_option_real_size_set_input_button = (Button) view.findViewById(R.id.feed_option_real_size_set_input_button);
                TextView tv_order_real = (TextView)view.findViewById(R.id.tv_order_real);

                final LinearLayout list_size = (LinearLayout)view.findViewById(R.id.feed_list_real_size_view_wrap);
                final LinearLayout option_size = (LinearLayout)view.findViewById(R.id.feed_option_real_size_view_wrap);


                feed_list_title.setText(data.feed_title);
                feed_list_company_name.setText(data.feed_company_name);
                feed_list_feed_name.setText(data.feed_name);
                feed_list_type.setText(data.feed_type);

                Log.d("DATA DETAIL!!!!!!!!!",data.feed_type);
                feed_list_section.setText(data.feed_section);
                feed_list_size.setText(comma(data.feed_size));

                if(data.feed_type.equals("포대")){
                    feed_list_real_size.setText("포대");
                }else {
                    feed_list_real_size.setText((data.feed_real_size.length() > 0 && !data.feed_real_size.equals("0") ? " kg / " + comma(data.feed_real_size) + " kg" : " kg / 준비중"));

                    /*if (user.getData("delivery_state").toString().equals("2")) {
                        feed_list_real_size_view_wrap.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                feed_list_real_size_view_wrap.setVisibility(View.GONE);
                                feed_list_real_size_set_wrap.setVisibility(View.VISIBLE);
                            }
                        });
                    }*/
                }

                feed_list_price.setText(data.feed_price);
                //feed_list_price_exp.setText(data.feed_price_exp);

                feed_option_title.setText("배합사료 : ");
                feed_option_company_name.setText(data.feed_option_company_name);
                feed_option_feed_name.setText(data.feed_option_feed_name);
                feed_option_type.setText(data.feed_option_feed_type);
                feed_option_section.setText(data.feed_option_feed_section);
                if(data.feed_option_status.equals("true")) {
                    feed_option_size.setText(comma(data.feed_option_size));
                    feed_option_real_size.setText((data.feed_option_real_size.length() > 0 && !data.feed_option_real_size.equals("0") ? " kg / " + comma(data.feed_option_real_size) + " kg" : " kg / 준비중"));
                }
                feed_option_price.setText(data.feed_option_price);
                //feed_option_price_exp.setText(data.feed_option_price_exp);
                feed_nutrient.setText(data.feed_nutrient);


                feed_list_real_size_set_wrap.setVisibility(View.GONE);

                feed_list_real_size_set_input.setText(data.feed_real_size);
                feed_option_real_size_set_input.setText(data.feed_option_real_size);

                if(feed_list_type.equals("포대")){
                }



                feed_list_real_size_set_input_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String number = feed_list_real_size_set_input.getText().toString();

                        if (number.length() == 0) {
                            user.Msg("실공급량을 입력해주세요.");
                        } else {


                            JSONObject obj1 = new JSONObject();
                            try {
                                obj1.put("SSID", user.getData("SSID"));
                                obj1.put("UID", user.getData("UID"));
                                obj1.put("PID", user.getData("PID"));
                                obj1.put("FID", data.feed_idx);
                                obj1.put("NUM", number);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String data = obj1.toString();
                            client2 = new WebClient(user.URL_DOMAIN + user.URL_ORDER_FEED_SET, data, mHandler, 200);


                            list_size.setOnClickListener(new View.OnClickListener() {
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
                            });

                            feed_list_real_size_view_wrap.setVisibility(View.VISIBLE);
                            feed_list_real_size_set_wrap.setVisibility(View.GONE);

                        }
                    }
                });


                feed_option_real_size_set_wrap.setVisibility(View.GONE);

               /* if(user.getData("delivery_state").toString().equals("2")) {
                    feed_option_real_size_view_wrap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            feed_option_real_size_view_wrap.setVisibility(View.GONE);
                            feed_option_real_size_set_wrap.setVisibility(View.VISIBLE);
                        }
                    });
                }*/


                feed_option_real_size_set_input_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String number = feed_option_real_size_set_input.getText().toString();

                        if (number.length() == 0) {
                            user.Msg("실공급량을 입력해주세요.");
                        } else {


                            JSONObject obj1 = new JSONObject();
                            try {
                                obj1.put("SSID", user.getData("SSID"));
                                obj1.put("UID", user.getData("UID"));
                                obj1.put("PID", user.getData("PID"));
                                obj1.put("OPT", "true");
                                obj1.put("FID", data.feed_idx);
                                obj1.put("NUM", number);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String data = obj1.toString();
                            client2 = new WebClient(user.URL_DOMAIN + user.URL_ORDER_FEED_SET, data, mHandler, 200);

                            list_size.setOnClickListener(new View.OnClickListener() {
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
                            });

                            feed_option_real_size_view_wrap.setVisibility(View.VISIBLE);
                            feed_option_real_size_set_wrap.setVisibility(View.GONE);

                        }
                    }
                });


                if (data.feed_option_status.equals("true")) {
                    user.setBoolean("feed_option",true);
                    feed_option_wrap.setVisibility(View.VISIBLE);


                } else {
                    user.setBoolean("feed_option",false);
                    feed_option_wrap.setVisibility(View.GONE);
                }

            }
            return view;
        }
    }

    public class DataOrder {
        String feed_idx, feed_title, feed_company_name, feed_name, feed_type, feed_section, feed_size, feed_real_size, feed_price, feed_price_exp, feed_option_status, feed_option_company_name, feed_option_feed_name, feed_option_feed_type, feed_option_feed_section, feed_option_size, feed_option_real_size, feed_option_price, feed_option_price_exp, feed_nutrient;

        DataOrder(String feed_idx, String feed_title, String feed_company_name, String feed_name, String feed_type, String feed_section, String feed_size, String feed_real_size, String feed_price, String feed_price_exp, String feed_option_status, String feed_option_company_name, String feed_option_feed_name, String feed_option_feed_type, String feed_option_feed_section, String feed_option_size, String feed_option_real_size, String feed_option_price, String feed_option_price_exp, String feed_nutrient) {
            this.feed_idx = feed_idx;
            this.feed_title = feed_title;
            this.feed_company_name = feed_company_name;
            this.feed_name = feed_name;
            this.feed_type = feed_type;
            this.feed_section = feed_section;
            this.feed_size = feed_size;
            this.feed_real_size = feed_real_size;
            this.feed_price = feed_price;
            this.feed_price_exp = feed_price_exp;
            this.feed_option_status = feed_option_status;
            this.feed_option_company_name = feed_option_company_name;
            this.feed_option_feed_name = feed_option_feed_name;
            this.feed_option_feed_type = feed_option_feed_type;
            this.feed_option_feed_section = feed_option_feed_section;
            this.feed_option_size = feed_option_size;
            this.feed_option_real_size = feed_option_real_size;
            this.feed_option_price = feed_option_price;
            this.feed_option_price_exp = feed_option_price_exp;
            this.feed_nutrient = feed_nutrient;

        }
    }

}
