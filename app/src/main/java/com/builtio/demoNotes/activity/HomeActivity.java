package com.builtio.demoNotes.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.builtio.demoNotes.AppConstants;
import com.builtio.demoNotes.AppSettings;
import com.builtio.demoNotes.R;
import com.raweng.built.Built;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltImageDownloadCallback;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.BuiltUpload;
import com.raweng.built.userInterface.BuiltListViewResultCallBack;
import com.raweng.built.userInterface.BuiltUIListViewController;
import com.raweng.built.utilities.BuiltConstant;
import com.raweng.built.view.BuiltImageView;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomeActivity extends Activity {

	// request code used if activity is opened for modification.
	public static final int OPEN_NOTE_EDIT_MODE = 1000;

	// request code used if activity is opened to create new note/check list.
	public static final int OPEN_NOTE_CREATE_MODE = 2000;

	public static boolean isDoneButtonClicked = false;

	// note types
	private final static int CHECK_LIST_ROW = 1;
	private final int LIST_ROW = 2;


	private BuiltUIListViewController  noteListView;
    public  static BuiltApplication builtApplication;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initialised BuiltUIListViewController instance with class uid.
		noteListView = new BuiltUIListViewController(HomeActivity.this, "bltdfcc61830fb5b32b","notes");

        try {
            builtApplication = Built.application(this, "bltdfcc61830fb5b32b");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(noteListView.getLayout());

		// set up action bar
		setUpActionBar();

		//to display built.io library logs.
		Built.showLogs(BuiltConstant.LogType.all.all);

		//To set item limits
		noteListView.setLimit(10);

		// To set listview background color.
		noteListView.setListViewDividerHeight(0);
		noteListView.setListViewBackgroundColor(getResources().getColor(R.color.lighter_gray));
		noteListView.getBuiltQueryInstance().includeOwner().descending("updated_at");

		noteListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				AppConstants.builtObject = (BuiltObject) arg0.getAdapter().getItem(position);
				AppConstants.position = position - 1;

				if(TextUtils.isEmpty(AppConstants.builtObject.getString("note_title")) || AppConstants.builtObject.getString("note_title") == null ){
					// open UIAddANoteCheckActivity activity to create new check list or update existing note.
					Intent openNoteIntent = new Intent(HomeActivity.this, CheckNoteActivity.class);
					openNoteIntent.putExtra("isEditable", true);
					startActivityForResult(openNoteIntent, OPEN_NOTE_EDIT_MODE);

				}else{
					// open UIAddANoteActivity activity to create new note or update existing note.
					Intent openNoteIntent = new Intent(HomeActivity.this, NoteActivity.class);
					openNoteIntent.putExtra("isEditable", true);
					startActivityForResult(openNoteIntent, OPEN_NOTE_EDIT_MODE);
				}
			}
		});

		final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
		dialog.setTitle(getResources().getString(R.string.please_wait));
		dialog.setMessage(getResources().getString(R.string.loading));
		dialog.setCancelable(false);
		noteListView.setProgressDialog(dialog);

		// To fetch all notes belongs to this user from built.io server.
		noteListView.loadData(new BuiltListViewResultCallBack() {

			@Override
			public void onError(BuiltError error) {}

			@Override
			public void onAlways() {

			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent, BuiltObject builtObject) {

				NoteViewHolder noteViewHolder;
				builtObject.toJSON();

				if(convertView == null){
					
					LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
					convertView = inflater.inflate(R.layout.view_note_list_row, parent, false);
					noteViewHolder      = new NoteViewHolder();
					
					noteViewHolder.noteTitleTextView      = (TextView) convertView.findViewById(R.id.noteTitleTextViewFormNoteRow);
					noteViewHolder.noteDesciptionTextView = (TextView) convertView.findViewById(R.id.noteDescriptionTextViewFormNoteRow);
					noteViewHolder.topBorder			  = (View) convertView.findViewById(R.id.topBorderFormNoteRow);
					noteViewHolder.builtImageView		  = (BuiltImageView) convertView.findViewById(R.id.builtImageView);	
					noteViewHolder.itemOneCheckBox   	  = (CheckBox) convertView.findViewById(R.id.itemOnecheckBoxFormCheckNoteRow);
					noteViewHolder.itemTwoCheckBox   	  = (CheckBox) convertView.findViewById(R.id.itemTwocheckBoxFormCheckNoteRow);
					noteViewHolder.itemOneTextView        = (TextView) convertView.findViewById(R.id.itemOneTextViewFormCheckNoteRow);
					noteViewHolder.itemTwoTextView        = (TextView) convertView.findViewById(R.id.itemTwoTextViewFormCheckNoteRow);
					noteViewHolder.checkListLayout        = (RelativeLayout)convertView.findViewById(R.id.mainContainerFormCheckNoteRow);
					noteViewHolder.listLayout		      = (RelativeLayout) convertView.findViewById(R.id.mainContainerFormNoteRow);
					noteViewHolder.progressBar			  = (ProgressBar) convertView.findViewById(R.id.progress);
					
					noteViewHolder.populateView(HomeActivity.this, builtObject, getViewType(builtObject));
					convertView.setTag(noteViewHolder);

				}else{
					noteViewHolder = (NoteViewHolder) convertView.getTag();
					noteViewHolder.populateView(HomeActivity.this, builtObject, getViewType(builtObject));
				}

				return convertView;
			}

			@Override
			public int getItemViewType(int position) {
				return position;
			}

			@Override
			public int getViewTypeCount() {
				return 1;
			}
		});

	}

	// To determine note type
	private int getViewType(BuiltObject builtObject) {
		if(builtObject.has("note_title")){
			if(TextUtils.isEmpty(builtObject.getString("note_title")) ||  builtObject.getString("note_title") == null){
				return CHECK_LIST_ROW;
			}else{
				return LIST_ROW;
			}
		}else{
			return CHECK_LIST_ROW;
		}

	}

	// To set action bar
	private void setUpActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(getResources().getString(R.string.uinotes_notes_text));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == OPEN_NOTE_EDIT_MODE){
			if(AppConstants.builtObject != null){
				if(isDoneButtonClicked){
					noteListView.replaceBuiltObjectAtIndex(AppConstants.position, AppConstants.builtObject);
					isDoneButtonClicked = false;
				}		
			}else{
				noteListView.deleteBuiltObjectAtIndex(AppConstants.position);
			}
			noteListView.notifyDataSetChanged();

		}else if(requestCode == OPEN_NOTE_CREATE_MODE){
			if(isDoneButtonClicked){
				noteListView.insertBuiltObjectAtIndex(0, AppConstants.builtObject);
				noteListView.notifyDataSetChanged();
				isDoneButtonClicked = false;
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addNoteMenu:
			// To create new note
			Intent openNoteIntent = new Intent(HomeActivity.this, NoteActivity.class);
			startActivityForResult(openNoteIntent, OPEN_NOTE_CREATE_MODE);
			break;

		case R.id.addCheckListMenu:
			// To create new check list
			Intent openCheckNoteIntent = new Intent(HomeActivity.this, CheckNoteActivity.class);
			startActivityForResult(openCheckNoteIntent, OPEN_NOTE_CREATE_MODE);
			break;

		case android.R.id.home:
			finish();	
			break;

		case R.id.logoutMenu:
			final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
			dialog.setTitle(getResources().getString(R.string.please_wait));
			dialog.setMessage(getResources().getString(R.string.loading));
            dialog.setCancelable(false);
            dialog.show();


			builtApplication.getCurrentUser().logoutInBackground(new BuiltResultCallBack() {

                @Override
                public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {

                    if (builtError == null) {
                        dialog.dismiss();
                        AppSettings.setIsLoggedIn(false, HomeActivity.this);
                        finish();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    } else {
                        dialog.dismiss();
                        Toast.makeText(HomeActivity.this, builtError.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

			break;

		}
		return super.onOptionsItemSelected(item);
	}

	// view holder for note row.//TODO
	static class NoteViewHolder{

		BuiltImageView builtImageView;
		TextView noteTitleTextView;
		View topBorder;
		TextView noteDesciptionTextView;

		CheckBox itemOneCheckBox;
		CheckBox itemTwoCheckBox;
		TextView itemOneTextView;
		TextView itemTwoTextView;

		ProgressBar progressBar;
		
		RelativeLayout checkListLayout;
		RelativeLayout listLayout;

		private JSONArray jsonArray;
		String colorValue = null;
		
		public void populateView(final Context context, BuiltObject builtObject, int ViewType){

			try {

				if(ViewType == CHECK_LIST_ROW){
					noteTitleTextView.setText(builtObject.getString("note_todo_list_title"));
					checkListLayout.setVisibility(View.VISIBLE);
					listLayout.setVisibility(View.GONE);
				}else{
					noteTitleTextView.setText(builtObject.getString("note_title"));
					checkListLayout.setVisibility(View.GONE);
					listLayout.setVisibility(View.VISIBLE);
				}

				if(builtObject.has("note_todo_items")){
					
					itemOneCheckBox.setEnabled(false);
					itemTwoCheckBox.setEnabled(false);


					jsonArray = builtObject.getJSONArray("note_todo_items");
					int count = jsonArray.length();

					JSONObject object = jsonArray.getJSONObject(0);
					itemOneCheckBox.setChecked(object.getBoolean("is_checked"));
					itemOneTextView.setText(object.getString("note_todo_item"));

					if(count > 1){
						JSONObject objectOne = jsonArray.getJSONObject(1);
						itemTwoCheckBox.setChecked(objectOne.getBoolean("is_checked"));
						itemTwoTextView.setText(objectOne.getString("note_todo_item"));
						itemTwoCheckBox.setVisibility(View.VISIBLE);
						itemTwoTextView.setVisibility(View.VISIBLE);
					}else{
						itemTwoCheckBox.setVisibility(View.GONE);
						itemTwoTextView.setVisibility(View.GONE);
					}
				}else{
					noteDesciptionTextView.setText(builtObject.getString("note_detail"));
				}
				
				if(builtObject.has("note_image")){
					
					progressBar.setVisibility(View.VISIBLE);

                    try {
                        JSONObject jsonObject = builtObject.getJSONObject("note_image");
                        BuiltUpload builtFile = builtApplication.upload();
                        builtFile.configure(jsonObject);

						builtImageView.setApplicationKey(context, "bltdfcc61830fb5b32b");

                        builtImageView.showProgressOnLoading(progressBar);
                        builtImageView.setTargetedWidth(300);
                        builtImageView.setBuiltUpload((Activity) context, builtFile, new BuiltImageDownloadCallback() {

							@Override
							public void onCompletion(BuiltConstant.ResponseType responseType, Bitmap bitmap, BuiltError builtError) {

								if (builtError == null) {
									builtImageView.setVisibility(View.VISIBLE);
									System.out.println("Image downloaded successfully...");
								} else {
									Toast.makeText(context, builtError.getErrorMessage(), Toast.LENGTH_SHORT).show();
								}
							}
						});

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


				}else{
					builtImageView.setVisibility(View.GONE);
				}
				if(builtObject.has("note_header_color")){

					colorValue = "#" + Integer.toHexString(Integer.parseInt(builtObject.getString("note_header_color")));
					topBorder.setBackgroundColor(Color.parseColor(colorValue));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 

		}
	}

}
