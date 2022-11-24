package ru.ayakovlev.WeatherWidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

public class Main extends WidgetBase {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
        }
}
