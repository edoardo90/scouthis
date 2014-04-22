package it.poli.android.scoutthisme.social;

import it.poli.android.scoutthisme.constants.Constants;
import it.poli.android.scoutthisme.constants.UserInformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.internal.js;

import android.os.AsyncTask;
import android.util.Log;

public class GetFBFriendsPositions  {
   		
		private double userLatitude ;
		private double userLongitude;
		private String userIDURL;
		
		public GetFBFriendsPositions(String userIDURL, double userLat, double userLong)
		{
			this.userLatitude = userLat;
			this.userLongitude = userLong;
			this.userIDURL = userIDURL;
		}
		
        protected String getFriendsGPS() {
              
              String result = getStringFromUrl(this.userIDURL) ;
         	  String userInfoJson = result;
        	  String userId = this.getUserIDFromJson(userInfoJson, UserInformation.USERID);
        	  
        	  Log.i("user", userId);
 
			  GetFriendsGPSFromURL ufl = new
					  GetFriendsGPSFromURL( userId, Constants.urlToGetCoords,
							  				this.userLatitude , this.userLongitude);
				
			  return ufl.getGPSFriends();
        }

       

        private String getStringFromUrl(String url){
    		InputStream inputStream = null;
    		String result = "";
    		try {
    			
    			// create HttpClient
    			HttpClient httpclient = new DefaultHttpClient();
    			
    			// make GET request to the given URL
    			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
    			
    			// receive response as inputStream
    			inputStream = httpResponse.getEntity().getContent();
    			
    			// convert inputstream to string
    			if(inputStream != null)
    				result = convertInputStreamToString(inputStream);
    			else
    				result = "Did not work!";
    		
    		} catch (Exception e) {
    			Log.d("InputStream", e.getLocalizedMessage());
    		}
    		
    		return result;
    	}
    	
        private static String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;
            
            inputStream.close();
            return result;
            
        }
        
        
		private String getUserIDFromJson(String userInfoJson, UserInformation infoToGet) {
				
			String s = "";
			try {
				JSONObject job = new JSONObject(userInfoJson);
				s = job.getString("id");
				if(infoToGet == UserInformation.USERID_AND_NAME)
				{
					s = s + " ";
					s = s + job.get("first_name");
					s = s + " ";
					s = s + job.get("last_name");
				}
			
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return s;
		}

		class GetFriendsGPSFromURL  {

		    private JSONParser jsonParser = new JSONParser();
			private String userId;
			private double latitude;
			private double longitude;
			
			private String urlGetGPSFriendList;
			
			public GetFriendsGPSFromURL(String userId, String urltogetcoords, double latitude, double longitude) {
				this.userId = userId;
				this.urlGetGPSFriendList = urltogetcoords;
				this.latitude = latitude;
				this.longitude = longitude;
			}


		    public String getGPSFriends(){
		        
		        // Building Parameters for POST METHOD 
		        List<NameValuePair> params = new ArrayList<NameValuePair>();
		        params.add(new BasicNameValuePair(Constants.UPDATE_POSTION_USER_PARAM, this.userId));
		        params.add(new BasicNameValuePair(Constants.UPDATE_POSTION_LATITUDE_PARAM, String.valueOf(this.latitude)));
		        params.add(new BasicNameValuePair(Constants.UPDATE_POSTION_LONGITUDE_PARAM, String.valueOf(this.longitude)));
		        
				// getting JSON Object FROM PHP PAGE, WE GET GPS FRIENDS COORD USING PHP PAGE
		        JSONObject json = jsonParser.makeHttpRequest(this.urlGetGPSFriendList, "POST", params);
		        
		        // check for success tag
		        try {
		            int success = json.getInt("success");
		            if(success!=0) {
		            		Log.i("get friends gps", "wooo! lette coord gps !");
		            		 return json.toString();
		            }
		            
		        } catch (JSONException e) {
		            e.printStackTrace();
		            return "error";
		        }
		        
		        return "error requesting gps coords to php ";
		       
		    }
		    

		}
		
		
    }
