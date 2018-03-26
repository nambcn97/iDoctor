package vn.edu.fpt.idoctor.api.service;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import vn.edu.fpt.idoctor.api.response.FindDoctorResponse;
import vn.edu.fpt.idoctor.api.response.SendEmergencyResponse;

/**
 * Created by NamBC on 3/25/2018.
 */

public interface EmergencyService {

    @POST("/get/sendEmergency")
    public Call<SendEmergencyResponse> sendEmergencyUser(@Header("Authorization") String auth, @Body HashMap<String, Object> json);

    @POST("/get/sendEmergency")
    public Call<SendEmergencyResponse> sendEmergencyAnonymous(@Body HashMap<String, Object> json);
}
