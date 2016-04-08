package cn.linyi.music;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

/**
 * Created by linyi on 2016/4/4.
 */
public class NetConnection  extends Activity{
    public void myClickHandler(View view) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            /**/
        } else {

        }
    }
}
