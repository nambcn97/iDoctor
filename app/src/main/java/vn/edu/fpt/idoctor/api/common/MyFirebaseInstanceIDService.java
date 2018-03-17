package vn.edu.fpt.idoctor.api.common;


import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by NamBC on 3/15/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String instanceId = FirebaseInstanceId.getInstance().getToken();
        Log.d(AppConstant.DEBUG_TAG, "Refreshed token: " + instanceId);
        SharedPreferences sharedPreferences = getSharedPreferences("idoctor-prefs", MODE_PRIVATE);
        sharedPreferences.edit().putString("instanceId", instanceId).commit();
    }
}
