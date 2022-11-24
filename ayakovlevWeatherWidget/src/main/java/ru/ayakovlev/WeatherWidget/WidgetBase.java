package ru.ayakovlev.WeatherWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.RemoteViews;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ayakovlev on 23.01.14.
 */
public abstract class WidgetBase extends AppWidgetProvider{

    public static String ACTION_EXTEND = "extend";
    public static String ACTION_INITIALUPDATE = "initialupdate";
    public final static String WIDGET_PREF = "widget_pref";
    public final static String WIDGET_COUNTRY = "widget_country_";
    public final static String WIDGET_CITY = "widget_city_";
    public final static String WIDGET_CITYID = "widget_city_id_";
    public final static String WIDGET_UNIT = "widget_unit_";
    public final static String WIDGET_TEMPERATURE = "widget_temperature_";
    public final static String WIDGET_WINDSPEED = "widget_windspeed_";
    public final static String WIDGET_HUMID = "widget_humid_";
    public final static String WIDGET_SUNRISE = "widget_sunrise_";
    public final static String WIDGET_SUNSET = "widget_sunset_";
    public final static String WIDGET_LONGITUDE = "widget_longitude_";
    public final static String WIDGET_LATITUDE = "widget_latitude_";
    public final static String WIDGET_DESCRIPTION = "widget_description_";
    public final static String WIDGET_PRESSURE = "widget_pressure_";
    public final static String WIDGET_TEMPMIN = "widget_temp_min_";
    public final static String WIDGET_TEMPMAX = "widget_temp_max_";
    public final static String WIDGET_WINDDEGREE = "widget_winddegree_";
    public final static String WIDGET_CLOUDPERCENTAGE = "widget_cloudpercentage_";
    public final static String WIDGET_WEATHERCODE = "widget_weathercode_";
    public final static String WIDGET_WEATHERICON = "widget_weathericon_";
    public final static String WIDGET_ID = "widget_id_";
    public final static String WIDGET_RAIN = "widget_rain_";
    public final static String WIDGET_SNOW = "widget_snow_";
    private static Context ctx;

