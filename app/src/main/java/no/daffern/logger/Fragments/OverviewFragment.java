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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

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

    ListView parameterListView;
    ListView sim1ListView;
    ListView sim2ListView;

    ArrayList<String> parameterList;
    ArrayAdapter<String> parameterAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        //Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_overview, container, false);
    }



    @Override
    public void onResume(){

        parameterList = new ArrayList<String>();

        parameterListView = (ListView)getView().findViewById(R.id.parameterListView);
        sim1ListView = (ListView)getView().findViewById(R.id.sim1ListView);
        sim2ListView = (ListView)getView().findViewById(R.id.sim2ListView);


        parameterAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1);

        parameterListView.setAdapter(parameterAdapter);

        super.onResume();
    }

    public void updateText(Bundle bundle) {

        Iterator<String> iterator = bundle.keySet().iterator();
        while (iterator.hasNext()){
            parameterAdapter.add(iterator.next());
        }






    }
}
