package com.ultreon.bubbles;

import android.app.AlertDialog;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.ultreon.bubbles.platform.android.AndroidPlatform;
import com.ultreon.bubbles.platform.android.AndroidUtils;
import org.jetbrains.annotations.Nullable;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// use the full display, even if we have a device with a notch
		Window applicationWindow = this.getApplicationWindow();
		WindowManager.LayoutParams attrib = applicationWindow.getAttributes();
		attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
		attrib.setTitle("Bubble Blaster 2");

		AndroidPlatform platform = new AndroidPlatform(this);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		config.numSamples = 8;
		config.resolutionStrategy = new FillResolutionStrategy();
		this.initialize(new GameLibGDXWrapper(platform), config);
	}

	public void showMessage(String title, @Nullable String description) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title);

		// Set up the input
		TextView textView = new TextView(this);
		ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.leftMargin = 24;
		params.rightMargin = 24;
		System.out.println(Build.VERSION.SDK_INT);
		textView.setBreakStrategy(LineBreaker.BREAK_STRATEGY_HIGH_QUALITY);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		textView.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(textView);

		textView.setText(description != null ? description : "...");

		// Set up the buttons
		builder.setPositiveButton("OK", (dialog, which) -> dialog.cancel());

		AlertDialog dialog = builder.show();
		AndroidUtils.doKeepDialog(dialog);
	}

}
