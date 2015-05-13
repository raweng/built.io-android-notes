package com.builtio.demoNotes.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.builtio.demoNotes.AppConstants;
import com.builtio.demoNotes.AppSettings;
import com.builtio.demoNotes.AppUtils;
import com.builtio.demoNotes.R;
import com.raweng.built.Built;
import com.raweng.built.BuiltACL;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltImageDownloadCallback;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.BuiltUpload;
import com.raweng.built.BuiltUploadCallback;
import com.raweng.built.userInterface.BuiltUIPickerController;
import com.raweng.built.userInterface.BuiltUIPickerController.PickerResultCode;
import com.raweng.built.utilities.BuiltConstant;
import com.raweng.built.view.BuiltImageView;

public class CheckNoteActivity extends Activity{


    private View topBorderFromAddANote;

    private ScrollView scrollView;
    private RelativeLayout loadingContainer;
    private RelativeLayout imageContainerFromAddANote;
    private LinearLayout linearLayoutContainer;

    private EditText titleEditText;
    private EditText addanotecheck_todoedittext;
    private EditText edittext;

    private CheckBox addanotecheck_todocheckBox;
    private CheckBox checkBox;

    private TextView loadingText;
    private Dialog dialog;

    private ImageView color1imageView;
    private ImageView color1checkedimageView;
    private ImageView color2checkedimageView;
    private ImageView color3checkedimageView;
    private ImageView color4checkedimageView;
    private ImageView color5checkedimageView;
    private ImageView color6checkedimageView;
    private ImageView closeImageView;
    private ImageButton deleteImageButton;

    private ProgressBar  progressBar;
    private BuiltImageView noteImageView;
    private BuiltUIPickerController pickerObject;

    private boolean isOpenInEditMode = false;
    private boolean is_checked;

    private ArrayList<String> note_todo_item_list;
    private ArrayList<Boolean> is_checked_list;

    private int count;
    private int colorPickerValue;

    private String note_todo_item;
    private String uploadedMediaUid = null;
    private String filePath = null;
    private BuiltApplication builtApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        // set up action bar
        setUpActionBar();

        try {
            builtApplication = Built.application(this, "bltdfcc61830fb5b32b");
        } catch (Exception e) {
            e.printStackTrace();
        }
        colorPickerValue = 0;
        pickerObject = new BuiltUIPickerController(this);

        // get Edittext for title and type a note.
        titleEditText 		= (EditText)findViewById(R.id.notesTitleEditTextFromAddANoteCheck);

        scrollView=(ScrollView)findViewById(R.id.addANoteScrollView);

        // top color border for the view
        topBorderFromAddANote = (View)findViewById(R.id.topBorderFromAddANoteCheck);

        // loading container displayed during done button click event
        loadingContainer 	= (RelativeLayout)findViewById(R.id.loadingContainer);

        // linearlayout container to store the list of relativelayout rows.
        linearLayoutContainer=(LinearLayout)findViewById(R.id.linearLayoutContainer);
        loadingText			 = (TextView)findViewById(R.id.loadingText);

        noteImageView				= (BuiltImageView)findViewById(R.id.noteImageViewFromAddANoteCheck);
        deleteImageButton			= (ImageButton)findViewById(R.id.deleteImageButtonFromAddANoteCheck);
        imageContainerFromAddANote 	= (RelativeLayout)findViewById(R.id.imageContainerFromAddANoteCheck);
        progressBar					= (ProgressBar)findViewById(R.id.progressBarCheck);

        // arraylist to store the list of editable todo list item string values
        note_todo_item_list = new ArrayList<String>();

        // arraylist to store the list of editable checked state of todo list item boolean values
        is_checked_list = new ArrayList<Boolean>();

        // to check whether the activity todo list items is in edit mode or not.
        isOpenInEditMode = getIntent().getBooleanExtra("isEditable", false);

