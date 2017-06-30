/*
 * ARshell 2014
 *
 * Authors: Mingyang Zhong, Jiahui Wen, Jadwiga Indulska, Peizhao Hu
 * Affiliations: School of Information Technology and Electrical Engineering, The University of Queensland, Australia
 *				National ICT Australia (NICTA), Australia
 * 				Rochester Institute of Technology, USA
 */

package com;

import com.arshell.ActivityUtils;
import com.arshell.ARshell;
import com.arshell.Result;
import com.example.android.activityrecognition.R;
import com.power.PowerDetails;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	ARshell as = new ARshell();
	// Recording flag for evaluation
	public static boolean RECORD_FLAG = false;
	// Human label for evaluation
	public static int humanLabel = 4;
    /*
     *  Intent filter for incoming broadcasts from the
     *  IntentService.
     */
    IntentFilter mBroadcastFilter;
    // Instance of a local broadcast manager
    private LocalBroadcastManager mBroadcastManager;
    
    TextView infoView;
    public static int GSMSIGNAL = 0;
    public static String power = "";

    /*
     * Set main UI layout, get a handle to the ListView for logs, and create the broadcast
     * receiver.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the main layout
        setContentView(R.layout.activity_main);
        // Set the broadcast receiver intent filer
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        // Create a new Intent filter for the broadcast receiver
        mBroadcastFilter = new IntentFilter(ActivityUtils.ACTION_REFRESH_STATUS_LIST);
        mBroadcastFilter.addCategory(ActivityUtils.CATEGORY_LOCATION_SERVICES);
        
        /**
         * signal monitor for activity recognition based on signal
         */
        TelephonyManager telM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telM.listen(new PhoneStateMonitor(),
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
				| PhoneStateListener.LISTEN_SERVICE_STATE
				| PhoneStateListener.LISTEN_CELL_LOCATION);
		
        infoView = (TextView) findViewById(R.id.info);
        infoView.setText("Press the button!");

    }
    
    public class PhoneStateMonitor extends PhoneStateListener {
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);
			GSMSIGNAL = signalStrength.getGsmSignalStrength();
		}
    }
    
    public void onStill(View view) {
    	humanLabel = 3;
    }
    public void onWalk(View view) {
    	humanLabel = 7;
    }
    public void onRun(View view) {
    	humanLabel = 8;
    }
    public void onCycle(View view) {
    	humanLabel = 1;
    }
    public void onVehicle(View view) {
    	humanLabel = 0;
    }
    public void onPower(View view) {
    	power = PowerDetails.batteryLevel(getApplicationContext());
    }
    
    public void onRecognising(View view) {
    	// Initialise ARshell
    	as.init(this, 0);
    	// Start recognition
    	as.start(this);
    	RECORD_FLAG = true;
    	new Thread(new Recorder()).start();
    }

    /*
     * Register the broadcast receiver and update the log of activity updates
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Register the broadcast receiver
        mBroadcastManager.registerReceiver(
                updateListReceiver,
                mBroadcastFilter);
        // Update TextView
        updateTextView();
    }

    /*
     * Create the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /*
     * Handle selections from the menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // Clear the log display and stop activity recognition
            case R.id.menu_item_clearlog:
            	// Stop recognition
            	as.stop(this);
            	RECORD_FLAG = false;
            	humanLabel = 4;
            	infoView.setText("Recognition stopped!");
                // Continue by passing true to the menu handler
                return true;
            // Display the update log
            case R.id.menu_item_showlog:
                // Update TextView
            	updateTextView();
                // Continue by passing true to the menu handler
                return true;
            // For any other choice, pass it to the super()
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Unregister the receiver during a pause
     */
    @Override
    protected void onPause() {
        // Stop listening to broadcasts when the Activity isn't visible.
        mBroadcastManager.unregisterReceiver(updateListReceiver);
        super.onPause();
    }

    /**
     * Broadcast receiver that receives activity update intents
     * It checks to see if the ListView contains items. If it
     * doesn't, it pulls in history.
     * This receiver is local only. It can't read broadcast Intents from other apps.
     */
    BroadcastReceiver updateListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	updateTextView();
        }
    };
    
    public void updateTextView() {
    	/*
    	 *  Display the results, and the results can be the get any time or be sent when activity changes.
    	 */
    	infoView.setText("Google: " + 
    			Result.getNameFromType(Result.getGGMostProbableActivity()) + // Get the original results of the AR service
    			"\nARshell: " + 
    			Result.getNameFromType(Result.getActivity()) + // Get the results of ARshell
    			"\nLast update: " + 
    			Result.getLastUpdateTime() +
    			"\nSignal: " +
    			(GSMSIGNAL * 2 - 113) + "dBm");
    }
}
