package vn.edu.fpt.idoctor.ui.fragment;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.api.model.User;
import vn.edu.fpt.idoctor.api.response.FindDoctorResponse;
import vn.edu.fpt.idoctor.api.response.PlaceSearchResponse;
import vn.edu.fpt.idoctor.api.service.SearchService;
import vn.edu.fpt.idoctor.common.GPSTracker;
import vn.edu.fpt.idoctor.common.RetrofitClient;
import vn.edu.fpt.idoctor.ui.MainActivity;

import static vn.edu.fpt.idoctor.common.AppConstant.ACCESS_TOKEN;
import static vn.edu.fpt.idoctor.common.AppConstant.API_HOST;
import static vn.edu.fpt.idoctor.common.AppConstant.DEBUG_TAG;
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
    private ProgressBar progressBar;
    private LoadingDialogFragment loadingDialogFragment;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString(ACCESS_TOKEN, "");
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, null);
        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.viewpager);

        if (!getMyLatLng()) {
            LocationErrorFragment locationErrorDialog = new LocationErrorFragment();
            locationErrorDialog.show(getFragmentManager(),"locationErrorDialog");
            return view;
        } else {
            searchNearby();
        }

        return view;
    }

    private Boolean getMyLatLng() {
        gps = new GPSTracker(this.getActivity().getApplicationContext());
        // check if GPS enabled
        if (gps.canGetLocation()) {

            myLat = gps.getLatitude();
            myLng = gps.getLongitude();
            return true;
        } else {
            // can't get location// GPS or Network is not enabled// Ask user to enable GPS/network in settings
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return false;
        }
    }

    public void searchNearby() {
        SearchNearbyTask searchHospitalNearbyTask = new SearchNearbyTask();
        searchHospitalNearbyTask.execute(myLat, myLng);
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
        body.put("radius", 500 + "");
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
            loadingDialogFragment = new LoadingDialogFragment();
            loadingDialogFragment.show(getFragmentManager(),"loading");
        }

        @Override
        protected Void doInBackground(Double... doubles) {
            searchHospitalNearby(doubles[0], doubles[1]);
            searchDoctorNearby(doubles[0], doubles[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadingDialogFragment.dismiss();
            viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
                @Override
                public Fragment getItem(int position) {

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("listPlace", (Serializable) listPlace);
                    bundle.putSerializable("listDoctor", (Serializable) listDoctor);
                    switch (position) {
                        case TAB_MAP:
                            MapTabFragment mapTabFragment = new MapTabFragment();
                            mapTabFragment.setArguments(bundle);
                            return mapTabFragment;

                        case TAB_LIST:
                            ListDoctorTabFragment listDoctorTabFragment = new ListDoctorTabFragment();
                            listDoctorTabFragment.setArguments(bundle);
                            return listDoctorTabFragment;

                    }
                    return null;
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
                    return null;
                }
            });

            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    tabLayout.setupWithViewPager(viewPager);
                }
            });
        }
    }



}
