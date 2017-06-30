/*
 * ARshell 2014
 *
 * Authors: Mingyang Zhong, Jiahui Wen, Jadwiga Indulska, Peizhao Hu
 * Affiliations: School of Information Technology and Electrical Engineering, The University of Queensland, Australia
 *				National ICT Australia (NICTA), Australia
 * 				Rochester Institute of Technology, USA
 */

package com.arshell;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.content.Context;

public class ARshell {
	
    // The activity recognition update request object
    private DetectionRequester mDetectionRequester;
    // The activity recognition update removal object
    private DetectionRemover mDetectionRemover;
    
    /**
     * Initialise ARshell
     * 
     * @param view The view that triggered this method
     * @param interval The value of recognition interval
     */
	public void init (Context context, int interval) {
        // Get detection requester and remover objects
        mDetectionRequester = new DetectionRequester(context);
        mDetectionRemover = new DetectionRemover(context);
        ActivityUtils.DETECTION_INTERVAL_SECONDS = interval;
    }
    
    /**
     * Start by requesting activity recognition
     * 
     * @param view The view that triggered this method.
     */
    public void start(Context context) {
        // Check for Google Play services
        if (!servicesConnected(context)) {
            return;
        }
        // Pass the update request to the requester object
        mDetectionRequester.requestUpdates();
    }

    /**
     * Stop by canceling activity recognition
     * 
     * @param view The view that triggered this method.
     */
    public void stop(Context context) {
        // Check for Google Play services
        if (!servicesConnected(context)) {
            return;
        }
        // Pass the remove request to the remover object
        mDetectionRemover.removeUpdates(mDetectionRequester.getRequestPendingIntent());
        /*
         * Cancel the PendingIntent. Even if the removal request fails, canceling the PendingIntent
         * will stop the updates.
         */
        mDetectionRequester.getRequestPendingIntent().cancel();
    }
    
    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected(Context context) {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            return false;
        }
    }

}
