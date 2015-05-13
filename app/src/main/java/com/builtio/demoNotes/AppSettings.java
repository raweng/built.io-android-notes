package com.builtio.demoNotes;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings {


	/**
	 *  use to checked whether user is logged-in or not.
	 *  
	 *  
	 */
	public static boolean getIsLoggedIn(Context context) {
		SharedPreferences prefs = Prefs.get(context);
		return prefs.getBoolean("isLoggedIn", false);
	}


	/**
	 * Flag to check one time user login.
	 * 
	 */
	public static void setIsLoggedIn(boolean islogin, Context context) {
		SharedPreferences prefs = Prefs.get(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("isLoggedIn", islogin);
		editor.commit();
	}

	/**
	 * To get logged in user id.
	 *
	 */
	public static String getUserUid(Context context) {
		SharedPreferences prefs = Prefs.get(context);
		return prefs.getString("userUid", null);
	}


	/**
	 * 
	 * To set user id.
	 */
	public static void setUserUid(String userUid, Context context) {
		SharedPreferences prefs = Prefs.get(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("userUid", userUid);
		editor.commit();
	}
}
