package vn.edu.fpt.idoctor.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.ui.fragment.HomeFragment;

public class EmergencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
//        homeFragment.setArguments();
    }
}
