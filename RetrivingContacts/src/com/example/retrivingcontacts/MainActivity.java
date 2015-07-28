package com.example.retrivingcontacts;

import java.util.ArrayList;

import org.w3c.dom.Text;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	
	ListView mylist;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		mCursor = getContentResolver().query(People.CONTENT_URI, null, null, null, People.NAME+" ASC");
//		startManagingCursor(mCursor);
//		
//		String[] columns = new String[] { People.NAME, People.NUMBER };
//		int[] to = new int[] { R.id.firstName, R.id.contactNumber };
//		
//		
		mylist = (ListView) findViewById(R.id.contact_list);
		mylist.setAdapter(new myAdapter(this));
		
		
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
			people = getContentResolver().query(uri, projection, null, null, null);
			Log.v("LEE", "Constructor called");
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			size = people.getCount();
			Log.v("LEE", "getCount : "+size);
			
			return people.getCount();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			Log.v("LEE", "getting item : "+people.getPosition());
			return 0;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			Log.v("LEE", "getting position "+position);
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
					
					    Log.v("getView", "name "+name);
					    Log.v("getview","number "+number);
					    firstName.setText(name);
					    contactNo.setText(number);
					    position++;
			}
			return row;
		}

		
	}
	

}
