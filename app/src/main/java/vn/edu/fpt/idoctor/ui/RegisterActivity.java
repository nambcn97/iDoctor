package vn.edu.fpt.idoctor.ui;

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

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import retrofit2.Call;
import retrofit2.Response;
import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.api.model.SpecialtyBean;
import vn.edu.fpt.idoctor.api.response.GetAllSpecialtyResponse;
import vn.edu.fpt.idoctor.api.service.SpecialtyService;
import static vn.edu.fpt.idoctor.common.AppConstant.*;
import vn.edu.fpt.idoctor.common.ServiceGenerator;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener  {
    private TextView tvReturn;
    private Button btnRegister;
    private EditText edtUsername, edtPassword, edtRepassword, edtFullname, edtAddress, edtPhone, edtWorkAddress;
    private RadioButton rbMale, rbFemale;
    private Spinner spinnerSpecialty;
    private CheckBox cbDoctor;
    private List<SpecialtyBean> specialties;
    private MaterialDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        tvReturn = findViewById(R.id.tvReturn);
        btnRegister = findViewById(R.id.btnRegister);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtRepassword = findViewById(R.id.edtRepassword);
        edtFullname = findViewById(R.id.edtFullname);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhone = findViewById(R.id.edtPhone);

        edtWorkAddress = findViewById(R.id.edtWorkAddress);
        edtWorkAddress.setEnabled(false);
        edtWorkAddress.setFocusable(false);

        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        spinnerSpecialty = findViewById(R.id.spinnerSpecialty);

        spinnerSpecialty.setEnabled(false);
        spinnerSpecialty.setBackground(getResources().getDrawable(R.drawable.bg_spinner_disable));
        cbDoctor = findViewById(R.id.cbDoctor);

        tvReturn.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        cbDoctor.setOnClickListener(this);

        specialties = new ArrayList<>();
        GetDataTask getDataTask = new GetDataTask();
        getDataTask.execute();

//        lvSpecialty.setOnItemClickListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvReturn:
                onBackPressed();
                break;
            case R.id.btnRegister:
                RegisterTask registerTask = new RegisterTask();
                registerTask.execute();
                break;
            case R.id.cbDoctor:
                if (cbDoctor.isChecked()){
                    spinnerSpecialty.setBackground(getResources().getDrawable(R.drawable.bg_spinner));
                    spinnerSpecialty.setEnabled(true);
                    spinnerSpecialty.setClickable(true);
                    edtWorkAddress.setEnabled(true);
                    edtWorkAddress.setFocusable(true);

                } else {
                    spinnerSpecialty.setBackground(getResources().getDrawable(R.drawable.bg_spinner_disable));
                    spinnerSpecialty.setEnabled(false);
                    spinnerSpecialty.setClickable(false);
                    edtWorkAddress.setEnabled(false);
                    edtWorkAddress.setFocusable(false);

                }
                break;
        }
    }

    private void getSpecialty(){
        SpecialtyService specialtyService = ServiceGenerator.createService(SpecialtyService.class, API_HOST);
        Call<GetAllSpecialtyResponse> call = specialtyService.getAllSpecialty();
        try {
            Response<GetAllSpecialtyResponse> response = call.execute();
            Log.d(DEBUG_TAG, "get all specialty: " + response.code());
            if (response.isSuccessful() && response.body().getResultCode() == 200){
                specialties = response.body().getSpecialties();
            } else {
                Log.d(DEBUG_TAG, "get all specialty: " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "get all specialty: " + e.getMessage());
        }

    }

    private class RegisterTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            return  null;
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = new MaterialDialog.Builder(RegisterActivity.this)
                    .title("In Progress")
                    .content("Please wait")
                    .progress(true, 0)
                    .show();
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
