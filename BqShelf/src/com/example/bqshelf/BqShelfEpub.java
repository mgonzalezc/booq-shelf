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

/**
 * Clase que permite realizar acciones una vez descargado el epub
 * @author MarinaG
 *
 */
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
	    
	    String nombre = getIntent().getExtras().getString("nombre"); //Recuperamos el nombre del elemento clickado
	    String path = getIntent().getExtras().getString("path"); //Recuperamos el path del elemento clickado
	    
	    Log.i(TAG, "Path " + path);
	    Bitmap coverImage = null;
	    
	    //Se usa una librería ya creada que permite leer el epub y mostrar la portada
	    try {
	      // find InputStream for book	
	      InputStream epubInputStream = new FileInputStream(path);
	      Book book = (new EpubReader()).readEpub(epubInputStream);
	      coverImage = BitmapFactory.decodeStream(book.getCoverImage().getInputStream()); //Muestra la portada
	      
		} catch (Exception e) {
			Log.i(TAG, "Error con el epub " + e);
		}
	        
	    if (coverImage!= null) mImageView.setImageBitmap(coverImage); //Carga la imagen en el ImageView, si el epub tenía portada
	    mTextView.setText(nombre); //Coloca también el texto
	    
	}

}
