package com.builtio.demoNotes.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.builtio.demoNotes.AppConstants;
import com.builtio.demoNotes.R;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltFile;
import com.raweng.built.BuiltImageDownloadCallback;
import com.raweng.built.view.BuiltImageView;

public class PreviewImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_image);

		final BuiltImageView imageView = (BuiltImageView) findViewById(R.id.previewImage);
		BuiltFile builtFile = BuiltFile.builtFileWithResponse(AppConstants.builtObject.getJSONObject("note_image"));

		ProgressBar progress = (ProgressBar) findViewById(R.id.progressBarImage);

		imageView.showProgressOnLoading(progress);
		imageView.setBuiltFile(PreviewImageActivity.this, builtFile, new BuiltImageDownloadCallback() {

			@Override
			public void onSuccess(Bitmap bitmap) {
				imageView.setImageBitmap(imageView.getCachedImage());
				Toast.makeText(PreviewImageActivity.this, "Image downloaded successfully...", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError(BuiltError error) {
				Toast.makeText(PreviewImageActivity.this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAlways() {}
		});
	}
}
