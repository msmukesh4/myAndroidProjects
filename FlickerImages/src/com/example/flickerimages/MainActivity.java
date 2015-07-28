package com.example.flickerimages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.flickerimages.ImageDownloaderTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	EditText query_et;
	Button search_btn;
	String keyword_str;
	String searchResult,jsonResult;
	ArrayList<String> image_urls;
	Thread thread;
	
	String FlickrQuery_url = "https://api.flickr.com/services/rest/?method=flickr.photos.search";
	String FlickrQuery_per_page = "&per_page=9";
	String FlickrQuery_format = "&format=json";
	String FlickrQuery_tag = "&tags=";
	String FlickrQuery_key = "&api_key=";

	String FlickrApiKey = "7163183a203a594208cb9262c75632fd";
	
	GridView gridview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gridview = (GridView) findViewById(R.id.gridView1);
		query_et=(EditText) findViewById(R.id.keyword);
		search_btn = (Button) findViewById(R.id.search_btn);
		search_btn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				
				thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						keyword_str = query_et.getText().toString();
						Log.v("LEE","Keyword length : "+keyword_str.length());
						searchResult = QueryFlickr(keyword_str);
						Log.e("LEE", "query data :: "+searchResult);
						
						constructingImageURLs(searchResult);
						Log.d("LEE","Image urls are now set "+image_urls);
						
						try{
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									gridview.setAdapter(new myAdapter(MainActivity.this,image_urls));
								}
							});
						}catch(Exception ee){
							ee.printStackTrace();
						}
					}
				});
				thread.start();
			}
		});
		
		
	}

	private void constructingImageURLs(String s) {
		try {
			Log.d("LEE", "string s "+s );
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
				 String u="https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+".jpg";
					
				image_urls.add(u); 
				
			}
			Log.v("LEE", "Image url :: "+image_urls);

	      } catch (JSONException e){
	    	  e.printStackTrace();
	    	 }
	      	catch(Exception ee){
	      		ee.printStackTrace();
	      	}
	}
	
	
	String QueryFlickr(String q){
		String qResult = null;

		 String qString =FlickrQuery_url + FlickrQuery_per_page+ FlickrQuery_format+ FlickrQuery_tag + q +
				 FlickrQuery_key + FlickrApiKey;
	    
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

	    return qResult;
	}
	
	class myAdapter extends BaseAdapter{
		ImageView iv ;
		View row;
		ArrayList<String> urls = new ArrayList<String>();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		public myAdapter(Context context, ArrayList<String> listData) {
			Log.v("LEE", "inside myAdapter constructor");
				urls=listData;
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
			  myHolder holder;
		        if (convertView == null) {
		            convertView = inflater.inflate(R.layout.my_simple_layout, null);
		            holder = new myHolder();
		          
		            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView1);
		            convertView.setTag(holder);

		        } else {
		            holder = (myHolder) convertView.getTag();
		        }


		        if (holder.imageView != null) {
		            new ImageDownloaderTask(holder.imageView).execute(urls.get(position));
		        }

		        return convertView;
		    }
		}
		    class myHolder {
		        ImageView imageView;
		    }
		    
}


