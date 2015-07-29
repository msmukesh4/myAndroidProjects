package com.example.trackingservice;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import org.xml.sax.InputSource;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemClickListener{

	// UI objects
	Button send,receive;
	TextView value,label,user;
	
	//Threads
	Thread sendDatagram;
	Thread queryThread;
	
	//Retrieved location form server  
	String UserLocationReceived;
	
	ListView userList;
	
	//Users list using this app...
	String[] friends;
	String provider1;
	
	//location 
	Location lastLoc;
	LocationManager mLocationManager;
	double latitude;
	double longitude;
	double altitude;
	String sendVal;
	
	/* Send one datagram
	 * change the "UserName" for every user
	 * for different user and accordingly change the 
	 * string array in the values directory
	 * 
	 * 
	 */
	
	String UserName = "Sachin";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		send = (Button) findViewById(R.id.send_location);
		receive = (Button) findViewById(R.id.receive_location);
		
		label = (TextView) findViewById(R.id.location_lable);
		user = (TextView) findViewById(R.id.user);
		value = (TextView) findViewById(R.id.location_value);
		label.setVisibility(View.GONE);
		value.setVisibility(View.GONE);
		userList = (ListView) findViewById(R.id.listView1);
		
		user.setText("Welcome :: "+UserName);
		//Friends is defined in the values folder as a string array you
		// can change for each user
		
		friends = getResources().getStringArray(R.array.Friends);
		
		//Generating the list view of the friends according to string array in 
		// values directory
		
		userList.setAdapter(new ArrayAdapter<String>
			(this, android.R.layout.simple_list_item_1,
				friends));
		userList.setOnItemClickListener(this);
		userList.setVisibility(View.INVISIBLE);
		
		// location manager is there is any location changes of the user
		// it will send the location updates to the server after a period of time
		// or after traveling some distance.
		
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		provider1 = mLocationManager.getBestProvider(criteria, false);
		Log.d("test", "Provider is "+provider1);
		
		LocationListener listener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub
				Log.d("test","status changed");
				
			}
			
			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub
				Log.d("test","provider enable");
			}
			
			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				Log.d("test","provider disable");
			}
			
			@Override
			public void onLocationChanged(Location arg0) {
				// TODO Auto-generated method stub
				Log.d("test","Location Changed");
				lastLoc = arg0;
				Log.d("test","Location "+lastLoc);
				latitude = lastLoc.getLatitude();
				longitude = lastLoc.getLongitude();
				altitude = lastLoc.getAltitude();
				sendVal = ""+longitude+" "+latitude+" "+altitude;				
				
			}
		};
		
		
		if (mLocationManager.isProviderEnabled(provider1)) {
			mLocationManager.requestLocationUpdates(provider1, 10000, 0, listener);
			
		}
		
		//initial assigning the location of the user...
		lastLoc = mLocationManager.getLastKnownLocation(provider1);
		if (lastLoc!=null) {
			
			latitude = lastLoc.getLatitude();
			longitude = lastLoc.getLongitude();
			altitude = lastLoc.getAltitude();
			Log.d("test", "longitude is "+longitude);
			Log.d("test", "latitude is "+latitude);
			sendVal = ""+longitude+" "+latitude+" "+altitude;	
			Log.d("test", "send value : "+sendVal);
			sendUDP();
			
		}
		
		
		mLocationManager.requestLocationUpdates(provider1, 10000, 10, listener);
		
		
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				userList.setVisibility(View.GONE);
				
				// calling the function for sending datagram packet
				// that contains the location of the user 
				sendUDP();
				
			}
		});
		
		receive.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				label.setVisibility(View.GONE);
				value.setVisibility(View.GONE);
				userList.setVisibility(View.VISIBLE);
				
			}
		});
		
	}
	
	
	
	
	// function for sending Datagram packet to sever...
	void sendUDP(){
		
		sendDatagram = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							label.setText("Estrablishing Connection...");
							label.setVisibility(View.VISIBLE);
							
						}
					});	
						
						Log.v("test", "Estrablishing Connection...");
						
						// Crate socket and bind it
						DatagramSocket clientsocket = new DatagramSocket(6001);																																									
						
						String send = UserName+"//"+sendVal+"//";
						
						//converting the string into bytes for transfer...
						byte[] data = send.getBytes("US-ASCII");
						
						//Making packet for transfer...
						DatagramPacket packet = new DatagramPacket(data, data.length);
						
						/*
						 * set the server address that is going to receive the user
						 * location periodically by proving the IP ADDRESS within the Network
						 * 
						 * Change the "IP" for different server
						 * 
						 */ 
							
						
						
						
						packet.setAddress(InetAddress.getByName("192.168.1.101"));
						
						// Port to which the server is going to receive the packet 
						packet.setPort(6000);
						
						// datagram socket is responsible for sending the packet created. 
						clientsocket.send(packet);
						
						//closing the datagram socket for reuse the nest time
						clientsocket.close();
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								value.setText("Sending Location updates...");
								value.setVisibility(View.VISIBLE);
							}
						});
						
						Log.v("test", "Sending Location updates...");
					
				}catch(Exception ee){
					ee.printStackTrace();
				}
			}
		});
		
		sendDatagram.start();
	}
	
	void requestLocation(final int pos){
		
		queryThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					
						
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							label.setText("Requesting location of "+friends[pos]);
							label.setVisibility(View.VISIBLE);
							value.setVisibility(View.GONE);
						}
					});
					
					Log.d("test", "tcp connection establishing.... ");
					
					// Establishing connection with the client app and receiving the location
					Socket tcpClient = new Socket("192.168.1.101", 8000);
					if (tcpClient.isConnected()) {
						Log.d("test", "tcp connection established ");
					}
					
					//Connection Established 
					InputStream is = tcpClient.getInputStream();
					OutputStream os = tcpClient.getOutputStream();
					
					//sending user name to server and requesting for user LOCATION 
					Log.v("test", "Requesting location for : "+friends[pos]);
					String friend = friends[pos]+"//";
					
					byte[] user = friend.getBytes("US-ASCII");
					os.write(user);
					
					// Reading Location of the user that is requested from the app...
					byte[] userLoc = new byte[100];
					is.read(userLoc);
					
					//Extracting location from received stream of bytes
					String loc = new String(userLoc, "US-ASCII");
					String[] splitting = loc.split("//");
					UserLocationReceived = splitting[0];
					
					
					Log.e("test","location of : "+friends+" is : "+UserLocationReceived);
										
					is.close();
					os.close();
					tcpClient.close();
					
					// displaying location of user received on the app screen
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							value.setText(UserLocationReceived);
							value.setVisibility(View.VISIBLE);
							
						}
					});
					
				}catch(Exception ee){
					ee.printStackTrace();
				}
			}
		});
		
		queryThread.start();
		
	}



	//when we will click on the "list view" it will call the "request location" method of the user
	//who is being clicked and display the location on the application screen
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		requestLocation(position);
		
	}

}
