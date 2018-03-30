package vn.edu.fpt.idoctor.api.service;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import vn.edu.fpt.idoctor.api.response.BaseResponse;
import vn.edu.fpt.idoctor.api.response.LoginResponse;
import vn.edu.fpt.idoctor.api.response.MyInfoResponse;

/**
 * Created by NamBC on 3/29/2018.
 */

public interface UserService {
    @POST("/mobile/updateData")
    public Call<BaseResponse> updateData(@Header("Authorization") String auth, @Body HashMap<String, Object> json);

    @GET("/mobile/myInfo")
    public Call<MyInfoResponse> getMyInfo(@Header("Authorization") String auth);

    @POST("/mobile/editMyInfo")
    public Call<BaseResponse> editMyInfo(@Header("Authorization") String auth, @Body HashMap<String, Object> json);
}
