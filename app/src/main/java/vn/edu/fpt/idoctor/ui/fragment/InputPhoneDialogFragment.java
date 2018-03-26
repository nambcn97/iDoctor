package vn.edu.fpt.idoctor.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.ui.AnonymousHomeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class InputPhoneDialogFragment extends DialogFragment implements View.OnClickListener {
    private Button btnCancel, btnOk;
    private EditText edtPhone;
    private ViewGroup container;
    private LayoutInflater inflater;

    public InputPhoneDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_phone_dialog, container, false);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnOk = view.findViewById(R.id.btnOk);
        edtPhone = view.findViewById(R.id.edtPhone);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        this.inflater = inflater;
        this.container = container;
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                getDialog().dismiss();
                break;
            case R.id.btnOk:
                getDialog().dismiss();
                Intent intent = new Intent(getContext(), AnonymousHomeActivity.class);
                intent.putExtra("phone", edtPhone.getText().toString());
                startActivity(intent);
                break;
        }
    }
}
