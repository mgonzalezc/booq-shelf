package com.example.bqshelf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.android.AndroidAuthSession;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class CustomArrayAdapter extends ArrayAdapter<Epub> {
	private final String TAG = "CustomArrayAdapter";
	private final int DOUBLE_CLICK_INTERVAL= ViewConfiguration.getDoubleTapTimeout();

	private ImageView mImageView;
	private long mLastPressTime;
	private String mCachePath;
	
	private DropboxAPI<AndroidAuthSession> mDBApi;

	
	public CustomArrayAdapter(Context context, int resource,
			int textViewResourceId, ArrayList<Epub> objects, DropboxAPI<AndroidAuthSession> mDBApi) {
		super(context, resource, textViewResourceId, objects);
		this.mDBApi= mDBApi;
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row=super.getView(position, convertView, parent);
		
		
		mImageView=(ImageView)row.findViewById(R.id.imageBook);
		
		mImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	long pressTime = System.currentTimeMillis();
               //Doble click
                if (pressTime - mLastPressTime <= DOUBLE_CLICK_INTERVAL) {
                	Log.i(TAG, "Double click");
                	
                	String nombre = getItem (position).getNombre();
                	downloadEpub(v.getContext(), nombre);
                	Intent i = new Intent(v.getContext(), BqShelfEpub.class);
                	i.putExtra("nombre", nombre);  
                	i.putExtra("path", mCachePath);
                	v.getContext().startActivity(i);
         	
                } else {     
                    Handler myHandler = new Handler();
                    Message m = new Message();
                    myHandler.sendMessageDelayed(m,DOUBLE_CLICK_INTERVAL);
                }
                
                // record the last time the menu button was pressed.
                mLastPressTime = pressTime;   
            }    
        });
		
		
		return(row);
	}
	
	public void downloadEpub (Context context, String nombre) {		
		FileOutputStream fos = null;

        try {       	
        	File file = new File (context.getCacheDir(), nombre);
        	mCachePath=file.getPath();
        	fos = new FileOutputStream(file);
        	DropboxFileInfo info = mDBApi.getFile("/"+nombre, null, fos, null);
        	Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
        } catch (Exception e) {
        	Log.i(TAG, "Error downloading" + e);
        }  finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                	Log.i(TAG, "Error closing fos" + e);
                }
            }
        }          
	}
}
