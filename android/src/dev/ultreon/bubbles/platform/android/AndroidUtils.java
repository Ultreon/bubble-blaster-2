package dev.ultreon.bubbles.platform.android;

import android.app.Dialog;
import android.view.WindowManager;

public class AndroidUtils {

    // Prevent dialog dismiss when orientation changes
    public static void doKeepDialog(Dialog dialog) {
        var lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }
}
