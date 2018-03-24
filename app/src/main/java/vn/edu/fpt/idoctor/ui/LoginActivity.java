package vn.edu.fpt.idoctor.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import static vn.edu.fpt.idoctor.common.AppConstant.*;
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;

import java.util.HashMap;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.common.APIError;
import vn.edu.fpt.idoctor.common.ErrorUtils;
import vn.edu.fpt.idoctor.api.response.LoginResponse;
import vn.edu.fpt.idoctor.api.service.AuthService;
import vn.edu.fpt.idoctor.common.GPSTracker;
import vn.edu.fpt.idoctor.common.ServiceGenerator;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin;
    private EditText edtUsername, edtPassword;
    private String username, password;
    private CheckBox cbRememberMe;
    private Boolean rememberMe;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        btnLogin = findViewById(R.id.btnLogin);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        cbRememberMe = findViewById(R.id.cbRemember);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        rememberMe = sharedPreferences.getBoolean(REMEMBER_ME, false);
        if (rememberMe) {
            edtUsername.setText(sharedPreferences.getString(USERNAME, ""));
            edtPassword.setText(sharedPreferences.getString(PASSWORD, ""));
            cbRememberMe.setChecked(true);
        }
        window = this.getWindow();
        cbRememberMe.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    private Boolean validate() {
        if (username.isEmpty()) {
            edtUsername.setError("Please input username!!!");
            return false;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Invalid!!!");
            return false;
        }
        return true;
    }

    private void init() {
        username = edtUsername.getText().toString().trim().toLowerCase();
        password = edtPassword.getText().toString().trim();

    }

    private Boolean getMyLatLng() {
        GPSTracker gps = new GPSTracker(LoginActivity.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            return true;
        } else {
            // can't get location// GPS or Network is not enabled// Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        init();
        switch (view.getId()) {
            case R.id.btnLogin:
                if (getMyLatLng() && validate()) {
                    LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
                    loginAsyncTask.execute(username, password);
                }
                break;
            case R.id.cbRemember:
                if (cbRememberMe.isChecked()) {
                    sharedPreferences.edit()
                            .putBoolean(REMEMBER_ME, true)
                            .putString(USERNAME, username)
                            .putString(PASSWORD, password)
                            .commit();
                } else {
                    sharedPreferences.edit()
                            .remove(REMEMBER_ME)
                            .remove(USERNAME)
                            .remove(PASSWORD)
                            .commit();
                }
        }
    }

    private Boolean sendPost(String username, String password) {
        HashMap<String, String> body = new HashMap<>();
        body.put("user", username);
        body.put("password", password);
        String authorization = Credentials.basic(API_KEY, API_SECRET);
        AuthService authService = ServiceGenerator.createService(AuthService.class, API_HOST);
        Call<LoginResponse> call = authService.login(authorization, body);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    String accessToken = response.body().getAccess_token();
                    String refreshToken = response.body().getRefresh_token();
                    Log.d(DEBUG_TAG, "loginResponse: " + response.toString());
                    sharedPreferences.edit()
                            .remove(ACCESS_TOKEN)
                            .remove(REFRESH_TOKEN)
                            .commit();
                    sharedPreferences.edit()
                            .putString(ACCESS_TOKEN, accessToken)
                            .putString(REFRESH_TOKEN, refreshToken)
                            .commit();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
//                    APIError apiError = ErrorUtils.parseError(response);
                    Log.d(DEBUG_TAG, response.errorBody().toString());
                    Toast.makeText(getApplicationContext(), response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d(DEBUG_TAG, t.getMessage());
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Network failure", Toast.LENGTH_LONG).show();
            }
        });
        return true;
    }

    public class LoginAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            sendPost(strings[0], strings[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void success) {

        }
    }
}
