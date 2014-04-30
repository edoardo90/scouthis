package it.poli.android.scoutthisme.social;
import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.constants.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;

public class FacebookActivity extends Activity
{  
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
    
    /**
     * Cambia l'aspetto del pulsante login una volta cliccato
     * @param  useButtons  an absolute URL giving the base location of the image
     * @param  name the location of the image, relative to the url argument
     * @return      the image at the specified URL
     * @see         Image
     */
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
    		if(useButtons) {
    			this.setLoginButton();
    		}
        } else if(useButtons) {
        	this.setLogoutButton();
        }
    }
    
    /**
     * Cambia l'aspetto del pulsante login una volta cliccato
     */
    private void setLoginButton()
    {
    	textInstructionsOrLink.setText("Facebook Logout");
    	buttonLoginLogout.setText("Logout");
        buttonLoginLogout.setOnClickListener(
        	new OnClickListener() {
        		public void onClick(View view) { onClickLogout(); }
        	}
        );
    }

    /**
     * Cambia l'aspetto del pulsante logout una volta cliccato
     */
    private void setLogoutButton()
    {
    	textInstructionsOrLink.setText("Facebook Login");
        buttonLoginLogout.setText("Login");
        buttonLoginLogout.setOnClickListener(
        	new OnClickListener() {
        		public void onClick(View view) { onClickLogin(); }
        	}
        );
    }    

    /**
     * Effettua il login su richiesta dell'utente
     */
    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }
    
    /**
     * Effettua il logout su richiesta dell'utente
     */
    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }
    
    /**
     * Classe a servizio di Facebook
     */    
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            getFriendsAndUpdate(true);
        }
    }   
}