package vn.edu.fpt.idoctor.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.api.model.User;
import vn.edu.fpt.idoctor.api.response.PlaceSearchResponse;

import static vn.edu.fpt.idoctor.common.AppConstant.DEBUG_TAG;

public class InformationActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView lstInfo;
    private TextView tvName, tvRole;
    private List<String> infos;
    private ImageButton imgBtnCall, imgBtnDirect, imgBtnAccept, imgBtnDeny;
    private String phone = "115";
    private GeoDataClient mGeoDataClient;
    private LatLng fromLatLng, toLatLng;
    private MaterialDialog loadingDialog;
    private String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        loadingDialog = new MaterialDialog.Builder(InformationActivity.this).content("Loading").progress(true, 70).show();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Thông tin cá nhân");
        mGeoDataClient = Places.getGeoDataClient(this, null);
        lstInfo = findViewById(R.id.lstInfo);
        tvName = findViewById(R.id.tvName);
        tvRole = findViewById(R.id.tvRole);
        imgBtnCall = findViewById(R.id.imgBtnCall);
        imgBtnDirect = findViewById(R.id.imgBtnDirect);
        imgBtnAccept = findViewById(R.id.imgBtnAccept);
        imgBtnDeny = findViewById(R.id.imgBtnDeny);
        imgBtnAccept.setOnClickListener(this);
        imgBtnDeny.setOnClickListener(this);
        imgBtnCall.setOnClickListener(this);
        imgBtnDirect.setOnClickListener(this);
        disableEmergencyBtn();
        infos = new ArrayList<>();

        Intent intent = getIntent();
        Double myLat = intent.getDoubleExtra("myLat",0);
        Double myLng = intent.getDoubleExtra("myLng",0);
        fromLatLng = new LatLng(myLat, myLng);
        String type = intent.getStringExtra("type");

        if (type.equals("place")) {
            PlaceSearchResponse.Result place = (PlaceSearchResponse.Result) intent.getSerializableExtra("info");
            toLatLng = new LatLng(place.getGeometry().getLocation().getLat(),place.getGeometry().getLocation().getLng());
            tvName.setText(place.getName());
            if (place.getOpening_hours() != null) {
                if (place.getOpening_hours().getOpen_now()) {
                    infos.add("Hiện tại: Đang mở cửa");
                } else {
                    infos.add("Hiện tại: Đang đóng cửa");
                }
            }
            tvRole.setText(place.getTypes().get(0).toUpperCase());
            // get place detail
            mGeoDataClient.getPlaceById(place.getPlace_id()).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse response = task.getResult();
                        Place detailPlace = response.get(0);
                        if (detailPlace.getAddress() != null) {
                            address = detailPlace.getAddress().toString();
                            infos.add("Địa chỉ: " + detailPlace.getAddress().toString());
                        }
                        if (detailPlace.getPhoneNumber() != null) {
                            phone = detailPlace.getPhoneNumber().toString();
                            infos.add("Số điện thoại: " + phone);
                        }
                        infos.add("Đánh giá: " + detailPlace.getRating());
                        if (detailPlace.getWebsiteUri() != null) {
                            infos.add("Website: " + detailPlace.getWebsiteUri());
                        }
                        response.release();
                        loadingDialog.dismiss();
                    }else {
                        Log.d(DEBUG_TAG, "Place not found.");
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(InformationActivity.this, R.layout.info_list_item, R.id.info_item, infos);
                    lstInfo.setAdapter(arrayAdapter);
                }
            });
            //TODO: get photo
