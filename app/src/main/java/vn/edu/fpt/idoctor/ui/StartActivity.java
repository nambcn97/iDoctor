package vn.edu.fpt.idoctor.ui;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.ui.fragment.InputPhoneDialogFragment;

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
        switch (view.getId()) {
            case R.id.btnLogin:
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.btnRegister:
                intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btnEmergency:
                InputPhoneDialogFragment inputPhoneDialogFragment = new InputPhoneDialogFragment();
                inputPhoneDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                inputPhoneDialogFragment.show(getSupportFragmentManager(), "input phone dialog");
                break;
        }
    }
}
