//
//   Copyright 2013 jordi domenech <http://iamyellow.net, jordi@iamyellow.net>
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//

package net.iamyellow.gcmjs;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiRHelper;

import android.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	
	private final static AtomicInteger c = new AtomicInteger(0);
	private final static Random generator = new Random();
	
    public GCMIntentService () {
		super(TiApplication.getInstance().getAppProperties().getString(GcmjsModule.PROPERTY_SENDER_ID, ""));
    }
        
    @Override
    protected void onRegistered (Context context, String registrationId) {
    	GcmjsModule module = GcmjsModule.getInstance();
    	if (module != null) {
    		GcmjsModule.logd("onRegistered: got the module!");
    		module.fireSuccess(registrationId);
    	}
    	else {
    		GcmjsModule.logd("onRegistered: module instance not found.");
    	}
    }
    
    @Override
    protected void onUnregistered (Context context, String registrationId) {
    	GcmjsModule module = GcmjsModule.getInstance();
    	if (module != null) {
    		GcmjsModule.logd("onUnregistered: got the module!");
    		module.fireUnregister(registrationId);
    	}
    	else {
    		GcmjsModule.logd("onUnregistered: module instance not found.");
    	}
    }

    @Override
    protected void onMessage (Context context, Intent messageIntent) {
    	TiApplication tiapp = TiApplication.getInstance();
    	
    	GcmjsModule module = GcmjsModule.getInstance();
    	if (module != null) {
    		GcmjsModule.logd("onMessage: got the module!");
    		if (module.isInFg()) {
    			GcmjsModule.logd("onMessage: app is in foreground, no need for notifications.");

    			HashMap<String, Object> messageData = new HashMap<String, Object>();
    	    	for (String key : messageIntent.getExtras().keySet()) {
    	    		String eventKey = key.startsWith("data.") ? key.substring(5) : key;
    				messageData.put(eventKey, messageIntent.getExtras().getString(key));
    			}
    			module.fireMessage(messageData);
    			return;
    		}
    	}
    	else {
    		GcmjsModule.logd("onMessage: module instance not found.");
    	}
    	
    	HashMap<String, Object> messageData = new HashMap<String, Object>();
    	try {
    		String className = TiApplication.getInstance().getAppProperties()
    				.getString(GcmjsModule.PROPERTY_CLASS_NAME, "");
    		// Log.d("gcmjs", "Application class is " + className);
			Intent intent = new Intent(context, Class.forName(className));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			// Log.d("gcmjs", "onMessage: I am in background!");
	    	String title = "", message = "";
	    	for (String key : messageIntent.getExtras().keySet()) {
	    		String eventKey = key.startsWith("data.") ? key.substring(5) : key;
				String value = messageIntent.getExtras().getString(key);
				// Log.d("gcmjs", "There is a key " + eventKey);
				// Log.d("gcmjs", "Its value is " + messageIntent.getExtras().getString(key));
				intent.putExtra(eventKey, value);
				messageData.put(eventKey, value);
				if(eventKey.equals("title")) {
					title = value;
				} else if(eventKey.equals("message")) {
					message = value;
				}
				
			}
	    	
	    	PendingIntent pendingIntent = PendingIntent.getActivity(context, generator.nextInt(), intent, 
					PendingIntent.FLAG_UPDATE_CURRENT);
			
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
	    	.setWhen(System.currentTimeMillis())
	    	.setSmallIcon(TiRHelper.getApplicationResource(
	    			"drawable." +
	    			TiApplication.getInstance().getAppProperties()
	    			.getString("GCM_icon", "appicon")))
	    	.setContentTitle(title)
	    	.setContentText(message)
	    	.setContentIntent(pendingIntent);
	    	Notification notification = builder.build();
	    	notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
	    	notification.tickerText = "Ticker text";
	    	
	    	
	    	NotificationManager manager = (NotificationManager)context.getSystemService(Activity.NOTIFICATION_SERVICE);
	    	manager.notify(c.incrementAndGet(), notification);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			// Log.d("gcmjs", "Class not found");
			e.printStackTrace();
		}
    	
    	/*
		Intent intent = new Intent(tiapp, GcmjsService.class);
        for (String key : messageIntent.getExtras().keySet()) {
			String eventKey = key.startsWith("data.") ? key.substring(5) : key;
			intent.putExtra(eventKey, messageIntent.getExtras().getString(key));
		}
        tiapp.startService(intent);
        */
    }

    @Override
    public void onError (Context context, String errorId) {
    	GcmjsModule module = GcmjsModule.getInstance();
    	if (module != null) {
    		GcmjsModule.logd("onError: got the module!");
    		module.fireError(errorId);
    	}
    	else {
    		GcmjsModule.logd("onError: module instance not found.");
    	}
    }

    @Override
    protected boolean onRecoverableError (Context context, String errorId) {
    	GcmjsModule module = GcmjsModule.getInstance();
    	if (module != null) {
    		GcmjsModule.logd("onRecoverableError: got the module!");
    		module.fireError(errorId);
    	}
    	else {
    		GcmjsModule.logd("onRecoverableError: module instance not found.");
    	}
        return super.onRecoverableError(context, errorId);
    }

    @Override
    protected void onDeletedMessages (Context context, int total) {
    }
}