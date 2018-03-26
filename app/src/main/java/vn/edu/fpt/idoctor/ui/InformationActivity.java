package vn.edu.fpt.idoctor.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.fpt.idoctor.R;
import vn.edu.fpt.idoctor.common.RetrofitClient;
import vn.edu.fpt.idoctor.api.response.PlaceDetailsResponses;
import vn.edu.fpt.idoctor.api.service.SearchService;

import static vn.edu.fpt.idoctor.common.AppConstant.DEBUG_TAG;
import static vn.edu.fpt.idoctor.common.AppConstant.GOOGLE_MAPS_BASE_URL;

public class InformationActivity extends AppCompatActivity {
    private ListView lstInfo;
    private TextView tvName, tvRole;
    private List<String> infos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lstInfo = findViewById(R.id.lstInfo);
        tvName = findViewById(R.id.tvName);
        tvRole = findViewById(R.id.tvRole);

        Intent intent = getIntent();
        String placeId = intent.getStringExtra("placeId");
        String name = intent.getStringExtra("name");
        String specialty = intent.getStringExtra("specialty");
        tvName.setText(name);
        if (placeId!=null){
            tvRole.setText("Bệnh viện");
            GetPlaceDetailTask getPlaceDetailTask = new GetPlaceDetailTask();
            getPlaceDetailTask.execute(placeId);
        } else {
            tvRole.setText(specialty);
            infos = (List<String>) intent.getSerializableExtra("infos");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.info_list_item, R.id.info_item, infos);
            lstInfo.setAdapter(arrayAdapter);

        }

    }

    private void getPlaceDetail(String placeId){
        SearchService searchService = RetrofitClient.getClient(GOOGLE_MAPS_BASE_URL).create(SearchService.class);
        final Call<PlaceDetailsResponses> call = searchService.getPlaceDetail(placeId);
        call.enqueue(new Callback<PlaceDetailsResponses>() {
            @Override
            public void onResponse(Call<PlaceDetailsResponses> call, Response<PlaceDetailsResponses> response) {
                Log.d(DEBUG_TAG, "place details response: " + response.code());
                Log.d(DEBUG_TAG, "status: " + response.body().getStatus());
                PlaceDetailsResponses placeDetailsResponses = response.body();
                PlaceDetailsResponses.Result detail = placeDetailsResponses.getResult();
                infos = new ArrayList<>();
                infos.add("Địa chỉ: " + detail.getFormatted_address());
                infos.add("Số điện thoại: " + detail.getFormatted_phone_number());
                infos.add("Website: " + detail.getWebsite());
                infos.add("Đánh giá: " + detail.getRating());
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.info_list_item,R.id.info_item, infos);
                lstInfo.setAdapter(arrayAdapter);
            }

            @Override
            public void onFailure(Call<PlaceDetailsResponses> call, Throwable t) {
                Log.d(DEBUG_TAG, "place detail: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public class GetPlaceDetailTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            getPlaceDetail(strings[0]);
            return null;
        }
    }

}
