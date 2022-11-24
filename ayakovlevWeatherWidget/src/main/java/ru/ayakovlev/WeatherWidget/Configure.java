package ru.ayakovlev.WeatherWidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

public class Configure extends Activity {

    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent resultValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.config);

        Spinner spinner = (Spinner) findViewById(R.id.conf_countryspinner);
        String[] countries = this.getResources().getStringArray(R.array.countries);
        ArrayAdapter<String> adapter = new MyAdapter(this, R.layout.countryspinner,countries);
        adapter.setDropDownViewResource(R.layout.countryspinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                String[] iso = getResources().getStringArray(R.array.iso);
                SharedPreferences sp = getSharedPreferences(WidgetBase.WIDGET_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(WidgetBase.WIDGET_COUNTRY + widgetID, iso[position].toLowerCase());
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //
            }
        });

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            this.finish();
        }

        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        setResult(RESULT_CANCELED, resultValue);
    }

    public class MyAdapter extends ArrayAdapter<String> {

        String[] countries;
        private Context context;

        public MyAdapter(Context ctx, int txtViewResourceId, String[] objects) {
            super(ctx, txtViewResourceId, objects);
            this.context = ctx;
            countries = objects.clone();
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return CustomView(position, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return CustomView(position, parent);
        }

        public View CustomView (int position, ViewGroup parent){

            String[] iso = context.getResources().getStringArray(R.array.iso);
            LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.countryspinner, parent,false);

            TextView main_text = null;
            if (mySpinner != null) {
                main_text = (TextView) mySpinner.findViewById(R.id.countryname);
            }
            if (main_text != null) {
                main_text.setText(countries[position]);
            }

            ImageView left_icon = null;
            if (mySpinner != null) {
                left_icon = (ImageView) mySpinner.findViewById(R.id.countrypic);
            }
            int iconid =  this.context.getResources().getIdentifier("_" + iso[position].toLowerCase(), "drawable", this.context.getPackageName());
            if (left_icon != null) {
                left_icon.setImageResource(iconid);
            }

            return mySpinner;
        }
    }

    public void onClick(View v) throws JSONException {

        SharedPreferences sp = getSharedPreferences(WidgetBase.WIDGET_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(WidgetBase.WIDGET_CITY + widgetID, ((EditText) findViewById(R.id.conf_cityname)).getText().toString());
        editor.putString(WidgetBase.WIDGET_CITYID + widgetID, ((EditText) findViewById(R.id.conf_citycode)).getText().toString());
        editor.putString(WidgetBase.WIDGET_LONGITUDE + widgetID, ((EditText) findViewById(R.id.conf_longitude)).getText().toString());
        editor.putString(WidgetBase.WIDGET_LATITUDE + widgetID, ((EditText) findViewById(R.id.conf_latitude)).getText().toString());

        if (((RadioButton) findViewById(R.id.conf_metricbutton)).isChecked()){
            editor.putString(WidgetBase.WIDGET_UNIT + widgetID, "metric");
        }else {
            editor.putString(WidgetBase.WIDGET_UNIT + widgetID, "imperial");
        }
        editor.commit();

        //TODO try obtain widget instance from appWidgetManager and call properly onUpdate
        //TODO ...or instancise Configure for widgets similar like Widget base<-->Main
        //TODO ...or use some events

        //AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        //RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.widget);
        //appWidgetManager.updateAppWidget(widgetID, views);
        //Intent resultValue = new Intent();
        //resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);

        //upd.onUpdate(this, AppWidgetManager.getInstance(this), new int[]{widgetID});

        Intent update = new Intent(WidgetBase.ACTION_INITIALUPDATE);
        update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        LocalBroadcastManager.getInstance(this).sendBroadcast(update);

        setResult(RESULT_OK, resultValue);
        this.finish();
    }
}
