package com.builtio.demoNotes.activity;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.builtio.demoNotes.AppConstants;
import com.builtio.demoNotes.AppSettings;
import com.builtio.demoNotes.AppUtils;
import com.builtio.demoNotes.R;
import com.raweng.built.BuiltACL;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltFile;
import com.raweng.built.BuiltImageDownloadCallback;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.userInterface.BuiltUIPickerController;
import com.raweng.built.userInterface.BuiltUIPickerController.PickerResultCode;
import com.raweng.built.view.BuiltImageView;
public class NoteActivity extends Activity{

	private EditText notesEditText;
	private EditText titleEditText;
	private View topBorder;
	private BuiltImageView noteImageView;
	private ImageButton deleteImageButton;
	private RelativeLayout imageContainerFromAddANote;
	private ProgressBar  progressBar;

	private RelativeLayout loadingContainer;
	private TextView loadingText;

	private boolean isOpenInEditMode = false;
	private Dialog dialog;

	private ImageView color1imageView;
	private ImageView color1checkedimageView;
	private ImageView color2checkedimageView;
	private ImageView color3checkedimageView;
	private ImageView color4checkedimageView;
	private ImageView color5checkedimageView;
	private ImageView color6checkedimageView;
	private int colorPickerValue = -1;

	private BuiltUIPickerController pickerObject;
	private String uploadedMediaUid = null;
	private String filePath = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		// set up action bar
		setUpActionBar();

		notesEditText 				= (EditText)findViewById(R.id.noteEditTextFromAddANote);
		titleEditText 				= (EditText)findViewById(R.id.notesTitleEditTextFromAddANote);
		topBorder 					= (View)findViewById(R.id.topBorderFromAddANote);
		noteImageView				= (BuiltImageView)findViewById(R.id.noteImageViewFromAddANote);
		deleteImageButton			= (ImageButton)findViewById(R.id.deleteImageButtonFromAddANote);	
		imageContainerFromAddANote 	= (RelativeLayout)findViewById(R.id.imageContainerFromAddANote);
		loadingContainer 	    	= (RelativeLayout)findViewById(R.id.loadingContainer);
		loadingText					= (TextView)findViewById(R.id.loadingText);
		progressBar					= (ProgressBar)findViewById(R.id.progressBarNote);
		
		pickerObject = new BuiltUIPickerController(this);

		isOpenInEditMode = getIntent().getBooleanExtra("isEditable", false);

		if(isOpenInEditMode){

			titleEditText.setText(AppConstants.builtObject.getString("note_title"));
			notesEditText.setText(AppConstants.builtObject.getString("note_detail"));

			if(AppConstants.builtObject.has("note_image")  && (AppConstants.builtObject.get("note_image") instanceof JSONObject)){
				imageContainerFromAddANote.setVisibility(View.VISIBLE);

				progressBar.setVisibility(View.VISIBLE);
				
				BuiltFile builtFile = BuiltFile.builtFileWithResponse(AppConstants.builtObject.getJSONObject("note_image"));
				noteImageView.showProgressOnLoading(progressBar);
				noteImageView.setTargetedWidth(500);
				noteImageView.setBuiltFile(NoteActivity.this, builtFile, new BuiltImageDownloadCallback() {

					@Override
					public void onSuccess(Bitmap bitmap) {
						AppUtils.showLog(NoteActivity.this.getClass().getSimpleName(), "Image downloaded successfully...");
						System.out.println("Image downloaded successfully...");
					}

					@Override
					public void onError(BuiltError error) {
						Toast.makeText(NoteActivity.this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onAlways() {}
				});

				noteImageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent previewIntent = new Intent(NoteActivity.this, PreviewImageActivity.class);
						startActivity(previewIntent);
					}
				});

			}else{
				imageContainerFromAddANote.setVisibility(View.GONE);
			}

