package com.github.lulewiczg.controller.client;

import android.app.Activity;
import android.content.DialogInterface;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.actions.Action;
import com.github.lulewiczg.controller.common.Helper;
import com.github.lulewiczg.controller.common.Response;
import com.github.lulewiczg.controller.common.Status;

/**
 * Implementation for disconnected client.
 */
public class DisconnectedClient extends Client {

    @Override
    public void doActionFast(Action action, final Activity activity) {
        Helper.displayAlert(activity, R.string.alert_connection_lost_title, R.string.alert_connection_lost_message,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        activity.finish();
                    }
                }, null);
    }

    @Override
    public Response doAction(Action action, final Activity activity) {
        Helper.displayAlert(activity, R.string.alert_connection_lost_title, R.string.alert_connection_lost_message,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        activity.finish();
                    }
                }, null);
        return new Response(Status.NOT_OK);
    }
}
