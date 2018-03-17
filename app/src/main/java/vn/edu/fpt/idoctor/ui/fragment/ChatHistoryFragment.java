package vn.edu.fpt.idoctor.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.edu.fpt.idoctor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatHistoryFragment extends Fragment {


    public ChatHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_history, container, false);
    }

}
