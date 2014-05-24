package it.poli.android.scoutthisme.stepcounter.utils;

import it.poli.android.scoutthisme.Constants;

public class RunEpisode {

	private int id;
	private float distance;
	private int steps;
	private float time;
	private float speed;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getStringId()
	{
		return String.valueOf(this.id);
	}
	
	public float getDistance() {
		return distance;
	}
	public String getStringDistance()
	{
		return String.valueOf(this.distance);
	}
	
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public float getTime() {
		return time;
	}
	public String getStringTime()
	{
		return String.valueOf(this.time);
	}
	public void setTime(float time) {
		this.time = time;
	}
	public int getSteps() {
		return steps;
	}
	
	public String getStringSteps()
	{
		return String.valueOf(steps);
	}
	
	public void setSteps(int steps) {
		this.steps = steps;
	}
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public String getStringSpeed()
	{
		return String.valueOf(this.speed);
	}
	public String getFotoName() {
		return Constants.IMAGE_MAP_PREFIX + this.getStringId();
	}

}
