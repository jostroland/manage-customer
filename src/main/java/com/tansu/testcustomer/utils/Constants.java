package com.tansu.testcustomer.utils;

public class Constants {
    public static final String APP_ROOT = "api";

    public final static  String CREATE_CUSTOMER_ENDPOINT = APP_ROOT + "/customer/save";
    public static final  String UPDATE_CUSTOMER_ENDPOINT = APP_ROOT+ "/customer/update/{id}";
    public final static  String FIND_BY_ID_CUSTOMER_ENDPOINT = APP_ROOT+ "/customer/findById/{id}";
    public final static  String FIND_ALL_CUSTOMER_ENDPOINT = APP_ROOT+ "/customer/all";
    public final static  String FIND_ALL_CUSTOMER_PAGE_ENDPOINT = APP_ROOT+ "/customer/all/page";
    public final static  String DELETE_BY_ID_CUSTOMER_ENDPOINT = APP_ROOT+ "/customer/delete/{id}";
}
