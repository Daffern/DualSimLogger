package no.daffern.logger;

import android.util.Log;

/**
 * Created by Daffern on 07.01.2016.
 */
public class DebugHelper {

    public static void e(String TAG, String s){
        if (s.length() > 4000) {

            int chunkCount = s.length() / 4000;     //max size is 4000 chars
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= s.length()) {
                    Log.e(TAG, s.substring(4000 * i));
                } else {
                    Log.e(TAG, s.substring(4000 * i, max));
                }
            }
        } else {
            
            Log.e(TAG, s.toString());
        }
    }
}
