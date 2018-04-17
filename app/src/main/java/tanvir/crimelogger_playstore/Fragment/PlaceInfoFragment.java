package tanvir.crimelogger_playstore.Fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import tanvir.crimelogger_playstore.HelperClass.MySingleton;
import tanvir.crimelogger_playstore.ModelClass.PieChartDataMC;
import tanvir.crimelogger_playstore.ModelClass.UserPostMC;
import tanvir.crimelogger_playstore.R;


public class PlaceInfoFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private Activity activity;
    private ArrayList<UserPostMC> userPostMCS;
    private ArrayList<String> crimeType;
    private ArrayList<String> seperatedCrimeType;
    ArrayList<String> crimeDate;

    TextView searchedPlaceTV;
    private TextView timeRangeTV, dateRangeTV;
    private Button showDateRangePickerDialog, showTimeRangePickerDialog;

    private PieChart pieChart;

    private ArrayList<PieChartDataMC> pieCharts;

    private int timeFromInt, timeToInt;
    private String dateFrom, dateTo;


    private String searchKey;
    RelativeLayout fullScreenProgressBarLayout;

    private Context context;


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
        userPostMCS = new ArrayList<>();
        seperatedCrimeType = new ArrayList<>();

        pieCharts = new ArrayList<>();
        pieChart = view.findViewById(R.id.pieChart);
        pieChart.setVisibility(View.GONE);

        dateRangeTV = view.findViewById(R.id.dateRangeTV);
        timeRangeTV = view.findViewById(R.id.timeRangeTV);

        context = getActivity();

        searchedPlaceTV = view.findViewById(R.id.placeTV);

        fullScreenProgressBarLayout = view.findViewById(R.id.fullScreenProgressBarLayoutInPlaceInfoFragment);

        showDateRangePickerDialog = view.findViewById(R.id.dateRangePickerButton);
        showTimeRangePickerDialog = view.findViewById(R.id.timeRangePickerBTN);

        showDateRangePickerDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateRange();
            }
        });

        showTimeRangePickerDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeRange();
            }
        });

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

    public void showTimeRange() {

        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                (TimePickerDialog.OnTimeSetListener) PlaceInfoFragment.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );


        tpd.show(activity.getFragmentManager(), "Timepickerdialog");
    }

    public void showDateRange() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                (DatePickerDialog.OnDateSetListener) PlaceInfoFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setMaxDate(now);
        dpd.show(activity.getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {

        String timeFrom, timeTo;

        String hourOfDayFormat;

        if (hourOfDay == 0) {

            hourOfDay += 12;

            hourOfDayFormat = "AM";
        } else if (hourOfDay == 12) {

            hourOfDayFormat = "PM";

        } else if (hourOfDay > 12) {

            hourOfDay -= 12;


            hourOfDayFormat = "PM";

        } else {

            hourOfDayFormat = "AM";
        }


        String hourOfDayEndFormat;

        if (hourOfDayEnd == 0) {

            hourOfDayEnd += 12;

            hourOfDayEndFormat = "AM";
        } else if (hourOfDayEnd == 12) {

            hourOfDayEndFormat = "PM";

        } else if (hourOfDayEnd > 12) {

            hourOfDayEnd -= 12;

            hourOfDayEndFormat = "PM";

        } else {

            hourOfDayEndFormat = "AM";
        }

        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String hourStringEnd = hourOfDayEnd < 10 ? "0" + hourOfDayEnd : "" + hourOfDayEnd;
        String minuteStringEnd = minuteEnd < 10 ? "0" + minuteEnd : "" + minuteEnd;

        String time = "Time:" + hourString + ":" + minuteString + ":" + hourOfDayFormat + " To " + hourStringEnd + ":" + minuteStringEnd + ":" + hourOfDayEndFormat;

        timeRangeTV.setText(time);

        timeFrom = hourString + ":" + minuteString + " " + hourOfDayFormat;
        timeTo = hourStringEnd + ":" + minuteStringEnd + " " + hourOfDayEndFormat;


        timeFromInt = getTimeInInteger(timeFrom);
        timeToInt = getTimeInInteger(timeTo);


        sortByTime();

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {

        String date = "Date:" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year + " To  " + dayOfMonthEnd + "/" + (monthOfYearEnd + 1) + "/" + yearEnd;

        dateRangeTV.setText(date);

        dateFrom = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        dateTo = dayOfMonthEnd + "/" + (monthOfYearEnd + 1) + "/" + yearEnd;

        sortByDate();
    }

    public void sortByTime() {

        boolean isItPMtoAM = false;

        if ((timeFromInt >= 1200 && timeFromInt <= 2459) && (timeToInt >= 100 && timeToInt <= 1259))
            isItPMtoAM = true;


        if (timeFromInt > timeToInt) {
            int i = timeFromInt;
            timeFromInt = timeToInt;
            timeToInt = i;

        }


        String time;
        int timeInt;


        for (int i = 0; i < userPostMCS.size(); i++) {
            time = userPostMCS.get(i).getCrimeTime();


            timeInt = getTimeInInteger(time);


            if (isItPMtoAM) {


                Log.d("Enter", "AM to PM");

                if ((timeInt >= 100 && timeInt <= timeFromInt) || (timeInt >= timeToInt && timeInt <= 2459)) {

                    Log.d("namePlaceAMPm  ", "name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate());
                    Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();

                }

            } else if (timeInt == timeFromInt || timeInt == timeToInt) {
                Log.d("namePlaceNormal  ", "name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate());
                Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();
            } else if ((timeInt > timeFromInt && timeInt < timeToInt)) {
                Log.d("namePlaceNormal  ", "name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate());
                Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();

            }


            ///Toast.makeText(context,"time : "+time, Toast.LENGTH_SHORT).show();
        }


    }

    public void sortByDate() {
        Toast.makeText(activity, "SortByDate", Toast.LENGTH_SHORT).show();

        for (int i = 0; i < userPostMCS.size(); i++) {

            String date = userPostMCS.get(i).getCrimeDate();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            try {
                Date dateFromDate = format.parse(dateFrom);
                Date dateToDate = format.parse(dateTo);
                Date date2 = format.parse(date);


                if (date2.compareTo(dateFromDate) > 0 && date2.compareTo(dateToDate) < 0)
                    Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();
                else if (date2.compareTo(dateFromDate) < 0 && date2.compareTo(dateToDate) > 0)
                    Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();
                else if (date2.compareTo(dateFromDate) == 0 || date2.compareTo(dateToDate) == 0)
                    Toast.makeText(context, " name : " + userPostMCS.get(i).getCrimePlace() + "\ndate : " + userPostMCS.get(i).getCrimeDate() + "\ntime : " + userPostMCS.get(i).getCrimeTime(), Toast.LENGTH_SHORT).show();


            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Exception : " + e.toString(), Toast.LENGTH_SHORT).show();
            }


        }
    }

    public int getTimeInInteger(String time) {


        String timeCopy = time;

        time = time.substring(0, time.length() - 2);
        time = time.replace(":", "");

        if (timeCopy.contains("PM"))

        {
            String timeSub = time.substring(0, 2);
            timeSub = timeSub.trim();
            int i1 = Integer.parseInt(timeSub);
            i1 += 12;
            time = Integer.toString(i1) + time.substring(2);

        }

        time = time.trim();
        time = time.replaceAll(" ", "");

        int timeInt = Integer.parseInt(time);

        return timeInt;


    }


}
