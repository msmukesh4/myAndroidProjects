package com.example.bluetoothparing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.R.string;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View.OnClickListener;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnItemClickListener{


	//Connection objects
	BluetoothServerSocket bluetoothserversocket;
	BluetoothSocket bluetoothsocket;
	BluetoothDevice bluetoothDevice;
	private final UUID my_UUID = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");
	
	// Objects declarations
	Button Enable,Disable,Discoverable,pairedDevices,searchResult;
	TextView menabled,mdiscovery,tv2,tv3;
	BluetoothAdapter mBluetoothAdapter;
	BroadcastReceiver receiver;
	Set<BluetoothDevice> pairedDevice;
	ArrayList<BluetoothDevice> devices;
	List<String> strings;
	ArrayAdapter<String> arrayAdapter;
	Thread client,server,sending,receiving;
	InputStream inputStream;
	OutputStream outputStream;
	int position_clicked=0,contact_clicked=0,onclickRan=0;
	String message,name,contactNumber;
	
	Uri uri;
	Cursor people;
	String[] projection;
	
	
	// listview
	ListView pair;
	
	//Intents
	Intent intent;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Contacts objects
		String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
		uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;						
		people = getContentResolver().query(uri, projection, null, null, null);

		
		
		//Objects creations
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevice = mBluetoothAdapter.getBondedDevices();
		strings =  new ArrayList<String>();
		
		devices = new ArrayList<BluetoothDevice>();
		pair = (ListView) findViewById(R.id.list);
		pair.setVisibility(View.GONE);
		Enable  = (Button) findViewById(R.id.bEnable);
		Disable  = (Button) findViewById(R.id.bDisable);
		Discoverable  = (Button) findViewById(R.id.bDiscoverable);
		pairedDevices  = (Button) findViewById(R.id.paired);
		searchResult  = (Button) findViewById(R.id.searchDevices);
		menabled = (TextView) findViewById(R.id.enableMessage);
		mdiscovery = (TextView) findViewById(R.id.discoverableMessage);
		tv2 = (TextView) findViewById(R.id.textView2);
		tv3 = (TextView) findViewById(R.id.textView3);
		tv2.setText("[]");
		
		
		// setting message to be transfered
		 message= " :: message ::";
		
		if(!mBluetoothAdapter.isEnabled())
		Disable.setVisibility(View.GONE);
		
		//Hiding Messages
		menabled.setVisibility(View.GONE);
		mdiscovery.setVisibility(View.GONE);
		
		//Enabling Bluetooth Connection
		Enable.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mBluetoothAdapter.enable();
			// startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),0);
			 Disable.setVisibility(View.VISIBLE);
			 server.start();
			}
		});
		
		//Disabling Bluetooth Connection
		Disable.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			 if (mBluetoothAdapter.isEnabled()) {
				mBluetoothAdapter.disable();
				server.stop();
			}
			 Disable.setVisibility(View.GONE);
			 Enable.setVisibility(View.VISIBLE);
			}
		});
		
		// Setting Bluetooth To discoverable 
		Discoverable.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					Intent discoverableIntent = new	Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
					discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
					startActivity(discoverableIntent);
					Disable.setVisibility(View.VISIBLE);
					mdiscovery.setVisibility(View.VISIBLE);
				}
			
		});
		
		// Generating Pair list
		
		pairedDevices.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				strings.clear();
				devices.clear();
				mBluetoothAdapter.cancelDiscovery();
				if (pairedDevice.size()>0) {
					for (BluetoothDevice device : pairedDevice) {
						tv2.setText("paired devices");
						devices.addAll(pairedDevice);
						strings.add(device.getName()+"\n"+device.getAddress());
						
						pair.setVisibility(View.VISIBLE);
					}
					
				}else{
					
					tv2.setText("No paired devices");
				}
				arrayAdapter.notifyDataSetChanged();
				
			}
		});
		
		
		// search devices
		
		searchResult.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				boolean temp = mBluetoothAdapter.startDiscovery();
				strings.clear();
				devices.clear();
				if(temp){
					tv2.setText("Searching...");
				}else{
					tv2.setText("Search Unsuccesful");
				}
				pair.setVisibility(View.VISIBLE);
			}
		});
		
		// Setting array list for paired devices
		
		arrayAdapter =  new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1,
				strings);
		pair.setAdapter(arrayAdapter);
		
		pair.setOnItemClickListener(this);
		
		
		
		// Receiver object to receive broadcast listener
		
		receiver = new BroadcastReceiver(){
				public void onReceive(android.content.Context context, Intent intent) {
					 final String action = intent.getAction();
					 
					 // Receiver to check Listen Enable/Disable of BLUETOOTH
				        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
				                                                 BluetoothAdapter.ERROR);
				            menabled.setVisibility(View.VISIBLE);
				            switch (state) {
					            case BluetoothAdapter.STATE_OFF:
					                menabled.setText("Bluetooth off");
					                break;
					            case BluetoothAdapter.STATE_TURNING_OFF:
					                menabled.setText("Turning Bluetooth off...");
					                break;
					            case BluetoothAdapter.STATE_ON:
					                menabled.setText("Bluetooth on");
					                break;
					            case BluetoothAdapter.STATE_TURNING_ON:
					                menabled.setText("Turning Bluetooth on...");
					                break;
				            }
				        }
				        
				        // Receiver to check  BLUETOOTH is Discoverable or not
				        if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
						            final int state2 = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,
                                    BluetoothAdapter.ERROR);
				            switch(state2){
					            case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
					                mdiscovery.setText("Discoverable");
					                break;
					            case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
					                mdiscovery.setText("Not Discoverable But Connectable");
					                break;
					            default:
					            	mdiscovery.setText("Bluetooth not Discoverable");
				            }
				        }
				        
				      //  check searching Bluetooth Connections....
				        if (action.equals(BluetoothDevice.ACTION_FOUND)) {
							BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
							Log.v("devices" ,""+device.getName());
							devices.add(device);
							strings.add(device.getName()+"\n"+device.getAddress());
							arrayAdapter.notifyDataSetChanged();
						}
					
				};
			};
			
			// intent filters to register listeners if the state is changed
			 IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
			 registerReceiver(receiver, filter);
			 IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
			 registerReceiver(receiver, filter2);
			 IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			 registerReceiver(receiver, filter3);
			 
	