//            mGeoDataClient.getPlacePhotos(place.getPlace_id()).addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
//                @Override
//                public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
//                    task.getResult().getPhotoMetadata();
//                }
//            });

        } else if (type.equals("doctor")) {
            User doctor = (User) intent.getSerializableExtra("info");
            toLatLng = new LatLng(doctor.getLat(), doctor.getLng());
            if (doctor.getFullName() != null) {
                tvName.setText(doctor.getFullName());
            } else {
                tvName.setText(doctor.getUsername());
            }
            tvRole.setText("Bác sỹ " + doctor.getSpecialty());
            address = doctor.getAddress();
            infos.add("Địa chỉ: " + doctor.getAddress());
            infos.add("Số điện thoại: " + doctor.getPhone());
            infos.add("Giới tính: " + (doctor.getGender() ? "Nam" : "Nữ"));
            infos.add("Hiện tại: " + doctor.getStatus());
            infos.add("Nơi làm việc: " + doctor.getWorkAddress());
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(InformationActivity.this, R.layout.info_list_item, R.id.info_item, infos);
            lstInfo.setAdapter(arrayAdapter);
            loadingDialog.dismiss();
        } else {
            String userJsonStr =  intent.getStringExtra("info");

            try {
                JSONObject userJson = new JSONObject(userJsonStr);
                toLatLng = new LatLng(userJson.getDouble("lat"), userJson.getDouble("lng"));
                String role = userJson.getString("role");
                tvRole.setText(role);
                if (role.equals("Anonymous")) {
                    tvName.setText(userJson.getString("phone"));
                    infos.add("Cuộc gọi khẩn cấp từ người lạ");
                } else {
                    tvName.setText(userJson.getString("fullName"));
                    address = userJson.getString("address");
                    infos.add("Địa chỉ: " + userJson.getString("address"));
                    phone = userJson.getString("phone");
                    infos.add("Số điện thoại: " + userJson.getString("phone"));
                    infos.add("Giới tính: " + (userJson.getBoolean("gender") ? "Nam" : "Nữ"));
                    infos.add("Hiện tại: " + userJson.getString("status"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(InformationActivity.this, R.layout.info_list_item, R.id.info_item, infos);
            lstInfo.setAdapter(arrayAdapter);
            loadingDialog.dismiss();
        }



    }

    public void enableEmergencyBtn(){
        imgBtnAccept.setVisibility(View.VISIBLE);
        imgBtnDeny.setVisibility(View.VISIBLE);
        imgBtnCall.setVisibility(View.GONE);
        imgBtnDirect.setVisibility(View.GONE);
//        imgChat.setVisibility(View.GONE);
    }
    public void disableEmergencyBtn(){
        imgBtnAccept.setVisibility(View.GONE);
        imgBtnDeny.setVisibility(View.GONE);
        imgBtnCall.setVisibility(View.VISIBLE);
        imgBtnDirect.setVisibility(View.VISIBLE);
    }

//    private void getPlaceDetail(String placeId) {
//        SearchService searchService = RetrofitClient.getClient(GOOGLE_MAPS_BASE_URL).create(SearchService.class);
//        final Call<PlaceDetailsResponses> call = searchService.getPlaceDetail(placeId);
//        call.enqueue(new Callback<PlaceDetailsResponses>() {
//            @Override
//            public void onResponse(Call<PlaceDetailsResponses> call, Response<PlaceDetailsResponses> response) {
//                Log.d(DEBUG_TAG, "place details response: " + response.code());
//                Log.d(DEBUG_TAG, "status: " + response.body().getStatus());
//                PlaceDetailsResponses placeDetailsResponses = response.body();
//                PlaceDetailsResponses.Result detail = placeDetailsResponses.getResult();
//                infos = new ArrayList<>();
//                infos.add("Địa chỉ: " + detail.getFormatted_address());
//                infos.add("Số điện thoại: " + detail.getFormatted_phone_number());
//                phone = detail.getFormatted_phone_number();
//                infos.add("Website: " + detail.getWebsite());
//                infos.add("Đánh giá: " + detail.getRating());
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.info_list_item, R.id.info_item, infos);
//                lstInfo.setAdapter(arrayAdapter);
//            }
//
//            @Override
//            public void onFailure(Call<PlaceDetailsResponses> call, Throwable t) {
//                Log.d(DEBUG_TAG, "place detail: " + t.getMessage());
//                t.printStackTrace();
//            }
//        });
//    }

    @SuppressLint("MissingPermission")
    private void callIntent() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtnCall:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 2609);
                    return;
                }
                callIntent();
                break;
            case R.id.imgBtnDirect:
                Intent intent = new Intent(getApplicationContext(), DirectionMapsActivity.class);
                intent.putExtra("fromLat", fromLatLng.latitude);
                intent.putExtra("fromLng", fromLatLng.longitude);
                intent.putExtra("toLat", toLatLng.latitude);
                intent.putExtra("toLng", toLatLng.longitude);
                intent.putExtra("name",tvName.getText().toString() + " - " + address);
                startActivity(intent);
                break;
        }
    }

//    public class GetPlaceDetailTask extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected Void doInBackground(String... strings) {
//            getPlaceDetail(strings[0]);
//            return null;
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callIntent();
            // permission was granted, yay! Do the
            // location-related task you need to do.
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accepted!", Toast.LENGTH_SHORT).show();
            }

        } else {

            // permission denied, boo! Disable the
            // functionality that depends on this permission.
            Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
        }
    }
}
