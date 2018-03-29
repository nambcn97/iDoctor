package vn.edu.fpt.idoctor.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.api.model.User;
import vn.edu.fpt.idoctor.api.response.PlaceSearchResponse;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListDoctorTabFragment extends Fragment {

    private List<PlaceSearchResponse.Result> listPlace;
    private List<User> listDoctor;
    public ListDoctorTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_doctor_tab, container, false);
    }

}