//		
		
		
		
		// Connecting Device (Server side)
		server = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					Log.v("server", "server running");
					final String name = "com.example.bluetoothparing";
			 		bluetoothserversocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(name,my_UUID);
					Log.v("server", "server "+bluetoothserversocket);
			 		bluetoothsocket = null;
					// Keep listening until exception occurs or a socket is returned
					while(true){
							try {
									bluetoothsocket = bluetoothserversocket.accept();
									Log.v("server", "socket "+bluetoothsocket);
							}catch(IOException ee){
								break;
							}
							// If a connection was accepted
							if(bluetoothsocket!=null){
							
								// Do work to manage the connection (in a separate thread)
								runOnUiThread(new Runnable() {
									public void run() {
										tv2.setText("Connected as server");
									}
								});
								
								
								
								inputStream = bluetoothsocket.getInputStream();
								outputStream = bluetoothsocket.getOutputStream();

								Log.v("server", "input "+inputStream+" output "+outputStream);
								
//								people.moveToPosition(contact_clicked);
//								
//								int idName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//								int idNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//								
//								String names   = people.getString(idName);
//								String number = people.getString(idNumber);
//								String send=""+names+"."+number;
//										
//								outputStream.write("send".getBytes());
								 Log.v("server", "input "+inputStream+" output "+outputStream);
								sending = new Thread(new Runnable() {
									
									@Override
									public void run() {
										 final int BUFFER_SIZE = 100;
										    byte[] buffer = new byte[BUFFER_SIZE];
										    int bytes = 0;
										    int b = BUFFER_SIZE;

										    while (true) {
										        try {
										        	
										        // Receiving data up here sent by the client 
										        // 1.storing it into a Byte[] object
										        
										            bytes = inputStream.read(buffer);
										            
										        // 2.Conversion into string object Using US-ASCII as encoding is in this format
										            
										            String str = new String(buffer, "US-ASCII");
										            
										        // 3. Extraction data from Received String and Storing it into Phone Book 
										            
										            extractDataFromString(str);
										            //Log.v("Server", "reviving "+str);
										        } catch (IOException e) {
										            e.printStackTrace();
										        }
										    }
									}
								});
								Log.v("server", "sending data...");
								sending.start();
								
								Log.v("server", "Server_Enabled");
							//	bluetoothserversocket.close();
							//	break;
							}
					}
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
		
		
		//auto start of bluetooth on start of program
		if (mBluetoothAdapter.isEnabled()) {
			server.start();
			Enable.setVisibility(View.INVISIBLE);
		}
		
		
		// Connecting Device Server side 
		
		 client = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					Log.v("client", "client running");
						
						bluetoothsocket = devices.get(position_clicked).createRfcommSocketToServiceRecord(my_UUID);
						// Cancel discovery because it will slow down the connection
						runOnUiThread(new Runnable() {
							public void run() {
								tv2.setText("Connecting...");
								pair.setVisibility(View.GONE);
								
								pair.setAdapter(new myAdapter(MainActivity.this));
								pair.setVisibility(View.VISIBLE);
								pair.setOnItemClickListener(MainActivity.this);
							}
						});
						
						mBluetoothAdapter.cancelDiscovery();
						try{
							// Connect the device through the socket. This will block
				            // until it succeeds or throws an exception
								bluetoothsocket.connect();
						}catch(IOException ie){
							ie.printStackTrace();
							 // Unable to connect; close the socket and get out
							bluetoothsocket.close();
							return;
						}
				}catch(Exception ee){
					ee.printStackTrace();
					
				}
				 // Do work to manage the connection (in a separate thread)
				 Log.v("Client", "Client_Enabled");
				 runOnUiThread(new Runnable() {
					public void run() {
						 tv2.setText("conected as client");

					}
				});
				 
				 try{
				 inputStream = bluetoothsocket.getInputStream();
				 outputStream = bluetoothsocket.getOutputStream();
				 Log.v("client", "input "+inputStream+" output "+outputStream);				 
				 
				// Log.v("client", "passing : "+message);
					receiving = new Thread(new Runnable() {

						@Override
						public void run() {
							 final int BUFFER_SIZE = 1024;
							    byte[] buffer = new byte[BUFFER_SIZE];
							    int bytes = 0;
							    int b = BUFFER_SIZE;

							    while (true) {
							        try {
							        	// Read bytes of data coming from server to the client 
							        	// as a conformation or received data... 
							        	
							            bytes = inputStream.read(buffer, bytes, BUFFER_SIZE - bytes);
							            Log.v("client", "reviving "+buffer.toString());
							            
							        } catch (IOException e) {
							            e.printStackTrace();
							        }
							    }
						}
					});
					Log.v("server", "receiving data...");
					receiving.start();
					
				 }catch(IOException ee){
					 ee.printStackTrace();
				 }								
			}
		});	
	
	}
	
	
	// function to write data from the phone Book of client to server
	// According to the list coming in client phone record on click store the 
	//contact from CLIENT To SERVER
	
	public void write() {
        try {
        	 people.moveToPosition(contact_clicked);
				
				int idName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
				int idNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
				
				String names   = people.getString(idName);
				String number = people.getString(idNumber);
				String s=""+names+"//"+number+"//";
				int size = s.length();
				String send = size+"//"+s; 
				byte[] b =send.getBytes("US-ASCII");
				
				Log.v("client", "sending ::string :: "+send);
				Log.v("client", "Byte "+b);
				outputStream.write(b);
				 Log.v("client", "input "+inputStream.toString()+" output "+outputStream.toString());
        } catch (IOException e) { }
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
           if (resultCode == RESULT_OK) {
               
              String contents = intent.getStringExtra("SCAN_RESULT");
              String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
           
              // Handle successful scan
                                        
           } else if (resultCode == RESULT_CANCELED) {
              // Handle cancel
              Log.i("App","Scan unsuccessful");
           }
      }
   }
	
	// this is active whenever we click on the list view
	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
			
				
				position_clicked = position;
				if (onclickRan==0) {
					client.start();
				}else{
					contact_clicked=position;
					write();
				}
				
				onclickRan++;
	}
		
	public void extractDataFromString(String data){
		
		String[] parts = data.split("//");
		 name = parts[1];
		 contactNumber= parts[2];
		Log.v("server", "Received\nName: "+name+"\nContact Number :"+contactNumber);
		
		// Generating a confirmation dialog to save contact or not a server
				// if he click on yes contact is saved in phoneBook 
				// if he clicked contact is discarded 
				
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("Storing contact")
				.setMessage("Do you want to store "+name+" in phonebook")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("YES", new DialogInterface.OnClickListener() {

					// Server allowed to click Save contact to phone book
				    public void onClick(DialogInterface dialog, int whichButton) {
				    
				    	//Generating a contactProviderOperations array and applying batch to store  
						ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
						int rawContactInsertIndex = ops.size();

						try{
								ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
								   .withValue(RawContacts.ACCOUNT_TYPE, null)
								   .withValue(RawContacts.ACCOUNT_NAME,null )
								   .build());
								ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
								   .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
								   .withValue(Data.MIMETYPE,Phone.CONTENT_ITEM_TYPE)
								   .withValue(Phone.NUMBER,contactNumber)
								   .build());
								ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
								   .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
								   .withValue(Data.MIMETYPE,StructuredName.CONTENT_ITEM_TYPE)
								   .withValue(StructuredName.DISPLAY_NAME, name)
								   .build());  
								ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
								Log.d("server", "writing contacts into phone book");
						}catch(Exception ee){
							ee.printStackTrace();
						}
				    	
				    	
				    }})
				    
				    // server discarded data....
				 .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						tv3.setText("Server discared "+name);
						
					}
				}).show();	
				
				
				
			}
		});
				
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		
		try {
			bluetoothsocket.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		mBluetoothAdapter.disable();
		
		try {
			bluetoothserversocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
		
	}
	
	class myAdapter extends BaseAdapter{

		LayoutInflater inflate;
		Cursor people;
		Uri uri;
		View row;
		int size;
		String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
		
		public myAdapter(Context context) {
		    inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;		
			Log.v("URI",""+uri);
			people = getContentResolver().query(uri, projection, null, null, null);
			Log.v("LEE", "Constructor called");
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			size = people.getCount();
		//	Log.v("LEE", "getCount : "+size);
			
			return people.getCount();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
		//	Log.v("LEE", "getting item : "+people.getPosition());
			return 0;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
		//	Log.v("LEE", "getting position "+position);
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			row = inflate.inflate(R.layout.individual_contact, parent, false);
			
			TextView firstName = (TextView)row.findViewById(R.id.firstName);
			TextView contactNo = (TextView) row.findViewById(R.id.contactNumber);					

			people.moveToPosition(position);
			if(size>=0 && size<=people.getCount()){
				
					int idName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
					int idNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					
					    String name   = people.getString(idName);
					    String number = people.getString(idNumber);
					
					  //  Log.v("getView", "name "+name);
					  //  Log.v("getview","number "+number);
					    firstName.setText(name);
					    contactNo.setText(number);
					    position++;
			}
			return row;
		}

		
	}

}

