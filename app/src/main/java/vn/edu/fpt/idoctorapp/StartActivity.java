package vn.edu.fpt.idoctorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin, btnRegister, btnEmergency;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnEmergency = findViewById(R.id.btnEmergency);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnEmergency.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btnLogin:
                 intent = new Intent(getApplicationContext(), LoginActivity.class);
                 startActivity(intent);
                break;
            case R.id.btnRegister:
                break;
            case R.id.btnEmergency:
                break;
        }
    }
}
