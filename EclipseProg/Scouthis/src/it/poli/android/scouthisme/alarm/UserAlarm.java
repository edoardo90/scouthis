package it.poli.android.scouthisme.alarm;

import java.io.Serializable;

public class UserAlarm implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String days;
	private boolean active;
	private boolean periodic;
	private String bird;
	private WeekDay day;
	private int hour;
	private int minute;
	private int id;
	
	public UserAlarm( String days, boolean active, boolean periodic,  int hour,
			int minute, String bird, int id)
	{
		this.days = days;
		this.active = active;
		this.periodic = periodic;
		this.hour = hour;
		this.minute = minute;
		this.bird = bird;
		this.setId(id);
	}

	public void toggleActive()
	{
		this.active = ! this.active;
	}
	
	/**
	 *  Get days when alarm is active
	 */
	public String getDays() {
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
	public WeekDay getDay() {
		return day;
	}
	public void setDay(WeekDay day) {
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
		return (pad(this.hour)) + ":" + (pad(this.minute));
	}
	
	private String pad(int i)
	{
		String s  = String.valueOf(i);
		return (s.length() == 1 ? "0" + s : s);
	}
}
