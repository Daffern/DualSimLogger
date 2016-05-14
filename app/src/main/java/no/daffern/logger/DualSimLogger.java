package no.daffern.logger;


import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import no.daffern.logger.DeviceSpecific.DualSimHelper;
import no.daffern.logger.DeviceSpecific.DualSimHelperHuawei;
import no.daffern.logger.DeviceSpecific.DualSimHelperSamsung;

/**
 * Created by Daff on 04.04.2015.
 */
public class DualSimLogger implements LocationListener {

    DualSimLoggerService dualSimLoggerService;

    private File file;
    private FileOutputStream out;

    private DualSimHelper dualSimHelper;
    private LocationManager locationManager;

    private boolean isLogging = false;
    private String[] buffer;
    private int bufferCount;
    private final int bufferSize = 20;

    private boolean showTimestamp;
    private boolean showLocation;
    private long locationUpdateInterval;

    private int minInterval = 100;
    private long lastLogTimeMillis = System.currentTimeMillis();

    private int sim1SignalStrength = 0;
    private int sim1CdmaDbm = 0;
    private int sim1EvdoDbm = 0;
    private int sim2SignalStrength = 0;
    private int sim2CdmaDbm = 0;
    private int sim2EvdoDbm = 0;

    private Location location;

    Bundle bundleSim1;
    Bundle bundleSim2;

    ArrayList<Method> getterMethods;
    ArrayList<String> methodNames;

    public DualSimLogger(DualSimLoggerService dualSimLoggerService) {
        this.dualSimLoggerService = dualSimLoggerService;

        bundleSim1 = new Bundle();
        bundleSim2 = new Bundle();

        getterMethods = new ArrayList<Method>();
        methodNames = new ArrayList<String>();

        String model = Build.MODEL;

        if (model.equals(DualSimHelperHuawei.model)) {
            dualSimHelper = new DualSimHelperHuawei(dualSimLoggerService.getApplicationContext());
        } else {
            dualSimHelper = new DualSimHelperSamsung(dualSimLoggerService.getApplicationContext());
        }

        locationManager = (LocationManager) dualSimLoggerService.getSystemService(Context.LOCATION_SERVICE);


        File folder = Environment.getExternalStoragePublicDirectory("DualSimLogger");
        if (!folder.exists())
            folder.mkdir();



        initGetterMethods();

        startListening();
        startCheckingLocation();
    }
    private void initGetterMethods(){
        Method[] methods = SignalStrength.class.getDeclaredMethods();

        for (Method method : methods){
            if (method.getName().startsWith("get")){
                getterMethods.add(method);
                methodNames.add(method.getName().substring(3));
            }
        }


    }

