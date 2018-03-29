package vn.edu.fpt.idoctor.api.response;

import java.util.List;

import vn.edu.fpt.idoctor.api.model.SpecialtyBean;
import vn.edu.fpt.idoctor.api.model.SymptomBean;

/**
 * Created by NamBC on 3/27/2018.
 */

public class GetAllSymptomResponse extends BaseResponse {
    private List<SymptomBean> symptoms;
    public List<SymptomBean> getSymptoms() {
        return this.symptoms;
    }
    public void setSymptoms(List<SymptomBean> symptoms) {
        this.symptoms = symptoms;
    }
}
