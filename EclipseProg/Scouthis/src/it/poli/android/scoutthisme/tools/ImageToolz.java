package it.poli.android.scoutthisme.tools;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class ImageToolz {

	public static void writeBitmapOnDisk( Bitmap bitmap, String fotoname)
	{
		String root = Environment.getExternalStorageDirectory().toString();
        File newDir = new File(root + "/saved_images");    
        newDir.mkdirs();
        File file = new File (newDir, fotoname);
        if (file.exists ()) file.delete (); 
        	try {
               FileOutputStream out = new FileOutputStream(file);
               bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
               out.flush();
               out.close();
               
               Log.i("write bmp", " bmp written, i think");
            } catch (Exception e) {}
        
        
	}
	
	public static Bitmap loadBitmapFromDisk(String fotoname)
	{
		String root = Environment.getExternalStorageDirectory().toString();
		
		String photoPath = root + "/saved_images/" + fotoname;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
		return bitmap;
	}

}