    public void startCheckingLocation() {

        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locationUpdateInterval, 10f, this);
                onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            } else {
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, true);
                locationManager.requestLocationUpdates(provider, locationUpdateInterval, 10f, this);
                onLocationChanged(locationManager.getLastKnownLocation(provider));
            }

        } catch (Exception e) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.MESSAGE_ERROR, "location error");
            dualSimLoggerService.sendMessageToUI(Constants.MSG_MESSAGE, bundle);
            Log.e("DualSimLogger", "failed to request location");
        }

    }

    public void startListening() {
        dualSimHelper.listen(DualSimHelperSamsung.SIM1, psl1);
        dualSimHelper.listen(DualSimHelperSamsung.SIM2, psl2);
    }

    public void stopListening() {
        dualSimHelper.unlisten(DualSimHelperSamsung.SIM1, psl1);
        dualSimHelper.unlisten(DualSimHelperSamsung.SIM2, psl2);
    }

    public void startLogging(String filename, boolean showTimestamp, boolean showLocation, int locationUpdateInterval, int minInterval) {
        if (!isLogging) {
            this.minInterval = minInterval;
            this.showTimestamp = showTimestamp;
            this.showLocation = showLocation;
            this.locationUpdateInterval = locationUpdateInterval;

            file = new File(Environment.getExternalStoragePublicDirectory("DualSimLogger"), filename);
            try {
                out = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            isLogging = true;

            dualSimLoggerService.sendMessageToUI(Constants.MSG_START_LOGGER, null);


            writeLogEntry();
        }
    }

    private void writeLogEntry() {

        if (!isLogging)
            return;

        String write = "";
        if (showTimestamp) {
            Calendar calendar = Calendar.getInstance();
            write = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + ", " + write + ", ";
        }
        write += sim1SignalStrength + ", " + sim2SignalStrength;
        if (showLocation && location != null) {
            write += location.getLatitude() + ", " + location.getLongitude();
            if (location.hasAltitude())
                write += ", " + location.getAltitude();
        }
        write += "\n";

        try {
            out.write(write.getBytes());
            Log.e("DualSimLogger", "Wrote an entry to file");
        } catch (IOException e) {
            isLogging = false;
            Log.e("DualSimLogger", "Failed to write to file");
        }

    }

    public void sendSignalStrengthUpdateToUi() {

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_GSM1, sim1SignalStrength);
        bundle.putInt(Constants.BUNDLE_CDMA1, sim1CdmaDbm);
        bundle.putInt(Constants.BUNDLE_EVDO1, sim1EvdoDbm);
        bundle.putInt(Constants.BUNDLE_GSM2, sim2SignalStrength);
        bundle.putInt(Constants.BUNDLE_CDMA2, sim2CdmaDbm);
        bundle.putInt(Constants.BUNDLE_EVDO2, sim2EvdoDbm);

        if (location != null) {
            bundle.putDouble(Constants.BUNDLE_LATITUDE, location.getLatitude());
            bundle.putDouble(Constants.BUNDLE_LONGITUDE, location.getLongitude());
            bundle.putDouble(Constants.BUNDLE_ALTITUDE, location.getAltitude());
        }

        dualSimLoggerService.sendMessageToUI(Constants.MSG_SIGNAL_BUNDLE, bundleSim1);

        //dualSimLoggerService.sendMessageToUI(Constants.MSG_SIGNAL_STRENGTH_CHANGED, bundle);
    }

    public void stopLogging() {
        if (isLogging) {

            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isLogging = false;
            dualSimLoggerService.sendMessageToUI(Constants.MSG_STOP_LOGGER, null);
        }
    }

    public boolean isLogging() {
        return isLogging;
    }


    PhoneStateListener psl1 = new PhoneStateListener() {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {


            for (int i = 0 ; i < getterMethods.size() ; i++){
                try {
                    int value = (int)getterMethods.get(i).invoke(signalStrength);
                    bundleSim1.putInt(methodNames.get(i), value);

                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }


            sim1SignalStrength = signalStrength.getGsmSignalStrength();
            try {
                Method GetLevel = SignalStrength.class.getDeclaredMethod("getDbm");
                sim1SignalStrength = (int) GetLevel.invoke(signalStrength);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            sim1CdmaDbm = signalStrength.getCdmaDbm();
            sim1EvdoDbm = signalStrength.getEvdoDbm();
            Log.e("DualSimLogger", "SIGNAL STRENGTH 1 CHANGED:" + signalStrength.getGsmSignalStrength());

            if (lastLogTimeMillis + minInterval < System.currentTimeMillis()) {//hvis det er mer enn 100ms siden sist oppdatering, skriv til log og oppdater ui
                sendSignalStrengthUpdateToUi();
                if (isLogging) {
                    writeLogEntry();
                }
                lastLogTimeMillis = System.currentTimeMillis();
            }
        }
    };
    PhoneStateListener psl2 = new PhoneStateListener() {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            sim2SignalStrength = signalStrength.getGsmSignalStrength();
            sim2CdmaDbm = signalStrength.getCdmaDbm();
            sim2EvdoDbm = signalStrength.getEvdoDbm();
            Log.e("DualSimLogger", "SIGNAL STRENGTH 2 CHANGED:" + signalStrength.getGsmSignalStrength());

            if (lastLogTimeMillis + minInterval < System.currentTimeMillis()) {
                sendSignalStrengthUpdateToUi();
                if (isLogging) {
                    writeLogEntry();
                }
                lastLogTimeMillis = System.currentTimeMillis();
            }
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        Log.d("DualSimLogger", "Location changed");
        this.location = location;

        if (lastLogTimeMillis + minInterval < System.currentTimeMillis()) {
            sendSignalStrengthUpdateToUi();
            if (isLogging) {
                writeLogEntry();
            }
            lastLogTimeMillis = System.currentTimeMillis();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        int i = 0;
    }

    @Override
    public void onProviderEnabled(String provider) {
        int i = 0;
    }

    @Override
    public void onProviderDisabled(String provider) {
        int i = 0;
    }


}
