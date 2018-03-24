package vn.edu.fpt.idoctor.common;


import android.content.SharedPreferences;
import android.util.Log;

import static vn.edu.fpt.idoctor.common.AppConstant.*;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by NamBC on 3/15/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String instanceId = FirebaseInstanceId.getInstance().getToken();
        Log.d(AppConstant.DEBUG_TAG, "Device ID: " + instanceId);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        sharedPreferences.edit().putString(DEVICE_ID, instanceId).commit();
    }
}
