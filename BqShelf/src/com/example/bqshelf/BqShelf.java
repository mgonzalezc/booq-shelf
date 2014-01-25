package com.example.bqshelf;

import com.dropbox.client2.session.Session.AccessType;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class BqShelf extends Activity {
	private static final String TAG = "BqAccess";

	final static private String APP_KEY = "j2kbcynosjjswjo";
	final static private String APP_SECRET = "jdc0gz2h05j6oyj";
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bq_shelf);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bq_shelf, menu);
		return true;
	}

}
