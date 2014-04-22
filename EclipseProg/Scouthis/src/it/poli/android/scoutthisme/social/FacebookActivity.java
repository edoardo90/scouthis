/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.poli.android.scoutthisme.social;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.constants.Constants;
import it.poli.android.scoutthisme.constants.UserInformation;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;

public class FacebookActivity extends Activity {
    
    private TextView textInstructionsOrLink;
    private Button buttonLoginLogout;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        buttonLoginLogout = (Button)findViewById(R.id.buttonLoginLogout);
        textInstructionsOrLink = (TextView)findViewById(R.id.instructionsOrLink);
        
        this.fbup(savedInstanceState, true);

    }
    
    
    public void fbup(Bundle savedInstanceState, boolean useButtons)
    {
        
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        //trova la sessione corrente di fb (es. già loggato)
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
        
        
        getFriendsAndUpdate(useButtons);
    }

    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    
    private void getFriendsAndUpdate(boolean useButtons)
    {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
        	
        	//if everything goes fine, now we got the url with all our friends stuff
        	String urlFriendsInfo = Constants.URL_PREFIX_FRIENDS + session.getAccessToken();
        	
        	String userInfo = Constants.URL_PREFIX_ME +	session.getAccessToken();
        			
        	
        	
        	// call AsynTask to perform network operation on separate thread
        	// vedi: onPostExecute
    		new NotifyServerFBFriendsAsyncTask().execute(urlFriendsInfo, userInfo);
    		if(useButtons)
    			this.setLoginButton();
        }
        else
        {  if(useButtons)
        	    this.setLogoutButton();
        }
    }
    
    private void setLoginButton()
    {
    	buttonLoginLogout.setText("Logout");
        buttonLoginLogout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) { onClickLogout(); }
        });
    }
    
    private void setLogoutButton()
    {
    	 textInstructionsOrLink.setText("Facebook Login");
         buttonLoginLogout.setText("Login");
         buttonLoginLogout.setOnClickListener(new OnClickListener() {
             public void onClick(View view) { onClickLogin(); }
         });
    }
    

    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }

    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }
    
    
    @SuppressLint("NewApi")
	private void debugShowSSHKey()
    {
        // solo per debug, genera la chiave hash, nel caso desse errore di accesso
        try  {  
        	   PackageInfo info = getPackageManager().  
        			   getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);

        	      for (Signature signature : info.signatures) {

        	          MessageDigest md = MessageDigest.getInstance("SHA");
        	          md.update(signature.toByteArray());
        	          Log.d("====Hash Key===",Base64.encodeToString(md.digest(), 
        	                   Base64.DEFAULT));
        	      }

        	  } catch (NameNotFoundException e) {
        	      e.printStackTrace();
        	  } catch (NoSuchAlgorithmException ex) {

        	      ex.printStackTrace();        	  }
    

    }
    
    
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            getFriendsAndUpdate(true);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
}