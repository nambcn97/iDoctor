package vn.edu.fpt.idoctor.ui;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import vn.edu.fpt.idoctor.api.service.AuthService;
import vn.edu.fpt.idoctor.api.service.SearchService;
import vn.edu.fpt.idoctor.api.service.UserService;
import vn.edu.fpt.idoctor.common.GPSTracker;
import vn.edu.fpt.idoctor.common.LogoutUtil;
import vn.edu.fpt.idoctor.common.RetrofitClient;
import vn.edu.fpt.idoctor.api.response.BaseResponse;
import vn.edu.fpt.idoctor.api.service.NotificationService;
import vn.edu.fpt.idoctor.common.ServiceGenerator;
import vn.edu.fpt.idoctor.ui.fragment.ChatHistoryFragment;
import vn.edu.fpt.idoctor.ui.fragment.FindFragment;
import vn.edu.fpt.idoctor.ui.fragment.HomeFragment;
import vn.edu.fpt.idoctor.ui.fragment.LogoutFragment;
import vn.edu.fpt.idoctor.ui.fragment.MapTabFragment;
import vn.edu.fpt.idoctor.ui.fragment.MyAccountFragment;
import vn.edu.fpt.idoctor.ui.fragment.NotificationFragment;

import static vn.edu.fpt.idoctor.common.AppConstant.*;

public class MainActivity extends AppCompatActivity
        /*implements NavigationView.OnNavigationItemSelectedListener */ {
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView tvName, tvRole;
    private Toolbar toolbar;
    //    private FloatingActionButton fab;
    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_FIND = "find";
    private static final String TAG_CHAT = "chat";
    private static final String TAG_INFO = "info";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_LOGOUT = "logout";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private SharedPreferences sharedPreferences;
    private String accessToken;
    private List<PlaceSearchResponse.Result> listPlace;
    private List<User> listDoctor;
    private Double myLat, myLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        accessToken = sharedPreferences.getString(ACCESS_TOKEN, "");
        if (getMyLatLng()) {
            SendDataTask sendDataTask = new SendDataTask();
            sendDataTask.execute();
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);


        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        tvName = (TextView) navHeader.findViewById(R.id.tvName);
        tvRole = (TextView) navHeader.findViewById(R.id.tvRole);
//        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.imgProfile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    private Boolean getMyLatLng() {
        GPSTracker gps = new GPSTracker(MainActivity.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            myLat = gps.getLatitude();
            myLng = gps.getLongitude();
            return true;
        } else {
            // can't get location// GPS or Network is not enabled// Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return false;
        }
    }

    private void sendDeviceIdToServer() {
        String deviceId = sharedPreferences.getString(DEVICE_ID, "");
        HashMap<String, Object> body = new HashMap<>();
        body.put("deviceId", deviceId);
        body.put("lat", myLat);
        body.put("lng", myLng);
        Log.d(DEBUG_TAG, "deviceId: " + deviceId);
        UserService userService = ServiceGenerator.createService(UserService.class, API_HOST);
        String authorization = String.format("Bearer %s", accessToken).trim();
        Call<BaseResponse> call = userService.updateData(authorization, body);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body().getResultCode() == 200) {
                    Log.d(DEBUG_TAG, "Update data successfully!!! ");
                } else {
                    Log.d(DEBUG_TAG, "Update data: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.d(DEBUG_TAG, "Send deviceID: " + t.getMessage());
                t.printStackTrace();
            }
        });

    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // TODO: name, website
        tvName.setText("Nam Bùi");
        tvRole.setText("Bệnh nhân");

        // TODO: loading header background image
//        Glide.with(this).load(urlNavHeaderBg)
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imgNavHeaderBg);
//
//        //TODO: Loading profile image
//        Glide.with(this).load(urlProfileImg)
//                .crossFade()
//                .thumbnail(0.5f)
//                .bitmapTransform(new CircleTransform(this))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imgProfile);

        // showing dot next to notifications label
//        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
//            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.mainLayout, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
//        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }


    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // find doctor
                FindFragment findFragment = new FindFragment();
                return findFragment;
            case 2:
                // emergency fragment
                homeFragment = new HomeFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isEmergency", true);
                homeFragment.setArguments(bundle);
                return homeFragment;
            case 3:
                // notifications fragment
                NotificationFragment notificationFragment = new NotificationFragment();
                return notificationFragment;
            case 4:
                MyAccountFragment myAccountFragment = new MyAccountFragment();
                return myAccountFragment;
            case 5:
                LogoutTask logoutTask = new LogoutTask();
                logoutTask.execute();
                this.finish();
                return new LogoutFragment();


        }
        return new HomeFragment();
    }

    private void logout() {
//        String accessToken = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE).getString(ACCESS_TOKEN,"");
        String authorization = String.format("Bearer %s", accessToken).trim();
        AuthService authService = ServiceGenerator.createService(AuthService.class, API_HOST);
        try {
            Response<BaseResponse> response = authService.logout(authorization).execute();
            if (response.isSuccessful() && response.body().getResultCode() == 200) {
                LogoutUtil.logout(getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE));
                Log.d(DEBUG_TAG, "Logout successfull");
            } else {
                Log.d(DEBUG_TAG, "Logout fail!" + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class LogoutTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            logout();
            return null;
        }
    }

    public class SendDataTask extends AsyncTask<Object, Object, Object> {

        @Override
        protected Object doInBackground(Object... objects) {
            sendDeviceIdToServer();
            return null;
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_find:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_FIND;
                        break;
                    case R.id.nav_chat:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_CHAT;
                        break;
                    case R.id.nav_info:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_INFO;
                        break;
                    case R.id.nav_notification:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_logout:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_LOGOUT;
                        break;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
//        if (navItemIndex == 0) {
//            getMenuInflater().inflate(R.menu.main, menu);
//        }
//
//        // when fragment is notifications, load the menu created for notifications
//        if (navItemIndex == 3) {
//            getMenuInflater().inflate(R.menu.notifications, menu);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_logout) {
//            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
//            return true;
//        }
//
//        // user is in notifications fragment
//        // and selected 'Mark all as Read'
//        if (id == R.id.action_mark_all_read) {
//            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
//        }
//
//        // user is in notifications fragment
//        // and selected 'Clear All'
//        if (id == R.id.action_clear_notifications) {
//            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
//        }

        return super.onOptionsItemSelected(item);
    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//    }
//
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            //TODO: exit? or not
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_home) {
//            // Handle the camera action
//        } else if (id == R.id.nav_info) {
//
//        } else if (id == R.id.nav_chat) {
//
//        } else if (id == R.id.nav_find) {
//
//        } else if (id == R.id.nav_notification) {
//
//        } else if (id == R.id.nav_logout) {
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }


}
