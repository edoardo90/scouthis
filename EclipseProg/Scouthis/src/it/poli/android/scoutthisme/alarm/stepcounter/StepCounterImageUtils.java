package it.poli.android.scoutthisme.alarm.stepcounter;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

public class StepCounterImageUtils {
	
	private Activity act;
	
	public StepCounterImageUtils(Activity act)
	{
		this.act = act;
	}
	
	public void writeBitmapOnDisk(Bitmap bitmap, int id)
	{
		String root = Environment.getExternalStorageDirectory().toString();
        File newDir = new File(root + "/saved_images");    
        newDir.mkdirs();

        String fotoname = "photo-map-"+ id +".jpg";
        File file = new File (newDir, fotoname);
        if (file.exists ()) file.delete (); 
        	try {
               FileOutputStream out = new FileOutputStream(file);
               bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
               out.flush();
               out.close();
               Toast.makeText(this.act.getApplicationContext(), "safed to your folder", Toast.LENGTH_SHORT ).show();

            } catch (Exception e) {
                   
            }
	}
	
	public Bitmap loadBitmapFromDisk(int id)
	{
		String root = Environment.getExternalStorageDirectory().toString();
		String fotoname = "photo-map-"+ id +".jpg";
		String photoPath = root + "/saved_images/" + fotoname;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
		return bitmap;
	}

}
