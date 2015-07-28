package com.example.gps_coordinates;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	String provider;
	 TextView latitude1, longitude1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
			
		longitude1 = (TextView) findViewById(R.id.vlongitude);
		 latitude1 =  (TextView) findViewById(R.id.vlatitude);
		TextView distance1 = (TextView) findViewById(R.id.vdistance);
		final TextView speed1 = (TextView) findViewById(R.id.vspeed); 
		
		
		
		
		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		
		Criteria criteria = new Criteria();
	     provider = manager.getBestProvider(criteria, false);
	    Log.v("provider","provider is: "+provider);
	    
        final LocationListener listner = new LocationListener() {
			
			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub
				Log.v("location","status changed to:");
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
				longitude1.setText(""+lastloc.getLongitude());
				latitude1.setText(""+ lastloc.getLatitude());
				speed1.setText(""+ lastloc.getSpeed());
				
			}
		};
	    
	    if (manager.isProviderEnabled(provider)) {
			manager.requestLocationUpdates(provider,
					10,1,listner);
		
		final Location lastloc = manager.getLastKnownLocation(provider);
		if(lastloc!=null){
			double latitu = lastloc.getLatitude();
			double longi = lastloc.getLongitude();
			Log.v("logitude", "longitude is "+longi);
			Log.v("latitude", "latitude is "+latitu);
			latitude1.setText(""+latitu);
			longitude1.setText(""+longi);
		}
		else{
			latitude1.setText("No values");
			longitude1.setText("No values");
		}
		
		manager.requestLocationUpdates(provider, 10, 10, listner);
		
		
		Button start = (Button) findViewById(R.id.start_service);
		start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e("LEE", "STARTING OVER");
				manager.requestLocationUpdates(provider, 10, 1, listner);
			}
		});
		
		Button stop = (Button) findViewById(R.id.stop_service);
		stop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("LEE", "STOPPING THE PROCESS");
				manager.removeUpdates(listner);
				finish();
				
			}
		});
		
		Button update =  (Button) findViewById(R.id.update_values);
		update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				manager.requestLocationUpdates(provider, 10, 10, listner);
				
			}
		});
		
		Button ext = (Button) findViewById(R.id.exit);
		ext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});

	}
	
	    
	}
	
}

