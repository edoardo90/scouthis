package it.poli.android.scoutthisme.tools;

import com.facebook.model.GraphUser;

public class UserMarker
{
	private String name;
	private GraphUser graphUser;
	private double latitude;
	private double longitude;

	public UserMarker(String name, double latitude, double longitude)
	{
		this.graphUser = null;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public UserMarker(GraphUser graphUser, double latitude, double longitude)
	{
		this.graphUser = graphUser;
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = graphUser.getFirstName();
	}

	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}	
	
	public GraphUser getUser() {
		return this.graphUser;
	}
	
	public void setUser(GraphUser graphUser) {
		this.graphUser = graphUser;
	}
	
	public String getReadableName()
	{
		return this.getUser().getFirstName() + "  " + this.getUser().getLastName();
	}

	@Override
	public String toString()
	{
		return name;
	}
}
