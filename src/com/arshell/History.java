/*
 * ARshell 2014
 *
 * Authors: Mingyang Zhong, Jiahui Wen, Jadwiga Indulska, Peizhao Hu
 * Affiliations: School of Information Technology and Electrical Engineering, The University of Queensland, Australia
 *				National ICT Australia (NICTA), Australia
 * 				Rochester Institute of Technology, USA
 */

package com.arshell;

import java.util.LinkedList;
import java.util.Queue;

import com.google.android.gms.location.ActivityRecognitionResult;

public class History {
	
	@SuppressWarnings("unused")
	private static ActivityRecognitionResult res = null;
	// recognition history (size 10)
	public static final int SAMPLE_SIZE = 10;
	private static Queue<ActivityRecognitionResult> queue = new LinkedList<ActivityRecognitionResult>();
	
	public static void offerElement(ActivityRecognitionResult r) { 
		queue.offer(r);
		res = r;
	}
	
	public static ActivityRecognitionResult getElement() { 
		return queue.peek();
	}
	
	public static ActivityRecognitionResult pollElement() { 
		return queue.poll();
	}
	
	public static int queueSize() { 
		return queue.size();
	}
	
}
