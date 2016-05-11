package no.daffern.logger;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by Daff on 01.01.2015.
 */
public class MethodReader {


    private static final String TAG = "MethodReader";


    public static String getConstructors(Class<?> c){
        String string="";
        Constructor<?>[] constructors = c.getConstructors();

        for (Constructor<?> constructor : constructors){
            string += constructor.toGenericString() + "/n";

        }
        return string;
    }
    public static Constructor getConstructor(Class<?> c, int i){

        Constructor<?>[] constructors = c.getConstructors();

        if (i + 1 > constructors.length){
            return null;
        }else{
            return constructors[i];
        }

    }

    public static Method findMethod(Class<?> c, String methodName) {

        Method[] methods = c.getMethods();
        Method[] declaredMethods = c.getDeclaredMethods();

        for (Method method : methods) {
            method.setAccessible(true);

            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        for (Method method : declaredMethods) {
            method.setAccessible(true);

            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        return null;
    }
    public static String getMethods(Class<?> c) {

        Method[] methods = c.getMethods();
        Method[] declaredMethods = c.getDeclaredMethods();

        String string = "###methods###";

        for (Method method : methods) {
            method.setAccessible(true);

            string += method.getName() + " - " + method.getReturnType() + ": " + getParameterTypes(method) + '\n';
        }
        string += "###declared methods###";
        for (Method method : declaredMethods) {
            method.setAccessible(true);

            string += method.getName() + " - " + method.getReturnType() + ": " + getParameterTypes(method) + '\n';
        }

        return string;
    }



/*
    public String getTelephonyMethods() {
        String string = "";

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        Class<?> telephonyManagerClass = null;
        Method[] methodsTelephonyManager = null;
        try {
            telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
            methodsTelephonyManager = telephonyManagerClass.getMethods();
        } catch (Exception e) {
            string += "***TELEPHONY_SERVICE ERROR***";
        } finally {

            Method getITelephony = null;

            string += "###standard methods:###\n";
            for (Method method : methodsTelephonyManager) {
                method.setAccessible(true);
                string += method.getName() + " - " + method.getReturnType() + ": " + getParameterTypes(method) + '\n';
            }

            string += "###declared methods###\n";
            Method[] declaredMethodsTelephonyManager = telephonyManagerClass.getDeclaredMethods();
            for (Method method : declaredMethodsTelephonyManager) {
                method.setAccessible(true);

                if (method.getName().equals("getITelephony")) {
                    getITelephony = method;
                }
                string += method.getName() + " - " + method.getReturnType() + ": " + getParameterTypes(method) + '\n';
            }

            string += "###ITelephony methods###\n";

            Class<?> ITelephonyClass = null;
            try {
                Object ITelephonyStub = getITelephony.invoke(telephonyManager);
                ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());
            } catch (Exception e) {
                string += "***ITELEPHONY ERROR***";

            } finally {
                Method[] methodsITelephony = ITelephonyClass.getMethods();


                for (Method method : methodsITelephony) {

                    string += method.getName() + " - " + method.getReturnType() + ": " + getParameterTypes(method) + '\n';
                }

            }//end finally
        }//end finally
        return string;
    }*/

    private static String getParameterTypes(Method method) {
        String string = "";
        boolean first = true;
        for (Class<?> parameterType : method.getParameterTypes()) {

            if (first) {
                string += parameterType.getName();
                first = false;
            } else {
                string += ", " + parameterType.getName();
            }
        }
        return string;
    }
}
