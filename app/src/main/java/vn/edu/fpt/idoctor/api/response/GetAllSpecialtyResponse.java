package vn.edu.fpt.idoctor.api.response;

import java.util.List;

import vn.edu.fpt.idoctor.api.model.SpecialtyBean;

/**
 * Created by NamBC on 3/27/2018.
 */

public class GetAllSpecialtyResponse extends BaseResponse {
    private List<SpecialtyBean> specialties;
    public List<SpecialtyBean> getSpecialties() {
        return this.specialties;
    }

    public void setSpecialties(List<SpecialtyBean> specialties) {
        this.specialties = specialties;
    }
}
