/*
 * ARshell 2014
 *
 * Authors: Mingyang Zhong, Jiahui Wen, Jadwiga Indulska, Peizhao Hu
 * Affiliations: School of Information Technology and Electrical Engineering, The University of Queensland, Australia
 *				National ICT Australia (NICTA), Australia
 * 				Rochester Institute of Technology, USA
 */

package com.arshell;

import com.google.android.gms.location.ActivityRecognitionResult;

public class Markov {
	private static final int numOfState = 9;
	private static final int step = 1;
	private static final int threshold = 60;
	private static int state = 5;
	private static boolean firstTime = true;
	private static int[] probability = new int[numOfState];
	
	/**
	 * Get Markov prediction
	 */
	public int getMarkovPrediction (ActivityRecognitionResult res) {
		return predict(format_data(res));
	}
	
	/**
	 * Prepare input data for Markov
	 */
	private int[] format_data (ActivityRecognitionResult res) {
		int[] data = new int[9];
		for(int i = 0; i < data.length; i++)
    		data[i] = 0;
		for(int i = 0; i < res.getProbableActivities().size(); i++)
    		data[res.getProbableActivities().get(i).getType()] = 
    			res.getProbableActivities().get(i).getConfidence();
		return data;
	}
	
	/**
	 * Markov process
	 * @param data[]: confidence corresponding to activities 0-8
	 * @return prediction result
	 */
	private int predict (int[] data) {
		int returnState = 0;
		if (max(data) == 5 || max(data) == 4)
			returnState = state;
		else if (state != max(data)) {
			if (state == 5) {
				copy_prob(data);
				state = max(probability);
				returnState = state;
			} else {
				if (data[max(data)] > threshold) {
					copy_prob(data);
					state = max(probability);
					returnState = state;
				} else {
					if (firstTime) {
						copy_prob(data);
						firstTime = false;
					}
					update_prob(data);
					probability[4] = probability[5] = 0;
					if (max(probability) > threshold)
						state = max(probability);
					returnState = state;
				}
			}
		} else {
			if (!firstTime)
				firstTime = true;
			copy_prob(data);
			returnState = state;
		}
		return returnState;
	}

	public void update_prob(int[] data) {
		for (int i = 0; i < data.length; i++) {
			if (data[i] > 0)
				probability[i] += step;
		}
	}

	public void copy_prob(int[] data) {
		for (int i = 0; i < data.length; i++)
			probability[i] = data[i];
	}

	public int max(int[] data) {
		int maximal = 0;
		int index = data.length - 1;
		for (int i = data.length - 1; i >= 0; i--) {
			if (data[i] > maximal) {
				maximal = data[i];
				index = i;
			}
		}
		return index;
	}

}
