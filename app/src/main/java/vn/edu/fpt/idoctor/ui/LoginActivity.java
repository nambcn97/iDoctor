package vn.edu.fpt.idoctor.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static vn.edu.fpt.idoctor.api.common.AppConstant.*;
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.api.common.RetrofitClient;
import vn.edu.fpt.idoctor.api.response.LoginResponse;
import vn.edu.fpt.idoctor.api.service.AuthService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin;
    private EditText edtUsername, edtPassword;
    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                init();
                if (validate()) {
                    LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
                    loginAsyncTask.execute(username, password);
                }
                break;
        }
    }

    private void sendPost(String username, String password) {
        HashMap<String, String> body = new HashMap<>();
        body.put("user", username);
        body.put("password", password);
        AuthService authService = RetrofitClient.getClient(API_HOST).create(AuthService.class);
        String authorization = String.format("Basic %s", Base64.encodeToString(
                (API_KEY + ":" + API_SECRET).getBytes(), Base64.DEFAULT)).trim();
        Call<LoginResponse> call = authService.login(authorization, body);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    String accessToken = response.body().getAccess_token();
                    String refreshToken = response.body().getRefresh_token();
                    SharedPreferences sharedPreferences = getSharedPreferences("idoctor-prefs", MODE_PRIVATE);
                    sharedPreferences.edit().putString("accessToken", accessToken).putString("refreshToken", refreshToken).commit();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Login fail! Check your username and password!!", Toast.LENGTH_LONG).show();
                    Log.d(DEBUG_TAG, response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(DEBUG_TAG, t.getMessage());
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Login fail! Check your username and password!!", Toast.LENGTH_LONG).show();
            }
        });

    }

    public class LoginAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            sendPost(strings[0], strings[1]);
            return null;
        }
    }
}
