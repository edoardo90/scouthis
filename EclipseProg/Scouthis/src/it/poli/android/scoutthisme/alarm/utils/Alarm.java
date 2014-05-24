package it.poli.android.scoutthisme.alarm.utils;

import it.poli.android.scoutthisme.Constants;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Alarm implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private DaysOfWeek day;
	private int hour;
	private int minute;
	
	private String days;
	private boolean active;
	private boolean periodic;
	private String bird;
	
	public Alarm( String days, boolean active, boolean periodic,  int hour,
			int minute, String bird, int id)
	{
		this.days = days;
		this.active = active;
		this.periodic = periodic;
		this.hour = hour;
		this.minute = minute;
		this.bird = bird;
		this.id = id;
	}
	
	public Alarm( String days, String active, String periodic,  String hour,
			String minute, String bird, String id)
	{
		this.days = days;
		this.active = Boolean.valueOf(active);
		this.periodic = Boolean.valueOf(periodic);
		this.hour = Integer.valueOf(hour);
		this.minute = Integer.valueOf(minute);
		this.bird = bird;
		this.id = Integer.valueOf(id);
	}
	
	
	
	

	public void toggleActive()
	{
		this.active = !this.active;
	}
	
	/**
	 *  Get days when alarm is active
	 */
	public String getActiveDays() {
		return days.replaceAll("\\s","");
	}
	
	public void setDays(String days) {
		this.days = days.replaceAll("\\s","");
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isPeriodic() {
		return periodic;
	}
	
	public void setPeriodic(boolean periodic) {
		this.periodic = periodic;
	}
	
	public DaysOfWeek getDay() {
		return day;
	}
	
	public void setDay(DaysOfWeek day) {
		this.day = day;
	}
	
	public int getHour() {
		return hour;
	}
	
	public void setHour(int hour) {
		this.hour = hour;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public void setMinute(int minute) {
		this.minute = minute;
	}
	
	public String getBird() {
		return bird.replaceAll("\\s","");
	}

	public void setBird(String bird) {
		this.bird = bird.replaceAll("\\s","");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStringTime() {
		return (AlarmUtils.addZeroToOneDigit(this.hour)) + ":" + (AlarmUtils.addZeroToOneDigit(this.minute));
	}

	public Map<String, String> getXmlTagFieldMap() {
		Map<String, String> mapElemValue = new HashMap<String, String>();
		
		mapElemValue.put(Constants.XML_TAG_BIRD, this.getBird());
		mapElemValue.put(Constants.XML_TAG_DAYS, this.getActiveDays());
		mapElemValue.put(Constants.XML_TAG_HOUR, String.valueOf(this.getHour()));
		mapElemValue.put(Constants.XML_TAG_MINUTE, String.valueOf(minute));
		mapElemValue.put(Constants.XML_TAG_ID, String.valueOf(this.getId()));
		mapElemValue.put(Constants.XML_TAG_SWITCH, String.valueOf(this.isActive()));
		mapElemValue.put(Constants.XML_ROOT_MAP, "alarm" );
		
		
		return mapElemValue;
		
	}
}
