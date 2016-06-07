package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_DEGREES
    };

    private static final int COL_WEATHER_ID = 0 ;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WIND_SPEED = 6;
    private static final int COL_PRESSURE = 7;
    private static final int COL_DEGREES = 8;

    private String mForecast;

    private View mRootView;

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private ShareActionProvider mShareActionProvider;

    private static final int DETAIL_LOADER = 1;
    private TextView mWeekDayTextView;
    private TextView mDateTextView;
    private TextView mMaxTempTextView;
    private TextView mMinTempTextView;
    private ImageView mIconImageView;
    private TextView mHumidityTextView;
    private TextView mWindTextView;
    private TextView mPressureTextView;
    private TextView mDescTextView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mWeekDayTextView =  (TextView) mRootView.findViewById(R.id.detail_week_day_textview);
        mDateTextView = (TextView) mRootView.findViewById(R.id.detail_date_textview);
        mMaxTempTextView = (TextView) mRootView.findViewById(R.id.detail_max_temp_textview);
        mMinTempTextView = (TextView) mRootView.findViewById(R.id.detail_min_temp_textview);
        mIconImageView = (ImageView) mRootView.findViewById(R.id.detail_icon);
        mHumidityTextView = (TextView) mRootView.findViewById(R.id.detail_humidity_textview);
        mWindTextView = (TextView) mRootView.findViewById(R.id.detail_wind_textview);
        mPressureTextView = (TextView) mRootView.findViewById(R.id.detail_pressure_textview);
        mDescTextView = (TextView) mRootView.findViewById(R.id.detail_desc_textview);




        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER,savedInstanceState,this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mForecast != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Sort order:  Ascending, by date.

        Intent intent = getActivity().getIntent();
        if(intent == null){
            return null;
        }

        return new CursorLoader(getActivity(),
                intent.getData(),
                DETAIL_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst()){return;}
        boolean isMetric = Utility.isMetric(getActivity());

        String dateString = Utility.getFormattedMonthDay(getActivity(),data.getLong(COL_WEATHER_DATE));
        String dayOfTheWeekString = Utility.getFriendlyDayString(getActivity(),data.getLong(COL_WEATHER_DATE));
        String short_desc = data.getString(COL_WEATHER_DESC);
        String maxTemp = Utility.formatTemperature(getActivity(),data.getLong(COL_WEATHER_MAX_TEMP),isMetric);
        String minTemp = Utility.formatTemperature(getActivity(),data.getLong(COL_WEATHER_MIN_TEMP),isMetric);
        double humidityValue = data.getDouble(COL_WEATHER_HUMIDITY);
        String windString = Utility.getFormattedWind(getActivity(),data.getFloat(COL_WIND_SPEED),data.getFloat(COL_DEGREES));
        double pressureValue = data.getDouble(COL_PRESSURE);

        int pressureFormant = R.string.format_pressure;
        String pressureString = String.format(getActivity().getString(pressureFormant),pressureValue);

        int humidityFormant = R.string.format_humidity;
        String humidityString = String.format(getString(humidityFormant), humidityValue);

        mWeekDayTextView.setText(dayOfTheWeekString);
        mDateTextView.setText(dateString);
        mMaxTempTextView.setText(maxTemp);
        mMinTempTextView.setText(minTemp);
        mIconImageView.setImageResource(R.drawable.ic_launcher);
        mHumidityTextView.setText(humidityString);
        mWindTextView.setText(windString);
        mPressureTextView.setText(pressureFormant);
        mDescTextView.setText(short_desc);



        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }




    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }
}
