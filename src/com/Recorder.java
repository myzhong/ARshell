package com;

import java.util.List;

import com.FileOps;
import com.MainActivity;
import com.arshell.Result;
import com.google.android.gms.location.DetectedActivity;

import android.os.Environment;

public class Recorder implements Runnable {
	
	private static String logPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyLogFiles/";
	public static FileOps fo = new FileOps();
	
	private static long ts = 0;
	
	// evaluate AR service
	@Override
	public void run() {
		String logFilePath = logPath + "JPMC_ARwithSignal_" + fo.getTime() + ".txt";
		fo.newFileCheck(logFilePath);
		long timeline = 0;
		String logContent = "";
		while (MainActivity.RECORD_FLAG) {
			int GARshellRes = Result.getActivity();
			List<DetectedActivity> GGActivities = Result.getGGProbableActivities();
			long currentTs = Result.getLastUpdateTime();
			if (currentTs != ts) {
				// prepare file str
				logContent = timeline + " " + MainActivity.humanLabel + " " + (MainActivity.GSMSIGNAL * 2 - 113) + " "; 
				if (GGActivities != null && GGActivities.size() > 0) {
					for (int i = 0; i < GGActivities.size(); i ++) {
						logContent += GGActivities.get(i).getType() + " " 
								+ GGActivities.get(i).getConfidence() + " ";
					}
				}
				logContent += currentTs + " " + GARshellRes + "\r\n";
				// write file
				fo.wirteFile(logContent, logFilePath);
				ts = currentTs;
				timeline ++;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
