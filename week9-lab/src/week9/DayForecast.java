package week9;

import java.io.Serializable;

/** Objects of this class represent an individual day's forecast. 
 *  @author Shane Moore
 */ 
public class DayForecast implements Serializable {
	private static final long serialVersionUID = 1L; // Added in for a uniquie ID for serialisation.
	private int minimum;			
	private int maximum;
	private String briefForecast;
	private String detail;
	
	/** Create a new DayForecast, by specifying the minimum and maximum temperature, a brief description of the weather, and a detailed description of the weather.*/
	public DayForecast(int min, int max, String brief, String detailed)
	{
		minimum = min;
		maximum = max;
		briefForecast = brief;
		detail = detailed;
	}
	
	/** Returns the minimum or low expected temperature for the day.*/
	public int getMinimum()
	{
		return minimum;
	}
	
	/** Returns the maximum or high expected temperature for the day.*/
	public int getMaximum()
	{
		return maximum;
	}
	
	/** Returns the brief description of the weather conditions for the day.*/
	public String getBriefForecast()
	{
		return briefForecast;
	}
	
	/** Returns the detailed description of the weather conditions for the day.*/
	public String getDetail()
	{ 
		return detail;
	}
	
	/** Returns a string showing the range of temperatures expected for the day. */
	public String toString()
	{
		return "" + minimum + "-" + maximum;
	}
}
