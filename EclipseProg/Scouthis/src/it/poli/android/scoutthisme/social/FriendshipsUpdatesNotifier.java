package it.poli.android.scoutthisme.social;

import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.social.utils.FacebookNetUtils;
import it.poli.android.scoutthisme.social.utils.JSONParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class FriendshipsUpdatesNotifier extends AsyncTask<String, String, String>
{
	String jsonFriendships;
	String jsonUserInformations;
	
	// Update friends list, there might be updates
	@Override
    protected String doInBackground(String... urls)
	{
		JSONParser jsonParser = new JSONParser();
		
		jsonFriendships = FacebookNetUtils.getStringFromUrl(urls[0]);
		jsonUserInformations = FacebookNetUtils.getStringFromUrl(urls[1]);
		
		String[] userInformations = jsonParser.getUserInfoFromJson(jsonUserInformations);
		String userId = userInformations[0];
		String userFirstName = userInformations[1];
    	
		// Building Parameters for POST METHOD 
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(Constants.PARAM_FRIENDSLIST, jsonFriendships));
		params.add(new BasicNameValuePair(Constants.PARAM_USERID, userId));

		// getting JSON Object FROM PHP PAGE, WE UPDATE FRIENDS LIST USING PHP PAGE
		JSONObject json = jsonParser.makeHttpRequest(Constants.URL_SEND_FRIENDSLIST, "POST", params);
		if(json != null)
		{
			// check for success tag
			try {
				json.getInt("success");
			} catch (JSONException e) {
				e.getLocalizedMessage();
			}
		}
		
		return userFirstName;
    }
}
