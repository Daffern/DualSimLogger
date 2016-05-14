package no.daffern.logger;

/**
 * Created by Daff on 04.04.2015.
 */
public class Constants {
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_BUNDLE = 3;
    public static final int MSG_SIGNAL_STRENGTH_CHANGED = 4 ;
    public static final int MSG_START_LOGGER = 5;
    public static final int MSG_STOP_LOGGER = 6;
    public static final int MSG_MESSAGE = 7;
    public static final int MSG_SIGNAL_BUNDLE = 8;

    public static final String MESSAGE_ERROR="error_message";

    public static final String BUNDLE_FILENAME="filename";
    public static final String BUNDLE_TIMESTAMP="timestamp";
    public static final String BUNDLE_COORDINATES="coordinates";
    public static final String BUNDLE_MIN_INTERVAL="min_log_interval";
    public static final String BUNDLE_POS_CHECK_INTERVAL="pos_check_interval";

    public static final String BUNDLE_GSM1="gsm_signal1";
    public static final String BUNDLE_CDMA1="cdma_dbm1";
    public static final String BUNDLE_EVDO1="evdo_dbm1";
    public static final String BUNDLE_GSM2="gsm_signal2";
    public static final String BUNDLE_CDMA2="cdma_dbm2";
    public static final String BUNDLE_EVDO2="evdo_dbm2";

    public static final String BUNDLE_LATITUDE="latitude";
    public static final String BUNDLE_LONGITUDE="longitude";
    public static final String BUNDLE_ALTITUDE="altitude";


}
