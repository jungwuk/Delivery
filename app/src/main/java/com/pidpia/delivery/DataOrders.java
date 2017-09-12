package com.pidpia.delivery;

/**
 * Created by jenorain on 2017-02-28.
 */
public class DataOrders {
    String order_type,order_title, order_status , brand ,order_name,order_address,order_reqeust_date,order_code;
    DataOrders(String order_type,String order_title, String order_status , String brand ,String order_name,String order_address,String order_reqeust_date,String order_code ){
        this.order_type=order_type;
        this.order_title=order_title;
        this.order_status =order_status;
        this.brand =brand;
        this.order_name=order_name;
        this.order_address=order_address;
        this.order_reqeust_date=order_reqeust_date;
        this.order_code =order_code;
    }
}
