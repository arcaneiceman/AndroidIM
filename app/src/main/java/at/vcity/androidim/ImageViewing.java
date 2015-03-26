package at.vcity.androidim;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;

import at.vcity.androidim.types.ImageInfo;

/**
 * Created by wali on 17/03/15.
 */
public class ImageViewing extends Activity {

    private String imgname=null;
    private String key = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        try {
            Bundle extras = this.getIntent().getExtras();
            imgname = extras.getString(ImageInfo.IMAGE_NAME);
            key = extras.getString(ImageInfo.IMAGE_KEY);
            Context context = this.getApplicationContext();

            //Display display = context.getWindowManager().getDefaultDisplay();
            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenWidth = size.x;
            int screenHeight = size.y;
            byte[] data = null;
            System.out.println("Key was " + key);
            if (key == null) {
                //Toast.
                this.finish();
            }

            File newFile = new File(imgname);
            System.out.println("File exists : " + newFile.getAbsolutePath() + " " + newFile.exists());
            data = CryptoFileUtils.decrypt(key, newFile);

            //File newFile = new File ("/storage/sdcard/Download/webcam-toy-photo1.jpg");
            //System.out.println("File exists : " +newFile.getAbsolutePath() + " "+  newFile.exists());
            //FileInputStream inputStream= new FileInputStream(newFile);
            //data= new byte[(int)newFile.length()];
            //inputStream.read(data);
            //inputStream.close();
            System.out.println("lenght =  " + data.length);


// Get target image size
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            int bitmapHeight = bitmap.getHeight();
            int bitmapWidth = bitmap.getWidth();

// Scale the image down to fit perfectly into the screen
// The value (250 in this case) must be adjusted for phone/tables displays
            while (bitmapHeight > (screenHeight - 250) || bitmapWidth > (screenWidth - 250)) {
                bitmapHeight = bitmapHeight / 2;
                bitmapWidth = bitmapWidth / 2;
            }

// Create resized bitmap image
            BitmapDrawable resizedBitmap = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, false));
            setContentView(R.layout.image_view);
// Create dialog
            // dialog = new Dialog(context);
            //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //dialog.setContentView(R.layout.image_view);

            ImageView image = (ImageView) findViewById(R.id.image_view);
            image.setImageDrawable(resizedBitmap);
// !!! Do here setBackground() instead of setImageDrawable() !!! //
            //image.setImageDrawable(R.drawable.greenstar)
        }
        catch (Exception E){
            System.err.println("It finished prematurely.");
            E.printStackTrace();
            this.finish();
        }
            //image.setImageDrawable(resizedBitmap);
    }
}