package tanvir.crimelogger_playstore.Fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tanvir.crimelogger_playstore.Activity.MainActivity;
import tanvir.crimelogger_playstore.HelperClass.MySingleton;
import tanvir.crimelogger_playstore.HelperClass.RecyclerAdapter;
import tanvir.crimelogger_playstore.ModelClass.UserPostMC;
import tanvir.crimelogger_playstore.R;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;

    ///OnMessageReadListener onMessageReadListener;

    KProgressHUD hud;

    FragmentActivity activity;

    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    RelativeLayout fullScreenProgressBarLayout;
    boolean isItFirstDataRetrivation = true;
    String searchKey = "";


    ArrayList<UserPostMC> userPostMCS;
    ArrayList<UserPostMC> userPostMCSCopy;
    boolean isLoading = true;
    int pastVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;
    int viewThreshold = 15;
    boolean isMaterialSearchViewUsed = false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        activity = getActivity();

        if (activity==null)
            Log.d("null6","activity = null in oncreate");




        ///Toast.makeText(activity, "Home", Toast.LENGTH_SHORT).show();

        progressBar = view.findViewById(R.id.prgrs);
        nestedScrollView = view.findViewById(R.id.homeFragMentNestedScrollView);
        fullScreenProgressBarLayout = view.findViewById(R.id.fullScreenProgressBarLayout);


        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        userPostMCS = new ArrayList<>();

        if (userPostMCS==null)
            Log.d("null5","userPostMCS = nll in oncreate");
            ///Toast.makeText(activity, "userPostMCS null", Toast.LENGTH_SHORT).show();


        userPostMCSCopy = new ArrayList<>();


        if (userPostMCSCopy==null)
            Log.d("null4","userPostMCSCopy = nll in oncreate");
            //Toast.makeText(activity, "userPostMCSCopy null", Toast.LENGTH_SHORT).show();


        updateRecyclerView(userPostMCS);


        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.6f)
                .setLabel("Please Wait")
                .setCancellable(false);


        if (savedInstanceState != null) {

            Log.d("null3","Enter savedInstanceState");
           /// Toast.makeText(activity, "Enter savedInstanceState", Toast.LENGTH_SHORT).show();
            userPostMCS.clear();
            ArrayList<UserPostMC> data = (ArrayList<UserPostMC>) savedInstanceState.getSerializable("userPostMCsData");
            userPostMCS.addAll(data);
            this.userPostMCSCopy.addAll(userPostMCS);
            this.isItFirstDataRetrivation = savedInstanceState.getBoolean("isItFirstDataRetrivation");
            this.isMaterialSearchViewUsed=savedInstanceState.getBoolean("isMaterialSearchViewUsed");
            this.searchKey=savedInstanceState.getString("searchKey");

            ///Toast.makeText(activity, "size : " + userPostMCS.size(), Toast.LENGTH_SHORT).show();


            updateRecyclerView(userPostMCS);
            recyclerView.scrollToPosition(savedInstanceState.getInt("restoreArrayListPosition"));


        } else if (isMaterialSearchViewUsed == false) {

            ///Toast.makeText(activity, "Enter else if", Toast.LENGTH_SHORT).show();
            retriveDataFromServer();
        }



        return view;
    }


    public void updateRecyclerView(ArrayList<UserPostMC> userPostMCS) {


        adapter = new RecyclerAdapter(activity, userPostMCS, "MainActivity");


        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > oldScrollY) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItem = layoutManager.findFirstVisibleItemPosition();

                    if (isLoading) {
                        if (totalItemCount > previousTotal) {
                            isLoading = false;
                            previousTotal = totalItemCount;
                        }
                    }

                    if (!isLoading && (totalItemCount - visibleItemCount) <= (pastVisibleItem + viewThreshold)) {

                        if (isMaterialSearchViewUsed==false)
                            retriveDataFromServer();
                        else
                            retiveDataByPlaceFromDatabase();

                        isLoading = true;
                    }

                }

            }
        });


    }

    public void retriveDataFromServer() {

        isMaterialSearchViewUsed=false;

        String p = "";
        if (isItFirstDataRetrivation) {
            isItFirstDataRetrivation = false;
            ///hud.show();
            fullScreenProgressBarLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            p = "0";
        } else {
            if (hud != null)
                hud.dismiss();


            progressBar.setVisibility(View.VISIBLE);
            fullScreenProgressBarLayout.setVisibility(View.GONE);
            p = Integer.toString(userPostMCS.size());

        }


        String url = "http://www.farhandroid.com/CrimeLogger/Script/retriveUserPostFromDatabaseWithLimit.php?position=" + p;


        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                JSONArray jsonArray = null;

                try {
                    jsonArray = new JSONArray(response);

                } catch (JSONException e) {
                    Toast.makeText(activity, "json array exception", Toast.LENGTH_SHORT).show();
                }

                if (jsonArray.length() == 0) {
                    if (hud != null)
                        hud.dismiss();
                    fullScreenProgressBarLayout.setVisibility(View.GONE);

                    progressBar.setVisibility(View.GONE);

                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {


                        try {


                            JSONObject postInfo = jsonArray.getJSONObject(i);


                            UserPostMC userPostMC = new UserPostMC(postInfo.getString("userName"), postInfo.getString("crimePlace"), postInfo.getString("crimeDate"), postInfo.getString("crimeTime"), postInfo.getString("crimeType"), postInfo.getString("crimeDesc"), postInfo.getString("postDateAndTime"), postInfo.getString("howManyImage"), postInfo.getString("howManyReport"));
                            userPostMCS.add(userPostMC);


                            if (i + 1 == jsonArray.length()) {


                                if (hud != null)
                                    hud.dismiss();

                                progressBar.setVisibility(View.GONE);
                                fullScreenProgressBarLayout.setVisibility(View.GONE);


                                updateRecyclerView(userPostMCS);
                                adapter.notifyDataSetChanged();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            hud.dismiss();

                            fullScreenProgressBarLayout.setVisibility(View.GONE);


                            Toast.makeText(activity, "Json Exception " + e.toString(), Toast.LENGTH_SHORT).show();


                        }
                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                hud.dismiss();
                fullScreenProgressBarLayout.setVisibility(View.GONE);


                Toast.makeText(activity, "Volley Error : " + error.toString(), Toast.LENGTH_SHORT).show();


            }
        }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        //AppController.getInstance().addToRequestQueue(jsonArrayRequest);
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);


    }

   @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("restoreArrayListPosition", layoutManager.findFirstVisibleItemPosition());
        outState.putSerializable("userPostMCsData", userPostMCS);
        outState.putBoolean("isItFirstDataRetrivation", isItFirstDataRetrivation);
        outState.putBoolean("isMaterialSearchViewUsed", isMaterialSearchViewUsed);
        outState.putString("searchKey",searchKey);

    }



    public void retiveDataByPlaceFromDatabase() {

        isMaterialSearchViewUsed=true;

        String p;

        if (isItFirstDataRetrivation) {
            isItFirstDataRetrivation = false;
            ///hud.show();
            fullScreenProgressBarLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            p = "0";
        } else {
            if (hud != null)
                hud.dismiss();


            progressBar.setVisibility(View.VISIBLE);
            fullScreenProgressBarLayout.setVisibility(View.GONE);
            p = Integer.toString(userPostMCS.size());

        }


        String url = "http://www.farhandroid.com/CrimeLogger/Script/retrieveSearchByPlaceData.php?placeName=" + searchKey + "&position=" + p;

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(activity, "rspnse : " + response.length(), Toast.LENGTH_LONG).show();
                Log.d("rspnse ",response);


                JSONArray jsonArray = null;

                try {
                    jsonArray = new JSONArray(response);

                } catch (JSONException e) {
                    Toast.makeText(activity, "json array exception", Toast.LENGTH_SHORT).show();
                }

                if (jsonArray.length() == 0) {
                    if (hud != null)
                        hud.dismiss();
                    fullScreenProgressBarLayout.setVisibility(View.GONE);

                    progressBar.setVisibility(View.GONE);

                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {


                        try {


                            JSONObject postInfo = jsonArray.getJSONObject(i);


                            UserPostMC userPostMC = new UserPostMC(postInfo.getString("userName"), postInfo.getString("crimePlace"), postInfo.getString("crimeDate"), postInfo.getString("crimeTime"), postInfo.getString("crimeType"), postInfo.getString("crimeDesc"), postInfo.getString("postDateAndTime"), postInfo.getString("howManyImage"), postInfo.getString("howManyReport"));
                            userPostMCS.add(userPostMC);


                            if (i + 1 == jsonArray.length()) {


                                if (hud != null)
                                    hud.dismiss();

                                progressBar.setVisibility(View.GONE);
                                fullScreenProgressBarLayout.setVisibility(View.GONE);


                                updateRecyclerView(userPostMCS);
                                adapter.notifyDataSetChanged();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            hud.dismiss();

                            fullScreenProgressBarLayout.setVisibility(View.GONE);


                            Toast.makeText(activity, "Json Exception " + e.toString(), Toast.LENGTH_SHORT).show();


                        }
                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                hud.dismiss();
                fullScreenProgressBarLayout.setVisibility(View.GONE);


                Toast.makeText(getActivity(), "Volley Error : " + error.toString(), Toast.LENGTH_SHORT).show();


            }
        }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toast.makeText(activity, "onActivityCreated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        Toast.makeText(activity, "onPause", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(activity, "onStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(activity, "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        Toast.makeText(activity, "onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(activity, "onDestroyView", Toast.LENGTH_SHORT).show();
    }*/

    public void onclikckedSearchInMainActivity(String searchKey, Boolean isItFirstSearch) {



        if (isItFirstSearch) {

            userPostMCSCopy.addAll(userPostMCS);

        }

        userPostMCS.clear();
        adapter.notifyDataSetChanged();


        if (searchKey.contains("SearchViewClosed")) {


            userPostMCS.clear();
            userPostMCS.addAll(userPostMCSCopy);
            adapter.notifyDataSetChanged();

            isMaterialSearchViewUsed=false;

        } else {



            isMaterialSearchViewUsed = true;
            isItFirstDataRetrivation = true;
            this.searchKey = searchKey;


            retiveDataByPlaceFromDatabase();
        }


    }


}
