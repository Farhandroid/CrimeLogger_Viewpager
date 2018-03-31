package tanvir.crimelogger_playstore.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tanvir.crimelogger_playstore.Fragment.HomeFragment;
import tanvir.crimelogger_playstore.Fragment.SafeTime;
import tanvir.crimelogger_playstore.ModelClass.UserPostMC;
import tanvir.crimelogger_playstore.R;
import tanvir.crimelogger_playstore.HelperClass.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;


    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

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

        toolbar = findViewById(R.id.tolbarlayoutinmainactivity);
        setSupportActionBar(toolbar);

        ///Toast.makeText(this, "MainActivity", Toast.LENGTH_SHORT).show();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        searchView = findViewById(R.id.search_view);

        checkPermissions();

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragments(new HomeFragment(), "Home");
        viewPagerAdapter.addFragments(new SafeTime(), "Safe");


        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                if (position == 1) {

                    ///Toast.makeText(MainActivity.this, "Safe", Toast.LENGTH_SHORT).show();
                } else {

                    ///Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }


        });
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);








        final Boolean[] isItFirchSearch = {true};
       /// isItFirchSearch[0]=true;

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        final Fragment fragment = viewPagerAdapter.getItem(0);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                ///Toast.makeText(MainActivity.this, "Submit", Toast.LENGTH_SHORT).show();

                if (query.length()>0)
                    ((HomeFragment)fragment).onclikckedSearchInMainActivity(query,isItFirchSearch[0]);
                else
                    Toast.makeText(MainActivity.this, "Blank", Toast.LENGTH_SHORT).show();

                ///return false;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
;


                if (newText.length()>0)
                {
                    /*int i= getResources().getConfiguration().orientation;

                    if (i == Configuration.ORIENTATION_PORTRAIT)
                        Toast.makeText(MainActivity.this, "Potrait", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(MainActivity.this, "Landscape", Toast.LENGTH_SHORT).show();*/




                    ///Toast.makeText(MainActivity.this, "MV isItFirchSearch : "+isItFirchSearch[0], Toast.LENGTH_SHORT).show();


                    ///Fragment fragment2= viewPagerAdapter.getItem(0);

                   /// if (fragment2 instanceof HomeFragment)
                        ///Toast.makeText(MainActivity.this, "Home fragment", Toast.LENGTH_SHORT).show();
                    ((HomeFragment)fragment).onclikckedSearchInMainActivity(newText,isItFirchSearch[0]);

                    if (isItFirchSearch[0])
                    {
                        ///Toast.makeText(MainActivity.this, "enter", Toast.LENGTH_SHORT).show();
                        isItFirchSearch[0] =false;
                    }




                }



                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {


            }

            @Override
            public void onSearchViewClosed() {



                ((HomeFragment)fragment).onclikckedSearchInMainActivity("SearchViewClosed",isItFirchSearch[0]);
            }
        });

        return true;
        //return false;
    }



}
