package vn.edu.fpt.idoctor.ui.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.api.model.SymptomBean;
import vn.edu.fpt.idoctor.api.response.BaseResponse;
import vn.edu.fpt.idoctor.api.response.GetAllSymptomResponse;
import vn.edu.fpt.idoctor.api.service.SymptomService;

import static vn.edu.fpt.idoctor.common.AppConstant.*;

import vn.edu.fpt.idoctor.common.ServiceGenerator;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFragment extends Fragment implements View.OnClickListener {

    private MaterialSpinner spnSymptom, spnAges;
    private Button btnFind;
    private EditText edtOther;
    private String others;
    private Long symptomId;
    private List<SymptomBean> symptoms;

    public FindFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, container, false);
//        spnAges = view.findViewById(R.id.spnAges);
        spnSymptom = view.findViewById(R.id.spnSymptom);
        btnFind = view.findViewById(R.id.btnFind);
        edtOther = view.findViewById(R.id.edtOtherSymptom);
        btnFind.setOnClickListener(this);
        GetDataTask getDataTask = new GetDataTask();
        getDataTask.execute();
        // Inflate the layout for this fragment
        return view;
    }

    private void init() {
        others = edtOther.getText().toString();
        symptomId = symptoms.get(spnSymptom.getSelectedIndex()).getId();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFind:
                init();
                HomeFragment homeFragment = new HomeFragment();
                Bundle bundle = new Bundle();
                bundle.putString("find_others", others);
                if (symptomId != null) bundle.putLong("find_symptomId", symptomId);
                homeFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, homeFragment).commit();
                break;
        }
    }

    private void getData() {
        SymptomService symptomService = ServiceGenerator.createService(SymptomService.class, API_HOST);
        Call<GetAllSymptomResponse> call = symptomService.getAllSymptom();
        try {
            Response<GetAllSymptomResponse> response = call.execute();
            if (response.isSuccessful() && response.body().getResultCode() == 200) {
                symptoms = response.body().getSymptoms();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            getData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter<SymptomBean> adapter = new ArrayAdapter<SymptomBean>(getContext(), R.layout.item_list, R.id.item_list, symptoms);
            spnSymptom.setAdapter(adapter);
        }
    }
}
