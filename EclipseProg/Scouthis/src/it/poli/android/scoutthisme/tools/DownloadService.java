package it.poli.android.scoutthisme.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class DownloadService extends IntentService {

  private int result = Activity.RESULT_CANCELED;
  public static final String URL = "urlpath";
  public static final String FILENAME = "filename";
  public static final String FILEPATH = "filepath";
  public static final String RESULT = "result";
  public static final String NOTIFICATION = "it.poli.android.scoutthisme";

  public DownloadService() {
	  
    super("GetFriendsPositionsService");
    Log.i(" download service ", "costructore");
  }

  // will be called asynchronously by Android
  @Override
  protected void onHandleIntent(Intent intent) {
	  Log.i("handle", "0");
	String urlPath = intent.getStringExtra(URL);
    String fileName = intent.getStringExtra(FILENAME);
    
    Log.i("handle", "now i donwload the file..");
    
    File output = new File(Environment.getExternalStorageDirectory(),
        fileName);
    if (output.exists()) {
      output.delete();
    }
    Log.i("handle", "1");
    InputStream stream = null;
    FileOutputStream fos = null;
    try {

      URL url = new URL(urlPath);
      stream = url.openConnection().getInputStream();
      InputStreamReader reader = new InputStreamReader(stream);
      fos = new FileOutputStream(output.getPath());
      int next = -1;
      int i = 1;
      while ((next = reader.read()) != -1) {
        fos.write(next);
        i ++;
        if ( i == 10000) { Log.i("DOWNLOADING STUF", " downloading... ");}
      }
      // successfully finished
      result = Activity.RESULT_OK;

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    Log.i("DS", "downloaded file");
    publishResults(output.getAbsolutePath(), result);
  }

  private void publishResults(String outputPath, int result) {
    Intent intent = new Intent(NOTIFICATION);
    intent.putExtra(FILEPATH, outputPath);
    intent.putExtra(RESULT, result);
    sendBroadcast(intent);
  }
} 