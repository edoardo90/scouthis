package it.poli.android.scoutthisme.social.utils;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
 
public class JSONParser
{ 
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
 
    // function get json from url by making HTTP POST or GET method
    public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params)
    {
        // Making HTTP request
        try
        {
            if(method.contentEquals("POST")){
            	DefaultHttpClient httpClient = new DefaultHttpClient();
            	
            	HttpPost httpPost = new HttpPost(url);
            	httpPost.setEntity(new UrlEncodedFormEntity(params));
            	
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                
                is = httpEntity.getContent();
            } else if (method.contentEquals("GET")) {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);
 
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                
                is = httpEntity.getContent();
            }           
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        try {     
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            
            
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
 
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
 
        return jObj;
    }
    
	public String[] getUserInfoFromJson(String userInfoJson)
	{
		String s[] = new String[3];
		
		try {
			JSONObject job = new JSONObject(userInfoJson);
			s[0] = job.getString("id");
			s[1] = job.get("first_name").toString();
			s[2] = job.get("last_name").toString();		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return s;
	}
}