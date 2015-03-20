package at.vcity.androidim;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

        Bundle extras = this.getIntent().getExtras();
        imgname=extras.getString(ImageInfo.IMAGE_BYTES);
        key = extras.getString(ImageInfo.IMAGE_KEY);
        Context context= this.getApplicationContext();

        //Display display = context.getWindowManager().getDefaultDisplay();
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        byte[] data= null;
        try{
            //data = CryptoFileUtils.decrypt(key,new File("/storage/sdcard/Download/"+imgname));
            File newFile = new File ("/storage/sdcard/Download/"+imgname);
            FileInputStream inputStream= new FileInputStream(newFile);
            inputStream.read(data);
            inputStream.close();
        }
        catch (Exception E){
            this.finish();
        }



// Get target image size
        Bitmap bitmap =BitmapFactory.decodeByteArray(data, 0, data.length);
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

// Create dialog
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_view);

        ImageView image = (ImageView) dialog.findViewById(R.id.image_view);

// !!! Do here setBackground() instead of setImageDrawable() !!! //
        image.setBackground(resizedBitmap);
    }
}