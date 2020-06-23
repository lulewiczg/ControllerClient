package com.github.lulewiczg.controller.client;

import android.app.Activity;
import android.os.AsyncTask;

import com.github.lulewiczg.controller.actions.Action;
import com.github.lulewiczg.controller.actions.impl.DisconnectAction;
import com.github.lulewiczg.controller.actions.impl.LoginAction;
import com.github.lulewiczg.controller.actions.impl.PingAction;
import com.github.lulewiczg.controller.common.Helper;
import com.github.lulewiczg.controller.common.Response;
import com.github.lulewiczg.controller.common.Status;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Client to connect with server.
 *
 * @author Grzegurz
 */
public class Client implements Closeable {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static Client instance;
    private ExecutorService exec;
    private AtomicInteger awaitingActions;
    private List<Action> recorded = new CopyOnWriteArrayList<>();
    private boolean record;
    private ScheduledExecutorService pingExec;
    private long lastActionTime;
    private long ping;
    private Activity activity;

    /**
     * Creates new client.
     *
     * @param address       address
     * @param port          port
     * @param timeout       timeout
     * @param serverTimeout server timeout
     * @param ping          ping interval
     * @return client
     * @throws IOException the IOException
     */
    public static Client create(String address, int port, int timeout, int serverTimeout, int ping) throws IOException {
        if (instance != null) {
            Helper.close(instance);
        }
        instance = new Client(address, port, timeout, serverTimeout, ping);
        return instance;
    }

    /**
     * Reqisters main activity.
     *
     * @param activity activity
     */
    public static void registerActivity(Activity activity) {
        instance.activity = activity;
    }

    /**
     * Gets previously created client.
     *
     * @return client
     */
    public static Client get() {
        return instance;
    }

    protected Client() {
        awaitingActions = new AtomicInteger(0);
    }

    private Client(String address, int port, int timeout, int serverTimeout, int ping) throws IOException {
        this();
        this.ping = ping;
        socket = new Socket();
        socket.setSoTimeout(serverTimeout);
        socket.connect(new InetSocketAddress(address, port), timeout);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        exec = Executors.newSingleThreadExecutor();
        pingExec = Executors.newSingleThreadScheduledExecutor();
        pingExec.scheduleWithFixedDelay(this::pingCheck, ping, ping, TimeUnit.SECONDS);
    }

    /**
     * Logs in to the server.
     *
     * @param password password
     * @param client   client info
     * @param address  local address
     * @param activity activity
     * @return server response
     */
    public Response login(String password, String client, String address, Activity activity) {
        return doAction(new LoginAction(password, client, address), activity);
    }

    /**
     * Disconnects from server.
     *
     * @param activity activity
     * @return server response
     */
    public Response logout(Activity activity) {
        return doAction(new DisconnectAction(), activity);
    }

    /**
     * Executes server action.
     *
     * @param action   action
     * @param activity activity
     * @return server response
     */
    public Response doAction(Action action, Activity activity) {
        if (record) {
            recorded.add(action);
        }
        AsyncTask<Action, Integer, Response> sendTask = new AsyncTask<Action, Integer, Response>() {
            @Override
            protected Response doInBackground(Action... params) {
                try {
                    awaitingActions.incrementAndGet();
                    out.writeObject(params[0]);
                    out.flush();
                    lastActionTime = System.currentTimeMillis();
                    return (Response) in.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    Helper.close(Client.this);
                    return new Response(com.github.lulewiczg.controller.common.Status.NOT_OK, e);
                } finally {
                    awaitingActions.decrementAndGet();
                }
            }

        };
        sendTask.execute(action);
        Response response;
        try {
            response = sendTask.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Status.NOT_OK);
        }
        sendTask.cancel(true);
        return response;
    }

    /**
     * Executes server and does not wait for response.
     *
     * @param action   action
     * @param activity activity
     */
    public void doActionFast(final Action action, final Activity activity) {
        if (record) {
            recorded.add(action);
        }
        exec.submit(() -> {
            try {
                awaitingActions.incrementAndGet();
                out.writeObject(action);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                Helper.close(Client.this);
            } finally {
                awaitingActions.decrementAndGet();
            }
        });
    }

    /**
     * Closes connection.
     */
    @Override
    public void close() {
        if (exec != null) {
            exec.shutdownNow();
            pingExec.shutdown();
        }
        Helper.close(in);
        Helper.close(socket);
        instance = new DisconnectedClient();
    }

    public int getAwaitingActions() {
        return awaitingActions.get();
    }

    /**
     * Records executed actions.
     */
    public void record() {
        record = true;
    }

    /**
     * Stops recording and returns results.
     *
     * @return recorded actions
     */
    public List<Action> stopRecord() {
        record = false;
        List<Action> res = recorded;
        recorded = new ArrayList<>();
        return res;
    }

    private void pingCheck() {
        if (System.currentTimeMillis() - lastActionTime >= ping * 1000) {
            try {
                out.writeObject(new PingAction());
                out.flush();
                Response r = (Response) in.readObject();
                lastActionTime = System.currentTimeMillis();
                if (r.getStatus() != Status.OK) {
                    onPingFailed();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                onPingFailed();
            }
        }
    }

    private void onPingFailed() {
        Helper.close(this);
        instance.doActionFast(new PingAction(), activity);
    }

    public boolean isRecord() {
        return record;
    }
}
