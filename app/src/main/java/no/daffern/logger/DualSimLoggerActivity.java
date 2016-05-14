package no.daffern.logger;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import dalvik.system.DexFile;
import no.daffern.logger.Fragments.AboutFragment;
import no.daffern.logger.Fragments.LoggerFragment;
import no.daffern.logger.Fragments.OverviewFragment;


public class DualSimLoggerActivity extends Activity {

    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    private Messenger mService = null;
    private boolean isBoundToService = false;

    private static final String TAG = "DualSimLoggerActivity";

    Bundle bundleSim1;

    Fragment currentFragment;
    FragmentState fragmentState = FragmentState.overview;
    enum FragmentState{
        overview,
        logger,
        about
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Log.e(TAG,"hei");
        //Log.e(TAG, MethodReader.getMethods(SignalStrength.class));

        ArrayList list = null;
        list.add("sdads");

        setContentView(R.layout.main_activity);
        //initializeService(); initialized in onResume

        currentFragment = getFragmentManager().findFragmentById(R.id.fragment_place);
    }

    public void selectFrag(View view) {

        switch (view.getId()) {
            default:
                currentFragment =  new OverviewFragment();
                fragmentState = FragmentState.overview;
                break;
            case R.id.loggerButton:
                currentFragment = new LoggerFragment();
                fragmentState = FragmentState.logger;
                break;
            case R.id.aboutButton:
                currentFragment = new AboutFragment();
                fragmentState = FragmentState.about;
                break;
        }


        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, currentFragment);
        fragmentTransaction.commit();

    }


/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.about:
                Toast.makeText(getApplicationContext(), "About menu item pressed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.restart:
                //stopService();
                //initializeService();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/


    private void stopService() {
        try {
            doUnbindService();
            Intent dualSimLoggerServiceIntent = new Intent(DualSimLoggerActivity.this, DualSimLoggerService.class);

            if (dualSimLoggerServiceIntent != null)
                stopService(dualSimLoggerServiceIntent);

        } catch (Throwable t) {
            Log.e("MainActivity", "Failed to unbind from the service", t);
        }
    }

    void doUnbindService() {
        if (isBoundToService) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, Constants.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            isBoundToService = false;
        }
    }

    private void initializeService() {
        //If the service is running when the activity starts, we want to automatically bind to it.

        Intent dualSimLoggerServiceIntent = new Intent(DualSimLoggerActivity.this, DualSimLoggerService.class);
        if (!DualSimLoggerService.isRunning()) {

            startService(dualSimLoggerServiceIntent);
        }

        bindService(dualSimLoggerServiceIntent, mConnection, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            Log.i("DualSimLoggerActivity", "Activity attached to Service");

            isBoundToService = true;
            sendMessageToService(Constants.MSG_REGISTER_CLIENT, null);

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
        }
    };

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.e("DualSimLoggerActivity", "received msg with what: " + msg.what);
            switch (msg.what) {

                case Constants.MSG_SIGNAL_STRENGTH_CHANGED:
                    //overviewFragment.updateText(msg);
                    break;
                case Constants.MSG_START_LOGGER:
                    //loggerFragment.loggerStarted();
                    break;
                case Constants.MSG_STOP_LOGGER:
                    //loggerFragment.loggerStopped();
                    break;
                case Constants.MSG_SIGNAL_BUNDLE:
                    bundleSim1 = msg.getData();
                    if (fragmentState == FragmentState.overview){
                        ((OverviewFragment)currentFragment).updateText(bundleSim1);
                    }
                default:
                    super.handleMessage(msg);
            }
        }

    }

    private void sendMessageToService(int what, Bundle bundle) {
        if (isBoundToService) {
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, what);
                    msg.setData(bundle);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {

                }
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        doUnbindService();
        super.onPause();
    }

    @Override
    protected void onResume() {
        initializeService();
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(DualSimLoggerActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
