package it.poli.android.scoutthisme.constants;

public class Constants
{
	public static final String urlToUpdateFriends = "http://192.168.1.2/crud/update_fnd.php";
	public static final String UP_LIST_PARAM = "jfriendslist";
	public static final String UP_USERID_PARAM = "userid";
	
	public static final String urlToGetCoords = "http://192.168.1.2/crud/get_friends_coord.php";
	public static final String UPDATE_POSTION_USER_PARAM = "userid";
	public static final String UPDATE_POSTION_LATITUDE_PARAM = "lat";
	public static final String UPDATE_POSTION_LONGITUDE_PARAM = "long";
	
	private final static int MILLISEC = 1000;
	private final static int MILLI_IN_MINUTE = MILLISEC * 60;
	public static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/friends?access_token=";
    public static final String URL_PREFIX_ME = "https://graph.facebook.com/me?access_token=";
    public static final int UPDATE_FRIENDS_POSITION = MILLI_IN_MINUTE * 1;
	public static final int UPDATE_USER_FRIENDS = MILLI_IN_MINUTE * 15;
	
	public static final int RESULT_OK = 1;
}
