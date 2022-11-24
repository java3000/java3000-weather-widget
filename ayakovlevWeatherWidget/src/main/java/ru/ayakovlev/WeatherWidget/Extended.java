package ru.ayakovlev.WeatherWidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Extended extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.extended);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int widgetID = 0;
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        SharedPreferences sp = getSharedPreferences(WidgetBase.WIDGET_PREF, MODE_PRIVATE);
        DateFormat format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
        String unit = sp.getString(WidgetBase.WIDGET_UNIT + widgetID, null);

        TextView tvcity = (TextView)findViewById(R.id.ext_city);
        tvcity.setText(sp.getString(WidgetBase.WIDGET_CITY + widgetID, null));

        TextView tvcountry = (TextView)findViewById(R.id.ext_country);
        tvcountry.setText(sp.getString(WidgetBase.WIDGET_COUNTRY + widgetID, null));

        TextView tvtime = (TextView)findViewById(R.id.ext_time);
        tvtime.setText(format.format(new Date()));

        TextView tvtemperature = (TextView)findViewById(R.id.ext_temperature);
        tvtemperature.setText(sp.getString(WidgetBase.WIDGET_TEMPERATURE + widgetID, null));

        TextView tvwindspeed = (TextView)findViewById(R.id.ext_windspeed);
        tvwindspeed.setText(sp.getString(WidgetBase.WIDGET_WINDSPEED + widgetID, null));

        TextView tvhumid = (TextView)findViewById(R.id.ext_humidity);
        tvhumid.setText(sp.getString(WidgetBase.WIDGET_HUMID + widgetID, null));

        TextView tvsunrise = (TextView)findViewById(R.id.ext_sunrise);
        tvsunrise.setText(sp.getString(WidgetBase.WIDGET_SUNRISE + widgetID, null));

        TextView tvsunset = (TextView)findViewById(R.id.ext_sunset);
        tvsunset.setText(sp.getString(WidgetBase.WIDGET_SUNSET + widgetID, null));

        TextView tvlongitude = (TextView)findViewById(R.id.ext_longitude);
        tvlongitude.setText(sp.getString(WidgetBase.WIDGET_LONGITUDE + widgetID, null));

        TextView tvlatitude = (TextView)findViewById(R.id.ext_latitude);
        tvlatitude.setText(sp.getString(WidgetBase.WIDGET_LATITUDE + widgetID, null));

        TextView tvdescription = (TextView)findViewById(R.id.ext_description);
        tvdescription.setText(sp.getString(WidgetBase.WIDGET_DESCRIPTION + widgetID, null));

        TextView tvpressure = (TextView)findViewById(R.id.ext_pressure);
        tvpressure.setText(sp.getString(WidgetBase.WIDGET_PRESSURE + widgetID, null));

        TextView tvtempmin = (TextView)findViewById(R.id.ext_tempmin);
        TextView tvtempmax = (TextView)findViewById(R.id.ext_tempmax);

        if (unit.equals("metric")) {
            tvtempmax.setText(sp.getString(WidgetBase.WIDGET_TEMPMAX + widgetID, null) + " °C");
            tvtempmin.setText(sp.getString(WidgetBase.WIDGET_TEMPMIN + widgetID, null) + " °C");

        }else{
            tvtempmin.setText(sp.getString(WidgetBase.WIDGET_TEMPMIN + widgetID, null) + " °F");
            tvtempmax.setText(sp.getString(WidgetBase.WIDGET_TEMPMAX + widgetID, null) + " °F");
        }

        TextView tvwinddegree = (TextView)findViewById(R.id.ext_winddegree);
        tvwinddegree.setText(sp.getString(WidgetBase.WIDGET_WINDDEGREE + widgetID, null));

        TextView tvcloudpercentage = (TextView)findViewById(R.id.ext_cloudpercentage);
        tvcloudpercentage.setText(sp.getString(WidgetBase.WIDGET_CLOUDPERCENTAGE + widgetID, null));

        ImageView weathericon = (ImageView) findViewById(R.id.ext_weatherimage);
        int drawableResourceId = this.getResources().getIdentifier("_" + sp.getString(WidgetBase.WIDGET_WEATHERICON + widgetID, null), "drawable", this.getPackageName());
        weathericon.setImageResource(drawableResourceId);
    }
}
