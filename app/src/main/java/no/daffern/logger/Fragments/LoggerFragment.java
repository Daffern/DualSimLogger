package no.daffern.logger.Fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
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
import no.daffern.logger.R;


public class LoggerFragment extends Fragment{


    private Button startLoggerButton;
    private TextView filenameTextView;
    private CheckBox showTimestampBox;
    private CheckBox showCoordinatesBox;
    private EditText posCheckIntervalText;
    private EditText minLogIntervalText;

    private boolean localIsLogging = false;
    private boolean remoteIsLogging = false;



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_logger, container, false);
    }


    private void initializeView() {


        showTimestampBox = (CheckBox) getView().findViewById(R.id.timetampBox);
        showCoordinatesBox = (CheckBox) getView().findViewById(R.id.coordinatesBox);
        posCheckIntervalText = (EditText) getView().findViewById(R.id.posIntervalField);
        minLogIntervalText = (EditText) getView().findViewById(R.id.minIntervalField);

        filenameTextView = (TextView) getView().findViewById(R.id.filenameText);
        Calendar calendar = Calendar.getInstance();
        String filename = calendar.get(Calendar.HOUR_OF_DAY) + "-" +
                calendar.get(Calendar.MINUTE) + "-" +
                calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                (calendar.get(Calendar.MONTH) + 1) + "-" +
                calendar.get(Calendar.YEAR) + ".txt";
        filenameTextView.setText(filename);


        startLoggerButton = (Button) getView().findViewById(R.id.startLoggerButton);
        startLoggerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!localIsLogging && !remoteIsLogging) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BUNDLE_FILENAME, filenameTextView.getText().toString());
                    bundle.putBoolean(Constants.BUNDLE_TIMESTAMP, showTimestampBox.isChecked());
                    bundle.putBoolean(Constants.BUNDLE_COORDINATES, showCoordinatesBox.isChecked());
                    bundle.putInt(Constants.BUNDLE_MIN_INTERVAL, Integer.parseInt(minLogIntervalText.getText().toString()));
                    bundle.putInt(Constants.BUNDLE_POS_CHECK_INTERVAL, Integer.parseInt(posCheckIntervalText.getText().toString()));

                    //sendMessageToService(Constants.MSG_START_LOGGER, bundle);

                    localIsLogging = true;
                    startLoggerButton.setEnabled(false);
                } else if (localIsLogging && remoteIsLogging) {

                    //sendMessageToService(Constants.MSG_STOP_LOGGER, null);

                    startLoggerButton.setEnabled(false);
                }
            }
        });
        showCoordinatesBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                posCheckIntervalText.setEnabled(isChecked);
            }
        });
    }
    public void loggerStarted(){
        remoteIsLogging = true;
        localIsLogging = true;
        startLoggerButton.setEnabled(true);
        startLoggerButton.setText("Stop");
    }
    public void loggerStopped(){
        remoteIsLogging = false;
        localIsLogging = false;
        startLoggerButton.setEnabled(true);
        startLoggerButton.setText("Start");
    }
}