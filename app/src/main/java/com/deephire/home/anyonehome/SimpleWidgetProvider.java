package com.deephire.home.anyonehome;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleWidgetProvider extends AppWidgetProvider {
    private static final String SYNC_CLICKED = "automaticWidgetSyncButtonClick";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            ComponentName watchWidget;
            watchWidget = new ComponentName(context, SimpleWidgetProvider.class);

            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.simple_widget);

            remoteViews.setImageViewResource(R.id.russellImage, R.drawable.russellyes);

            remoteViews.setOnClickPendingIntent(R.id.nickImage, getPendingSelfIntent(context, SYNC_CLICKED));
            remoteViews.setOnClickPendingIntent(R.id.russellImage, getPendingSelfIntent(context, SYNC_CLICKED));
            remoteViews.setOnClickPendingIntent(R.id.steveImage, getPendingSelfIntent(context, SYNC_CLICKED));

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        if (SYNC_CLICKED.equals(intent.getAction())) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.simple_widget);
            watchWidget = new ComponentName(context, SimpleWidgetProvider.class);

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);

            try {
                JSONObject jsonObject = getHomeStatus("http://48cda868.ngrok.io/scan");
                if (jsonObject.get("Nick").equals("True")) {
                    remoteViews.setImageViewResource(R.id.russellImage, R.drawable.nickyes);
                } else {
                    remoteViews.setImageViewResource(R.id.russellImage, R.drawable.nickno);
                }
                if (jsonObject.get("Roos").equals("True")) {
                    remoteViews.setImageViewResource(R.id.russellImage, R.drawable.russellyes);
                } else {
                    remoteViews.setImageViewResource(R.id.russellImage, R.drawable.russellno);
                }
                if (jsonObject.get("Steve").equals("True")) {
                    remoteViews.setImageViewResource(R.id.russellImage, R.drawable.steveyes);
                } else {
                    remoteViews.setImageViewResource(R.id.russellImage, R.drawable.steveno);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static JSONObject getHomeStatus(String urlString) throws IOException, JSONException {

        HttpURLConnection urlConnection = null;

        URL url = new URL(urlString);

        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(20000 /* milliseconds */);
        urlConnection.setConnectTimeout(30000 /* milliseconds */);

        urlConnection.setDoOutput(true);

        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

        char[] buffer = new char[1024];

        String jsonString = new String();

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        jsonString = sb.toString();

        System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

}
