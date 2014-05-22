package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.social.NotifyFriendsAsyncTask;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;

public class FindFriendsDisconnectedFragment extends Fragment
{
	Activity mAct;
	Session session;
    private StatusCallback statusCallback;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView;
		mAct = getActivity();
		statusCallback = new SessionStatusCallback();
		session = Session.getActiveSession();
		
		rootView = inflater.inflate(R.layout.fragment_section_findfriends_logged, container, false);
		
        if (savedInstanceState != null) {
            session = Session.restoreSession(mAct, null, statusCallback, savedInstanceState);
        }
        if (session == null) {
            session = new Session(mAct);
        }
        Session.setActiveSession(session);
        if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        }
		return rootView;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if (session.isOpened()) {
			switchToLoggedFragment();
		} else {			
			setLoginButton();
		}
		session = Session.getActiveSession();
		session.addCallback(statusCallback);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		session = Session.getActiveSession();
		session.removeCallback(statusCallback);
		/*if (session.isOpened()) {
			switchToDisconnectedFragment();
		}*/
	}
	
	private void switchToLoggedFragment()
	{
    	FragmentTransaction transaction = getFragmentManager().beginTransaction();
		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.findfriends_frame, new FindFriendsLoggedFragment());
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.addToBackStack(null);
		// Commit the transaction
		transaction.commit();
	}
	
    /**
     * Imposta l'aspetto del pulsante Login
     */
    private void setLoginButton()
    {
        TextView textInstructionsOrLink = (TextView)mAct.findViewById(R.id.txtFFLoginMessage);
    	textInstructionsOrLink.setText(getString(R.string.findfriends_login_message));

    	Button buttonLoginLogout = (Button) this.getActivity().findViewById(R.id.btnLogIn);
    	buttonLoginLogout.setText(getString(R.string.findfriends_login));
        buttonLoginLogout.setOnClickListener(
        	new OnClickListener() {
        		public void onClick(View view) { 
        			Log.i("btn ", "button");
        			onClickLogin(); }
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
            Session.openActiveSession(mAct, true, statusCallback);
        }
    }
    
    /**
     * Classe a servizio di Facebook
     */    
    private class SessionStatusCallback implements StatusCallback {
        @Override
        public void call(Session sessionF, SessionState state, Exception exception) {
            session = Session.getActiveSession();
            if (session.isOpened()) {        	
            	// If everything goes fine, now we got the url with all our friends stuff
            	String urlFriendsInfo = Constants.URL_PREFIX_FRIENDS + session.getAccessToken();
            	String userInfo = Constants.URL_PREFIX_ME +	session.getAccessToken();
            	// Call AsynTask to perform network operation on separate thread
            	// see: doInBackground and onPostExecute
        		new NotifyFriendsAsyncTask(getActivity()).execute(urlFriendsInfo, userInfo);
        		switchToLoggedFragment();
            }
        }
    }
}
