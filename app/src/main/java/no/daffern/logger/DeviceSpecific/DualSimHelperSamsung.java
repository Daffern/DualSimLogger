package no.daffern.logger.DeviceSpecific;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import no.daffern.logger.DebugHelper;
import no.daffern.logger.DualSimClassNames;
import no.daffern.logger.MethodReader;

/**
 * For the Samsung GT-S7392
 *
 */
public class DualSimHelperSamsung implements DualSimHelper{

    private final static String TAG="DualSimHelperSamsung";

    private Method listenMethod;
    private Object multiSimTelephonyManager1;
    private Object multiSimTelephonyManager2;

    public static final int SIM1=0;
    public static final int SIM2=1;

    public DualSimHelperSamsung(Context context){
        Class<?> MultiSimClass=null;

        //find a working multi sim class (different devices, different names)
        String[] mSimClassNames = DualSimClassNames.getAll();

        for (int i = 0 ; i < mSimClassNames.length; i++) {
            try {
                MultiSimClass = Class.forName(mSimClassNames[i]);
                break;
            } catch (ClassNotFoundException e) {

                e.printStackTrace();
            }
        }

        Constructor<?>[] constructors = MultiSimClass.getConstructors();

        DebugHelper.e(TAG, MethodReader.getMethods(MultiSimClass));

        for (Constructor<?> constructor : constructors){
            if (constructor.getParameterTypes().length == 2){
                try {
                    multiSimTelephonyManager1 = constructor.newInstance(context,0);
                    multiSimTelephonyManager2 = constructor.newInstance(context,1);

                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Method method : MultiSimClass.getMethods()){
            if (method.getName().equals("listen")){
               listenMethod=method;
            }
        }

    }
    public void listen(int simId, PhoneStateListener psl){
        try {
            if (simId == SIM1 ){
                listenMethod.invoke(multiSimTelephonyManager1, psl, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            }
            else if (simId == SIM2){
                listenMethod.invoke(multiSimTelephonyManager2, psl, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    public void unlisten(int simId, PhoneStateListener psl){

        try {
            if (simId == SIM1) {
                listenMethod.invoke(multiSimTelephonyManager1, psl, PhoneStateListener.LISTEN_NONE);
            }
            else if (simId == SIM2){
                listenMethod.invoke(multiSimTelephonyManager2, psl, PhoneStateListener.LISTEN_NONE);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
