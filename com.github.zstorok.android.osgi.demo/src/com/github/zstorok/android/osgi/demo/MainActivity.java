/**
 * Copyright (c) 2011 Zsolt Török.
 *  
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0, 
 * available at http://www.eclipse.org/legal/epl-v10.html  
 */
package com.github.zstorok.android.osgi.demo;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * @author zsolt
 *
 */
public class MainActivity extends Activity {

	    /** Called when the activity is first created. 
	     * @throws IOException */
	    @Override
	    public synchronized void onCreate(Bundle icicle) {
	    	Log.i(getClass().getSimpleName(), "MainActivity.onCreate()");
	        super.onCreate(icicle);
	        setContentView(R.layout.main);
	    }
	    
	    @Override
	    public synchronized void onStart() {
	    	Log.i(getClass().getSimpleName(), "MainActivity.onStart()");
	        super.onStart();
	        setContentView(new View(this));
	        startService(new Intent(this, EmbeddedOsgiService.class));
	    }
	    
	    @Override
	    protected void onStop() {
	    	Log.i(getClass().getSimpleName(), "MainActivity.onStop()");
	    	super.onStop();
	    	stopService(new Intent(this, EmbeddedOsgiService.class));
	    }
	    
	    @Override
	    protected void onDestroy() {
	    	Log.i(getClass().getSimpleName(), "MainActivity.onDestroy()");
	    	super.onDestroy();
	    }
}
