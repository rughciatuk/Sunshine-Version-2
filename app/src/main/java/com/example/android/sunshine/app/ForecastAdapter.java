package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.


    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor

        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        return LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent,false);
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView dateTextView = (TextView) view.findViewById(R.id.list_item_date_textview);
        long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        dateTextView.setText(Utility.getFriendlyDayString(mContext,date));

        TextView descTextView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        String desc = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        descTextView.setText(desc);

        TextView highTextView = (TextView) view.findViewById(R.id.list_item_high_textview);
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        boolean isMetric = Utility.isMetric(context);
        String highStr = Utility.formatTemperature(high,isMetric);
        highTextView.setText(highStr);

        TextView minTextView = (TextView) view.findViewById(R.id.list_item_low_textview);
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        String lowStr = Utility.formatTemperature(low,isMetric);
        minTextView.setText(lowStr);

    }
}