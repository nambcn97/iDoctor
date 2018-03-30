package vn.edu.fpt.idoctor.common;

/**
 * Created by NamBC on 3/10/2018.
 */

public class AppConstant {
    public static final String API_KEY = "idoctor";
    public static final String API_SECRET = "secret";
//    public static final String API_HOST = "http://10.20.30.136:8080"; // phong c105
    public static final String API_HOST = "https://idoctor-prm391.herokuapp.com"; // server
//    public static final String API_HOST = "http://10.22.165.216:8080"; // DH-FPT
//    public static final String API_HOST = "http://192.168.43.186:8080"; // 4g MY-ASUS
//    public static final String API_HOST = "http://172.20.10.12:8080"; //FUHL - FREE wifi
    public static final String API_LOGIN = API_HOST + "/oauth/token";
    public static final String API_GET_SPECIALTY = API_HOST + "/mobile/specialty";

    public static final String DEBUG_TAG = "debug-idoctor";
    public static final String SHARED_PREF = "idoctor-prefs";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String DEVICE_ID = "deviceId";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String REMEMBER_ME = "rememberMe";
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2609;
    public static final int PLACE_PICKER_REQUEST = 1407;
    public static final String GOOGLE_MAPS_KEY = "AIzaSyCcg2G5sUUd9g-azj9PEEMfPm71pZrq8Sc";
    public static final String GOOGLE_SERVER_KEY = "AIzaSyBKt1-ZbhGq2PfuDIvbo_VjnLCuPUIIoNs"; //no restriction

    public static final String GOOGLE_MAPS_BASE_URL = "https://maps.googleapis.com";
}
