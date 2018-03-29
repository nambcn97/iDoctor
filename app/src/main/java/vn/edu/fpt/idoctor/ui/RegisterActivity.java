package vn.edu.fpt.idoctor.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import retrofit2.Call;
import retrofit2.Response;
import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.api.model.SpecialtyBean;
import vn.edu.fpt.idoctor.api.response.BaseResponse;
import vn.edu.fpt.idoctor.api.response.GetAllSpecialtyResponse;
import vn.edu.fpt.idoctor.api.service.AuthService;
import vn.edu.fpt.idoctor.api.service.SpecialtyService;

import static vn.edu.fpt.idoctor.common.AppConstant.*;

import vn.edu.fpt.idoctor.common.ServiceGenerator;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvReturn;
    private Button btnSignup;
    private EditText edtUsername, edtPassword, edtRepassword, edtFullname, edtAddress, edtPhone, edtWorkAddress;
    private RadioButton rbMale, rbFemale;
    private MaterialSpinner spinnerSpecialty;
    private CheckBox cbDoctor;
    private List<SpecialtyBean> specialties;
    private MaterialDialog loadingDialog;
    private String username, password, rePassword, fullName, address, phone, workAddress;
    private Boolean gender, isDoctor;
    private Long specialtyId, roleId;
    private Double lat, lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        tvReturn = findViewById(R.id.tvReturn);
        btnSignup = findViewById(R.id.btnSignup);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtRepassword = findViewById(R.id.edtRepassword);
        edtFullname = findViewById(R.id.edtFullname);
        edtAddress = findViewById(R.id.edtAddress);
        edtAddress.setOnClickListener(this);
        edtPhone = findViewById(R.id.edtPhone);

        edtWorkAddress = findViewById(R.id.edtWorkAddress);
        edtWorkAddress.setEnabled(false);
        edtWorkAddress.setFocusableInTouchMode(false);

        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        spinnerSpecialty = findViewById(R.id.spinnerSpecialty);

        spinnerSpecialty.setEnabled(false);
        spinnerSpecialty.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        cbDoctor = findViewById(R.id.cbDoctor);

        tvReturn.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        cbDoctor.setOnClickListener(this);

        specialties = new ArrayList<>();
        GetDataTask getDataTask = new GetDataTask();
        getDataTask.execute();

//        lvSpecialty.setOnItemClickListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvReturn:
                onBackPressed();
                break;
            case R.id.btnSignup:
                if (validate()) {
                    RegisterTask registerTask = new RegisterTask();
                    registerTask.execute();
                }
                break;
            case R.id.edtAddress:

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.cbDoctor:
                if (cbDoctor.isChecked()) {
                    spinnerSpecialty.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    spinnerSpecialty.setEnabled(true);
                    spinnerSpecialty.setClickable(true);
                    edtWorkAddress.setEnabled(true);
                    edtWorkAddress.setFocusableInTouchMode(true);

                } else {
                    spinnerSpecialty.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    spinnerSpecialty.setEnabled(false);
                    spinnerSpecialty.setClickable(false);
                    edtWorkAddress.setEnabled(false);
                    edtWorkAddress.setFocusableInTouchMode(false);

                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;
                Log.d(DEBUG_TAG,  "LatLng: " + lat + ", " + lng);
                edtAddress.setText(place.getAddress().toString());
            } else {
                edtAddress.setText("");
            }
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

    private void init() {
        username = edtUsername.getText().toString().trim().toLowerCase();
        password = edtPassword.getText().toString().trim();
        rePassword = edtRepassword.getText().toString().trim();
        fullName = edtFullname.getText().toString().trim();
        address = edtAddress.getText().toString().trim();

        phone = edtPhone.getText().toString().trim();
        gender = rbMale.isChecked();
        roleId = 1L;
        if (isDoctor = cbDoctor.isChecked()) {
            SpecialtyBean specialtyBean = specialties.get(spinnerSpecialty.getSelectedIndex());
            specialtyId = specialtyBean.getId();
            workAddress = edtWorkAddress.getText().toString().trim();
            roleId = 2L;
        }
    }

    private Boolean validate() {
        init();
        if (username.isEmpty()) {
            edtUsername.setError("Please input username!!!");
            return false;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Invalid!!!");
            return false;
        }
        if (!rePassword.equals(password)) {
            edtRepassword.setError("Not match!!!");
        }
        if (fullName.isEmpty()) {
            edtFullname.setError("Please input your full name");
        }
        if (address.isEmpty()) {
            edtAddress.setError("Please input your address");
        }
        if (phone.isEmpty() || !phone.matches("[0-9]+")) {
            edtPhone.setError("Invalid phone number");
        }
        return true;
    }

    private boolean signUp() {
        HashMap<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("fullname", fullName);
        body.put("password", password);
        body.put("address", address);
        body.put("phone", phone);
        body.put("gender", gender);
        body.put("roleId", roleId);
        body.put("specialtyId", specialtyId);
        body.put("workAddress", workAddress);
        body.put("lat", lat);
        body.put("lng", lng);
        AuthService authService = ServiceGenerator.createService(AuthService.class, API_HOST);
        Call<BaseResponse> call = authService.signUp(body);
        try {
            Response<BaseResponse> response = call.execute();
            if (response.isSuccessful() && response.body().getResultCode() == 200){
                return  true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return  true;
    }

    private class RegisterTask extends AsyncTask<Void, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return signUp();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                new MaterialDialog.Builder(RegisterActivity.this).title("Đăng ký").content("Đăng ký tài khoản thành công").show();
            }
            super.onPostExecute(aBoolean);
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

            loadingDialog = new MaterialDialog.Builder(RegisterActivity.this)
                    .title("In Progress")
                    .content("Please wait")
                    .progress(true, 0)
                    .show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getSpecialty();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter adapter = new ArrayAdapter<SpecialtyBean>(getApplicationContext(), R.layout.specialty_item, R.id.specialty_item, specialties);
            spinnerSpecialty.setAdapter(adapter);
            loadingDialog.dismiss();
        }
    }
}
