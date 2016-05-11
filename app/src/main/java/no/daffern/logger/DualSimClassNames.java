package no.daffern.logger;

/**
 * Created by Daffern on 09.01.2016.
 */
public class DualSimClassNames {
    public static String samsungMultiSimClassName = "android.telephony.MultiSimTelephonyManager";
    public static String hw4XMultiSimClassName = "android.telephony.MSimTelephonyManager";


    public static String[] getAll(){
        String[] list = new String[2];
        list[0] = samsungMultiSimClassName;
        list[1] = hw4XMultiSimClassName;
        return list;
    }
}
