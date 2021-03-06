package com.example.bqshelf;

import java.util.ArrayList;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.TokenPair;
import com.dropbox.client2.session.Session.AccessType;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Clase principal de la aplicaci�n
 * @author MarinaG
 *
 */
public class BqShelf extends Activity {
	private static final String TAG = "BqAccess";

	final static private String APP_KEY = "j2kbcynosjjswjo";
	final static private String APP_SECRET = "jdc0gz2h05j6oyj";
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
	
	final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
	
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private CustomArrayAdapter mAdapter;
    
    private Button mLogin;
    private ListView mList;
    
	private ArrayList<Epub> mEpubList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bq_shelf);
		
		AndroidAuthSession session = buildSession();
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		mLogin = (Button)findViewById(R.id.butLogin);
		
		mLogin.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	mDBApi.getSession().startAuthentication(BqShelf.this);
            }    
        });
		
		setLoggedIn(mDBApi.getSession().isLinked());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bq_shelf, menu);
		return true;
	}
	
	/**
	 * Opciones del men�
	 */
	public boolean onOptionsItemSelected (MenuItem item) {
		switch (item.getItemId()) {
			case R.id.optNombre:
				ordenaNombre();
				mAdapter.notifyDataSetChanged();
				return true;
			case R.id.optFecha:
				ordenaFecha();
				mAdapter.notifyDataSetChanged();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}		
	}
	
	
	/**
	 * Construye la sesi�n 
	 * @return la sesi�n del usuario
	 */
	public AndroidAuthSession buildSession() {
		AndroidAuthSession session; 
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET); //Claves del desarrollador
        
        //Si el usuario ya ha introducido sus credenciales en sesiones anteriores, se recuperan y no tiene que
        //introducirlas de nuevo
        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }
        return session;
       
    }
 
	/**
	 * Da el token-key del usuario si ha sido almacenado
	 * @return ret array con el par key-secret
	 */
	private String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
    }
	
	 /**
	  * Guarda el par token-key.
	  */
	private void storeKeys(String key, String secret) {
       // Save the access key for later
       SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
       Editor edit = prefs.edit();
       edit.putString(ACCESS_KEY_NAME, key);
       edit.putString(ACCESS_SECRET_NAME, secret);
       edit.commit();
   }
	
	/**
	 * Una vez que el usuario realiza la autenticaci�n, se ejecuta este m�todo. 
	 */
	 protected void onResume() {
	        super.onResume();
	        AndroidAuthSession session = mDBApi.getSession(); //Sesi�n actual

	        if (session.authenticationSuccessful()) {
	            try {
	                // Mandatory call to complete the auth
	                session.finishAuthentication();
	                // Store it locally in our app for later use
	                TokenPair tokens = session.getAccessTokenPair(); //Obtiene el par token-key de acceso
	                storeKeys(tokens.key, tokens.secret); //Almacena el token-key de acceso
	                setLoggedIn(true);
	            } catch (IllegalStateException e) {
	                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage()); //Mensaje de error
	                Log.i(TAG, "Error authenticating", e);
	            }
	        }
	 }
	 
	 /**
	 * Mostrar mensaje de error
	 * @param msg El mensaje de error
	 */
	 private void showToast(String msg) {
	        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
	        error.show();
	    }

	 /**
	  * Ajustar la View seg�n si se ha loggeado o no
	  * @param loggedIn true indica que el usuario ha iniciado la sesi�n
	  */
	 private void setLoggedIn(boolean loggedIn) {
	    	if (loggedIn) {
	    		mLogin.setVisibility(View.GONE);  
	    		mLogin.setVisibility(View.GONE);
	    		mList = (ListView)findViewById(R.id.listView);
	    		
	    		mEpubList=new ArrayList<Epub>();	    			
	    		mEpubList = getEpub("/");
	    				
	    		mAdapter = new CustomArrayAdapter(BqShelf.this, R.layout.lista, R.id.textViewFile, mEpubList, mDBApi);
	    		mList.setAdapter(mAdapter);
	    	}
	 }
	 
	 /**
	  * Averigua la extensi�n de un archivo
	  * @param file el nombre del archivo
	  * @return extensi�n del archivo
	  */
	 public String getExtension(String file) {
		 String extFile = "";
		 int index = file.lastIndexOf('.');
		 if (index != -1) {
			 extFile = file.substring(index+1); 
		 }
		 
		 return extFile;
	 }
	 

	 /**
	  * Obtiene de Dropbox los archivos del directorio indicado. Los archivos con extensi�n .epub se almacenan 
	  * en una lista de objetos Epub.
	  * @param path directorio
	  * @return lista de Epub
	  */
	 public ArrayList<Epub> getEpub(String path) { 
		try {	    		
			ArrayList<Epub> mEpubList=new ArrayList<Epub>();
 			Entry entries = mDBApi.metadata(path, 1000, null, true, null);	
 			for (Entry e : entries.contents) {
 				String extFile = getExtension(e.fileName());
 				if (extFile.equals("epub")){
 					Epub epub = new Epub (e.fileName(), e.clientMtime);
 					mEpubList.add(epub);
 				} 
 			}
 			
 			return mEpubList;
 		} catch (DropboxException e) {
 			Log.i(TAG, "Error Listing", e);
 		}	
		return null;	 
	 }
	 
	 /**
	  * Ordena alfab�ticamente
	  */
	 public void ordenaNombre() { 
		 for(int i=0;i<mEpubList.size();i++) {
			 for(int j=i+1;j<mEpubList.size()-1;j++) {				
				 if(mEpubList.get(i).getNombre().compareTo(mEpubList.get(j).getNombre())>0) {
					 Epub auxiliar=mEpubList.get(i);
			 		 mEpubList.set(i, mEpubList.get(j));
			 		 mEpubList.set(j, auxiliar); 
				 }
		 	}
		 } 
	 }
	 
	 /**
	  * Ordena por fecha, de m�s antiguo a m�s reciente
	  */
	 public void ordenaFecha() {	
		 for (int i=0; i<mEpubList.size(); i++) {
			 for (int j=i+1; j<mEpubList.size()-1; j++) {
				 if (mEpubList.get(i).getFecha().after(mEpubList.get(j).getFecha())) {
					 Epub auxiliar = mEpubList.get(i);
					 mEpubList.set(i, mEpubList.get(j));
					 mEpubList.set(j, auxiliar);
				 }
			 }
		 }
	 } 
}
