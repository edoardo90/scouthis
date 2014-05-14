package it.poli.android.scoutthisme;

import it.poli.android.scoutthisme.fragments.AlarmsHomeFragment;
import it.poli.android.scoutthisme.fragments.FindFriendsFragment;
import it.poli.android.scoutthisme.fragments.GpsSectionFragment;
import it.poli.android.scoutthisme.fragments.HomeSectionFragment;
import it.poli.android.scoutthisme.fragments.LumusSectionFragment;
import it.poli.android.scoutthisme.fragments.SetAlarmClockFragment;
import it.poli.android.scoutthisme.fragments.StepCounterSectionFragment;
import it.poli.android.scoutthisme.fragments.WalkieTalkieSectionFragment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;

public class Constants
{
	public static final String INTENT_NOTIFICATION = "it.poli.android.scoutthisme";
	
    public static final String URL_FACEBOOK_GRAPH = "https://graph.facebook.com/";
	public static final String URL_PREFIX_FRIENDS = URL_FACEBOOK_GRAPH + "me/friends?access_token=";
    public static final String URL_PREFIX_ME = URL_FACEBOOK_GRAPH + "me?access_token=";
    
	public static final String URL_GET_FRIENDSLIST = "http://192.168.1.2/crud/get_friends_coord.php";
	public static final String URL_SEND_FRIENDSLIST = "http://192.168.1.2/crud/update_fnd.php";

	public static final String[] TAB_TITLES = {"News", "GPS", "Contapassi", "Trovamici", "Lumus", "Walkie Talkie", "Wake Up!"};	
	//public static final String[] TAB_ICONS = {R.drawable., "GPS", "Contapassi", "Trovamici", "Lumus", "Walkie Talkie", "Wake Up!"};

	public static final String PARAM_USERID = "userId";
	public static final String PARAM_USERID_AND_NAME = "userIdAndName";
    public static final String PARAM_FRIENDSLIST = "jfriendslist";
	public static final String PARAM_POSITION_LATITUDE = "latitude";
	public static final String PARAM_POSITION_LONGITUDE = "longitude";
	public static final String PARAM_GPS_COORDINATES = "coordinates";
	public static final String PARAM_RESULT = "result";
	
	private final static int MINUTE_MILLISECONDS = 60 * 1000;
    public static final int TIME_UPDATE_FRIENDS_POSITION = MINUTE_MILLISECONDS * 1;
	public static final int TIME_UPDATE_FRIENDSLIST = MINUTE_MILLISECONDS * 15;
	
	public static final int RESULT_OK = 1;
	
	
	  public static final Map<String, Fragment> fragmentMap ;
	    static {
	        
	        Map<String, Fragment> fragmentMapMod = new HashMap<String, Fragment>();
	        fragmentMapMod.put(ScoutMiniAppEnum.GPS.toString(), new GpsSectionFragment());
	        fragmentMapMod.put(ScoutMiniAppEnum.WalkieTalkie.toString(), new WalkieTalkieSectionFragment());
			fragmentMapMod.put(ScoutMiniAppEnum.WakeUp.toString(), new AlarmsHomeFragment());
			fragmentMapMod.put(ScoutMiniAppEnum.WakeSet.toString(), new SetAlarmClockFragment());
			fragmentMapMod.put(ScoutMiniAppEnum.Lumus.toString(), new LumusSectionFragment());
			fragmentMapMod.put(ScoutMiniAppEnum.News.toString(), new HomeSectionFragment());
			fragmentMapMod.put(ScoutMiniAppEnum.Trovamici.toString(), new FindFriendsFragment());
			fragmentMapMod.put(ScoutMiniAppEnum.Contapassi.toString(), new StepCounterSectionFragment() );
			
			fragmentMap = Collections.unmodifiableMap(fragmentMapMod);
	        
	    }
	
	
	
}