        if(isOpenInEditMode){
            // if it is an existing todo list item then this block of code gets executed.

            // sets title of Title edittext obtained from built object.
            titleEditText.setText(AppConstants.builtObject.getString("note_todo_list_title"));

            // sets the notetodoitemslist obtained from builtobject.
            org.json.JSONArray noteTodoItemsList = AppConstants.builtObject.getJSONArray("note_todo_items");
            int count = noteTodoItemsList.length();
            for(int n = 0 ; n < count; n++){
                try {
                    org.json.JSONObject jsonObject = noteTodoItemsList.getJSONObject(n);
                    is_checked = jsonObject.getBoolean("is_checked");
                    note_todo_item = jsonObject.getString("note_todo_item");
                    // add note todo item one by one in arraylist
                    note_todo_item_list.add(note_todo_item);
                    // add note todo item checked state one by one in arraylist
                    is_checked_list.add(is_checked);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // this method creates rows of existing todo list .
            createNewRowInEditableMode();

            try {
                // sets the color which was previously saved by user obtained from built object.
                if(AppConstants.builtObject.has("note_header_color")){
                    String value = "#" + Integer.toHexString(Integer.parseInt(AppConstants.builtObject.getString("note_header_color")));
                    topBorderFromAddANote.setBackgroundColor(Color.parseColor(value));

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

            if(AppConstants.builtObject.has("note_image")  && (AppConstants.builtObject.get("note_image") instanceof JSONObject)){
                imageContainerFromAddANote.setVisibility(View.VISIBLE);
                AppConstants.builtObject.toJSON();

                progressBar.setVisibility(View.VISIBLE);

                Activity activity = CheckNoteActivity.this;

                try {
                    JSONObject jsonObject = AppConstants.builtObject.getJSONObject("note_image");
                    BuiltUpload builtFile = builtApplication.upload();
                    builtFile.configure(jsonObject);

                    noteImageView.setApplicationKey(CheckNoteActivity.this, "bltdfcc61830fb5b32b");

                    noteImageView.showProgressOnLoading(progressBar);
                    noteImageView.setTargetedWidth(500);
                    noteImageView.setBuiltUpload(activity, builtFile, new BuiltImageDownloadCallback() {

                        @Override
                        public void onCompletion(BuiltConstant.ResponseType responseType, Bitmap bitmap, BuiltError builtError) {

                            if (builtError == null) {
                                AppUtils.showLog("CheckNoteActivity", "Image Download Successfully...");
                            } else {
                                Toast.makeText(CheckNoteActivity.this, builtError.getErrorMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    noteImageView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent previewIntent = new Intent(CheckNoteActivity.this, PreviewImageActivity.class);
                            startActivity(previewIntent);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }else{
                imageContainerFromAddANote.setVisibility(View.GONE);
            }
        }else{
            // if a new todo list is to be created this block of code gets executed.

            // inflate xml row to add in the layout
            View linearLayout=(View)RelativeLayout.inflate(CheckNoteActivity.this, R.layout.view_row_check_list, null);

            // this is the parent linearlayout container in which many relativelayouts can be added.
            linearLayoutContainer.addView(linearLayout);

            // obtain todo edittext from inflated relativelayout.
            addanotecheck_todoedittext = (EditText) linearLayoutContainer.findViewById(R.id.addanotecheck_todoedittext);

            closeImageView =(ImageView)linearLayout.findViewById(R.id.closeImageView);

            addanotecheck_todocheckBox =(CheckBox) linearLayoutContainer.findViewById(R.id.addanotecheck_todocheckBox);

            // set initial paint flag to be striked to match with the unstriked text style.
            addanotecheck_todoedittext.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            addanotecheck_todoedittext.setPaintFlags(addanotecheck_todoedittext.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));

            // initially every edittext will be tagged so that next time user clicks on this edittext it should not add a new row, since rows should be added only once.
            addanotecheck_todoedittext.setTag("checked");

            // set click listener to add a new row.
            addanotecheck_todoedittext.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String tag =(String) v.getTag();
                    // if first time the row is clicked then a new row gets created.
                    int count = linearLayoutContainer.getChildCount();
                    // if count is 1 then a new row is created
                    if(count==1){
                        v.setTag("checked");
                    }
                    if(tag.equals("checked")){
                        createNewRow();
                    }
                    v.setTag("alreadyClicked");
                }
            });

            addanotecheck_todoedittext.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String tag =(String) v.getTag();
                    // if first time the row is clicked then a new row gets created.
                    int count = linearLayoutContainer.getChildCount();
                    // if count is 1 then a new row is created
                    if(count==1){
                        v.setTag("checked");
                    }
                    if(tag.equals("checked")){
                        createNewRow();
                    }
                    v.setTag("alreadyClicked");
                }
            });

            // add text change listener to set the text style as striked or unstriked.

            addanotecheck_todoedittext.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // set text style as striked if checkbox state is checked , unstriked otherwise.
                    if(s.toString().length()>0){
                        if(addanotecheck_todocheckBox.isChecked()){
                            addanotecheck_todoedittext.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        }else{
                            addanotecheck_todoedittext.setPaintFlags(addanotecheck_todoedittext.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                        }
                    }else{
                        addanotecheck_todoedittext.setPaintFlags(addanotecheck_todoedittext.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                }
            });

            closeImageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View closeButtonImageView) {
                    // remove the row on click of close button.
                    int count = linearLayoutContainer.getChildCount();
                    if(count>1){
                        one: for(int n = 0; n < count; n++){
                            RelativeLayout linearLayout =(RelativeLayout)linearLayoutContainer.getChildAt(n);
                            ImageView	closeImageView =(ImageView)linearLayout.findViewById(R.id.closeImageView);
                            if(closeImageView == closeButtonImageView){
                                linearLayoutContainer.removeViewAt(n);
                                break one;
                            }
                        }
                    }else if(count==1){
                        // if only one row left then the text would be cleared and also the checkbox state is changed
                        RelativeLayout linearLayout =(RelativeLayout)linearLayoutContainer.getChildAt(0);
                        EditText	editText =(EditText)linearLayout.findViewById(R.id.addanotecheck_todoedittext);
                        CheckBox	checkBox =(CheckBox)linearLayout.findViewById(R.id.addanotecheck_todocheckBox);
                        editText.setText("");
                        checkBox.setChecked(false);
                    }
                }
            });


            addanotecheck_todocheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {


                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                    String	value = addanotecheck_todoedittext.getText().toString();
                    // check the state of checkbox and set text style according to it.
                    if(isChecked){
                        addanotecheck_todoedittext.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        addanotecheck_todoedittext.setText(value);
                    }else{
                        addanotecheck_todoedittext.setPaintFlags(addanotecheck_todoedittext.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                        addanotecheck_todoedittext.setText(value);
                    }
                }
            });
        }

        deleteImageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(CheckNoteActivity.this);
                dialog.setTitle(getResources().getString(R.string.dialog_title));
                dialog.setContentView(R.layout.view_delete_note_dialog);
                dialog.findViewById(R.id.dialogButtonYes).setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        dialog.dismiss();
                        loadingContainer.setVisibility(View.VISIBLE);
                        loadingText.setText(getResources().getString(R.string.deleting_note));
                        loadingContainer.bringToFront();

                        noteImageView.setImageBitmap(null);
                        imageContainerFromAddANote.setVisibility(View.GONE);

                        BuiltUpload builtFile = builtApplication.upload(uploadedMediaUid);
                        builtFile.destroyInBackground(new BuiltResultCallBack() {

                            @Override
                            public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {

                                if (builtError == null){
                                    loadingContainer.setVisibility(View.GONE);
                                    uploadedMediaUid = null;

                                }else {
                                    loadingContainer.setVisibility(View.GONE);

                                    if (builtError.getErrorMessage().equalsIgnoreCase("Set media file upload uid first.")) {
                                        Log.i(getClass().getSimpleName(), builtError.getErrorMessage());
                                    } else {
                                        Toast.makeText(CheckNoteActivity.this, builtError.getErrorMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
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

    }

    public void createNewRowInEditableMode(){
        // this is the method called in editable mode.
        count = note_todo_item_list.size();

        // creates rows which were already saved.
        for(int index = 0; index<count; index++){

            String note_todo_item_list_value = note_todo_item_list.get(index);
            boolean isChecked = is_checked_list.get(index);

            View	linearLayout=(View)RelativeLayout.inflate(CheckNoteActivity.this, R.layout.view_row_check_list, null);
            edittext = (EditText)linearLayout.findViewById(R.id.addanotecheck_todoedittext);
            checkBox = (CheckBox)linearLayout.findViewById(R.id.addanotecheck_todocheckBox);
            closeImageView =(ImageView)linearLayout.findViewById(R.id.closeImageView);

            checkBox.setChecked(isChecked);

            edittext.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            edittext.setPaintFlags(edittext.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));

            // set the tag only for last item so that on clicking previous items the row should not get added.
            if(index == count - 1){
                edittext.setTag("checked");
            }
            edittext.setText(note_todo_item_list_value);
            edittext.setHint(getString(R.string.addanotecheck_todo_title));

            String value = edittext.getText().toString();
            edittext.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            if(isChecked){
                if(value.length()>0){
                    edittext.setText(value);
                }
            }else{
                edittext.setPaintFlags(edittext.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                edittext.setText(value);
            }

            linearLayoutContainer.addView(linearLayout);

            edittext.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int count = linearLayoutContainer.getChildCount();
                    if(count>1){
                        // code to check whether it is the last row item , so that on clicking it a new row should get added.
                        one: for(int n = 0; n < count; n++){
                            RelativeLayout linearLayout =(RelativeLayout)linearLayoutContainer.getChildAt(n);
                            EditText	editText =(EditText)linearLayout.findViewById(R.id.addanotecheck_todoedittext);
                            if(v == editText){
                                if(count-1 == n)
                                    v.setTag("checked");
                                break one;
                            }
                        }
                    }
                    // if there is only one row , a new row should get added.
                    if(count==1){
                        v.setTag("checked");
                    }
                    String tag =(String) v.getTag();
                    if(tag!=null){
                        if(tag.equals("checked")){
                            createNewRow();
                        }
                    }
                    v.setTag("alreadyClicked");
                }
            });

            edittext.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // code to check whether it is the last row item , so that on clicking it a new row should get added.
                    int count = linearLayoutContainer.getChildCount();
                    if(count>1){
                        one: for(int n = 0; n < count; n++){
                            RelativeLayout linearLayout =(RelativeLayout)linearLayoutContainer.getChildAt(n);
                            EditText	editText =(EditText)linearLayout.findViewById(R.id.addanotecheck_todoedittext);
                            if(v == editText){
                                if(count-1 == n)
                                    v.setTag("checked");
                                break one;
                            }
                        }
                    }
                    // if there is only one row , a new row should get added.
                    if(count==1){
                        v.setTag("checked");
                    }
                    String tag =(String) v.getTag();
                    if(tag!=null){
                        if(tag.equals("checked")){
                            createNewRow();
                        }
                    }
                    v.setTag("alreadyClicked");
                }
            });

            closeImageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View closeButtonImageView) {
                    int count = linearLayoutContainer.getChildCount();
                    if(count>1){
                        // remove the row on click of close button.
                        one: for(int n = 0; n < count; n++){
                            RelativeLayout linearLayout =(RelativeLayout)linearLayoutContainer.getChildAt(n);
                            ImageView	closeImageView =(ImageView)linearLayout.findViewById(R.id.closeImageView);
                            if(closeImageView == closeButtonImageView){
                                linearLayoutContainer.removeViewAt(n);
                                break one;
                            }
                        }
                    }else if(count==1){
                        // if only one row left then the text would be cleared and also the checkbox state is changed
                        RelativeLayout linearLayout =(RelativeLayout)linearLayoutContainer.getChildAt(0);
                        EditText	editText =(EditText)linearLayout.findViewById(R.id.addanotecheck_todoedittext);
                        CheckBox	checkBox =(CheckBox)linearLayout.findViewById(R.id.addanotecheck_todocheckBox);
                        editText.setText("");
                        checkBox.setChecked(false);
                    }

                }
            });

            checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                private String value;

                @Override
                public void onCheckedChanged(CompoundButton arg0, final boolean isChecked) {
                    // change text style according to checkbox state change.
                    int count = linearLayoutContainer.getChildCount();
                    one: for(int n = 0; n < count; n++){
                        RelativeLayout linearLayout =(RelativeLayout)linearLayoutContainer.getChildAt(n);
                        final CheckBox checkBox = (CheckBox)linearLayout.getChildAt(0);
                        if(checkBox == arg0){
                            final EditText edittext = (EditText)linearLayout.getChildAt(1);
                            value = edittext.getText().toString();
                            if(isChecked){
                                if(value.length()>0){
                                    edittext.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                    edittext.setText(value);
                                }
                            }else{
                                edittext.setPaintFlags(edittext.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                                edittext.setText(value);
                            }
                            edittext.addTextChangedListener(new TextWatcher() {

                                @Override
                                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                                }

                                @Override
                                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                              int arg3) {
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if(s.toString().length()>0){
                                        if(checkBox.isChecked()){
                                            edittext.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                        }else{
                                            edittext.setPaintFlags(edittext.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                                        }
                                    }else{
                                        edittext.setPaintFlags(edittext.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                                    }
                                }
                            });
                            break one;
                        }
                    }
                }
            });
        }
    }

    public void createNewRow(){
        // code to create a new todo list.
        // inflate layout for row from xml.
        View	linearLayout=(View)RelativeLayout.inflate(CheckNoteActivity.this, R.layout.view_row_check_list, null);
        edittext = (EditText)linearLayout.findViewById(R.id.addanotecheck_todoedittext);
        checkBox = (CheckBox)linearLayout.findViewById(R.id.addanotecheck_todocheckBox);
        closeImageView = (ImageView) linearLayout.findViewById(R.id.closeImageView);
        edittext.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        edittext.setPaintFlags(edittext.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        edittext.setTag("checked");
        edittext.setHint(getString(R.string.addanotecheck_todo_title));
        linearLayoutContainer.addView(linearLayout);

        // iterate the scrollview to bottom position.
        scrollView.post(new Runnable() {
            private int counter;

            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
                counter = linearLayoutContainer.getChildCount();
                if(counter>1){
                    RelativeLayout linearLayout =(RelativeLayout)linearLayoutContainer.getChildAt(counter-2);
                    EditText edittext = (EditText)linearLayout.getChildAt(1);
                    edittext.requestFocus();
                }
            }
        });

        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // create a new row for the first time the row is clicked.
                int count = linearLayoutContainer.getChildCount();
                // create a new row also when the row left is only one.
                if(count==1){
                    v.setTag("checked");
                }
                String tag =(String) v.getTag();
                if(tag.equals("checked")){
                    createNewRow();
                }
                v.setTag("alreadyClicked");
            }
        });

        edittext.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // create a new row for the first time the row is clicked.
                int count = linearLayoutContainer.getChildCount();
                // create a new row also when the row left is only one.
                if(count==1){
                    v.setTag("checked");
                }
                String tag =(String) v.getTag();
                if(tag.equals("checked")){
                    createNewRow();
                }
                v.setTag("alreadyClicked");
            }
        });

        closeImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View closeButtonImageView) {
                int count = linearLayoutContainer.getChildCount();
                // remove the row on click of close button.
                if(count>1){
                    one: for(int n = 0; n < count; n++){
                        RelativeLayout linearLayout =(RelativeLayout)linearLayoutContainer.getChildAt(n);
                        ImageView	closeImageView =(ImageView)linearLayout.findViewById(R.id.closeImageView);
                        if(closeImageView == closeButtonImageView){
                            linearLayoutContainer.removeViewAt(n);
                            break one;
                        }
                    }
                }else if(count==1){
                    // clear the todo list item and change check box state when item left is only one.
                    RelativeLayout linearLayout =(RelativeLayout)linearLayoutContainer.getChildAt(0);
                    EditText	editText =(EditText)linearLayout.findViewById(R.id.addanotecheck_todoedittext);
                    CheckBox	checkBox =(CheckBox)linearLayout.findViewById(R.id.addanotecheck_todocheckBox);
                    editText.setText("");
                    checkBox.setChecked(false);
                }
            }
        });

        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {


            private String value;

            @Override
            public void onCheckedChanged(CompoundButton arg0, final boolean isChecked) {
                int count = linearLayoutContainer.getChildCount();
                // check checkbox state and change text style according to it.
                one: for(int n = 0; n < count; n++){
                    RelativeLayout linearLayout =(RelativeLayout)linearLayoutContainer.getChildAt(n);
                    final CheckBox checkBox = (CheckBox)linearLayout.getChildAt(0);
                    if(checkBox == arg0){
                        final EditText edittext = (EditText)linearLayout.getChildAt(1);
                        value = edittext.getText().toString();
                        if(isChecked){
                            if(value.length()>0){
                                edittext.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                edittext.setText(value);
                            }
                        }else{
                            edittext.setPaintFlags(edittext.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                            edittext.setText(value);
                        }
                        edittext.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                            }

                            @Override
                            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                          int arg3) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if(s.toString().length()>0){
                                    if(checkBox.isChecked()){
                                        edittext.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                    }else{
                                        edittext.setPaintFlags(edittext.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                                    }
                                }else{
                                    edittext.setPaintFlags(edittext.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                                }
                            }
                        });
                        break one;
                    }
                }
            }
        });
    }

    public void showColorPickerDialog() {
        dialog = new Dialog(this);
        dialog.setTitle(getString(R.string.action_color_title_text));
        dialog.setContentView(R.layout.colorpickerdialog);

        color1checkedimageView =(ImageView)dialog.findViewById(R.id.color1checkedimageView);
        color2checkedimageView =(ImageView)dialog.findViewById(R.id.color2checkedimageView);
        color3checkedimageView =(ImageView)dialog.findViewById(R.id.color3checkedimageView);
        color4checkedimageView =(ImageView)dialog.findViewById(R.id.color4checkedimageView);
        color5checkedimageView =(ImageView)dialog.findViewById(R.id.color5checkedimageView);
        color6checkedimageView =(ImageView)dialog.findViewById(R.id.color6checkedimageView);

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

        color1imageView =(ImageView)dialog.findViewById(R.id.color1imageView);
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
                topBorderFromAddANote.setBackgroundColor(Color.WHITE);
                dialog.dismiss();
            }
        });
        ImageView color2imageView =(ImageView)dialog.findViewById(R.id.color2imageView);
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
                topBorderFromAddANote.setBackgroundColor(getResources().getColor(R.color.color_1_color_picker));
                dialog.dismiss();
            }
        });
        ImageView color3imageView =(ImageView)dialog.findViewById(R.id.color3imageView);
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
                topBorderFromAddANote.setBackgroundColor(getResources().getColor(R.color.color_2_color_picker));
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
                topBorderFromAddANote.setBackgroundColor(getResources().getColor(R.color.color_3_color_picker));
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
                topBorderFromAddANote.setBackgroundColor(getResources().getColor(R.color.color_4_color_picker));
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
                topBorderFromAddANote.setBackgroundColor(getResources().getColor(R.color.color_5_color_picker));
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // if in edit mode inflate edit_note_menu layout else add_a_note_menu.
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
                HomeActivity.isDoneButtonClicked = false;
                finish();
                return true;
            case R.id.action_done:
                // Show error message if title is blank.
                if(TextUtils.isEmpty(titleEditText.getText())){
                    Toast.makeText(CheckNoteActivity.this, getResources().getString(R.string.title_blank_error), Toast.LENGTH_SHORT).show();

                }else if(linearLayoutContainer.getChildCount() == 0){

                    Toast.makeText(CheckNoteActivity.this, getResources().getString(R.string.todo_list_item_blank_error), Toast.LENGTH_SHORT).show();

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

            case R.id.action_delete:
                // action to delete a to do list item.
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
                        final BuiltObject builtObject = builtApplication.classWithUid("notes").object(AppConstants.builtObject.getUid());
                        builtObject.destroyInBackground(new BuiltResultCallBack() {

                            @Override
                            public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {

                                if (builtError == null){
                                    loadingContainer.setVisibility(View.GONE);
                                    AppConstants.builtObject = null;
                                    finishActivity(HomeActivity.OPEN_NOTE_EDIT_MODE);
                                    finish();
                                }else {
                                    loadingContainer.setVisibility(View.GONE);
                                    Toast.makeText(CheckNoteActivity.this, builtError.getErrorMessage(), Toast.LENGTH_SHORT).show();
                                }
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

        BuiltACL builtACL = builtApplication.acl();
        builtACL.setPublicReadAccess(false);
        builtACL.setPublicDeleteAccess(false);
        builtACL.setPublicWriteAccess(false);
        builtACL.setUserDeleteAccess(AppSettings.getUserUid(CheckNoteActivity.this), true);
        builtACL.setUserReadAccess(AppSettings.getUserUid(CheckNoteActivity.this), true);
        builtACL.setUserWriteAccess(AppSettings.getUserUid(CheckNoteActivity.this), true);

        final BuiltUpload fileObject = builtApplication.upload();
        fileObject.setFile(filePath);
        fileObject.setACL(builtACL);
        fileObject.saveInBackground(new BuiltUploadCallback() {

            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {

                if (builtError == null){
                    uploadedMediaUid = fileObject.getUploadUid();
                    handler.sendEmptyMessage(0);
                }else {
                    loadingContainer.setVisibility(View.GONE);
                    Toast.makeText(CheckNoteActivity.this, builtError.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onProgress(int i) {

            }
        });

    }

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){

                loadingContainer.bringToFront();

                HomeActivity.isDoneButtonClicked = true;	// check whether the list was edited
                //ArrayList<String> notesNames = new ArrayList<String>(); // arrylist to store notes name values
                //ArrayList<Boolean> notesBooleanValues = new ArrayList<Boolean>(); // arraylist to store notes boolean values

                final BuiltObject builtObject = isOpenInEditMode ? builtApplication.classWithUid("notes").object(AppConstants.builtObject.getUid()): builtApplication.classWithUid("notes").object();

                builtObject.set("note_todo_list_title", titleEditText.getText().toString()); // set title in built object

                if(uploadedMediaUid != null){
                    builtObject.set("note_image", uploadedMediaUid);
                }

                int counter = linearLayoutContainer.getChildCount();

                List<HashMap<String, Object>> listTask = new ArrayList<HashMap<String,Object>>();

                for(int n = 0; n < counter; n++){

                    RelativeLayout linearLayout =(RelativeLayout)linearLayoutContainer.getChildAt(n);
                    CheckBox checkBox = (CheckBox)linearLayout.getChildAt(0);
                    EditText edittext = (EditText)linearLayout.getChildAt(1);
                    String	value = edittext.getText().toString();

                    HashMap<String, Object> map = new HashMap<String, Object>();

                    listTask.add(map);
                    if(value.length()>0){
                        map.put("note_todo_item", value);
                        if(checkBox.isChecked()){
                            map.put("is_checked",true);
                        }else{
                            map.put("is_checked",false);
                        }
                    }
                }

                loadingContainer.setVisibility(View.VISIBLE);
                if(isOpenInEditMode){
                    loadingText.setText(getResources().getString(R.string.Updating_note));
                }else{
                    loadingText.setText(getResources().getString(R.string.saving_note));
                }
                loadingContainer.bringToFront();
                // store the list todo item values in arraylist of hashmap.

				/*for(int i = 0; i < counter; i++){
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("note_todo_item", (String)notesNames.get(i));
					map.put("is_checked",(Boolean)notesBooleanValues.get(i));
					listTask.add(map);
				}*/

                // set todo items values in built object.
                builtObject.set("note_todo_items", listTask);
                builtObject.set("note_header_color", AppUtils.getColorValue(CheckNoteActivity.this, colorPickerValue));

                BuiltACL builtACL = builtApplication.acl();
                builtACL.setPublicReadAccess(false);
                builtACL.setPublicDeleteAccess(false);
                builtACL.setPublicWriteAccess(false);
                builtACL.setUserDeleteAccess(AppSettings.getUserUid(CheckNoteActivity.this), true);
                builtACL.setUserReadAccess(AppSettings.getUserUid(CheckNoteActivity.this), true);
                builtACL.setUserWriteAccess(AppSettings.getUserUid(CheckNoteActivity.this), true);
                builtObject.setACL(builtACL);


                // save the todo list items.
                builtObject.saveInBackground(new BuiltResultCallBack() {

                    @Override
                    public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {

                        if (builtError == null){
                            if (isOpenInEditMode) {
                                isOpenInEditMode = false;
                                loadingContainer.setVisibility(View.GONE);
                                AppConstants.builtObject = builtObject;
                                finishActivity(HomeActivity.OPEN_NOTE_EDIT_MODE);
                            } else {
                                loadingContainer.setVisibility(View.GONE);
                                AppConstants.builtObject = builtObject;
                                finishActivity(HomeActivity.OPEN_NOTE_CREATE_MODE);
                            }
                            finish();

                        }else {
                            loadingContainer.setVisibility(View.GONE);
                            Toast.makeText(CheckNoteActivity.this, builtError.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        };

    };
}
