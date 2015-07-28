package com.example.mapview_google_maps;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import  android.view.View.OnClickListener;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressLint("DrawAllocation")
public class MainActivity extends Activity  implements  OnClickListener{

	//default latitude values in case the GPS has no last known values()
	double longitude=7.909167d,
		   latitude=47.968056d,
		   refLatitude,
		   refLongitude;
	// Variable for checking tapping
	long tap_1,tap_2;
	
	
	
	//Location Manager to register location listener 
	LocationManager manager;
	String provider;
	
	// default doom level 
	int zoomLevel=6,x,y;
	int xtile=0;
	int ytile=0;
	int once=0;
	int clicked_on_tile=-1;
	
	//thread to download images whenever user touch on screen
	//its for running network operations that can't be done on main thread
	Thread th;
	
	//Four tiles to be displayed on the screen each time
	//they are being download 
	Bitmap tile00,tile01,tile10,tile11;
	
	Boolean loadLeft=false,loadUp=false,
			loadRight=false,loadDown=false,
			doubleTapped=false,
			Zooming = false;
	
	String url;
	
	// button for setting everything default
	Button zoom_out_btn_obj;
	
	//Relative layout without .xml file
	MapView mapView;
	RelativeLayout rel_layout;
	RelativeLayout.LayoutParams map_lay_params;
	RelativeLayout.LayoutParams zoom_out_btn;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		
		
		
		// working location listener
		
		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
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
//				longitude=lastloc.getLongitude();
//				latitude=lastloc.getLatitude();
				
			}
		};
		
	    if (manager.isProviderEnabled(provider)) {
	    	if (clicked_on_tile==-1) {
	    		manager.requestLocationUpdates(provider,
						10,1,listner);
			}
				
		
			final Location lastloc = manager.getLastKnownLocation(provider);
			if(lastloc!=null){
				double latitu = lastloc.getLatitude();
				double longi = lastloc.getLongitude();
				Log.v("logitude", "longitude is "+longi);
				Log.v("latitude", "latitude is "+latitu);
				latitude=latitu;
				longitude=longi;
			}
		
	    }
	    
	    
	    // declaring button and mapview
	    zoom_out_btn_obj = new Button(this);
	    zoom_out_btn_obj.setText("Tap to Zoom Out");
	    zoom_out_btn = 
				new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	 zoom_out_btn.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	 zoom_out_btn_obj.setLayoutParams(zoom_out_btn);
	 zoom_out_btn_obj.setOnClickListener(this);
	    
		

	    
		map_lay_params = 
				new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		map_lay_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		
		//Initially running the thread
		
	    th =  new Thread(new Runnable() {
			
			@Override
			public void run() {
					try{
								Log.v("thread", "Executing thread");
								tile00 = loadimage(0,0);
								tile01 =loadimage(0,1);
								tile10 = loadimage(1,0);
								tile11 = loadimage(1,1);
								Log.v("thread", "Image loaded");
						
								// adding views to main relative layout only once as req.
								//as doing it in main thread
								if (once==0) {
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										
										rel_layout.addView(mapView, map_lay_params);
										rel_layout.addView(zoom_out_btn_obj, zoom_out_btn);
										once++;
										
									}
								});
							}
							
						
					}catch(Exception ee){
						ee.printStackTrace();
					}
					
		//		}
			}
		});
	   
	    
	    // start downloading and showing... 
		th.start();
		
		    rel_layout = new RelativeLayout(this);
			mapView = new MapView(this);
			this.setContentView(rel_layout);
		
		
	}
	
	// function for running the downloading task of tiles whenever the screen is touched
	
	public void runInAnotherThread() {
		// TODO Auto-generated method stub
		try{
			th = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.v("thread", "Executing thread");
					tile00 = loadimage(0,0);
					tile01 =loadimage(0,1);
					tile10 = loadimage(1,0);
					tile11 = loadimage(1,1);
					
					Log.v("thread", "Image loaded");
										
				}
			});
			
			th.start();
		}catch(Exception ee){
			ee.printStackTrace();
		}
	}
	
	// extracting the tiles to put in into the url for downloading...
	
	public String getTilesWithZoom(double lat,double lon,int zoom,int x,int y,int t){
		
		//calculating tile values from latitude, longitude and Zoom 
	
			xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) ) ;
			ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) 
				+ 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom) ) ;
		    if (xtile < 0)
		     xtile=0;
		    if (xtile >= (1<<zoom))
		     xtile=((1<<zoom)-1);
		    if (ytile < 0)
		     ytile=0;
		    if (ytile >= (1<<zoom))
		     ytile=((1<<zoom)-1);
		    
		    
		    /* t to find out which tile to be zoomed in
		    
		    * t = -1 ()----> for the initial time from your location
		    * t = 00         if the user clicks on tile 00
		    *     01                                    01
		    *     11                                    11  
		    *     
		    *     which is being set by touch events
		    */  
		    
		    switch (t) {
			case -1:
						 // tile position when they load first time
					    
					    if (x==0 && y==0) {
					    	
					    	refLatitude = Math.toDegrees(Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * ytile) / Math.pow(2.0, zoom))));
					    	refLongitude = xtile / Math.pow(2.0, zoom) * 360.0 - 180;
					    	
					    	 return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==0&&y==1) {
					    	xtile+=1;
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==1 && y==0) {
					    	ytile+=1;
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==1 && y==1) {
					    	xtile+=1; ytile+=1;
					    	
					    	latitude = refLatitude;
					    	longitude = refLongitude;
					    	Log.v("resetting","latitude : "+latitude+"\nlongitude : "+longitude);
					    						    	
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
						break;
				
				
			case -2:
					/*
					 * t== -2 is set when a swipe event takes place
					 * rather than a zoom event the tile which is being loaded is set by
					 * the load variables
					 * 
					 * 
					 */
							
						if (x==0 && y==0) {
								 if (loadUp) {
									ytile--;
								 }
								 else if (loadDown) {
									ytile++;
								}
								 else if (loadLeft) {
									xtile--;
								}
								 else if (loadRight) {
									xtile++;
								}
								refLatitude = Math.toDegrees(Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * ytile) / Math.pow(2.0, zoom))));
							    refLongitude = xtile / Math.pow(2.0, zoom) * 360.0 - 180;
								 
						    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==0 && y==1) {
						    	
					    			if (loadUp) {
										ytile--;
										xtile++;
									 }
									 else if (loadDown) {
										ytile++;
										xtile++;
										
									}
									 else if (loadLeft) {
										 
									}
									 else if (loadRight) {
										xtile+=2;
									}
					    			return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==1 && y==0) {
							    	 
					    				if (loadUp) {
										
										 }
										 else if (loadDown) {
											ytile+=2;
										}
										 else if (loadLeft) {
											xtile--;
											ytile++;
										}
										 else if (loadRight) {
											xtile++;
											ytile++;
										}
					    				return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==1 && y==1) {
							    	 if (loadUp) {
											xtile++;
										 }
										 else if (loadDown) {
											ytile+=2;
											xtile++;
										}
										 else if (loadLeft) {
											ytile++;
										}
										 else if (loadRight) {
											xtile+=2;
											ytile+=1;
										}
							    	 	
								        latitude = refLatitude;
								    	longitude = refLongitude;
								    	Log.v("resetting","latitude : "+latitude+"\nlongitude : "+longitude);
					    	
								    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
				 
						break;
						
						
				 // tile position when the load after tapping
				
			case 00:
						
						if (x==0 && y==0) {							
					    	 return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==0 && y==1) {
					    	xtile+=1;
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==1 && y==0) {
					    	ytile+=1;
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==1 && y==1){
					    	xtile+=1; ytile+=1;
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
						 
						break;
				
				
			case 01:
						
						if (x==0 && y==0) {
							 xtile+=2;
							 refLatitude = Math.toDegrees(Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * ytile) / Math.pow(2.0, zoom))));
						     refLongitude = xtile / Math.pow(2.0, zoom) * 360.0 - 180;
							 
					    	 return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==0 && y==1) {
					    	xtile+=3;
					    	
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==1 && y==0) {
					    	xtile+=2;
					    	ytile+=1;
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==1 && y==1) {
					    	xtile+=3;
					    	ytile+=1;
					    	
					    	Log.v("resetting","latitude : "+latitude+"\nlongitude : "+longitude);
					    	
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
						 
						break;
		
			case 10:
							
						if (x==0 && y==0) {
							 ytile+=2;
							 refLatitude = Math.toDegrees(Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * ytile) / Math.pow(2.0, zoom))));
						     refLongitude = xtile / Math.pow(2.0, zoom) * 360.0 - 180;
							 
					    	 return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==0 && y==1) {
					    	ytile+=2;
					    	xtile+=1;
					    	
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==1 && y==0) {
					    	ytile+=3;
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==1 && y==1) {
					    	ytile+=3;
					    	xtile+=1;
					    	
					    	Log.v("resetting","latitude : "+latitude+"\nlongitude : "+longitude);
					    	
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
						 
						break;
						
			case 11:
						
						if (x==0 && y==0) {
							 ytile+=2;
							 xtile+=2;
							 refLatitude = Math.toDegrees(Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * ytile) / Math.pow(2.0, zoom))));
						     refLongitude = xtile / Math.pow(2.0, zoom) * 360.0 - 180;
							 
					    	 return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==0 && y==1) {
					    	ytile+=2;
					    	xtile+=3;
					    	
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==1 && y==0) {
					    	ytile+=3;
					    	xtile+=2;
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
					    else if (x==1 && y==1) {
					    	ytile+=3;
					    	xtile+=3;
					
					    	Log.v("resetting","latitude : "+latitude+"\nlongitude : "+longitude);
					    	
					    	return("" + zoom + "/" + xtile + "/" + ytile);
						}
						break;	

			// end of cases so default ...
			default:
						break;
			} 
		   
		    return "null";
    }
	
	
	// NETWORK OPERATION for downloading the tiles...
	// by getting the url from getTilesWithZoom
	
	private Bitmap loadimage(int x,int y) {
		HttpURLConnection conn = null;
		try{
			

				String tiles_Zoom = getTilesWithZoom(latitude,longitude,zoomLevel,x,y,clicked_on_tile);
				  url="http://tile.openstreetmap.org/"+tiles_Zoom+".png";
			URL url_var = new URL(url);
			conn = (HttpURLConnection) url_var.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			Bitmap bitmap= BitmapFactory.decodeStream(is);		
				if(bitmap!=null)
					return bitmap;		
		}catch(Exception ee){
			ee.printStackTrace();
		}finally{
			conn.disconnect();
		}
		return null;
	}

	/*
	 *  MapView class extending View from the various drawing of tiles on the screen 
	 *  having onDraw() and on touchEvent()
	 *  
	 *  onDraw to draw the bitmap on the screen at a particular location
	 *  and on touch event to find out that when 
	 *  screen is touched which tile is touched and movement is
	 *  from left  to right or
	 *  from Right to left
	 */
	
	@SuppressLint("DrawAllocation")
	class MapView extends View{
		int tapped=0;
		float screen_x,screen_y,
		curr_x,curr_y;
		
		@SuppressLint("DrawAllocation")
		public MapView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@SuppressLint("DrawAllocation")
		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub

			Log.v("validating", "Invalidating");	
			canvas.drawBitmap(tile00, 0, 0, new Paint());
			canvas.drawBitmap(tile01, 256, 0, new Paint());
			canvas.drawBitmap(tile10, 0, 256, new Paint());
			canvas.drawBitmap(tile11, 256, 256, new Paint());
			
			MapView.this.invalidate();
		
			
			super.onDraw(canvas);
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			
			Log.v("running", "ontouch");
			
			MapView.this.invalidate();
			Log.v("validating", "Invalidating");
			int action = event.getActionMasked();
			
			switch(action){
			
			
			case MotionEvent.ACTION_DOWN:
				Toast.makeText(getApplicationContext(), "action down", Toast.LENGTH_SHORT).show();
						
				tap_2 = System.currentTimeMillis();
				
				x = (int) event.getX();
				y = (int) event.getY();
				
				Log.v("test", "x "+x+" y = "+y);
				
				// checking double tapped or not within 250ms
				if (tap_2-tap_1<250) {
					
				
						 screen_x=event.getX();
						 screen_y=event.getY();
						 Log.d("test", "X "+screen_x+" Y "+screen_y);
						 
						 if (screen_x>=0 && screen_x<=256 && screen_y>0 && screen_y<256) {
							clicked_on_tile=00;
								if (zoomLevel<18) {
									zoomLevel++;
									Log.v("zoomed","zoomLevel "+zoomLevel);
									Zooming=true;									
								}
								else{
									Toast.makeText(getApplicationContext(), "Max ZOOM level Reached", Toast.LENGTH_SHORT).show();
									zoomLevel--;

								}
							Toast.makeText(getApplicationContext(), "clicked on tile 00", Toast.LENGTH_LONG).show();

						}
						else if (screen_x>=256 && screen_x<=512 && screen_y>0 && screen_y<256) {
							clicked_on_tile=01;
							if (zoomLevel<18) {
								zoomLevel++;
								Log.v("zoomed","zoomLevel "+zoomLevel);
								Zooming=true;									
							}
							else{
								Toast.makeText(getApplicationContext(), "Max ZOOM level Reached", Toast.LENGTH_SHORT).show();
								zoomLevel--;

							}
							
							Toast.makeText(getApplicationContext(), "clicked on tile 01", Toast.LENGTH_LONG).show();

						}
						else if (screen_x>=0 && screen_x<=256 && screen_y>256 && screen_y<512) {
							clicked_on_tile=10;
							if (zoomLevel<18) {
								zoomLevel++;
								Log.v("zoomed","zoomLevel "+zoomLevel);
								Zooming=true;									
							}
							else{
								Toast.makeText(getApplicationContext(), "Max ZOOM level Reached", Toast.LENGTH_SHORT).show();
								zoomLevel--;

							}
							Toast.makeText(getApplicationContext(), "cliked on tile 10", Toast.LENGTH_LONG).show();

						}
						else if (screen_x>=256 && screen_x<=512 && screen_y>256 && screen_y<512) {
							clicked_on_tile=11;
							if (zoomLevel<18) {
								zoomLevel++;
								Log.v("zoomed","zoomLevel "+zoomLevel);
								Zooming=true;									
							}
							else{
								Toast.makeText(getApplicationContext(), "Max ZOOM level Reached", Toast.LENGTH_SHORT).show();
								zoomLevel--;
							}
							
							Toast.makeText(getApplicationContext(), "cliked on tile 11", Toast.LENGTH_LONG).show();

					
						 
						}
						latitude = refLatitude;
						longitude= refLongitude;
						Log.v("resetting", "latitude "+latitude+"\nlonitude "+longitude);
						if (Zooming) {
							runInAnotherThread();
						}
						
				}	
				tap_1=tap_2;
						break;
					
			
			//whenever a swipe event has taken place...
			
			case MotionEvent.ACTION_UP: 
				Toast.makeText(getApplicationContext(), "action up", Toast.LENGTH_SHORT).show();
				
				int curr_x = (int) event.getX();
				int curr_y = (int) event.getY();
				Log.v("test","comparing :: x = "+x+" y = "+y+ "current x= "+curr_x+ " y = "+curr_y);
				if (curr_x-x>70) {
					Toast.makeText(getApplicationContext(), "Swiped right ", Toast.LENGTH_SHORT).show();
					loadLeft=true;
					loadRight=false;
					loadUp=false;
					loadDown=false;
					clicked_on_tile=-2;
				}
				else if(curr_x-x<-70){
					Toast.makeText(getApplicationContext(), "Swiped left ", Toast.LENGTH_SHORT).show();
					loadLeft=false;
					loadRight=true;
					loadUp=false;
					loadDown=false;
					clicked_on_tile=-2;
				}
				else if(curr_y-y>70){
					Toast.makeText(getApplicationContext(), "Swiped  down", Toast.LENGTH_SHORT).show();
					loadLeft=false;
					loadRight=false;
					loadUp=true;
					loadDown=false;
					clicked_on_tile=-2;
				}
				else if(curr_y-y<-70){
					Toast.makeText(getApplicationContext(), "Swiped up ", Toast.LENGTH_SHORT).show();
					loadLeft=false;
					loadRight=false;
					loadUp=false;
					loadDown=true;
					clicked_on_tile=-2;
				}
				else{
					loadLeft=false;
					loadRight=false;
					loadUp=false;
					loadDown=false;
				}
				
				if (loadLeft || loadDown || loadRight || loadUp) {
					runInAnotherThread();
				}

				break;
			}
		
					
					return true;
		}
		
		
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		zoomLevel=6;
		latitude = refLatitude;
		longitude= refLongitude;
		Zooming = true;
		
	}
	

}
