package com.github.lulewiczg.controller.common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.github.lulewiczg.controller.R;

/**
 * Created by Grzegurz on 2016-03-05.
 */
public final class Helper {

    private Helper() {
    }

    public static Thread displayToast(final Context c, final int title) {
        Thread t = new Thread() {
            @Override
            public void run() {
                Toast.makeText(c, title, Toast.LENGTH_LONG).show();
            }
        };
        return t;
    }

    public static void displayAlert(Context c, int title, int msg, DialogInterface.OnClickListener onTrue,
                                    DialogInterface.OnClickListener onFalse) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(title);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(msg);
        if (onFalse == null) {
            builder.setPositiveButton(R.string.ok, onTrue);
        }
        else{
            builder.setPositiveButton(R.string.yes, onTrue);
            builder.setNegativeButton(R.string.no, onFalse);
        }
        builder.create().show();
    }

    public static Thread closeProgress(final ProgressDialog dialog) {
        Thread t = new Thread() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        };
        return t;
    }

}
