/*
 * ARshell 2014
 *
 * Authors: Mingyang Zhong, Jiahui Wen, Jadwiga Indulska, Peizhao Hu
 * Affiliations: School of Information Technology and Electrical Engineering, The University of Queensland, Australia
 *				National ICT Australia (NICTA), Australia
 * 				Rochester Institute of Technology, USA
 */

package com.arshell;

import com.example.android.activityrecognition.R;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Service that receives ActivityRecognition updates. It receives updates
 * in the background, even if the main Activity is not visible.
 */
public class ActivityRecognitionIntentService extends IntentService {
    // Formats the timestamp in the log
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSSZ";
    // A date formatter
    private SimpleDateFormat mDateFormat;
    // Store the app's shared preferences repository
    private SharedPreferences mPrefs;
    // Log for history
//    private History his = new History();
    // Markov
    private Markov m = new Markov();
    
    public ActivityRecognitionIntentService() {
        // Set the label for the service's background thread
        super("ActivityRecognitionIntentService");
    }

    /**
     * Called when a new activity detection update is available.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Get a handle to the repository
        mPrefs = getApplicationContext().getSharedPreferences(ActivityUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        // Get a date formatter, and catch errors in the returned timestamp
        try {
            mDateFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance();
        } catch (Exception e) {
            Log.e(ActivityUtils.APPTAG, getString(R.string.date_format_error));
        }
        // Format the timestamp according to the pattern, then localize the pattern
        mDateFormat.applyPattern(DATE_FORMAT_PATTERN);
        mDateFormat.applyLocalizedPattern(mDateFormat.toLocalizedPattern());
        // If the intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            // Log the update
//            his.offerElement(result);
            // Get the most probable activity from the list of activities in the update
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();
            // Get the confidence percentage for the most probable activity
            int confidence = mostProbableActivity.getConfidence();
            // Get the type of activity
            int activityType = mostProbableActivity.getType();
            
            Result.setGGMostProbableActivity(activityType);
            Result.setGGMostProbableActivityConfidence(confidence);
            Result.setGGProbableActivities(result.getProbableActivities());
            Result.setLastUpdateTime(System.currentTimeMillis());
            /**
             * Markov process
             */
            Result.setActivity(m.getMarkovPrediction(result));
            
            // Check to see if the repository contains a previous activity
            if (!mPrefs.contains(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE)) {
                // This is the first type an activity has been detected. Store the type
                Editor editor = mPrefs.edit();
                editor.putInt(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE, activityType);
                editor.commit();
            // If the repository contains a type
            } else if (
                       // The activity has changed from the previous activity
                       activityChanged(activityType)
                       // The confidence level for the current activity is > 60%
                       && (confidence >= 60)) {
                // Notify the user
                sendNotification();
            }
        }
    }

    /**
     * Post a notification to the user. The notification prompts the user to click it to
     * open the device's GPS settings
     */
    private void sendNotification() {
        // Create a notification builder that's compatible with platforms >= version 4
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext());
        // Set the title, text, and icon
        builder.setContentTitle(getString(R.string.app_name))
               .setContentText(getString(R.string.turn_on_GPS))
               .setSmallIcon(R.drawable.ic_notification)
               // Get the Intent that starts the Location settings panel
               .setContentIntent(getContentIntent());
        // Get an instance of the Notification Manager
        NotificationManager notifyManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Build the notification and post it
        notifyManager.notify(0, builder.build());
    }
    
    /**
     * Get a content Intent for the notification
     *
     * @return A PendingIntent that starts the device's Location Settings panel.
     */
    private PendingIntent getContentIntent() {
        // Set the Intent action to open Location Settings
        Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        // Create a PendingIntent to start an Activity
        return PendingIntent.getActivity(getApplicationContext(), 0, gpsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Tests to see if the activity has changed
     *
     * @param currentType The current activity type
     * @return true if the user's current activity is different from the previous most probable
     * activity; otherwise, false.
     */
    private boolean activityChanged(int currentType) {
        // Get the previous type, otherwise return the "unknown" type
        int previousType = mPrefs.getInt(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE, DetectedActivity.UNKNOWN);
        // If the previous type isn't the same as the current type, the activity has changed
        if (previousType != currentType) {
            return true;
        // Otherwise, it hasn't.
        } else {
            return false;
        }
    }

}
