package it.poli.android.scoutthisme.social.utils;

public interface FacebookListener {
    public void onFriendsUpdates(String location);
    public void onFacebookResponse(int response);
}