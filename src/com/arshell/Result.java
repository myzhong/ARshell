/*
 * ARshell 2014
 *
 * Authors: Mingyang Zhong, Jiahui Wen, Jadwiga Indulska, Peizhao Hu
 * Affiliations: School of Information Technology and Electrical Engineering, The University of Queensland, Australia
 *				National ICT Australia (NICTA), Australia
 * 				Rochester Institute of Technology, USA
 */

package com.arshell;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.location.DetectedActivity;

public class Result {
	
	private static int activity = 4;
	private static int GGMostProbableActivity = 4;
	private static int GGMostProbableActivityConfidence = 0;
	private static List<DetectedActivity> GGProbableActivities = new ArrayList<DetectedActivity>();
	private static long lastUpdateTime = 0;

	
	public static int getActivity() {
		return activity;
	}

	public static void setActivity(int a) {
		activity = a;
	}

	public static int getGGMostProbableActivity() {
		return GGMostProbableActivity;
	}

	public static void setGGMostProbableActivity(int a) {
		GGMostProbableActivity = a;
	}

	public static List<DetectedActivity> getGGProbableActivities() {
		return GGProbableActivities;
	}

	public static void setGGProbableActivities(
			List<DetectedActivity> aList) {
		GGProbableActivities = aList;
	}

	public static long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public static void setLastUpdateTime(long t) {
		lastUpdateTime = t;
	}

	public static int getGGMostProbableActivityConfidence() {
		return GGMostProbableActivityConfidence;
	}

	public static void setGGMostProbableActivityConfidence(
			int c) {
		GGMostProbableActivityConfidence = c;
	}

	/**
     * Map detected activity types to strings
     *
     * @param activityType The detected activity type
     * @return A user-readable name for the type
     */
    public static String getNameFromType(int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.RUNNING:
                return "running";
            case DetectedActivity.WALKING:
                return "walking";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }
	
}
