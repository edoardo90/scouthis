package it.poli.android.scoutthisme.tools;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    
    
	public static boolean[] daysStringToBooleanArray(String days)
	{
		days = days.substring(1, days.length() - 1);
		String [] bdays = days.split(",");
		boolean [] bbdays = {false, false, false, false, false, false, false };

		for(int i=0; i<7; i++)
		{
			if(bdays[i].equalsIgnoreCase("true"))
			{
				bbdays[i] = true;
			}
		}
		return bbdays;
	}
    
    
}