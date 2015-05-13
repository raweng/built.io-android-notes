package com.builtio.demoNotes.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.builtio.demoNotes.AppSettings;
import com.builtio.demoNotes.AppUtils;
import com.builtio.demoNotes.R;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltUser;
import com.raweng.built.userInterface.BuiltUILoginController;


public class LoginActivity extends BuiltUILoginController{

	private final String TAG = "LoginActivity";
	private Context context;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = LoginActivity.this;
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(getResources().getString(R.string.loading));
		progressDialog.setTitle(getResources().getString(R.string.please_wait));
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);

		//Set progress dialog.
		setProgressDialog(progressDialog);

        //Set appilication api key
        setApplicationKey("bltdfcc61830fb5b32b");

        //Check if user is already logged in.
		if(AppSettings.getIsLoggedIn(context)){
			Intent mainIntent = new Intent(context, HomeActivity.class);
			startActivity(mainIntent);        
			finish();
		}


		getActionBar().setTitle(getResources().getString(R.string.uinotes_notes_text));

		//Set visibility false to default closeImageView in BuiltLogin layout.
		closeImageView.setVisibility(View.GONE);

		Intent signUpIntent = new Intent(context, SignUpActivity.class);

		//Intent to open signUp Activity.
		setSignUpIntent(signUpIntent);
	}



	@Override
	public void loginSuccess(BuiltUser user) {
		//Set true when user successfully logged in.
		AppSettings.setIsLoggedIn(true, context);

		//Set user uid.
		AppSettings.setUserUid(user.getUserUid(), context);

		Intent mainIntent = new Intent(context, HomeActivity.class);
		startActivity(mainIntent);        
		finish();
	}
	@Override
	public void loginError(BuiltError error) {
		AppUtils.showLog(TAG, error.getErrorMessage());
		Toast.makeText(context,error.getErrorMessage(),Toast.LENGTH_LONG).show();
	}

}
