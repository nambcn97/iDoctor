package vn.edu.fpt.idoctor.ui;

import android.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.common.LogoutUtil;
import vn.edu.fpt.idoctor.ui.fragment.HomeFragment;

import static vn.edu.fpt.idoctor.common.AppConstant.ACCESS_TOKEN;
import static vn.edu.fpt.idoctor.common.AppConstant.SHARED_PREF;

public class AnonymousHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogoutUtil.logout(getSharedPreferences(SHARED_PREF, MODE_PRIVATE));
        setContentView(R.layout.activity_anonymous);
        Intent intent = getIntent();
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("phone", intent.getStringExtra("phone"));
        bundle.putBoolean("isEmergency", true);
        homeFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.anonymousHomeLayout, homeFragment); // give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
    }
}
