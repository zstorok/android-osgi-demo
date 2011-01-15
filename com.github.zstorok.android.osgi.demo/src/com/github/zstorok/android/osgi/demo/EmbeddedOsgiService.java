/**
 * Copyright (c) 2011 Zsolt Török.
 *  
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0, 
 * available at http://www.eclipse.org/legal/epl-v10.html  
 */
package com.github.zstorok.android.osgi.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.felix.framework.Felix;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.github.zstorok.android.osgi.demo.service.ServiceInterfaceBundleActivator;
import com.github.zstorok.android.osgi.demo.service.api.HelloService;
import com.github.zstorok.android.osgi.demo.service.hu.HungarianBundleActivator;

/**
 * @author zsolt
 * 
 */
public class EmbeddedOsgiService extends Service {

	private Felix felix;
	private ServiceTracker tracker;

	public EmbeddedOsgiService() {
		Log.i(getClass().getSimpleName(), "EmbeddedOsgiService()");
	}

	@Override
	public void onCreate() {
		Log.i(getClass().getSimpleName(), "EmbeddedOsgiService.onCreate() start.");

		Thread osgiInitThread = new Thread(new Runnable() {

			public void run() {
				Properties config = System.getProperties();
				config.put("org.osgi.framework.startlevel.beginning", "0");
				config.put("org.osgi.framework.storage.clean", "onFirstInit");

				// make sure the OSGi cache dir is set to something sensible
				File cacheDir = EmbeddedOsgiService.this.getDir("osgi.cache", Context.MODE_PRIVATE);
				Log.d(getClass().getSimpleName(),
					"Setting osgi cache location to: " + cacheDir.getAbsolutePath());
				config.put("org.osgi.framework.storage", cacheDir.getAbsolutePath());
				config.put("felix.log.level", "1");

				List<BundleActivator> activators = new ArrayList<BundleActivator>();
				activators.add(new ServiceInterfaceBundleActivator());
				activators.add(new HungarianBundleActivator());
				config.put("felix.systembundle.activators", activators);

				try {
					// Create an instance of the framework with our configuration properties
					Log.d(EmbeddedOsgiService.class.getSimpleName(),
							"Starting Felix...");
					felix = new Felix(config);

					// Start Felix instance
					felix.start();

					tracker = new ServiceTracker(felix.getBundleContext(),
							felix.getBundleContext().createFilter("(" + Constants.OBJECTCLASS + "=" + HelloService.class.getName() + ")"),
							new ServiceTrackerCustomizer() {

								public Object addingService(ServiceReference ref) {
									final HelloService service = (HelloService) felix.getBundleContext().getService(ref);
									System.out.println("addingService(" + ref + "): " + service);
									showNotification("Added " + service.getClass().getSimpleName() + ".", "Service says: '" + service.getMessage() + "'");
									return service;
								}

								public void modifiedService(
										ServiceReference ref, Object service) {
									removedService(ref, service);
									addingService(ref);
								}

								public void removedService(ServiceReference ref, Object service) {
									System.out.println("removedService(" + ref + ", " + service + ")");
									showNotification("Removed " + service.getClass().getSimpleName() + ".", "");
									felix.getBundleContext().ungetService(ref);
								}
							});
					tracker.open();
				} catch (BundleException e) {
					Log.e(getClass().getSimpleName(), e.getMessage());
				} catch (InvalidSyntaxException e) {
					Log.e(getClass().getSimpleName(), e.getMessage());
				}
			}
		});
		osgiInitThread.start();

		Log.i(getClass().getSimpleName(), "EmbeddedOsgiService.onCreate() end.");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(getClass().getSimpleName(), "EmbeddedOsgiService.onDestroy()");
		tracker.close();
		tracker = null;
		try {
			felix.stop();
			felix.waitForStop(5000);
		} catch (BundleException e) {
			Log.e(getClass().getSimpleName(), e.getMessage());
		} catch (InterruptedException e) {
			Log.e(getClass().getSimpleName(), e.getMessage());
		}
		felix = null;
	}

	private void showNotification(String title, String message) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon, "OSGi Service Notification", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
		notification.setLatestEventInfo(this, title, message, contentIntent);
		notificationManager.notify(title.hashCode(), notification);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