    public WidgetBase () {
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        SharedPreferences.Editor editor = context.getSharedPreferences(WIDGET_PREF, MODE_PRIVATE).edit();
        for (int widgetID : appWidgetIds) {
            editor.remove(WIDGET_COUNTRY + widgetID);
            editor.remove(WIDGET_CITY + widgetID);
            editor.remove(WIDGET_CITYID + widgetID);
            editor.remove(WIDGET_LONGITUDE + widgetID);
            editor.remove(WIDGET_LATITUDE + widgetID);
            editor.remove(WIDGET_TEMPERATURE + widgetID);
            editor.remove(WIDGET_WINDSPEED + widgetID);
            editor.remove(WIDGET_HUMID + widgetID);
            editor.remove(WIDGET_SUNRISE + widgetID);
            editor.remove(WIDGET_SUNSET + widgetID);
            editor.remove(WIDGET_DESCRIPTION + widgetID);
            editor.remove(WIDGET_PRESSURE + widgetID);
            editor.remove(WIDGET_TEMPMIN + widgetID);
            editor.remove(WIDGET_TEMPMAX + widgetID);
            editor.remove(WIDGET_WINDDEGREE + widgetID);
            editor.remove(WIDGET_CLOUDPERCENTAGE + widgetID);
            editor.remove(WIDGET_WEATHERCODE + widgetID);
            editor.remove(WIDGET_WEATHERICON + widgetID);
            editor.remove(WIDGET_UNIT + widgetID);
            editor.remove(WIDGET_ID + widgetID);
            editor.remove(WIDGET_RAIN + widgetID);
            editor.remove(WIDGET_SNOW + widgetID);
            editor.commit();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (intent.getAction().equals(ACTION_EXTEND)) {

            Bundle extras = intent.getExtras();
            if (extras != null) {
                mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            }
            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                context.startActivity(intent);
            }
        }else if (intent.getAction().equals(ACTION_INITIALUPDATE)) {
            int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, Main.class));
           onUpdate(context, AppWidgetManager.getInstance(context), ids);
        }else {
            super.onReceive(context, intent);
        }
        //}else {
        //    super.onReceive(context, intent);
       // }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        ctx = context.getApplicationContext();

        SharedPreferences sp = context.getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);
        for (int id : appWidgetIds) {
            try {
                Intent intent = new Intent(context, Extended.class);
                intent.setAction(ACTION_EXTEND);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
                Uri data = Uri.withAppendedPath(Uri.parse("ayacall" + "://widget/id/"),String.valueOf(id));
                intent.setData(data);
                PendingIntent pIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                RemoteViews views = UpdateWidget(context, sp, id);
                views.setOnClickPendingIntent(R.id.pusharea , pIntent);
                appWidgetManager.updateAppWidget(id, views);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static JSONObject getJSON(String url) throws JSONException {

        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject(builder.toString());
    }

    public static boolean isNetworkOnline(Context context) {
        boolean status = false;
        try {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED && netInfo.isConnected()) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    static RemoteViews UpdateWidget(Context context, SharedPreferences sp, int widgetID) throws JSONException {

        String temperature;
        String windspeed;
        String humid;
        String temp_min;
        String temp_max;
        String rain = "";
        String snow = "";
        String description;
        String unit = sp.getString(WIDGET_UNIT + widgetID, null);
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        SharedPreferences.Editor editor = sp.edit();
        String[] iddesc = ctx.getResources().getStringArray(R.array.weatherconditions);
        String[] idcode = ctx.getResources().getStringArray(R.array.weathercode);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        DataDownload dd = new DataDownload(ctx, widgetID);

        try {

            dd.execute();
            JSONObject result = dd.get();

            if (result.has("rain")){
                if (result.getJSONObject("rain").has("3h")){
                    rain = result.getJSONObject("rain").getString("3h") + " mm";
                }else{
                    rain = result.getJSONObject("rain").getString("1h") + " mm";
                }
            }
            if (result.has("snow")){
                snow = result.getJSONObject("snow").getString("3h") + " mm";
            }

            if (unit.equals("metric")) {
                temperature = String.format("%.2f %s", Float.parseFloat(result.getJSONObject("main").getString("temp"))," °C");
            }else{
                temperature = String.format("%.2f %s", Float.parseFloat(result.getJSONObject("main").getString("temp"))," °F");
            }

            windspeed = result.getJSONObject("wind").getString("speed") + " " + (ctx.getResources().getString(R.string.windsuffix));
            humid = result.getJSONObject("main").getString("humidity")  + " %";
            String sunrise = sdf.format(Long.parseLong(result.getJSONObject("sys").getString("sunrise")) * (long) 1000);
            String sunset = sdf.format(Long.parseLong(result.getJSONObject("sys").getString("sunset")) * (long) 1000);
            String longitude = result.getJSONObject("coord").getString("lon");
            String latitude = result.getJSONObject("coord").getString("lat");
            String weathericon =result.getJSONArray("weather").getJSONObject(0).getString("icon");
            String id = result.getJSONArray("weather").getJSONObject(0).getString("id");
            String weathercode = result.getJSONArray("weather").getJSONObject(0).getString("id");
            String pressure = result.getJSONObject("main").getString("pressure") + " " + (ctx.getResources().getString(R.string.pascal));
            temp_min = String.format("%.2f", Float.parseFloat(result.getJSONObject("main").getString("temp_min")));
            temp_max = String.format("%.2f", Float.parseFloat(result.getJSONObject("main").getString("temp_max")));
            String winddegree = result.getJSONObject("wind").getString("deg") + " °";
            String cloudpercentage = result.getJSONObject("clouds").getString("all") + " %";

            editor.putString(WIDGET_TEMPERATURE + widgetID, temperature);
            editor.putString(WIDGET_WINDSPEED + widgetID, windspeed);
            editor.putString(WIDGET_HUMID + widgetID, humid);
            editor.putString(WIDGET_SUNRISE + widgetID, sunrise);
            editor.putString(WIDGET_SUNSET + widgetID, sunset);
            editor.putString(WIDGET_LONGITUDE + widgetID, longitude);
            editor.putString(WIDGET_LATITUDE + widgetID, latitude);
            editor.putString(WIDGET_PRESSURE + widgetID, pressure);
            editor.putString(WIDGET_TEMPMIN + widgetID, temp_min);
            editor.putString(WIDGET_TEMPMAX + widgetID, temp_max);
            editor.putString(WIDGET_WINDDEGREE + widgetID, winddegree);
            editor.putString(WIDGET_CLOUDPERCENTAGE + widgetID, cloudpercentage);
            editor.putString(WIDGET_WEATHERCODE + widgetID, weathercode);
            editor.putString(WIDGET_WEATHERICON + widgetID, "w" + weathericon);
            editor.putString(WIDGET_SNOW + widgetID, snow);
            editor.putString(WIDGET_RAIN + widgetID, rain);

            for (int i=0;i < idcode.length; i++){
                if(idcode[i].equals(id)){
                    description = iddesc[i];
                    editor.putString(WIDGET_DESCRIPTION + widgetID, description);
                }
            }

            editor.putString(WIDGET_ID + widgetID, id);
            editor.commit();

            }catch(Exception e){

            temperature = sp.getString(WIDGET_TEMPERATURE + widgetID, null);
            windspeed = sp.getString(WIDGET_WINDSPEED + widgetID, null);
            humid = sp.getString(WIDGET_HUMID + widgetID, null);
            temp_min = sp.getString(WIDGET_TEMPMIN + widgetID, null);
            temp_max = sp.getString(WIDGET_TEMPMAX + widgetID, null);
            e.printStackTrace();
            }

        views.setTextViewText(R.id.wid_temperature, temperature);
        views.setTextViewText(R.id.wid_windspeed, windspeed);
        views.setTextViewText(R.id.wid_humidity, humid);
        views.setTextViewText(R.id.wid_minmax, "(" + temp_min + "/" + temp_max + ")");
        views.setTextViewText(R.id.wid_cityname, sp.getString(WIDGET_CITY + widgetID, null));
        views.setTextViewText(R.id.wid_countyname, "[" + sp.getString(WIDGET_COUNTRY + widgetID, null) + "]");

        return views;
    }

    public void tryUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private static class DataDownload extends AsyncTask<Void, Integer, JSONObject> {
        private Context ctx;
        private int widgetID;

        public DataDownload(Context ctx, int widgetID){
            this.ctx = ctx;
            this.widgetID = widgetID;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            //TODO add waiting indicator on widget view...
        }

        protected void onPostExecute(JSONObject result) {
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            SharedPreferences sp = ctx.getSharedPreferences(WidgetBase.WIDGET_PREF, MODE_PRIVATE);
            String country = sp.getString(WIDGET_COUNTRY + widgetID, null);
            if (country == null) return null;
            String city = sp.getString(WIDGET_CITY + widgetID, null);
            String cityID =  sp.getString(WIDGET_CITYID + widgetID, null);
            String lon = sp.getString(WIDGET_LONGITUDE + widgetID, null);
            String lat = sp.getString(WIDGET_LATITUDE + widgetID, null);
            String unit = sp.getString(WIDGET_UNIT + widgetID, null);
            String url = "http://api.openweathermap.org/data/2.5/weather?";

            if (!lat.isEmpty() && !lon.isEmpty()){
                url = url + "lat=" + lat + "&lon=" + lon;
            }
            if (!cityID.isEmpty()){
                url = url + "id=" + cityID;
            }else{
                url = url + "q=" + city + "," + country;
            }

            url = url + "&units=" + unit;
            url = url + "&APPID=17aefdee1b81db8a1e2d1b3ec2df3c5d";

            JSONObject resp =null;
            if (isNetworkOnline(ctx)) {
                try {
                    resp = getJSON(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return resp;
        }
    }
}


