package vn.edu.fpt.idoctor.api.service;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import vn.edu.fpt.idoctor.api.response.FindDoctorResponse;
import vn.edu.fpt.idoctor.api.response.GetAllSpecialtyResponse;

/**
 * Created by NamBC on 3/27/2018.
 */

public interface SpecialtyService {
    @GET("/get/allSpecialty")
    public Call<GetAllSpecialtyResponse> getAllSpecialty();
}
