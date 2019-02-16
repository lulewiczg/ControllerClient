package com.github.lulewiczg.controller.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.github.lulewiczg.controller.R;

import java.io.Closeable;
import java.io.IOException;

/**
 * Helper class for common methods.
 */
public final class Helper {

    private Helper() {
    }

    /**
     * Closes given closeable.
     *
     * @param c closeable
     */
    public static void close(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Displays toast
     *
     * @param a     activity
     * @param title message title
     */
    public static void displayToast(final Activity a, final int title) {
        a.runOnUiThread(new Thread() {
            @Override
            public void run() {
                Toast.makeText(a, title, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Displays alert message.
     *
     * @param c       context
     * @param title   alert title
     * @param msg     alert message
     * @param onTrue  accept listener
     * @param onFalse deny listener
     */
    public static void displayAlert(Context c, int title, int msg, DialogInterface.OnClickListener onTrue,
                                    DialogInterface.OnClickListener onFalse) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(title);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(msg);
        if (onFalse == null) {
            builder.setPositiveButton(R.string.ok, onTrue);
        } else {
            builder.setPositiveButton(R.string.yes, onTrue);
            builder.setNegativeButton(R.string.no, onFalse);
        }
        builder.create().show();
    }

    /**
     * Closes progress dialog.
     *
     * @param dialog dialog
     * @param a      activity
     */
    public static void closeProgress(final ProgressDialog dialog, Activity a) {
        a.runOnUiThread(new Thread() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
    }

    /**
     * Hides keyboard if opened.
     *
     * @param activity activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
