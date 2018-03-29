package vn.edu.fpt.idoctor.api.service;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import vn.edu.fpt.idoctor.api.response.BaseResponse;
import vn.edu.fpt.idoctor.api.response.LoginResponse;

/**
 * Created by NamBC on 3/12/2018.
 */

public interface AuthService {
    @POST("/oauth/token")
    public Call<LoginResponse> login(@Header("Authorization") String auth, @Body HashMap<String, String> json);

    @POST("/auth/signUp")
    public Call<BaseResponse> signUp(@Body HashMap<String, Object> json);

    @GET("/auth/logout")
    public Call<BaseResponse> logout(@Header("Authorization") String auth);

}
