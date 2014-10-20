package io.panwrona.turnmeoff;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Mariusz on 15.10.14.
 */
public class HibernateAsyncTask extends AsyncTask<String, String, TCPClient> {

    private TCPClient tcpClient;
    private Handler mHandler;
    private static final String COMMAND = "shutdown -h";
    private static final String TAG = "HibernateAsyncTask";

    public HibernateAsyncTask(Handler mHandler){
        this.mHandler = mHandler;
    }

    @Override
    protected TCPClient doInBackground(String... params) {
        Log.d(TAG, "In do in background");

        try{
            tcpClient = new TCPClient(mHandler,COMMAND,new IpGetter().getIp(), new TCPClient.OnMessageReceived() {
                @Override
                public void messageReceived(String message) {
                    publishProgress(message);
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        tcpClient.run();
        return tcpClient;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "In progress update, values: " + values);
        if(values.equals("hibernate")){
            tcpClient.stopClient();
            mHandler.obtainMessage(MainActivity.HIBERNATE);
        }else{
            tcpClient.sendMessage("wrong");
            mHandler.obtainMessage(MainActivity.ERROR);
            tcpClient.stopClient();
        }
    }

    @Override
    protected void onPostExecute(TCPClient result){
        super.onPostExecute(result);
        Log.d(TAG, "In on post execute");
    }
}

