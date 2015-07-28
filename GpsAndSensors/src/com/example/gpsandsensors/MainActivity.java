package com.example.gpsandsensors;

import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity{

	ServiceConnection con;
	Location current;
	TextView longitude1,latitude1,distance_textView;
	protected Sensor_aidl sensor_aidl_obj;
	Intent it;
	float distance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		longitude1=(TextView) findViewById(R.id.longitude);
		latitude1=(TextView) findViewById(R.id.latitude);
		distance_textView=(TextView) findViewById(R.id.distance);
		latitude1.setText("no values");
		longitude1.setText("no values");
		
		con = new ServiceConnection() {
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.v("LEE", "Service connected");
				sensor_aidl_obj=Sensor_aidl.Stub.asInterface((IBinder)service);
				
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.v("LEE", "Service disconnected");
				sensor_aidl_obj=null;
				
			}
		};
		if (sensor_aidl_obj==null) {
			Log.v("LEE", "aidl value is null so new intent is created");
			it =  new Intent();
			it.setAction("action_set");
			Log.v("LEE", "binding service");
			bindService(it, con,Service.BIND_AUTO_CREATE);
		}
		
		
		
		try {
			current = sensor_aidl_obj.send_Location();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (current!=null) {
			latitude1.setText((int) current.getLatitude());
			longitude1.setText((int) current.getLongitude());
		}
	}
	@Override
	protected void onDestroy() {
		Log.v("LEE", "onDestroy is called service is unBinded");
		unbindService(con);
		super.onDestroy();
	}
	
		
		

	
}
