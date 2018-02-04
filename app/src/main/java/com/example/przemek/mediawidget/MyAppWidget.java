package com.example.przemek.mediawidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class MyAppWidget extends AppWidgetProvider {

    private static boolean firstPhotoIsSet;
    private static boolean firstMusicIsSet;
    private static MediaPlayer mediaPlayer;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);
        views.setOnClickPendingIntent(R.id.web_btn, CreateWebIntent(context));
        views.setOnClickPendingIntent(R.id.image_btn, CreatePendingIntent(context, "changePhoto"));
        views.setOnClickPendingIntent(R.id.play_btn, CreatePendingIntent(context, "startMusic"));
        views.setOnClickPendingIntent(R.id.stop_btn, CreatePendingIntent(context, "stopMusic"));
        views.setOnClickPendingIntent(R.id.next_btn, CreatePendingIntent(context, "changeMusic"));
        views.setImageViewResource(R.id.imageView3, R.drawable.dog);
        firstPhotoIsSet = true;
        firstMusicIsSet = true;
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        mediaPlayer = MediaPlayer.create(context, R.raw.dogbark);
        if (intent.getAction().equals("changePhoto")) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = mgr.getAppWidgetIds(new ComponentName(context, MyAppWidget.class));
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);
            if (firstPhotoIsSet) {
                views.setImageViewResource(R.id.imageView3, R.drawable.cat);
            } else {
                views.setImageViewResource(R.id.imageView3, R.drawable.dog);
            }
            firstPhotoIsSet = !firstPhotoIsSet;
            mgr.updateAppWidget(appWidgetIds, views);
        } else if (intent.getAction().equals("changeMusic")) {
            AppWidgetManager mgr2 = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = mgr2.getAppWidgetIds(new ComponentName(context, MyAppWidget.class));
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);
            if (firstMusicIsSet) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer = MediaPlayer.create(context, R.raw.catmeow);
                    mediaPlayer.start();
                } else {
                    mediaPlayer = MediaPlayer.create(context, R.raw.catmeow);
                }
            } else {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer = MediaPlayer.create(context, R.raw.dogbark);
                    mediaPlayer.start();
                } else {
                    mediaPlayer = MediaPlayer.create(context, R.raw.dogbark);
                }
            }
            firstMusicIsSet = !firstMusicIsSet;
            mgr2.updateAppWidget(appWidgetIds, views);
        } else if (intent.getAction().equals("startMusic")) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.dogbark);
                mediaPlayer.start();
            } else {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
        } else if (intent.getAction().equals("stopMusic")) {
            mediaPlayer.reset();
            if (firstMusicIsSet) {
                mediaPlayer = MediaPlayer.create(context, R.raw.dogbark);
            } else {
                mediaPlayer = MediaPlayer.create(context, R.raw.catmeow);
            }
        }
        super.onReceive(context, intent);
    }

    public static PendingIntent CreateWebIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("https://google.com"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return pendingIntent;
    }

    public static PendingIntent CreatePendingIntent(Context context, String action) {
        Intent intent = new Intent(context, MyAppWidget.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pendingIntent;
    }
}

