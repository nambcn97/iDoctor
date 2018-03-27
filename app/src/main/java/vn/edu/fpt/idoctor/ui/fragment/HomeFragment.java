package vn.edu.fpt.idoctor.ui.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.api.model.EmergencyBean;
import vn.edu.fpt.idoctor.api.model.User;
import vn.edu.fpt.idoctor.api.response.FindDoctorResponse;
import vn.edu.fpt.idoctor.api.response.PlaceSearchResponse;
import vn.edu.fpt.idoctor.api.response.SendEmergencyResponse;
import vn.edu.fpt.idoctor.api.service.EmergencyService;
import vn.edu.fpt.idoctor.api.service.SearchService;
import vn.edu.fpt.idoctor.common.GPSTracker;
import vn.edu.fpt.idoctor.common.RetrofitClient;
import vn.edu.fpt.idoctor.common.ServiceGenerator;
import vn.edu.fpt.idoctor.ui.MainActivity;
import vn.edu.fpt.idoctor.ui.StartActivity;

import static vn.edu.fpt.idoctor.common.AppConstant.ACCESS_TOKEN;
import static vn.edu.fpt.idoctor.common.AppConstant.API_HOST;
import static vn.edu.fpt.idoctor.common.AppConstant.DEBUG_TAG;
import static vn.edu.fpt.idoctor.common.AppConstant.DEVICE_ID;
import static vn.edu.fpt.idoctor.common.AppConstant.GOOGLE_MAPS_BASE_URL;
import static vn.edu.fpt.idoctor.common.AppConstant.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static vn.edu.fpt.idoctor.common.AppConstant.SHARED_PREF;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int items = 2;
    private List<PlaceSearchResponse.Result> listPlace;
    private List<User> listDoctor;
    private static final int TAB_MAP = 0;
    private static final int TAB_LIST = 1;
    private static final String TAB_MAP_TITLE = "Map";
    private static final String TAB_LIST_TITLE = "List";
    private GPSTracker gps;
    private double myLat, myLng;
    private SharedPreferences sharedPreferences;
    private String accessToken;
    private LoadingDialogFragment loadingDialogFragment;
    private MapTabFragment mapTabFragment;
    private ListDoctorTabFragment listDoctorTabFragment;
    private String deviceId;
    private MaterialDialog loadingDialog;
    private Boolean isEmergency;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new MaterialDialog.Builder(getContext())
                .title("In Progress")
                .content("Please wait")
                .progress(true, 0)
                .show();

        isEmergency = getArguments().getBoolean("isEmergency", false);
        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString(ACCESS_TOKEN, "");
        deviceId = sharedPreferences.getString(DEVICE_ID, "");
        Log.d(DEBUG_TAG, accessToken);
        Log.d(DEBUG_TAG, "DeviceID: " + deviceId);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "home on view created");
        if (!getMyLatLng()) {
            LocationErrorFragment locationErrorDialog = new LocationErrorFragment();
            locationErrorDialog.show(getFragmentManager(), "locationErrorDialog");
        } else {
            searchNearby();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, null);
        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.viewpager);
        Log.d(DEBUG_TAG, "home on create view");

        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Log.d(DEBUG_TAG, "home on getItem");
                switch (position) {
                    case TAB_MAP:
                        mapTabFragment = new MapTabFragment();
                        return mapTabFragment;

                    case TAB_LIST:
                        listDoctorTabFragment = new ListDoctorTabFragment();
                        return listDoctorTabFragment;

                }
                return new MapTabFragment();
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case TAB_MAP:
                        return TAB_MAP_TITLE;
                    case TAB_LIST:
                        return TAB_LIST_TITLE;
                }
                return TAB_MAP_TITLE;
            }
        });

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return view;
    }

    private Boolean getMyLatLng() {
        gps = new GPSTracker(getContext());
        // check if GPS enabled
        if (gps.canGetLocation()) {

            myLat = gps.getLatitude();
            myLng = gps.getLongitude();
            return true;
        } else {
            // can't get location// GPS or Network is not enabled// Ask user to enable GPS/network in settings
//            gps.showSettingsAlert();
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return false;
        }
    }

    public void searchNearby() {
        SearchNearbyTask searchHospitalNearbyTask = new SearchNearbyTask();
        searchHospitalNearbyTask.execute(myLat, myLng);
    }

    public void searchAndSendEmergency() {
        Call<SendEmergencyResponse> call;
        HashMap<String, Object> body = new HashMap<>();
        body.put("lat", myLat);
        body.put("lng", myLng);
        body.put("radius", 3000);
        body.put("deviceId", deviceId);
        if (accessToken.isEmpty()) {
            String phone = getArguments().getString("phone");
            body.put("phone", phone);
            body.put("loggedIn", false);
            EmergencyService emergencyService = ServiceGenerator.createService(EmergencyService.class, API_HOST);
            call = emergencyService.sendEmergencyAnonymous(body);
        } else {
            body.put("loggedIn", true);
            String header = String.format("Bearer %s", accessToken).trim();
            EmergencyService emergencyService = ServiceGenerator.createService(EmergencyService.class, API_HOST);
            call = emergencyService.sendEmergencyUser(header, body);
        }
        try {
            Response<SendEmergencyResponse> response = call.execute();
            Log.d(DEBUG_TAG, "sendNoti: " + response.code());
            if (response.isSuccessful()) {

                SendEmergencyResponse responseBody = response.body();
                List<EmergencyBean> emergencyBeans = responseBody.getEmergencies();
                List<User> sendedDoctor = new ArrayList<>();
                for (int i = 0; i < emergencyBeans.size(); i++) {
                    sendedDoctor.add(emergencyBeans.get(i).getToUser());
                }
                listDoctor = sendedDoctor;
                Log.d(DEBUG_TAG, "sendNoti: result - " + emergencyBeans.size());
            } else {
                Log.d(DEBUG_TAG, "sendNoti: " + response.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "sendNoti: " + e.getMessage());
        }
    }


    public void searchHospitalNearby(Double lat, Double lng) {
        SearchService searchService = RetrofitClient.getClient(GOOGLE_MAPS_BASE_URL).create(SearchService.class);
        final Call<PlaceSearchResponse> hospitalCall = searchService.searchHospital(lat + "," + lng);
        try {
            Response<PlaceSearchResponse> response = hospitalCall.execute();
            Log.d(DEBUG_TAG, "search hospital response: " + response.code());
            if (response.isSuccessful()) {
                if (!response.isSuccessful()) return;
                Log.d(DEBUG_TAG, "result: " + response.body().getResults().size());
                PlaceSearchResponse hospitals = response.body();
                listPlace = hospitals.getResults();
            } else {
                Log.d(DEBUG_TAG, "search hospital response: " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "search hospital fail: " + e.getMessage());
        }
    }

    public void searchDoctorNearby(Double lat, Double lng) {
        HashMap<String, String> body = new HashMap<>();
        body.put("lat", lat + "");
        body.put("lng", lng + "");
        body.put("radius", 3000 + "");
        SearchService searchService = RetrofitClient.getClient(API_HOST).create(SearchService.class);
        String authorization = String.format("Bearer %s", accessToken).trim();
        Call<FindDoctorResponse> call = searchService.searchDoctor(authorization, body);
        try {
            Response<FindDoctorResponse> response = call.execute();
            Log.d(DEBUG_TAG, "search doctor response: " + response.code());
            if (response.isSuccessful()) {

                if (!response.isSuccessful()) return;
                Log.d(DEBUG_TAG, "result: " + response.body().getDoctors().size());
                FindDoctorResponse findRes = response.body();
                listDoctor = findRes.getDoctors();
            } else {
                Log.d(DEBUG_TAG, "search doctor response: " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "search hospital: " + e.getMessage());
        }

    }


    public class SearchNearbyTask extends AsyncTask<Double, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Double... doubles) {
            searchHospitalNearby(doubles[0], doubles[1]);
            if (isEmergency) {
                searchAndSendEmergency();
            } else {
                searchDoctorNearby(doubles[0], doubles[1]);
            }
            return null;
        }

        @SuppressLint("MissingPermission")
        private void callIntent(){
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "115"));
            startActivity(intent);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadingDialog.dismiss();
            mapTabFragment.addMarker(listPlace, listDoctor);
            if (isEmergency){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                // Setting Dialog Title
                alertDialog.setTitle("Gọi cấp cứu");
                // Setting Dialog Message
                if (listDoctor == null || listDoctor.size() == 0){
                    alertDialog.setTitle("Không tìm thấy bác sỹ nào đang trực tuyến gần bạn");
                    if (accessToken.isEmpty()){
                        alertDialog.setMessage("Hãy gọi 115 hoặc liên lạc với các bệnh viện lân cận");
                    } else
                        alertDialog.setMessage("Hãy gọi 115 hoặc Về trang chủ để liên lạc với các bác sỹ ngoại tuyến và các bệnh viện xung quanh");
                    alertDialog.setPositiveButton("Gọi 115", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            callIntent();
                        }
                    });
                    alertDialog.setPositiveButton("Về trang chủ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!accessToken.isEmpty()) refresh();
                            else {
                                Intent intent = new Intent();

                            }
                        }
                    });
                }
                alertDialog.setMessage("");

                // On pressing Settings button
                alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getActivity().startActivity(intent);
                    }
                });
            }
        }
    }

    public void refresh(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

}
