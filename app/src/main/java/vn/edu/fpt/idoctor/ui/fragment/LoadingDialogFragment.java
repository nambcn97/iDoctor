package vn.edu.fpt.idoctor.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import vn.edu.fpt.idoctor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoadingDialogFragment extends DialogFragment {


    public LoadingDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return new MaterialDialog.Builder(this.getContext())
                .title("In Progress")
                .content("Please wait")
                .progress(true, 0)
                .show().getView();
//        return inflater.inflate(R.layout.fragment_loading_dialog, container, false);
    }

}
