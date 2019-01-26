package com.github.lulewiczg.controller.client;

import android.os.AsyncTask;

import com.github.lulewiczg.controller.actions.Action;
import com.github.lulewiczg.controller.actions.impl.DisconnectAction;
import com.github.lulewiczg.controller.actions.impl.LoginAction;
import com.github.lulewiczg.controller.common.Consts;
import com.github.lulewiczg.controller.common.Helper;
import com.github.lulewiczg.controller.common.Response;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    /**
     * Creates new client.
     *
     * @param address address
     * @param port    port
     * @return client
     */
    public static Client create(String address, int port) throws IOException, InterruptedException {
        if (instance != null) {
            Helper.close(instance);
        }
        instance = new Client(address, port);
        return instance;
    }

    /**
     * Gets previously created client.
     *
     * @return client
     */
    public static Client get() {
        if (instance == null) {
            throw new RuntimeException("Not connected");
        }
        return instance;
    }

    /**
     * Destroys client.
     */
    public static void destroy() {
        Helper.close(instance);
        instance = null;
    }

    private Client(String address, int port) throws IOException, InterruptedException {
        socket = new Socket();
        socket.setSoTimeout(Consts.TIMEOUT);
        socket.connect(new InetSocketAddress(address, port));
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        exec = Executors.newSingleThreadExecutor();
        awaitingActions=new AtomicInteger(0);
    }

    /**
     * Logs in to the server.
     *
     * @param password password
     * @param client   client info
     * @param address  local address
     * @return server response
     */
    public Response login(String password, String client, String address) {
        return doAction(new LoginAction(password, client, address));
    }

    /**
     * Disconnects from server.
     *
     * @return server response
     */
    public Response logout() {
        return doAction(new DisconnectAction());
    }

    /**
     * Executes server action.
     *
     * @param action action
     * @return server response
     */
    public Response doAction(Action action) {
        AsyncTask<Action, Integer, Response> sendTask = new AsyncTask<Action, Integer, Response>() {
            @Override
            protected Response doInBackground(Action... params) {
                try {
                    awaitingActions.incrementAndGet();
                    out.writeObject(params[0]);
                    out.flush();
                    return (Response) in.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    return new Response(com.github.lulewiczg.controller.common.Status.NOT_OK, e);
                } finally {
                    awaitingActions.decrementAndGet();
                }
            }

        };
        sendTask.execute(action);
        Response response = null;
        try {
            response = sendTask.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sendTask.cancel(true);
        return response;
    }

    /**
     * Executes server and does not wait for response.
     *
     * @param action action
     */
    public void doActionFast(final Action action) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    awaitingActions.incrementAndGet();
                    out.writeObject(action);
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Ignore
                } finally {
                    awaitingActions.decrementAndGet();
                }
            }
        };
        exec.submit(t);
    }

    /**
     * Closes connection.
     *
     * @throws IOException the IOException
     */
    @Override
    public void close() throws IOException {
        exec.shutdownNow();
        in.close();
        out.close();
        socket.close();
    }

    public int getAwaitingActions() {
        return awaitingActions.get();
    }
}
