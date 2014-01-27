package com.example.bqshelf;

import java.io.FileInputStream;
import java.io.InputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class BqShelfEpub extends Activity {
	private final String TAG = "BQShelfEpub";
	private TextView mTextView;
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bq_shelf_epub);
	}

	
	/**
	 * Se ejecuta cada vez que se abre la actividad.
	 */
	public void onResume() {
	    super.onResume();   
	    mTextView = (TextView) findViewById(R.id.titulo);
	    mImageView = (ImageView) findViewById(R.id.imagePortada);
	    
	    String nombre = getIntent().getExtras().getString("nombre");
	    String path = getIntent().getExtras().getString("path");
	    
	    Log.i(TAG, "Path " + path);
	    Bitmap coverImage = null;
	    
	    try {
	      // find InputStream for book	
	      InputStream epubInputStream = new FileInputStream(path);
	      Book book = (new EpubReader()).readEpub(epubInputStream);
	      coverImage = BitmapFactory.decodeStream(book.getCoverImage().getInputStream());
	      
		} catch (Exception e) {
			Log.i(TAG, "Error con el epub " + e);
		}
	        
	    if (coverImage!= null) mImageView.setImageBitmap(coverImage);
	    mTextView.setText(nombre);
	    
	}

}
