package no.daffern.logger;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Daff on 03.04.2015.
 */
public class DualSimLoggerService extends Service {



    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    final Messenger messenger = new Messenger(new IncomingHandler());

    private static boolean isRunning=false;

    DualSimLogger dualSimLogger;

    PowerManager.WakeLock wakeLock;

    @Override
    public IBinder onBind(Intent intent){
        return messenger.getBinder();
    }
    class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            Log.d("DualSimLoggerService", "message received with what: " + msg.what);
            switch (msg.what) {

                case Constants.MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);

                    if (dualSimLogger.isLogging())
                        sendMessageToUI(Constants.MSG_START_LOGGER,null);
                    else
                        sendMessageToUI(Constants.MSG_STOP_LOGGER,null);

                    dualSimLogger.sendSignalStrengthUpdateToUi();
                    break;
                case Constants.MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case Constants.MSG_BUNDLE:
                    Log.d("DualSimLoggerService", "message received: " + msg.getData().toString());
                    break;
                case Constants.MSG_START_LOGGER:
                    Log.d("DualSimLoggerService", "starting logger");
                    wakeLock.acquire();
                    Bundle bundle = msg.getData();
                    dualSimLogger.startLogging(
                            bundle.getString(Constants.BUNDLE_FILENAME),
                            bundle.getBoolean(Constants.BUNDLE_TIMESTAMP),
                            bundle.getBoolean(Constants.BUNDLE_COORDINATES),
                            bundle.getInt(Constants.BUNDLE_POS_CHECK_INTERVAL),
                            bundle.getInt(Constants.BUNDLE_MIN_INTERVAL));
                    break;
                case Constants.MSG_STOP_LOGGER:
                    Log.d("DualSimLoggerService", "stopping logger");
                    dualSimLogger.stopLogging();
                    wakeLock.release();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    public void sendMessageToUI(int what , Bundle bundle) {
        for (int i= mClients.size()-1; i>=0; i--) {
            try {
                //Send data as a String
                Message msg = Message.obtain(null, what);
                msg.setData(bundle);
                mClients.get(i).send(msg);

            }
            catch (RemoteException e) {
                // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("DualSimLoggerService", "Received start id " + startId + ": " + intent);

        isRunning=true;

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DualSimLoggerTag");

        return START_STICKY; // run until explicitly stopped.
    }


    public static boolean isRunning()
    {
        return isRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();


            android.os.Debug.waitForDebugger();



        dualSimLogger = new DualSimLogger(this);
        Log.d("DualSimLoggerService", "Service Started.");
        isRunning = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning=false;
        Log.d("DualSimLoggerService", "Service Stopped.");
    }
}
