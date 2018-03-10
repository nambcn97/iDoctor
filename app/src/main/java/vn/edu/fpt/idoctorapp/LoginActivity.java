package vn.edu.fpt.idoctorapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static vn.edu.fpt.idoctorapp.AppConstant.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

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

    private void validate() {
        if (username.isEmpty()) {
            edtUsername.setError("Please input username!!!");
        }
        if (password.isEmpty()) {
            edtPassword.setError("Invalid!!!");
        }
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
                validate();
                LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
                loginAsyncTask.execute(username, password);
                break;
        }
    }


    private void getAuthentication(final String username, final String password) {
//        RequestQueue queue = Volley.newRequestQueue(this);  // this = context
//        JSONObject params = new JSONObject();
//        try {
//            params.put("user", username);
//            params.put("password", password);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, API_LOGIN, params, new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
//                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
//                Log.d("123456", response.toString());
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // error
//                Log.d("123456", error.toString());
//                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
//            }
//        }) {
//
//            @Override
//            public String getBodyContentType() {
//                return "application/json";
//            }
//
//            //            @Override
////            protected Map<String, String> getParams()
////            {
////                Map<String, String>  params = new HashMap<>();
//////                params.put("id", username);
//////                params.put("password", password);
////                params.put("id", "1");
////                return params;
////            }
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/json");
//                params.put("Authorization",
//                        String.format("Basic %s", Base64.encodeToString(
//                                String.format("%s:%s", API_KEY, API_SECRET).getBytes(), Base64.DEFAULT)));
//
////                params.put("Authorization", "Bearer b4c43bb3-3c14-4133-86a3-6e68ff3175a7");
//                return params;
//            }
//        };
//        queue.add(postRequest);
//        queue.start();
        try {
            URL url = new URL(API_LOGIN);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            String baseAuthStr = API_KEY + ":" + API_SECRET;
            connection.addRequestProperty("Authorization", String.format("Basic %s", Base64.encodeToString(
                                String.format("%s:%s", API_KEY, API_SECRET).getBytes(), Base64.DEFAULT)));
            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user", username);
                jsonObject.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            connection.setDoOutput(true);
            connection.setDoInput(true);
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(jsonObject.toString());
            System.out.println(jsonObject.toString());
            os.flush();
            os.close();
            connection.connect();
            Log.d("123456", connection.getContentType());
//            Log.d("123456",connection.getHeaderField("Authorization"));
//            Log.d("123456",connection.getHeaderField("Content-Type"));
            Log.d("123456", connection.getResponseCode() + "");
            Log.d("123456", connection.getResponseMessage() + "");

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));

            StringBuilder output = new StringBuilder();
            String line;
            System.out.println("Output from Server .... \n");
            while ((line = br.readLine()) != null) {
                output.append(line);
            }
            Log.d("idoctor", output.toString());
            connection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public interface PostCommentResponseListener {
//        public void requestStarted();
//
//        public void requestCompleted();
//
//        public void requestEndedWithError(VolleyError error);
//    }

    public class LoginAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            getAuthentication(strings[0], strings[1]);
            return null;
        }
    }
}
