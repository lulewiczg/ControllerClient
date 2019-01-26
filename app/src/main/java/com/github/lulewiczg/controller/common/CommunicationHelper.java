package com.github.lulewiczg.controller.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.actions.Action;
import com.github.lulewiczg.controller.actions.impl.LoginAction;
import com.github.lulewiczg.controller.exception.ConnectException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Grzegurz on 2016-03-07.
 */
public final class CommunicationHelper {
    private static String ip;
    private static Socket globalSocket;
    private static String password = "";
    private static boolean toDestroy;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile int awaitingActions = 0;
    private static CommunicationHelper instance;
    private static boolean displayed;
    private static boolean dirty = false;

    private CommunicationHelper(Socket socket) throws IOException, ClassNotFoundException {
        this.socket = socket;
        displayed = false;
        toDestroy = false;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        LoginAction login = new LoginAction(password, Build.MANUFACTURER + ", " + Build.MODEL, ip);
        out.writeObject(login);
        Response r = (Response) in.readObject();
        if (r.getStatus() != Status.OK) {
            throw new ConnectException(r);
        }
        dirty = false;
    }

    public static synchronized CommunicationHelper getInstance() {
        try {
            if (instance == null) {
                instance = new CommunicationHelper(globalSocket);
            } else if (dirty) {
                destroy();
                dirty = false;
                instance = new CommunicationHelper(globalSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public synchronized static void destroy() {
        toDestroy = true;
        if (instance != null && instance.socket != null) {
            try {
                int retries = 0;
                while (retries < 10) {
                    if (instance.awaitingActions != 0) {
                        Thread.sleep(500);
                        retries++;
                    } else {
                        break;
                    }
                }
                instance.socket.close();
                Log.i(CommunicationHelper.class.getSimpleName(), "Socket closed");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                instance = null;
            }
        }
    }

    private void sendAction(final Action action) {
        if (toDestroy) {
            return;
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    out.writeObject(action);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    destroy();
                }
                Log.d("status", action.toString());
                try {
                    Response status = (Response) in.readObject();
                    Log.d("status", status.toString());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    destroy();
                } finally {
                    awaitingActions--;
                }
                return null;
            }
        }.execute();
        awaitingActions++;
    }

    public static boolean sendAction(Action a, final Activity activity) {
        if (instance == null || toDestroy) {
            if (!displayed) {
                Helper.displayAlert(activity, R.string.alert_connection_lost_title, R.string.alert_connection_lost_message,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                activity.finish();
                            }
                        }, null);
            }
            displayed = true;
            return false;
        } else {
            if (a.isValid()) {
                instance.sendAction(a);
            }
            return true;
        }
    }

    public static void setGlobalSocket(Socket globalSocket) {
        if (!CommunicationHelper.password.equals(password)) {
            dirty = true;
        }
        CommunicationHelper.globalSocket = globalSocket;
    }

    public static void setPassword(String password) {
        if (!CommunicationHelper.password.equals(password)) {
            dirty = true;
        }
        CommunicationHelper.password = password;
    }

    public static void setIp(String ip) {
        CommunicationHelper.ip = ip;
    }

    public int getAwaitingActions() {
        return awaitingActions;
    }
}
