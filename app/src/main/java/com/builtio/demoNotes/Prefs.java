package com.builtio.demoNotes;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

	public static SharedPreferences get(Context context) {

		return context.getSharedPreferences("BUILT_IO_NOTES", 0);
	}
}
