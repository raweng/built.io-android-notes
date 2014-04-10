package com.builtio.demoNotes;

import android.app.Application;

import com.raweng.built.Built;

public class ApplicationClass extends Application{

	@Override
	public void onCreate() {
		super.onCreate();                
		try {
			//Initializing built with your application
			Built.initializeWithApiKey(getApplicationContext(), "bltdfcc61830fb5b32b", "notesapp");
		} catch (Exception e) {
			AppUtils.showLog("ApplicationClass", e.toString());
		}
	}
}
