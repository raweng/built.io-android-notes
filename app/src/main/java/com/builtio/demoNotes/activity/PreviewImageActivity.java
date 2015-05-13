package com.builtio.demoNotes.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.builtio.demoNotes.AppConstants;
import com.builtio.demoNotes.R;
import com.raweng.built.Built;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltImageDownloadCallback;
import com.raweng.built.BuiltUpload;
import com.raweng.built.utilities.BuiltConstant;
import com.raweng.built.view.BuiltImageView;

import org.json.JSONObject;

public class PreviewImageActivity extends Activity {

    private BuiltApplication builtApplication;
    private BuiltUpload builtFile;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_image);

        try {
            JSONObject jsonObject = AppConstants.builtObject.getJSONObject("note_image");
            builtFile = Built.application(this, "bltdfcc61830fb5b32b").upload();
            builtFile.configure(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final BuiltImageView imageView = (BuiltImageView) findViewById(R.id.previewImage);

		ProgressBar progress = (ProgressBar) findViewById(R.id.progressBarImage);

        imageView.setApplicationKey(PreviewImageActivity.this, "bltdfcc61830fb5b32b");

		imageView.showProgressOnLoading(progress);
		imageView.setBuiltUpload(PreviewImageActivity.this, builtFile, new BuiltImageDownloadCallback() {

            @Override
            public void onCompletion(BuiltConstant.ResponseType responseType, Bitmap bitmap, BuiltError builtError) {

                if (builtError == null) {
                    imageView.setImageBitmap(imageView.getCachedImage());
                    Toast.makeText(PreviewImageActivity.this, "Image downloaded successfully...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PreviewImageActivity.this, "Error:" + builtError.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
	}
}