			try {
				if(AppConstants.builtObject.has("note_header_color")){
					String value = "#" + Integer.toHexString(Integer.parseInt(AppConstants.builtObject.getString("note_header_color")));
					topBorder.setBackgroundColor(Color.parseColor(value));
					
					if(value.equalsIgnoreCase("#FFFFFF")){
						colorPickerValue = 0;
					}else if(value.equalsIgnoreCase("#FFBB22")){
						colorPickerValue = 1;
					}else if(value.equalsIgnoreCase("#EEEE22")){
						colorPickerValue = 2;
					}else if(value.equalsIgnoreCase("#BBE535")){
						colorPickerValue = 3;
					}else if(value.equalsIgnoreCase("#77DDBB")){
						colorPickerValue = 4;
					}else if(value.equalsIgnoreCase("#66CCDD")){
						colorPickerValue = 5;
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			deleteImageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final Dialog dialog = new Dialog(NoteActivity.this);
					dialog.setTitle(getResources().getString(R.string.dialog_title));
					dialog.setContentView(R.layout.view_delete_note_dialog);
					dialog.findViewById(R.id.dialogButtonYes).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							dialog.dismiss();
							
							noteImageView.setImageBitmap(null);
							imageContainerFromAddANote.setVisibility(View.GONE);
							
							loadingContainer.setVisibility(View.VISIBLE);
							loadingText.setText(getResources().getString(R.string.deleting_note));
							loadingContainer.bringToFront();

							BuiltFile fileObject = new BuiltFile();
							fileObject.setUid(uploadedMediaUid);
							fileObject.destroy(new BuiltResultCallBack() {

								@Override
								public void onSuccess() {
									uploadedMediaUid = null;
								}

								@Override
								public void onError(BuiltError error) {
									if(error.getErrorMessage().equalsIgnoreCase("Set media file upload uid first.")){
										Log.i(getClass().getSimpleName(), error.getErrorMessage());
									}else{
										Toast.makeText(NoteActivity.this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
									}
								}

								@Override
								public void onAlways() {
									loadingContainer.setVisibility(View.GONE);
								}
							});
						}
					});

					dialog.findViewById(R.id.dialogButtonCancel).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();

						}
					});

					dialog.show();					
				}
			});
		}else{
			deleteImageButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					noteImageView.setImageBitmap(null);
					imageContainerFromAddANote.setVisibility(View.GONE);
				}
			});
		}
	}

	// To display color picker dialog.
	public void showColorPickerDialog() {
		dialog = new Dialog(this);
		dialog.setTitle(getString(R.string.action_color_title_text));
		dialog.setContentView(R.layout.colorpickerdialog);
		
		color1checkedimageView = (ImageView)dialog.findViewById(R.id.color1checkedimageView);
		color2checkedimageView = (ImageView)dialog.findViewById(R.id.color2checkedimageView);
		color3checkedimageView = (ImageView)dialog.findViewById(R.id.color3checkedimageView);
		color4checkedimageView = (ImageView)dialog.findViewById(R.id.color4checkedimageView);
		color5checkedimageView = (ImageView)dialog.findViewById(R.id.color5checkedimageView);
		color6checkedimageView = (ImageView)dialog.findViewById(R.id.color6checkedimageView);

		if(	colorPickerValue  == 0){
			color1checkedimageView.setVisibility(View.VISIBLE);

		}else if(colorPickerValue  == 1){
			color2checkedimageView.setVisibility(View.VISIBLE);

		}else if(colorPickerValue  == 2){
			color3checkedimageView.setVisibility(View.VISIBLE);

		}else if(colorPickerValue  == 3){
			color4checkedimageView.setVisibility(View.VISIBLE);

		}else if(colorPickerValue  == 4){
			color5checkedimageView.setVisibility(View.VISIBLE);

		}else if(colorPickerValue  == 5){
			color6checkedimageView.setVisibility(View.VISIBLE);
		}

		color1imageView = (ImageView)dialog.findViewById(R.id.color1imageView);
		color1imageView.setBackgroundColor(getResources().getColor(R.color.color_0_color_picker));

		color1imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				color1checkedimageView.setVisibility(View.VISIBLE);
				colorPickerValue = 0;
				color2checkedimageView.setVisibility(View.GONE);
				color3checkedimageView.setVisibility(View.GONE);
				color4checkedimageView.setVisibility(View.GONE);
				color5checkedimageView.setVisibility(View.GONE);
				color6checkedimageView.setVisibility(View.GONE);
				topBorder.setBackgroundColor(Color.WHITE);
				dialog.dismiss();
			}
		});
		ImageView color2imageView = (ImageView)dialog.findViewById(R.id.color2imageView);
		color2imageView.setBackgroundColor(getResources().getColor(R.color.color_1_color_picker));
		color2imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				colorPickerValue = 1;
				color2checkedimageView.setVisibility(View.VISIBLE);
				color1checkedimageView.setVisibility(View.GONE);
				color3checkedimageView.setVisibility(View.GONE);
				color4checkedimageView.setVisibility(View.GONE);
				color5checkedimageView.setVisibility(View.GONE);
				color6checkedimageView.setVisibility(View.GONE);
				topBorder.setBackgroundColor(getResources().getColor(R.color.color_1_color_picker));
				dialog.dismiss();
			}
		});
		ImageView color3imageView = (ImageView)dialog.findViewById(R.id.color3imageView);
		color3imageView.setBackgroundColor(getResources().getColor(R.color.color_2_color_picker));
		color3imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				colorPickerValue = 2;
				color3checkedimageView.setVisibility(View.VISIBLE);
				color2checkedimageView.setVisibility(View.GONE);
				color1checkedimageView.setVisibility(View.GONE);
				color4checkedimageView.setVisibility(View.GONE);
				color5checkedimageView.setVisibility(View.GONE);
				color6checkedimageView.setVisibility(View.GONE);
				topBorder.setBackgroundColor(getResources().getColor(R.color.color_2_color_picker));
				dialog.dismiss();
			}
		});
		ImageView color4imageView =(ImageView)dialog.findViewById(R.id.color4imageView);
		color4imageView.setBackgroundColor(getResources().getColor(R.color.color_3_color_picker));
		color4imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				colorPickerValue = 3;
				color4checkedimageView.setVisibility(View.VISIBLE);
				color2checkedimageView.setVisibility(View.GONE);
				color3checkedimageView.setVisibility(View.GONE);
				color4checkedimageView.setVisibility(View.GONE);
				color5checkedimageView.setVisibility(View.GONE);
				color6checkedimageView.setVisibility(View.GONE);
				topBorder.setBackgroundColor(getResources().getColor(R.color.color_3_color_picker));
				dialog.dismiss();
			}
		});
		ImageView color5imageView =(ImageView)dialog.findViewById(R.id.color5imageView);
		color5imageView.setBackgroundColor(getResources().getColor(R.color.color_4_color_picker));
		color5imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				colorPickerValue = 4;
				color5checkedimageView.setVisibility(View.VISIBLE);
				color2checkedimageView.setVisibility(View.GONE);
				color3checkedimageView.setVisibility(View.GONE);
				color4checkedimageView.setVisibility(View.GONE);
				color1checkedimageView.setVisibility(View.GONE);
				color6checkedimageView.setVisibility(View.GONE);
				topBorder.setBackgroundColor(getResources().getColor(R.color.color_4_color_picker));
				dialog.dismiss();
			}
		});
		ImageView color6imageView =(ImageView)dialog.findViewById(R.id.color6imageView);
		color6imageView.setBackgroundColor(getResources().getColor(R.color.color_5_color_picker));
		color6imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				colorPickerValue = 5;
				color6checkedimageView.setVisibility(View.VISIBLE);
				color2checkedimageView.setVisibility(View.GONE);
				color3checkedimageView.setVisibility(View.GONE);
				color4checkedimageView.setVisibility(View.GONE);
				color5checkedimageView.setVisibility(View.GONE);
				color1checkedimageView.setVisibility(View.GONE);
				topBorder.setBackgroundColor(getResources().getColor(R.color.color_5_color_picker));
				dialog.dismiss();
			}
		});
		dialog.show();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(isOpenInEditMode){
			getMenuInflater().inflate(R.menu.edit_note_menu, menu);
		}else{
			getMenuInflater().inflate(R.menu.add_a_note_menu, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;

		case R.id.action_done:
			if(TextUtils.isEmpty(titleEditText.getText())){
				Toast.makeText(NoteActivity.this, getResources().getString(R.string.title_blank_error), Toast.LENGTH_SHORT).show();

			}else if(TextUtils.isEmpty(notesEditText.getText())){
				Toast.makeText(NoteActivity.this, getResources().getString(R.string.note_blank_error), Toast.LENGTH_SHORT).show();

			}else{
				HomeActivity.isDoneButtonClicked = true;
				loadingContainer.setVisibility(View.VISIBLE);

				if(isOpenInEditMode){
					loadingText.setText(getResources().getString(R.string.Updating_note));
				}else{
					loadingText.setText(getResources().getString(R.string.saving_note));
				}

				if(filePath != null){
					uploadMediaFile(filePath);
				}else{
					handler.sendEmptyMessage(0);
				}
			}
			return true;

			//To delete note from built.io server
		case R.id.action_delete:
			
			final Dialog dialog = new Dialog(this);
			dialog.setTitle(getResources().getString(R.string.dialog_title));
			dialog.setContentView(R.layout.view_delete_note_dialog);
			dialog.findViewById(R.id.dialogButtonYes).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
					loadingContainer.setVisibility(View.VISIBLE);
					loadingText.setText(getResources().getString(R.string.deleting_note));
					loadingContainer.bringToFront();

					final BuiltObject builtObject = new BuiltObject("notes"); 
					builtObject.setUid(AppConstants.builtObject.getUid());
					builtObject.destroy(new BuiltResultCallBack() {

						@Override
						public void onSuccess() {
							loadingContainer.setVisibility(View.GONE);
							AppConstants.builtObject = null;
							finishActivity(HomeActivity.OPEN_NOTE_EDIT_MODE);
							finish();
						}

						@Override
						public void onError(BuiltError error) {
							loadingContainer.setVisibility(View.GONE);
							Toast.makeText(NoteActivity.this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onAlways() {}
					});
				}
			});

			dialog.findViewById(R.id.dialogButtonCancel).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();
			return true;

		case R.id.action_choose_color:
			showColorPickerDialog();
			return true;

		case R.id.action_add_image:
			try {
				pickerObject.showPicker(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			imageContainerFromAddANote.setVisibility(View.VISIBLE);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@SuppressLint("NewApi")
	private void setUpActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(getResources().getString(R.string.uinotes_notes_text));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == PickerResultCode.SELECT_FROM_FILE_SYSTEM_REQUEST_CODE.getValue() && data != null){
			if(resultCode == RESULT_OK){

				filePath = (String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("filePath");
				Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
				noteImageView.setImageBitmap(myBitmap);
				imageContainerFromAddANote.setVisibility(View.VISIBLE);

			}else if(resultCode == RESULT_CANCELED){
				Log.i("BuiltUIPickerController", "Nothing selected");
			}
		}else if(requestCode == PickerResultCode.SELECT_IMAGE_FROM_GALLERY_REQUEST_CODE.getValue() && data != null){
			if(resultCode == RESULT_OK){

				filePath = (String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("filePath");
				Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
				noteImageView.setImageBitmap(myBitmap);
				imageContainerFromAddANote.setVisibility(View.VISIBLE);

			}else if(resultCode == RESULT_CANCELED){
				Log.i("BuiltUIPickerController", "Nothing selected");
			}
		}else if(requestCode == PickerResultCode.CAPTURE_IMAGE_REQUEST_CODE.getValue()){
			if(resultCode == RESULT_OK){

				filePath = (String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("filePath");
				Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
				noteImageView.setImageBitmap(myBitmap);
				imageContainerFromAddANote.setVisibility(View.VISIBLE);

			}else if(resultCode == RESULT_CANCELED){
				Log.i("BuiltUIPickerController", "Nothing selected");
			}
		}else if(requestCode == PickerResultCode.CAPTURE_VIDEO_REQUEST_CODE.getValue() && data != null){
			if(resultCode == RESULT_OK){

				filePath = (String) pickerObject.getFileInfoForMediaFile(data ,requestCode).get("filePath");
				Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
				noteImageView.setImageBitmap(myBitmap);
				imageContainerFromAddANote.setVisibility(View.VISIBLE);

			}else if(resultCode == RESULT_CANCELED){
				Log.i("BuiltUIPickerController", "Nothing selected");
			}
		}
	}


	private void uploadMediaFile(String filePath) {

		loadingContainer.bringToFront();
		
		BuiltACL builtACL = new BuiltACL();
		//		builtACL.disableACL(true);
		builtACL.setPublicReadAccess(false);
		builtACL.setPublicDeleteAccess(false);
		builtACL.setPublicWriteAccess(false);
		builtACL.setUserDeleteAccess(AppSettings.getUserUid(NoteActivity.this), true);
		builtACL.setUserReadAccess(AppSettings.getUserUid(NoteActivity.this), true);
		builtACL.setUserWriteAccess(AppSettings.getUserUid(NoteActivity.this), true);

		final BuiltFile fileObject = new BuiltFile();
		fileObject.setFile(filePath);
		fileObject.setACL(builtACL);
		fileObject.save(new BuiltResultCallBack() {

			@Override
			public void onSuccess() {
				uploadedMediaUid = fileObject.getUploadUid();		
				handler.sendEmptyMessage(0);
			}

			@Override
			public void onError(BuiltError error) {
				loadingContainer.setVisibility(View.GONE);
				Toast.makeText(NoteActivity.this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAlways() {}
		});

	}

	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0){


				loadingContainer.bringToFront();
				final BuiltObject builtObject = new BuiltObject("notes");
				builtObject.set("note_title", titleEditText.getText().toString());
				builtObject.set("note_detail", notesEditText.getText().toString());

				builtObject.set("note_image", uploadedMediaUid);

				if(colorPickerValue != -1){
					builtObject.set("note_header_color", AppUtils.getColorValue(NoteActivity.this, colorPickerValue));
				}

				BuiltACL builtACL = new BuiltACL();
				builtACL.setPublicReadAccess(false);
				builtACL.setPublicDeleteAccess(false);
				builtACL.setPublicWriteAccess(false);
				builtACL.setUserDeleteAccess(AppSettings.getUserUid(NoteActivity.this), true);
				builtACL.setUserReadAccess(AppSettings.getUserUid(NoteActivity.this), true);
				builtACL.setUserWriteAccess(AppSettings.getUserUid(NoteActivity.this), true);
				builtObject.setACL(builtACL); 

				if(isOpenInEditMode){
					builtObject.setUid(AppConstants.builtObject.getUid());
				}

				// To save object on built.io server.
				builtObject.save(new BuiltResultCallBack() {

					@Override
					public void onSuccess() {
						if(isOpenInEditMode){
							isOpenInEditMode = false; 
							loadingContainer.setVisibility(View.GONE);
							AppConstants.builtObject = builtObject;
							finishActivity(HomeActivity.OPEN_NOTE_EDIT_MODE);
						}else{
							loadingContainer.setVisibility(View.GONE);
							AppConstants.builtObject = builtObject;
							finishActivity(HomeActivity.OPEN_NOTE_CREATE_MODE);
						}
						finish();
					}

					@Override
					public void onError(BuiltError error) {
						loadingContainer.setVisibility(View.GONE);
						Toast.makeText(NoteActivity.this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();

					}

					@Override
					public void onAlways() {}
				});

			}
		}
	};

}
