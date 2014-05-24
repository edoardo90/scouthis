package it.poli.android.scoutthisme;

public class Constants
{
	public static final boolean DEBUG_ENABLED = false;
	
	public static final String INTENT_NOTIFICATION = "it.poli.android.scoutthisme";
	public static final String INTENT_ALARM = "it.poli.android.scoutthisme.alarm.utils.bird";
	
	public final static String PARAM_ALARM_DAYS = "it.polimi.scout.activedays";
	public final static String PARAM_ALARM_TIME = "it.polimi.scout.time";
	public static final String PARAM_ALARM_BIRD = "it.polimi.scout.bird";
	public static final String PARAM_ALARM_ACTIVE = "it.polimi.scout.active";
	
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    
    public static final String ALARM_HOUR = "it.polimi.scout.hour";
    public static final String ALARM_MINUTE = "it.polimi.scout.minute";
	
	public static final String XML_PATH_ALARM = "alarms.xml";
	
    public static final String URL_FACEBOOK_GRAPH = "https://graph.facebook.com/";
	public static final String URL_PREFIX_FRIENDS = URL_FACEBOOK_GRAPH + "me/friends?access_token=";
    public static final String URL_PREFIX_ME = URL_FACEBOOK_GRAPH + "me?access_token=";
	public static final String URL_GET_FRIENDSLIST = "http://centriestivi.eu/scouthisme/crud/exchange_friends_coordinates.php";
	public static final String URL_SEND_FRIENDSLIST = "http://centriestivi.eu/scouthisme/crud/update_friends_list.php";

	public static final String PARAM_USERID = "userId";
	public static final String PARAM_USERID_AND_NAME = "userIdAndName";
    public static final String PARAM_FRIENDSLIST = "jfriendslist";
    public static final String PARAM_NAME = "name";
	public static final String PARAM_POSITION_LATITUDE = "latitude";
	public static final String PARAM_POSITION_LONGITUDE = "longitude";
	public static final String PARAM_GPS_COORDINATES = "coordinates";
	public static final String PARAM_RESULT = "result";
	
	public static final String XML_TAG_ALARMS = "alarms";
	public static final String XML_TAG_ALARM = "alarm";
	public static final String XML_TAG_DAYS = "days";
	public static final String XML_TAG_HOUR = "hour";
	public static final String XML_TAG_BIRD = "bird";
	public static final String XML_TAG_SWITCH = "switch";
	public static final String XML_TAG_ID = "id";
	public static final String XML_ROOT_MAP = "root";
	
	private static final int MINUTE_MILLISECONDS = 60 * 1000;
    public static final int TIME_UPDATE_FRIENDS_POSITION = 10 * 1000;
	public static final int TIME_UPDATE_FRIENDSLIST = MINUTE_MILLISECONDS * 15;
	
	public static final String IMAGE_MAP_PREFIX = "img_google_map";
	public static final String SAVED_IMAGE_FOLDER_NAME = "img_folder";
	
	public static final int RESULT_OK = 1;

	

	
}