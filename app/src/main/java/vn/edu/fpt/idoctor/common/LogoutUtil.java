package vn.edu.fpt.idoctor.common;

import android.content.SharedPreferences;

/**
 * Created by NamBC on 3/26/2018.
 */

public class LogoutUtil {

    public static void logout(SharedPreferences sharedPreferences){
        if (sharedPreferences!=null){
            sharedPreferences.edit().remove(AppConstant.ACCESS_TOKEN).remove(AppConstant.REFRESH_TOKEN).commit();
        }
    }
}
