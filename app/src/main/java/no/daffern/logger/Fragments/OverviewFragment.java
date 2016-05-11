package no.daffern.logger.Fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import no.daffern.logger.Constants;
import no.daffern.logger.DualSimLoggerActivity;
import no.daffern.logger.DualSimLoggerService;
import no.daffern.logger.R;

/**
 * Created by Daffern on 11.05.2016.
 */
public class OverviewFragment extends Fragment {

    private int sim1SignalStrength = 0;
    private int sim2SignalStrength = 0;
    private int sim1CdmaDbm = 0;
    private int sim2CdmaDbm = 0;
    private int sim1EvdoDbm = 0;
    private int sim2EvdoDbm = 0;

    double latitude = 0;
    double longitude = 0;
    double altitude = 0;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_overview, container, false);
    }
    @Override
    public void onResume(){

    }

    public void updateText(Message msg) {/*
        sim1SignalStrength = msg.getData().getInt(Constants.BUNDLE_GSM1);
        sim1CdmaDbm = msg.getData().getInt(Constants.BUNDLE_CDMA1);
        sim1EvdoDbm = msg.getData().getInt(Constants.BUNDLE_EVDO1);
        sim2SignalStrength = msg.getData().getInt(Constants.BUNDLE_GSM2);
        sim2CdmaDbm = msg.getData().getInt(Constants.BUNDLE_CDMA2);
        sim2EvdoDbm = msg.getData().getInt(Constants.BUNDLE_EVDO2);

        latitude = msg.getData().getDouble(Constants.BUNDLE_LATITUDE);
        longitude = msg.getData().getDouble(Constants.BUNDLE_LONGITUDE);
        altitude = msg.getData().getDouble(Constants.BUNDLE_ALTITUDE);

        TextView latitudeText = (TextView) getView().findViewById(R.id.latitudeText);
        TextView longitudeText = (TextView) getView().findViewById(R.id.longitudeText);
        TextView altitudeText = (TextView) getView().findViewById(R.id.altitudeText);
        latitudeText.setText(Double.toString(latitude));
        longitudeText.setText(Double.toString(longitude));
        altitudeText.setText(Double.toString(altitude));

        TextView sim1_signalStrength = (TextView) getView().findViewById(R.id.sim1_signalStrength);
        TextView sim1_cdmaDbm = (TextView) getView().findViewById(R.id.sim1_cdmaDbm);
        TextView sim1_evdoDbm = (TextView) getView().findViewById(R.id.sim1_evdoDbm);


        TextView sim2_signalStrength = (TextView) getView().findViewById(R.id.sim2_signalStrength);
        TextView sim2_cdmaDbm = (TextView) getView().findViewById(R.id.sim2_cdmaDbm);
        TextView sim2_evdoDbm = (TextView) getView().findViewById(R.id.sim2_evdoDbm);

        sim1_signalStrength.setText(Integer.toString(sim1SignalStrength));
        sim1_cdmaDbm.setText(Integer.toString(sim1CdmaDbm));
        sim1_evdoDbm.setText(Integer.toString(sim1EvdoDbm));

        sim2_signalStrength.setText(Integer.toString(sim2SignalStrength));
        sim2_cdmaDbm.setText(Integer.toString(sim2CdmaDbm));
        sim2_evdoDbm.setText(Integer.toString(sim2EvdoDbm));*/
    }
}
