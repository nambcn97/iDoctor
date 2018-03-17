package vn.edu.fpt.idoctor.api.service;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import vn.edu.fpt.idoctor.api.common.AppConstant;
import vn.edu.fpt.idoctor.api.response.PlaceSearchResponse;
import vn.edu.fpt.idoctor.api.response.FindDoctorResponse;
import vn.edu.fpt.idoctor.api.response.PlaceDetailsResponses;

/**
 * Created by NamBC on 3/16/2018.
 */

public interface SearchService {
    @GET("/maps/api/place/nearbysearch/json?type=health|pharmacy|doctor|dentist|hospital&radius=3000&sensor=true&key="+ AppConstant.GOOGLE_SERVER_KEY)
    public Call<PlaceSearchResponse> searchHospital(@Query("location") String location);


    @GET("maps/api/place/details/json?key="+ AppConstant.GOOGLE_SERVER_KEY)
    public Call<PlaceDetailsResponses> getPlaceDetail(@Query("placeid") String placeId);

    @POST("/mobile/doctor")
    public Call<FindDoctorResponse> searchDoctor(@Header("Authorization") String auth, @Body HashMap<String, String> json);

}
