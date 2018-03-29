package vn.edu.fpt.idoctor.api.service;

import retrofit2.Call;
import retrofit2.http.GET;
import vn.edu.fpt.idoctor.api.response.GetAllSpecialtyResponse;
import vn.edu.fpt.idoctor.api.response.GetAllSymptomResponse;

/**
 * Created by NamBC on 3/28/2018.
 */

public interface SymptomService {
    @GET("/get/allSymptom")
    public Call<GetAllSymptomResponse> getAllSymptom();
}
