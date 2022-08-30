package com.codezon.ludofantacy.helper;

import com.codezon.ludofantacy.remote.APIService;
import com.codezon.ludofantacy.remote.FCMRetrofitClient;

public class AppConstant {

    // Put your API URL
    public static final String API_URL = "http://ludofantasy.ratechnoworld.com/";

    // Put your purchase key & authorization key
    public static final String PURCHASE_KEY = "1234567890";
    public static final String AUTHORIZATION_KEY = "AAAALwiwBIs:APA91bGSaV4e4YZ2JOVeN7imRkwvXogL4c3FZUv0Ukl8CL3QwhUweLOpfZY5dHnOCHalkyri8xsU_uAu5Bj_LXxcpfMj3NebPSwhkCznxstbCorIFek1ZaQ2bMJiMeMtS5K7CgzYDYGD";

    // Set withdraw limit
    public static int MIN_WITHDRAW_LIMIT = 100;
    public static int MAX_WITHDRAW_LIMIT = 5000;

    // Set deposit limit
    public static int MIN_DEPOSIT_LIMIT = 50;
    public static int MAX_DEPOSIT_LIMIT = 5000;

    //TODO: No Need change below value here, it's come dynamic so change it from admin panel

    public static String SUPPORT_EMAIL = "xxxxx@gmail.com";
    public static String HOW_TO_PLAY = "https://google.com";

    // Put your PayTm production merchant id
    public static String M_ID = "XXXXXXXXXXXXXXXXXXX";

    // Put your PayU production Merchant id & key
    public static String MERCHANT_ID = "XXXXXXXXXXXX";
    public static String MERCHANT_KEY = "XXXXXXXXXXX";

    // Set default country code, currency code and sign
    public static String COUNTRY_CODE = "+91";
    public static String CURRENCY_CODE = "INR";
    public static String CURRENCY_SIGN = "₹";

    // Set default app configuration
    public static int REFER_PERCENTAGE = 1;
    public static int MAINTENANCE_MODE = 0;
    public static int MODE_OF_PAYMENT = 0;

    // Set game name and package name
    public static final String GAME_NAME = "Ludo King is Not Installed";
    public static final String PACKAGE_NAME = "com.ludo.king";

    // PayU Production API Details
    public static final long API_CONNECTION_TIMEOUT = 1201;
    public static final long API_READ_TIMEOUT = 901;
    public static final String SERVER_MAIN_FOLDER = "";

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "Global";

    // broadcast receiver intent filters
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // FCM URL
    private static final String FCM_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService() {
        return FCMRetrofitClient.getClient(FCM_URL).create(APIService.class);
    }

    public interface IntentExtras {
        String ACTION_CAMERA = "action-camera";
        String ACTION_GALLERY = "action-gallery";
    }

    public interface PicModes {
        String CAMERA = "Camera";
        String GALLERY = "Gallery";
    }
}
