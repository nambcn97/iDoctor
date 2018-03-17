package vn.edu.fpt.idoctor.api.service;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import vn.edu.fpt.idoctor.api.response.BaseResponse;
import vn.edu.fpt.idoctor.api.response.LoginResponse;

/**
 * Created by NamBC on 3/16/2018.
 */

public interface NotificationService {
    @POST("/mobile/registerDeviceId")
    public Call<BaseResponse> registerDeviceId(@Header("Authorization") String auth, @Body HashMap<String, String> json);

}
