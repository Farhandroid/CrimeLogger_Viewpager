package tanvir.crimelogger_playstore.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import tanvir.crimelogger_playstore.Fragment.HomeFragment;
import tanvir.crimelogger_playstore.Fragment.PlaceInfoFragment;
import tanvir.crimelogger_playstore.R;
import tanvir.crimelogger_playstore.HelperClass.Adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainActivityToolBar;
    private Toolbar placeInfoToolBar;
    private Toolbar placeInfoToolBarWithPlaceName;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;


    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    boolean isUserAlreadyClickedOnShowGooglePlaceAutocomplete = false;
    boolean isUserInPlaceInfoFragment = false;
    KProgressHUD hud;

    ///OnTest onTest;


    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_PHONE_STATE

    };

    private MaterialSearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityToolBar = findViewById(R.id.toolbarLayoutInMainActivity);
        placeInfoToolBar = findViewById(R.id.toolbarLayoutInPlaceInfoFragment);
        setSupportActionBar(mainActivityToolBar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        changeNavigationDrawer("home");

        ///Toast.makeText(this, "MainActivity", Toast.LENGTH_SHORT).show();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);


        checkPermissions();

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragments(new HomeFragment(), "Home");
        viewPagerAdapter.addFragments(new PlaceInfoFragment(), "Safe");


        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                if (position == 1) {
                    changeNavigationDrawer("placeInfo");
                    placeInfoToolBar.setVisibility(View.VISIBLE);
                    mainActivityToolBar.setVisibility(View.GONE);
                    isUserInPlaceInfoFragment = true;
                    ///Toast.makeText(MainActivity.this, "Safe", Toast.LENGTH_SHORT).show();
                } else {
                    changeNavigationDrawer("home");
                    isUserInPlaceInfoFragment = false;
                    placeInfoToolBar.setVisibility(View.GONE);
                    mainActivityToolBar.setVisibility(View.VISIBLE);
                    ///Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }


        });

        hud = KProgressHUD.create(MainActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.6f)
                .setLabel("Please Wait")
                .setCancellable(false);
    }


    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }


    public void showPlaceAutoComplete(View view) {


        if (isUserAlreadyClickedOnShowGooglePlaceAutocomplete == false) {
            hud.show();
            isUserAlreadyClickedOnShowGooglePlaceAutocomplete = true;

            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("BD")
                    .build();

            try {
                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(typeFilter).build(this);

                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        hud.dismiss();

        isUserAlreadyClickedOnShowGooglePlaceAutocomplete = false;

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                final Boolean[] isItFirchSearch = {true};


                String placeName = (String) place.getName();

                if (isUserInPlaceInfoFragment == false) {
                    Fragment fragment = viewPagerAdapter.getItem(0);
                    ((HomeFragment) fragment).onclikckedSearchInMainActivity(placeName, isItFirchSearch[0]);

                    if (isItFirchSearch[0]) {

                        isItFirchSearch[0] = false;
                    }


                    Toast.makeText(this, "name : " + place.getName() + "expense : " + place.getPriceLevel(), Toast.LENGTH_SHORT).show();

                } else {

                    Fragment fragment = viewPagerAdapter.getItem(1);
                    ((PlaceInfoFragment) fragment).setSearchKey(placeName);
                    Toast.makeText(this, "inPlaceInfo", Toast.LENGTH_SHORT).show();
                }


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                ////Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }


    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public void changeNavigationDrawer(String fragmentName) {
        actionBarDrawerToggle = null;

        if (fragmentName.contains("home"))
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mainActivityToolBar, R.string.drawer_open
                    , R.string.drawer_close);
        else if (fragmentName.contains("placeInfo"))
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, placeInfoToolBar, R.string.drawer_open
                    , R.string.drawer_close);
        else if (fragmentName.contains("placeInfoWithPlaceName"))
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, placeInfoToolBarWithPlaceName, R.string.drawer_open
                    , R.string.drawer_close);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.HOmeId).setChecked(true);
        actionBarDrawerToggle.syncState();
    }


}
