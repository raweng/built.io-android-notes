package com.builtio.demoNotes;


import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

public class AppUtils {


	public static void showLog(String tag, String message) {

		if(BuildConfig.DEBUG){
			Log.i(tag, message);
		}
	}

	public static int getWidthofDeviceInPX(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.widthPixels;
	}
	
	public static String getColorValue(Context context, int position) {

		String colorValue = null;
		switch (position) {
		case 0:
			colorValue = "#FFFFFF";
			return "" + Integer.parseInt(colorValue.replaceFirst("^#",""), 16);

		case 1:
			colorValue = "#FFBB22";
			return "" + Integer.parseInt(colorValue.replaceFirst("^#",""), 16);

		case 2:
			colorValue = "#EEEE22";
			return "" + Integer.parseInt(colorValue.replaceFirst("^#",""), 16);

		case 3:
			colorValue = "#BBE535";
			return "" + Integer.parseInt(colorValue.replaceFirst("^#",""), 16);

		case 4:
			colorValue = "#77DDBB";
			return "" + Integer.parseInt(colorValue.replaceFirst("^#",""), 16);

		case 5:
			colorValue = "#66CCDD";
			return "" + Integer.parseInt(colorValue.replaceFirst("^#",""), 16);

		default:
			colorValue = "#FFFFFF";
			return "" + Integer.parseInt(colorValue.replaceFirst("^#",""), 16);


		}
	}
}
