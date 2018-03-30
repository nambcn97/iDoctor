package vn.edu.fpt.idoctor.ui.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.api.model.SpecialtyBean;
import vn.edu.fpt.idoctor.api.model.User;
import vn.edu.fpt.idoctor.api.response.BaseResponse;
import vn.edu.fpt.idoctor.api.response.GetAllSpecialtyResponse;
import vn.edu.fpt.idoctor.api.response.MyInfoResponse;
import vn.edu.fpt.idoctor.api.service.SpecialtyService;
import vn.edu.fpt.idoctor.api.service.UserService;

import static vn.edu.fpt.idoctor.common.AppConstant.*;

import vn.edu.fpt.idoctor.common.ServiceGenerator;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment implements View.OnClickListener {
    private EditText edtFullName, edtPhone, edtAddress, edtWorkAddress;
    private Button btnEdit, btnSave;
    private Spinner spnGender, spnSpecialty;
    private TextView tvWorkAddress, tvSpecialty, tvName;
    private String accessToken;
    private User user;
    private MaterialDialog loadingDialog;
    private List<SpecialtyBean> specialties;
    private String[] genders = new String[]{"Nam", "Nữ"};
    private Double lat, lng;
    public MyAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accessToken = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE).getString(ACCESS_TOKEN, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        edtAddress = view.findViewById(R.id.edtAddress);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtFullName = view.findViewById(R.id.edtFullName);
        edtWorkAddress = view.findViewById(R.id.edtWorkAddress);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnSave = view.findViewById(R.id.btnSave);
        spnGender = view.findViewById(R.id.spnGender);
        spnSpecialty = view.findViewById(R.id.spnSpecialty);
        tvSpecialty = view.findViewById(R.id.tvSpecialty);
        tvWorkAddress = view.findViewById(R.id.tvWorkAddress);
        tvName = view.findViewById(R.id.tvName);

        enableViewMode();
        spnSpecialty.setVisibility(View.INVISIBLE);
        edtWorkAddress.setVisibility(View.INVISIBLE);
        tvSpecialty.setVisibility(View.INVISIBLE);
        tvWorkAddress.setVisibility(View.INVISIBLE);

        btnSave.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        edtAddress.setOnClickListener(this);

        GetInfoTask getInfoTask = new GetInfoTask();
        getInfoTask.execute();
        return view;
    }

    private void enableViewMode() {
        edtAddress.setFocusableInTouchMode(false);
        edtFullName.setFocusableInTouchMode(false);
        edtPhone.setFocusableInTouchMode(false);
        edtWorkAddress.setFocusableInTouchMode(false);
        spnGender.setEnabled(false);
        spnSpecialty.setEnabled(false);
        btnEdit.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.INVISIBLE);
    }

    private void enableEditMode() {
        edtAddress.setFocusableInTouchMode(true);
        edtFullName.setFocusableInTouchMode(true);
        edtPhone.setFocusableInTouchMode(true);
        edtWorkAddress.setFocusableInTouchMode(true);
        spnGender.setEnabled(true);
        spnSpecialty.setEnabled(true);
        btnEdit.setVisibility(View.INVISIBLE);
        btnSave.setVisibility(View.VISIBLE);
    }

    private void getMyInfo() {
        UserService userService = ServiceGenerator.createService(UserService.class, API_HOST);
        String header = String.format("Bearer %s", accessToken).trim();
        try {
            Response<MyInfoResponse> response = userService.getMyInfo(header).execute();
            if (response.isSuccessful() && response.body().getResultCode() == 200) {
                user = response.body().getMyInfo();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSpecialty() {
        SpecialtyService specialtyService = ServiceGenerator.createService(SpecialtyService.class, API_HOST);
        Call<GetAllSpecialtyResponse> call = specialtyService.getAllSpecialty();
        try {
            Response<GetAllSpecialtyResponse> response = call.execute();
            Log.d(DEBUG_TAG, "get all specialty: " + response.code());
            if (response.isSuccessful() && response.body().getResultCode() == 200) {
                specialties = response.body().getSpecialties();
            } else {
                Log.d(DEBUG_TAG, "get all specialty: " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "get all specialty: " + e.getMessage());
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEdit:
                enableEditMode();
                break;
            case R.id.btnSave:
                SaveInfoTask saveInfoTask = new SaveInfoTask();
                saveInfoTask.execute();
                enableViewMode();
                break;
            case R.id.edtAddress:
                if (!edtAddress.isFocusableInTouchMode()) break;
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;
                Log.d(DEBUG_TAG,  "LatLng: " + lat + ", " + lng);
                edtAddress.setText(place.getAddress().toString());
            }
        }
    }
    private class GetInfoTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = new MaterialDialog.Builder(getContext())
                    .title("In Progress")
                    .content("Please wait")
                    .progress(true, 0)
                    .show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getSpecialty();
            getMyInfo();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter adapter = new ArrayAdapter<SpecialtyBean>(getContext(), R.layout.specialty_item, R.id.specialty_item, specialties);
            spnSpecialty.setAdapter(adapter);
            adapter = new ArrayAdapter<String>(getContext(), R.layout.specialty_item, R.id.specialty_item, genders);
            spnGender.setAdapter(adapter);

            tvName.setText(user.getFullName());
            // fill value to edit text
            edtAddress.setText(user.getAddress());
            edtFullName.setText(user.getFullName());
            edtPhone.setText(user.getPhone());
            if (user.getGender()) spnGender.setSelection(0);
            else spnGender.setSelection(1);

            if (user.getRole().equals("Doctor")) {
                edtWorkAddress.setText(user.getWorkAddress());
                for(int i=0;i<specialties.size();i++){
                    if (user.getSpecialty().equals(specialties.get(i).getName())){
                        spnSpecialty.setSelection(i);
                        break;
                    }
                }
                edtWorkAddress.setVisibility(View.VISIBLE);
                spnSpecialty.setVisibility(View.VISIBLE);
                tvSpecialty.setVisibility(View.VISIBLE);
                tvWorkAddress.setVisibility(View.VISIBLE);
            }

            loadingDialog.dismiss();
        }
    }

    private Boolean saveMyInfo(){
        HashMap<String, Object> body = new HashMap<>();
        body.put("fullName", edtFullName.getText().toString());
        body.put("phone", edtPhone.getText().toString());
        body.put("address", edtAddress.getText().toString());
        if (lat==null || lng == null){
            lat = user.getLat();
            lng = user.getLng();
        }
        body.put("lat", lat);
        body.put("lng", lng);
        body.put("gender", spnGender.getSelectedItemId()==0);
        if (user.getRole().equals("Doctor")){
            body.put("workAddress", edtWorkAddress.getText().toString());
            body.put("specialtyId", ((SpecialtyBean)spnSpecialty.getSelectedItem()).getId());
        }

        UserService userService = ServiceGenerator.createService(UserService.class, API_HOST);
        String header = String.format("Bearer %s", accessToken).trim();
        Call<BaseResponse> call = userService.editMyInfo(header, body);
        try {
            Response<BaseResponse> response = call.execute();
            if (response.isSuccessful() && response.body().getResultCode() == 200) {
                Log.d(DEBUG_TAG, "Save info success");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public class SaveInfoTask extends  AsyncTask<Void,Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            return saveMyInfo();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Thông tin cá nhân")
                        .setMessage("Thông tin của bạn đã được cập nhật thành công!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                //refresh
//                                getFragmentManager().beginTransaction().detach(getParentFragment()).attach(getParentFragment()).commit();
                            }
                        }).show();

            }
        }
    }

}
