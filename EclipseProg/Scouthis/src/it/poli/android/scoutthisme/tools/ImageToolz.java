package it.poli.android.scoutthisme.tools;

import it.poli.android.scoutthisme.Constants;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class ImageToolz {
	
	public static  boolean storeImage(Bitmap imageData, String partialPath, String filename) {
		//get path to external storage (SD card)
		String iconsStoragePath = Environment.getExternalStorageDirectory() + partialPath;
		File sdIconStorageDir = new File(iconsStoragePath);

		//create storage directories, if they don't exist
		sdIconStorageDir.mkdirs();

		try {
			String filePath = sdIconStorageDir.toString() + "/" + filename;
			FileOutputStream fileOutputStream = new FileOutputStream(filePath);

			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

			//choose another format if PNG doesn't suit you
			imageData.compress(CompressFormat.PNG, 100, bos);

			bos.flush();
			bos.close();

		} catch (FileNotFoundException e) {
			Log.w("TAG", "Error saving image file: " + e.getMessage());
			return false;
		} catch (IOException e) {
			Log.w("TAG", "Error saving image file: " + e.getMessage());
			return false;
		}

		return true;
	}
	
	public static Bitmap loadBitmapFromDisk(String partialPath, String fotoname)
	{
		String root = Environment.getExternalStorageDirectory().toString();
		
		String photoPath = root + partialPath + fotoname;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
		return bitmap;
	}

}
