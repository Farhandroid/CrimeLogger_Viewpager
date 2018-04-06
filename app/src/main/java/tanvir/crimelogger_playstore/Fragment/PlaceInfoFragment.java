package tanvir.crimelogger_playstore.Fragment;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivateKey;
import java.util.ArrayList;

import tanvir.crimelogger_playstore.HelperClass.MySingleton;
import tanvir.crimelogger_playstore.ModelClass.PieChartDataMC;
import tanvir.crimelogger_playstore.ModelClass.UserPostMC;
import tanvir.crimelogger_playstore.R;


public class PlaceInfoFragment extends Fragment {

    private Activity activity;
    private ArrayList<UserPostMC> userPostMCS;
    private ArrayList<String> crimeType;
    private ArrayList<String> seperatedCrimeType;
    ArrayList<String> crimeDate;

    TextView searchedPlaceTV;

    private PieChart pieChart;

    private ArrayList<PieChartDataMC> pieCharts;


    private String searchKey;
    RelativeLayout fullScreenProgressBarLayout;


    public PlaceInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_place_info, container, false);
        activity = getActivity();
        crimeType = new ArrayList<>();
        crimeDate = new ArrayList<>();
        userPostMCS=new ArrayList<>();
        seperatedCrimeType = new ArrayList<>();

        pieCharts = new ArrayList<>();
        pieChart = view.findViewById(R.id.pieChart);
        pieChart.setVisibility(View.GONE);

        searchedPlaceTV = view.findViewById(R.id.placeTV);

        fullScreenProgressBarLayout = view.findViewById(R.id.fullScreenProgressBarLayoutInPlaceInfoFragment);

        return view;
    }


    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
        retiveDataByPlaceFromDatabase();
    }


    public void retiveDataByPlaceFromDatabase() {

        fullScreenProgressBarLayout.setVisibility(View.VISIBLE);




        String url = "http://www.farhandroid.com/CrimeLogger/Script/retrieveSearchByPlaceData.php?placeName=" + searchKey.replaceAll(" ", "%20") + "&allData=yes";

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(activity, "rspnse : " + response.length(), Toast.LENGTH_LONG).show();
                Log.d("rspnse ", response);


                JSONArray jsonArray = null;

                try {
                    jsonArray = new JSONArray(response);

                } catch (JSONException e) {
                    Toast.makeText(activity, "json array exception", Toast.LENGTH_SHORT).show();
                }

                if (jsonArray.length() == 0) {

                    fullScreenProgressBarLayout.setVisibility(View.GONE);


                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {


                        try {


                            JSONObject postInfo = jsonArray.getJSONObject(i);


                            UserPostMC userPostMC = new UserPostMC(postInfo.getString("userName"), postInfo.getString("crimePlace"), postInfo.getString("crimeDate"), postInfo.getString("crimeTime"), postInfo.getString("crimeType"), postInfo.getString("crimeDesc"), postInfo.getString("postDateAndTime"), postInfo.getString("howManyImage"), postInfo.getString("howManyReport"));
                            userPostMCS.add(userPostMC);


                            if (i + 1 == jsonArray.length()) {


                                fullScreenProgressBarLayout.setVisibility(View.GONE);
                                retrieveCrimeType(searchKey);
                                setPieChart();


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();


                            fullScreenProgressBarLayout.setVisibility(View.GONE);


                            Toast.makeText(activity, "Json Exception " + e.toString(), Toast.LENGTH_SHORT).show();


                        }
                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {


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

    public void retrieveCrimeType(String query) {


        searchedPlaceTV.setText(query);

        seperatedCrimeType.clear();
        crimeType.clear();
        pieCharts.clear();
        String place = query.toLowerCase();

        for (int i = 0; i < userPostMCS.size(); i++) {


            String crimePlace = userPostMCS.get(i).getCrimePlace().toLowerCase();


            if (crimePlace.contains(place) || place.contains(crimePlace)) {

                crimeType.add(userPostMCS.get(i).getCrimeType());
                crimeDate.add(userPostMCS.get(i).getCrimeDate());
            }


        }
        if (crimeType.size() > 0) {

            ///emptyRelativeLayout.setVisibility(View.GONE);

            pieChart.setVisibility(View.VISIBLE);
            ///relativeLayout.setVisibility(View.GONE);

            seperateCrimeType();
        } else {
            ///emptyRelativeLayout.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.GONE);
            Toast.makeText(activity, "\"Sorry , no data found", Toast.LENGTH_SHORT).show();
            ///TastyToast.makeText(getApplicationContext(), "Sorry , no data found  !", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }


    }

    public void seperateCrimeType() {
        String currentCrimeType;
        int initialPosition;
        int commaPosition;

        for (int i = 0; i < crimeType.size(); i++) {
            currentCrimeType = crimeType.get(i);
            initialPosition = 0;

            for (int j = 0; j < currentCrimeType.length(); j++) {
                commaPosition = currentCrimeType.indexOf(',', initialPosition);


                if (commaPosition != -1) {

                    seperatedCrimeType.add(currentCrimeType.substring(initialPosition, commaPosition));
                    initialPosition = commaPosition + 1;
                }

                if (commaPosition == -1 && initialPosition < (currentCrimeType.length() - 1)) {

                    seperatedCrimeType.add(currentCrimeType.substring(initialPosition));
                    break;
                }


            }
        }

        showSeparetedCrimeType();
    }

    public void showSeparetedCrimeType() {
        /*for (int i = 0; i < seperatedCrimeType.size(); i++) {
            Toast.makeText(this, "crimeType :  "+Integer.toString(i)+" "+seperatedCrimeType.get(i), Toast.LENGTH_SHORT).show();
        }*/
        countCrimeType();
    }


    public void countCrimeType() {
        String crimeType;
        int length;
        int count;
        pieCharts.clear();

        for (; ; ) {
            crimeType = seperatedCrimeType.get(0);
            seperatedCrimeType.remove(0);
            length = seperatedCrimeType.size();
            count = 1;

            for (int j = 0; j < length; j++) {


                if (seperatedCrimeType.get(j).contains(crimeType)) {
                    count = count + 1;

                    ////Toast.makeText(this, "Count :  " + Integer.toString(count) + " " + crimeType, Toast.LENGTH_SHORT).show();
                    seperatedCrimeType.remove(j);
                    length = seperatedCrimeType.size();

                }
            }

            if (length == 1) {
                if (seperatedCrimeType.get(0).contains(crimeType)) {
                    count = count + 1;

                    ////Toast.makeText(this, "Count :  " + Integer.toString(count) + " " + crimeType, Toast.LENGTH_SHORT).show();
                    seperatedCrimeType.remove(0);

                }
            }

            ///Toast.makeText(this, "total Count :  " + Integer.toString(count) + " " + crimeType, Toast.LENGTH_SHORT).show();

            PieChartDataMC pieChartDataMC = new PieChartDataMC(crimeType, count);
            pieCharts.add(pieChartDataMC);

            if (seperatedCrimeType.size() == 0)
                break;


        }


    }

    public void setPieChart() {


        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.parseColor("#455A64"));
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        for (int i = 0; i < pieCharts.size(); i++) {
            PieChartDataMC pieChartDataMC = pieCharts.get(i);

            yValues.add(new PieEntry(pieChartDataMC.getCrimeNumber(), pieChartDataMC.getCrimeType()));
        }

        PieDataSet dataSet = new PieDataSet(yValues, "CrimeType");

        dataSet.setSliceSpace(3);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setSelectionShift(3f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);

        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);


        pieChart.animateY(1000);

        pieChart.setData(data);
        pieChart.invalidate();
    }

}
