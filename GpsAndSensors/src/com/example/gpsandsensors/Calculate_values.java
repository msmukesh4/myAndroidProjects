package com.example.gpsandsensors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class Calculate_values extends Service {

    LocationManager manager;
    Criteria criteria;
    String provider;
    LocationListener listener;
    Location curr_loc;
    
	@Override
	public IBinder onBind(Intent intent) {
		
		Log.v("LEE", "within onBind method");
		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		provider = manager.getBestProvider(criteria,false);
		Log.v("LEE", "Location provider :: "+provider);
		Log.v("LEE", "Initialing listner");
        listener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub
				Log.v("location","status changed bu location listner");
			}
			
			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub
				Log.v("location","provider enabled");

				
			}
			
			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				Log.v("location","provider disabled");

			}
			
			@Override
			public void onLocationChanged(Location lastloc) {
				Log.v("LEE","location changed : "+lastloc);
				curr_loc = lastloc; 
			}
		};
		if (manager.isProviderEnabled(provider)) {
			manager.requestLocationUpdates(provider, 10, 1, listener);
		}
		Log.v("LEE", "initialising local_location");
		Location local_loc = manager.getLastKnownLocation(provider);
		if(local_loc!= null){
			Log.v("LEE", "Background Location is found");
			curr_loc = local_loc;
		}
		else{
			Log.v("LEE", "Background Location Is NULL :(");
		}
		Log.v("LEE", "--- End of onBind() method");
		return binder_obj;
	}
	
	// defining AIDL Methods...
	
	private final Sensor_aidl.Stub binder_obj = new Sensor_aidl.Stub() {

		@Override
		public Location send_Location() throws RemoteException {
			// TODO Auto-generated method stub
			return curr_loc;
		}

		@Override
		public float distance(int steps) throws RemoteException {
			float distance= (float) (steps*.75);
			return distance;
			
		}
	};
	
}
