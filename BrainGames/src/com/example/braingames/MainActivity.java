package com.example.braingames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Delayed;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnItemClickListener,OnClickListener{

	// Class Variables Declaration
	TextView timer_text,remember_images_text;
	CountDownTimer ct ;
	Thread thread,asyn_thread,startGame;
	String JSON;
	ImageView random_image;
	ArrayList<String> image_urls;
	GridView grid_image_view;
	myHolder holder;
	int getview_counter=0;
	int random_number,total_choices=0,correct_choices=0,xcorrect_choice=0;
	Random r;
	String curr_url;
	ArrayList<Integer> numbers_generated;
	Button PlayAgain;
	Boolean recreated=false;
	
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//creating objects
		random_image = (ImageView) findViewById(R.id.imageView1);
		random_image.setVisibility(View.INVISIBLE);
		PlayAgain = (Button) findViewById(R.id.play_again);
		PlayAgain.setVisibility(View.GONE);
		
		timer_text = (TextView) findViewById(R.id.remaining_time);
		remember_images_text = (TextView) findViewById(R.id.remember_image_text);
		grid_image_view = (GridView) findViewById(R.id.gridView1);
		
		r = new Random();
		numbers_generated =  new ArrayList<Integer>();
		

		
	
//		final ProgressDialog ringProgressDialog = ProgressDialog.show(MainActivity.this, 
//			        "Please wait ...", "Downloading Image ...", true);
//		ringProgressDialog.setCancelable(true);
//		
//		thread=new Thread(new Runnable() {
//			    @Override
//			    public void run() {
//			        try {
//			        	asyn_thread.start();    
//			        	
//			                } catch (Exception e) {
//			                }
//			                ringProgressDialog.dismiss();
//			                ct.start();
//			            }
//			        });
//			thread.start();
		
		//to check that APP is restarted
		if (recreated==true) {
			asyn_thread.stop();
			recreated=false;
		}
		
		// Thread to accomplish network operations because network operations cannot be run on main thread
		asyn_thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// Downloading JSON file from flickr
				JSON = QueryFlickr("India");
				Log.v("games","JSON Data "+JSON);
				
				// Constructing urls From JSON file
				constructingImageURLs(JSON);
				Log.v("games", "so URLS are :: "+image_urls);
				
				//For UI objects can only be alter on main thread 
				//so running on main thread / UIThread
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						//Initializing GridView  in custom adapter (extending Base Adapter interface) 
						grid_image_view.setAdapter(new myAdapter(MainActivity.this,image_urls));
						// Starting Countdown 
						ct.start();
						
					}
				});			
				
			}
		});
		
		
		//Starting the thread (APP Started...)
		asyn_thread.start();
		grid_image_view.setOnItemClickListener(this);
		PlayAgain.setOnClickListener(this);
		
		// Countdown Function 
		ct = new CountDownTimer(25000,1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				if(millisUntilFinished<16000){
					timer_text.setText("Remaining Time :: "+millisUntilFinished/1000);
				}
				
			}
			
			@Override
			public void onFinish() {
				timer_text.setText("Select the position of image");
				//After the countdown finish Fliping all the images (so parameter -1) 
				flipimages(-1);
				// Starting the game function
				startGame();
			}
			};
			Log.v("after", "end of oncreate");
	}
	
	
	public void startGame() {
		if(correct_choices<9){
			
			//Generating a random number between 0-9 including 0 
			random_number = r.nextInt(9 - 0) + 0;
			
			if(numbers_generated.contains(random_number))
				startGame();
			
			//adding into previous random numbers
			numbers_generated.add(random_number);
			Log.v("games", "Random number is"+random_number);
			Log.v("games", "Downloading current image");
			remember_images_text.setText("Total Attempts :: "+total_choices);
			
			curr_url=image_urls.get(random_number);
			Log.v("games", "Downloading current image");
			
			//Downloading current Image
			new ImageDownloaderTask(random_image).execute(curr_url);
			random_image.setVisibility(View.VISIBLE);}
		else{
			
			//End Of APP 
			grid_image_view.setVisibility(View.GONE);
			random_image.setVisibility(View.GONE);
			timer_text.setText("Game Success Fully Completed");
			PlayAgain.setVisibility(View.VISIBLE);
		}
		xcorrect_choice=correct_choices;
	}
	
	//flip images.....
	private void flipimages(int pos){
		int childSize;
		final int size;
			if(pos<0){
				size= grid_image_view.getChildCount();
				//iNTIIAL FLIP OF ALL IMAGES
				for(int i = 0; i <size; i++) {
					ViewGroup gridChild = (ViewGroup) grid_image_view.getChildAt(i);
					childSize = gridChild.getChildCount();
					gridChild.getChildAt(0).setVisibility(View.GONE);
					gridChild.getChildAt(1).setVisibility(View.VISIBLE);

				}
			}else{
				//Flip single image at selected pos
					ViewGroup gridChild = (ViewGroup) grid_image_view.getChildAt(pos);
					gridChild.getChildAt(0).setVisibility(View.VISIBLE);
					gridChild.getChildAt(1).setVisibility(View.GONE);
	
			}
	}
	
	//URLs being contructed from JSON object
	private void constructingImageURLs(String s) {
		try {
			image_urls = new ArrayList<String>();
			JSONObject root = new JSONObject(s.replace("jsonFlickrApi(", "").replace(")", ""));
			JSONObject photos = root.getJSONObject("photos");
			JSONArray imageJSONArray = photos.getJSONArray("photo");
			
//			https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
			
			for (int i = 0; i < imageJSONArray.length(); i++) {
				JSONObject item = imageJSONArray.getJSONObject(i);
				 String farm = item.getString("farm").toString();
				 String server = item.getString("server");
				 String id = item.getString("id").toString();
				 String secret = item.getString("secret").toString();
				 String u="https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+"_m.jpg";
					
				image_urls.add(u); 
				Log.v("URLS", "URLS :: "+u);
				
			}
	      } catch (JSONException e){
	    	  e.printStackTrace();
	    	 }
	      	catch(Exception ee){
	      		ee.printStackTrace();
	      	}
	}
	
	// Extraction data from Flickr
	String QueryFlickr(String q){
		String qResult = null;

		Log.v("games", "Tags "+q);
		 String qString ="https://api.flickr.com/services/rest/?method=flickr.photos.search"+
		 		"&per_page=9&format=json&tags="+q +"&api_key=7163183a203a594208cb9262c75632fd";
		 //FlickrQuery_per_page+ FlickrQuery_format+ FlickrQuery_tag + q +
			//	 FlickrQuery_key + FlickrApiKey;
	    Log.v("test", "qstring "+qString);
		 
	    HttpClient httpClient = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet(qString);

	    try {
	    HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();

	    if (httpEntity != null){
	    InputStream inputStream = httpEntity.getContent();
	    Reader in = new InputStreamReader(inputStream);
	    BufferedReader bufferedreader = new BufferedReader(in);
	    StringBuilder stringBuilder = new StringBuilder();

	    String stringReadLine = null;

	    while ((stringReadLine = bufferedreader.readLine()) != null) {
	    stringBuilder.append(stringReadLine + "\n");
	    }

	    qResult = stringBuilder.toString();

	    }

	    } catch (ClientProtocolException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    } catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    }
	    Log.v("test", "Qresult "+qResult);
	    return qResult;
	}
	
	// Customised Adapter for generating GridView
	class myAdapter extends BaseAdapter{
		View row;
		ArrayList<String> urls = new ArrayList<String>();
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		public myAdapter(Context context, ArrayList<String> listData) {
			Log.v("LEE", "inside myAdapter constructor");
				urls=listData;
				Log.v("GAmes", "inside arrayadapter constructor");
			}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return urls.size();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return urls.get(position);
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.v("games", "Inside getview");
			  
		        if (convertView == null) {
		            convertView = inflater.inflate(R.layout.double_images_layout,parent,false);
		            holder = new myHolder();
		          
		            holder.downloaded_image = (ImageView) convertView.findViewById(R.id.original_image);
		            holder.flipped_image = (ImageView) convertView.findViewById(R.id.flipped_image);
		            convertView.setTag(holder);

		        } else {
		            holder = (myHolder) convertView.getTag();
		        }

		        if (holder.downloaded_image != null) {
		        	getview_counter++;
		        	Log.v("counter", "get view counter "+getview_counter);
		        	if(getview_counter<16){
		            new ImageDownloaderTask(holder.downloaded_image).execute(urls.get(position));
		            holder.flipped_image.setVisibility(View.GONE);}
		        	else if(getview_counter==20){
		           
		           }
		            
		        }
		        return convertView;
		    }
		
		}
	
		    class myHolder {
		        ImageView downloaded_image;
		        ImageView flipped_image;
		    }
		    
		    //Gridview onClick method
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				total_choices++;
				remember_images_text.setText("Total Attempts :: "+total_choices);
				
				//Toast.makeText(getApplicationContext(), "Position "+position+" clicked", Toast.LENGTH_SHORT).show();
				if(position==random_number){
					//Toast.makeText(getApplicationContext(), "Position correct choice", Toast.LENGTH_SHORT).show();
					correct_choices++;
					flipimages(position);
					startGame();
				}
				
				
			}

			//Play Again onClick method
			@Override
			public void onClick(View v) {
				recreated=true;
				recreate();
			}
	
	
}
