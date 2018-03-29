package vn.edu.fpt.idoctor.ui.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import retrofit2.Response;
import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.api.response.BaseResponse;
import vn.edu.fpt.idoctor.api.service.AuthService;
import vn.edu.fpt.idoctor.common.LogoutUtil;
import vn.edu.fpt.idoctor.common.ServiceGenerator;
import vn.edu.fpt.idoctor.ui.StartActivity;

import static vn.edu.fpt.idoctor.common.AppConstant.ACCESS_TOKEN;
import static vn.edu.fpt.idoctor.common.AppConstant.API_HOST;
import static vn.edu.fpt.idoctor.common.AppConstant.DEBUG_TAG;
import static vn.edu.fpt.idoctor.common.AppConstant.SHARED_PREF;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogoutFragment extends DialogFragment implements View.OnClickListener{

    private Button btnCancel, btnOk;
    private EditText edtPhone;
    public LogoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_logout, container, false);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnOk = view.findViewById(R.id.btnOk);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle("Đăng xuất").setMessage("Bạn có chắc chắn muốn đăng xuất không?")
//                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                        LogoutTask logoutTask = new LogoutTask();
//                        logoutTask.execute();
//                    }
//                })
//                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                }).create();
//        return dialog;
//    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnOk:

                getDialog().dismiss();
//                LogoutTask logoutTask = new LogoutTask();
//                logoutTask.execute();
                break;
            case R.id.btnCancel:
                getDialog().dismiss();
                break;
        }

    }

}
