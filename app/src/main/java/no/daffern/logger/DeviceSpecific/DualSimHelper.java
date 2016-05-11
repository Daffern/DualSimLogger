package no.daffern.logger.DeviceSpecific;

import android.telephony.PhoneStateListener;

/**
 * Created by Daffern on 07.05.2016.
 */
public interface DualSimHelper {
    void listen(int simId, PhoneStateListener phoneStateListener);
    void unlisten(int simId, PhoneStateListener psl);
}
