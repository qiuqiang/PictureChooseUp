package com.example.picture.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by QiuQ on 2016-12-08.
 */

public class PostProDialog {
    private Context context;
    private String message;
    private ProgressDialog dialog;

    public PostProDialog(Context context, String message) {
        this.context = context;
        this.message = message;
        init();
    }

    private void init() {
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.setCancelable(false);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }
}
