package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.Settings;

public class FindFriendsDisconnectedFragment extends Fragment
{
	Activity mAct;
    private StatusCallback statusCallback;
	
	public void onCreate (Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
		
		mAct = getActivity();
		statusCallback = new SessionStatusCallback();

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(getActivity(), null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(getActivity());
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{		
		View rootView = inflater.inflate(R.layout.fragment_section_findfriends_disconnected, container, false);
		return rootView;
	}
	
    @Override
    public void onResume() {
        super.onResume();
        Session.getActiveSession().addCallback(statusCallback);
        updateView();
    }

    @Override
    public void onPause() {
        super.onPause();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    private void updateView() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            switchToLoggedFragment();
        } else {
        	setLoginButton();
        }
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
        TextView textInstructionsOrLink = (TextView)getView().findViewById(R.id.txtFFLoginMessage);
    	textInstructionsOrLink.setText(getString(R.string.findfriends_login_message));

    	textInstructionsOrLink.setText(Html.fromHtml(getString(R.string.findfriends_login_message)));
    	
    	TextView textDontWarry = (TextView)getView().findViewById(R.id.txtFFDontWorryMessage);
    	textDontWarry.setText(getString(R.string.findfriends_dontwarry_message));
    	
    	Button buttonLogin = (Button)getView().findViewById(R.id.btnLogIn);
    	
        buttonLogin.setOnClickListener(
        	new OnClickListener() {
        		public void onClick(View view) {
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
            Session.openActiveSession(mAct, this, true, statusCallback);
        }
    }
    
    /**
     * Classe a servizio di Facebook
     */    
    private class SessionStatusCallback implements StatusCallback {
        @Override
        public void call(Session sessionF, SessionState state, Exception exception) {
            updateView();
        }
    }
    
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(mAct, requestCode, resultCode, data);
    }
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
}
