package com.tansu.testcustomer.utils;


import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String APP_ROOT = "/api/v1";

    public final static  String CREATE_CUSTOMER_ENDPOINT        = APP_ROOT + "/customer/save";
    public final static  String UPDATE_CUSTOMER_ENDPOINT        = APP_ROOT+ "/customer/update/{id}";
    public final static  String FIND_BY_ID_CUSTOMER_ENDPOINT    = APP_ROOT+ "/customer/findById/{id}";
    public final static  String FIND_ALL_CUSTOMER_ENDPOINT      = APP_ROOT+ "/customer/all";
    public final static  String FIND_ALL_CUSTOMER_PAGE_ENDPOINT = APP_ROOT+ "/customer/all/page";
    public final static  String DELETE_BY_ID_CUSTOMER_ENDPOINT  = APP_ROOT+ "/customer/delete/{id}";

    public final static  String CREATE_USER_ENDPOINT        = APP_ROOT + "/user/save";
    public final static  String UPDATE_USER_ENDPOINT        = APP_ROOT+ "/user/update/{id}";
    public final static  String FIND_BY_ID_USER_ENDPOINT    = APP_ROOT+ "/user/findById/{id}";
    public final static  String FIND_ALL_USER_ENDPOINT      = APP_ROOT+ "/user/all";
    public final static  String FIND_ALL_USER_PAGE_ENDPOINT = APP_ROOT+ "/user/all/page";
    public final static  String DELETE_BY_ID_USER_ENDPOINT  = APP_ROOT+ "/user/delete/{id}";

    public final static String DEV = "dev";
    public final static String TEST = "test";
    public final static String PROD = "prod";
}
