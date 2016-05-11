package no.daffern.logger.DeviceSpecific;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * For the Huawei 4X che2-L11
 */
public class DualSimHelperHuawei implements DualSimHelper{

    private static final String TAG = "DualSimHelperHuawei";

    public static final String model = "Che2-L11";

    TelephonyManager telephonyManager;

    public DualSimHelperHuawei(Context context){

        telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

    }

    @Override
    public void listen(int simId, PhoneStateListener phoneStateListener) {

        Class PSLClass = null;
        try {

            PSLClass = Class.forName("android.telephony.PhoneStateListener");
            Field field = PSLClass.getDeclaredField("mSubscription");

            field.setAccessible(true);
            field.setInt(phoneStateListener, simId);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

    }

    @Override
    public void unlisten(int simId, PhoneStateListener psl) {

    }


}
