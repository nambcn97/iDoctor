package vn.edu.fpt.idoctor.ui.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
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
import java.util.List;

import vn.edu.fpt.idoctor.R;

import static vn.edu.fpt.idoctor.common.AppConstant.*;

import vn.edu.fpt.idoctor.common.GPSTracker;
import vn.edu.fpt.idoctor.api.response.PlaceSearchResponse;
import vn.edu.fpt.idoctor.api.model.User;
import vn.edu.fpt.idoctor.ui.InformationActivity;
import vn.edu.fpt.idoctor.ui.PatientRequestMapsActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapTabFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted;
    private GPSTracker gps;
    private double myLat, myLng;
    private SharedPreferences sharedPreferences;
    private String accessToken;
    private List<PlaceSearchResponse.Result> listPlace;
    private List<User> listDoctor;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    public MapTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }

//    private Boolean getMyLatLng() {
//        gps = new GPSTracker(getContext());
//        // check if GPS enabled
//        if (gps.canGetLocation()) {
//            myLat = gps.getLatitude();
//            myLng = gps.getLongitude();
//            return true;
//        } else {
//            // can't get location// GPS or Network is not enabled// Ask user to enable GPS/network in settings
////            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//            return false;
//        }
//    }

    public void addMarker(List<PlaceSearchResponse.Result> listPlace, List<User> listDoctor) {
        Log.d(DEBUG_TAG, "addMarker");
        this.listPlace = listPlace;
        this.listDoctor = listDoctor;
        if (mMap != null) {
//            addMyMarker(myLat, myLng);
            addDoctorMarker();
            addPlaceMarker();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString(ACCESS_TOKEN, "");


//        getMyLatLng();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(DEBUG_TAG, "maptab onCreateView");
        View fragmentHome = inflater.inflate(R.layout.fragment_map_tab, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapHome);
        mapFragment.getMapAsync(this);
        return fragmentHome;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(DEBUG_TAG, "maptab onviewCreated");
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(DEBUG_TAG, "OnMapReady");
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
//        addMyMarker(myLat, myLng);
        addDoctorMarker();
        addPlaceMarker();

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }


    private void addMyMarker(Double myLat, Double myLng) {
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


    public void addDoctorMarker() {
        if (listDoctor != null)
            for (User doctor : listDoctor) {
                Double lat = doctor.getLat();
                Double lng = doctor.getLng();
                String title = doctor.getFullName();
                LatLng nearby = new LatLng(lat, lng);
                MarkerOptions markerOptions = new MarkerOptions().position(nearby).title(title)
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("doctor", 70, 70)));
                Marker doctorMarker = mMap.addMarker(markerOptions);
                doctorMarker.setTag(doctor);
            }

    }

    public void addPlaceMarker() {
        if (listPlace != null)
            for (PlaceSearchResponse.Result result : listPlace) {
                Double lat = result.getGeometry().getLocation().getLat();
                Double lng = result.getGeometry().getLocation().getLng();
                String title = result.getName() + ", " + result.getVicinity();
                LatLng nearby = new LatLng(lat, lng);
                MarkerOptions markerOptions = new MarkerOptions().position(nearby).title(title)
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("hospital", 70, 70)));
                Marker hospitalMarker = mMap.addMarker(markerOptions);
                hospitalMarker.setTag(result);
            }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Object info = marker.getTag();
        if (info instanceof PlaceSearchResponse.Result) {
            PlaceSearchResponse.Result hospitalResult = (PlaceSearchResponse.Result) info;
            Intent intent = new Intent(this.getContext(), InformationActivity.class);
            intent.putExtra("type","place");
            intent.putExtra("info", hospitalResult);
            intent.putExtra("myLat", myLat);
            intent.putExtra("myLng", myLng);
            startActivity(intent);
        } else if (info instanceof User) {
            User doctor = (User) info;
            Intent intent = new Intent(this.getContext(), InformationActivity.class);
            intent.putExtra("type", "doctor");
            intent.putExtra("info", doctor);
            intent.putExtra("myLat", myLat);
            intent.putExtra("myLng", myLng);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        myLat = location.getLatitude();
        myLng = location.getLongitude();
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
