package vn.edu.fpt.idoctor.ui.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.fpt.idoctor.R;

import static vn.edu.fpt.idoctor.api.common.AppConstant.*;

import vn.edu.fpt.idoctor.api.common.GPSTracker;
import vn.edu.fpt.idoctor.api.common.RetrofitClient;
import vn.edu.fpt.idoctor.api.response.PlaceSearchResponse;
import vn.edu.fpt.idoctor.api.model.User;
import vn.edu.fpt.idoctor.api.response.FindDoctorResponse;
import vn.edu.fpt.idoctor.api.service.SearchService;
import vn.edu.fpt.idoctor.ui.InformationActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationSource.OnLocationChangedListener {
    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted;
    private GPSTracker gps;
    private double myLat, myLng;
    private SharedPreferences sharedPreferences;
    private String accessToken;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        gps = new GPSTracker(this.getContext());
        // check if GPS enabled
        if (gps.canGetLocation()) {

            myLat = gps.getLatitude();
            myLng = gps.getLongitude();

            // \n is for new line
            Toast.makeText(getContext(), "Your Location is - \nLat: " + myLat + "\nLong: " + myLng, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AppConstant.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString(ACCESS_TOKEN, "");
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentHome = inflater.inflate(R.layout.fragment_home, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapHome);
        mapFragment.getMapAsync(this);
        return fragmentHome;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        SearchHospitalNearbyTask searchHospitalNearbyTask = new SearchHospitalNearbyTask();
        searchHospitalNearbyTask.execute(myLat, myLng);
        SearchDoctorNearbyTask searchDoctorNearbyTask = new SearchDoctorNearbyTask();
        searchDoctorNearbyTask.execute(myLat, myLng);
        // Add a marker in Sydney and move the camera

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        addMyMarker(myLat,myLng);
    }

    private void addMyMarker(Double myLat, Double myLng){
        LatLng myLocation = new LatLng(myLat, myLng);
        MarkerOptions myMarker = new MarkerOptions().position(myLocation).title("My location")
                .icon(BitmapDescriptorFactory.defaultMarker());
        mMap.addMarker(myMarker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f));
    }

    //
//    public void test(View v) {
//        mMap.clear();
//        String url = getUrl(latitude, longitude, Restaurant);
//        Object[] DataTransfer = new Object[2];
//        DataTransfer[0] = mMap;
//        DataTransfer[1] = url;
//        Log.d("onClick", url);
//        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
//        getNearbyPlacesData.execute(DataTransfer);
//        Toast.makeText(MapsActivity.this, "Nearby Restaurants", Toast.LENGTH_LONG).show();
//    }
    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }


    public void searchHospitalNearby(Double lat, Double lng) {
        SearchService searchService = RetrofitClient.getClient(GOOGLE_MAPS_BASE_URL).create(SearchService.class);
        final Call<PlaceSearchResponse> hospitalCall = searchService.searchHospital(lat + "," + lng);
        hospitalCall.enqueue(new Callback<PlaceSearchResponse>() {
            @Override
            public void onResponse(Call<PlaceSearchResponse> call, Response<PlaceSearchResponse> response) {
                Log.d(DEBUG_TAG, "search hospital response: " + response.code());
                Log.d(DEBUG_TAG, "result: " + response.body().getResults().size());
                PlaceSearchResponse hospitals = response.body();
                List<PlaceSearchResponse.Result> results = hospitals.getResults();
                for (PlaceSearchResponse.Result result : results) {
                    Double lat = result.getGeometry().getLocation().getLat();
                    Double lng = result.getGeometry().getLocation().getLng();
                    String title = result.getName() + ", " + result.getVicinity();
                    LatLng nearby = new LatLng(lat, lng);
                    MarkerOptions markerOptions = new MarkerOptions().position(nearby).title(title)
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("hospital",70,70)));
                    Marker hospitalMarker = mMap.addMarker(markerOptions);
                    hospitalMarker.setTag(result);
                }
            }

            @Override
            public void onFailure(Call<PlaceSearchResponse> call, Throwable t) {
                Log.d(DEBUG_TAG, "search hospital: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void searchDoctorNearby(Double lat, Double lng) {
        HashMap<String, String> body = new HashMap<>();
        body.put("lat", lat + "");
        body.put("lng", lng + "");
        body.put("radius", 500 + "");
        SearchService searchService = RetrofitClient.getClient(API_HOST).create(SearchService.class);
        String authorization = String.format("Bearer %s", accessToken).trim();
        Call<FindDoctorResponse> call = searchService.searchDoctor(authorization, body);
        call.enqueue(new Callback<FindDoctorResponse>() {
            @Override
            public void onResponse(Call<FindDoctorResponse> call, Response<FindDoctorResponse> response) {
                FindDoctorResponse findRes = response.body();
                List<User> doctors = findRes.getDoctors();
                for (User doctor : doctors) {
                    Double lat = doctor.getLat();
                    Double lng = doctor.getLng();
                    String title = doctor.getFullName();
                    LatLng nearby = new LatLng(lat, lng);
                    MarkerOptions markerOptions = new MarkerOptions().position(nearby).title(title)
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("doctor",70,70)));
                    Marker doctorMarker = mMap.addMarker(markerOptions);
                    doctorMarker.setTag(doctor);
                }
            }

            @Override
            public void onFailure(Call<FindDoctorResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Object info = marker.getTag();
        if (info instanceof PlaceSearchResponse.Result) {
            PlaceSearchResponse.Result hospitalResult = (PlaceSearchResponse.Result) info;
            Intent intent = new Intent(this.getContext(), InformationActivity.class);
            intent.putExtra("placeId", hospitalResult.getPlace_id());
            intent.putExtra("name", hospitalResult.getName());
            startActivity(intent);
        } else if (info instanceof User) {
            User doctor = (User) info;
            Intent intent = new Intent(this.getContext(), InformationActivity.class);
            List<String> infos = new ArrayList<>();
            infos.add("Giới tính: " + (doctor.getGender() ? "Nam" : "Nữ"));
            infos.add("Địa chỉ: " + doctor.getAddress());
            infos.add("Số điện thoại: " + doctor.getPhone());
            intent.putExtra("specialty", doctor.getSpecialty());
            intent.putExtra("name", "Bác sỹ " + doctor.getFullName());
            intent.putExtra("infos", (Serializable) infos);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        addMyMarker(location.getLatitude(), location.getLongitude());
    }

    public class SearchHospitalNearbyTask extends AsyncTask<Double, Void, Void> {

        @Override
        protected Void doInBackground(Double... doubles) {
            searchHospitalNearby(doubles[0], doubles[1]);
            return null;
        }
    }

    public class SearchDoctorNearbyTask extends AsyncTask<Double, Void, Void> {

        @Override
        protected Void doInBackground(Double... doubles) {
            searchDoctorNearby(doubles[0], doubles[1]);
            return null;
        }
    }
}
