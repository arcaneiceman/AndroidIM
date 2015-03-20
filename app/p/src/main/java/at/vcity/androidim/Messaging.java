package at.vcity.androidim;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import at.vcity.androidim.interfaces.IAppManager;
import at.vcity.androidim.services.IMService;
import at.vcity.androidim.tools.FriendController;
import at.vcity.androidim.tools.LocalStorageHandler;
import at.vcity.androidim.types.FriendInfo;
import at.vcity.androidim.types.MessageInfo;


public class Messaging extends Activity {
//ht
    private static final int ADD_IMAGE_ID = Menu.FIRST;
    private static final int FROM_CAMERA_ID = Menu.FIRST+1;
	private static final int MESSAGE_CANNOT_BE_SENT = 0;
    private static final int SELECT_PICTURE = 1;

    private  String selectedImagePath="";
    public String outputfilename;
	public String username;
	private EditText messageText;
	private EditText messageHistoryText;
	private Button sendMessageButton;
	private IAppManager imService;
	private FriendInfo friend = new FriendInfo();
	private LocalStorageHandler localstoragehandler; 
	private Cursor dbCursor;
	
	private ServiceConnection mConnection = new ServiceConnection() {
      
		
		
		public void onServiceConnected(ComponentName className, IBinder service) {          
            imService = ((IMService.IMBinder)service).getService();
        }
        public void onServiceDisconnected(ComponentName className) {
        	imService = null;
            Toast.makeText(Messaging.this, R.string.local_service_stopped,
                    Toast.LENGTH_SHORT).show();
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	   
		
		setContentView(R.layout.messaging_screen); //messaging_screen);
				
		messageHistoryText = (EditText) findViewById(R.id.messageHistory);
		
		messageText = (EditText) findViewById(R.id.message);
		
		messageText.requestFocus();			
		
		sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
		
		Bundle extras = this.getIntent().getExtras();
		
		
		friend.userName = extras.getString(FriendInfo.USERNAME);
		friend.ip = extras.getString(FriendInfo.IP);
		friend.port = extras.getString(FriendInfo.PORT);
		String msg = extras.getString(MessageInfo.MESSAGETEXT);
		 
		
		
		setTitle("Messaging with " + friend.userName);
	
		
	//	EditText friendUserName = (EditText) findViewById(R.id.friendUserName);
	//	friendUserName.setText(friend.userName);


        Linkify.addLinks(messageHistoryText, Linkify.WEB_URLS);

		/*
		localstoragehandler = new LocalStorageHandler(this);
		dbCursor = localstoragehandler.get(friend.userName, IMService.USERNAME );
		
		if (dbCursor.getCount() > 0){
		int noOfScorer = 0;
		dbCursor.moveToFirst();
		    while ((!dbCursor.isAfterLast())&&noOfScorer<dbCursor.getCount()) 
		    {
		        noOfScorer++;

				this.appendToMessageHistory(dbCursor.getString(2) , dbCursor.getString(3), dbCursor.getString(4));
		        dbCursor.moveToNext();
		    }
		}
		localstoragehandler.close();
		*/
		if (msg != null) 
		{
			this.appendToMessageHistory(friend.userName , msg, null);
			((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel((friend.userName+msg).hashCode());
		}
		
		sendMessageButton.setOnClickListener(new OnClickListener(){
			CharSequence message;
            CharSequence unencryptedlink;
			Handler handler = new Handler();
			public void onClick(View arg0) {
				message = messageText.getText();
                unencryptedlink = selectedImagePath;

                if (message.length()>0){
                    //These is a message therefore lets process it
                    appendToMessageHistory(imService.getUsername(), message.toString(),"");
                    //localstoragehandler.insert(imService.getUsername(), friend.userName, message.toString() ,null ,null);
                    messageText.setText("");
                    Thread thread = new Thread() {
                        public void run() {
                            try{
                            String result = imService.sendMessage(imService.getUsername(), friend.userName, message.toString(), null, null);
                            if (result!= null){
                                   //Success
                            }
                            else
                            {
                                handler.post(new Runnable(){

                                    public void run() {
                                        Toast.makeText(getApplicationContext(),R.string.message_cannot_be_sent, Toast.LENGTH_LONG).show();
                                    }

                                });
                            }
                            } catch (UnsupportedEncodingException e) {
                                Toast.makeText(getApplicationContext(),R.string.message_cannot_be_sent, Toast.LENGTH_LONG).show();

                                e.printStackTrace();
                            } catch (Exception ex){
                                Toast.makeText(getApplicationContext()," Encryption failure (or file error)",Toast.LENGTH_LONG ).show();
                                ex.printStackTrace();
                            }
                        }

                    };
                    thread.start();
                }
                else if (unencryptedlink.length()>0){
                    //I got a picture.
                    //Step 1 encrypt it
                   outputfilename="/storage/sdcard/Download/"+RandomStringUtils.randomAlphabetic(10);
                    Thread thread = new Thread(){
                        public void run(){
                            try{
                                SecureRandom random = new SecureRandom();
                                String tkey=new BigInteger(130,random).toString(32);
                                String fkey=tkey.substring(0,Math.min(tkey.length(),16));
                                File input= new File(unencryptedlink.toString());

                                File output= new File(outputfilename);
                                if (!output.exists()){
                                    output.createNewFile();
                                }
                                ArrayList<byte[]> imagetosend= new ArrayList<byte[]>();
                                byte [] encryptedpic=CryptoFileUtils.encrypt(fkey,input,output);
                                //PictureMessage imagetosend = new PictureMessage(encryptedpic,outputfilename);
                                imagetosend.add(encryptedpic);
                                imagetosend.add(outputfilename.getBytes("UTF-8"));
                                SendImage sender=  new SendImage(imagetosend);
                                sender.run();

                                //input.delete();//delete the picture/// make this a secure delete
                                String result = imService.sendMessage(imService.getUsername(), friend.userName, message.toString(), outputfilename, fkey);
                                fkey=null;
                                tkey=null;
                                System.gc();
                                //log this at local side
                                //localstoragehandler.insert(imService.getUsername(), friend.userName, "",outputfilename,null);
                                   System.out.println("Return was : "+result);
                                if (result!= null){
                                    //did not fail GOT TGT REPLY
                                    //parse TGT
                                    //localstoragehandler.Update(outputfilename,result);
                                    //now add TGT to your answer
                                }
                                else
                                {
                                    System.out.println("IM service result was null");
                                    /*
                                    handler.post(new Runnable(){

                                        public void run() {
                                            Toast.makeText(getApplicationContext(),R.string.message_cannot_be_sent, Toast.LENGTH_LONG).show();
                                        }

                                    });
                                    */
                                }

                            }
                            catch (UnsupportedEncodingException e) {
                                //Toast.makeText(getApplicationContext(),R.string.message_cannot_be_sent, Toast.LENGTH_LONG).show();

                                e.printStackTrace();
                            } catch (Exception ex){
                                 //Toast.makeText(getApplicationContext()," Encryption failure (or file error)",Toast.LENGTH_LONG ).show();
                                ex.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                    //with pic sent , now i can append it
                    appendToMessageHistory(imService.getUsername(), message.toString(),outputfilename);
                }


			}});
		
		messageText.setOnKeyListener(new OnKeyListener(){
			public boolean onKey(View v, int keyCode, KeyEvent event) 
			{
				if (keyCode == 66){
					sendMessageButton.performClick();
					return true;
				}
				return false;
			}
			
			
		});
				
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		int message = -1;
		switch (id)
		{
		case MESSAGE_CANNOT_BE_SENT:
			message = R.string.message_cannot_be_sent;
		break;
		}
		
		if (message == -1)
		{
			return null;
		}
		else
		{
			return new AlertDialog.Builder(Messaging.this)       
			.setMessage(message)
			.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked OK so do some stuff */
				}
			})        
			.create();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(messageReceiver);
		unbindService(mConnection);
		
		FriendController.setActiveFriend(null);
		
	}

	@Override
	protected void onResume() 
	{		
		super.onResume();
		bindService(new Intent(Messaging.this, IMService.class), mConnection , Context.BIND_AUTO_CREATE);
				
		IntentFilter i = new IntentFilter();
		i.addAction(IMService.TAKE_MESSAGE);
		
		registerReceiver(messageReceiver, i);
		
		FriendController.setActiveFriend(friend.userName);		
		
		
	}
	
	
	public class  MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) 
		{		
			Bundle extra = intent.getExtras();
			String username = extra.getString(MessageInfo.USERID);			
			String message = extra.getString(MessageInfo.MESSAGETEXT);
            String picname = extra.getString(MessageInfo.PICName);
            String tgt = extra.getString(MessageInfo.TGT);
			System.out.println("message on receive "  +message);
			if (username != null && message != null)
			{
				if (friend.userName.equals(username)) {
					appendToMessageHistory(username, message,picname);
					//localstoragehandler.insert(username, imService.getUsername(), message, picname,tgt);
				}
				else {
					if (message.length() > 15) {
						message = message.substring(0, 15);
					}
					Toast.makeText(Messaging.this,  username + " says '"+
													message + "'",
													Toast.LENGTH_SHORT).show();		
				}
			}			
		}
		
	};
	private MessageReceiver messageReceiver = new MessageReceiver();
	
	public  void appendToMessageHistory(String username, String message, String picname){
        if (username != null ) {
            if(picname.equals("") && (!message.isEmpty())){
                messageHistoryText.append(username + ":\n");
                messageHistoryText.append(message + "\n");
            }
            else{
                messageHistoryText.append(username + ":\n");

                //make it depend on who sent it
                System.out.println("was in append");
                //messageHistoryText.setCompoundDrawablesWithIntrinsicBounds(null, null,
                 //       getResources().getDrawable(R.drawable.blacksquare), null);
                Drawable d = getResources().getDrawable(R.drawable.blacksquare);
                //d.setBounds(new Rect(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight()));
                //messageHistoryText.setCompoundDrawables(null, d, null, null);
                //contactLine.setOnTouchListener(new OnTouchListener() {
                //below works but not touchable

                SpannableString ss = new SpannableString("abc\n");
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                ss.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                messageHistoryText.append(ss);
                messageHistoryText.append("www.ali.com \n");

            }
            Linkify.addLinks(messageHistoryText, Linkify.WEB_URLS);
		}
	}
	
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (localstoragehandler != null) {
	    	localstoragehandler.close();
	    }
	    if (dbCursor != null) {
	    	dbCursor.close();
	    }
	}

    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);

        menu.add(0, ADD_IMAGE_ID, 0, "Send Snap");

        return result;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {

        switch(item.getItemId())
        {

            case ADD_IMAGE_ID:
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);

                //add it to stuff

                return true;


            }
            case FROM_CAMERA_ID:
            {
                // impklement camera
                return true;
            }
        }

        return super.onMenuItemSelected(featureId, item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                System.err.println("path is : "+ selectedImagePath);
                //return selectedImagePath;
                sendMessageButton.performClick();
            }
        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

}
