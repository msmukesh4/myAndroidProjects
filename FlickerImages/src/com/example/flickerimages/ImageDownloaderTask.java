package com.example.flickerimages;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.HttpStatus;
import com.example.flickerimages.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	
    private final WeakReference<ImageView> imageViewReference;

    public ImageDownloaderTask(ImageView imageView) {
    	Log.v("LEE", "constructing object");
        imageViewReference = new WeakReference<ImageView>(imageView);
        
    }
    
    @Override
    protected Bitmap doInBackground(String... params) {
    	Log.v("LEE", "Executing do in background");
        return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
    	Log.v("LEE", "Executing on post execute");
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                	Log.v("LEE", "Bitmap value :: "+bitmap);
                    imageView.setImageBitmap(bitmap);
                } else {
                	Log.v("LEE", "Bitmap Value is NULL");
                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.ic_launcher);
                    imageView.setImageDrawable(placeholder);
                }
            }

        }
    }

    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.connect();
            
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
            	Log.v("LEE", "Decoding Bitmap");
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}