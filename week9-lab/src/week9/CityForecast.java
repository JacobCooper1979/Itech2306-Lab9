 package week9;
import java.io.Serializable;

import java.util.ArrayList;

/**
 * Objects of this class represent the 4-day forecast for a city. 
 * @author Shane Moore
 *
 */
public class CityForecast implements Serializable {
	private static final long serialVersionUID = 1L; // Added in for a uniquie ID for serialisation.
	private String cityName;						// The name of the city for which we have forecasts
	private ArrayList<DayForecast> forecasts;		// The forecasts for the next few days
	

	/** Create a new CityForecast for a specified City */ 
	public CityForecast(String nameOfCity)
	{
		this.cityName = nameOfCity;
		forecasts = new ArrayList<DayForecast>(4);		// Create an ArrayList and reserve enough spaces for 4 days' forecasts
	}
	
	/** Add a forecast of another day to the list of forecasts for this city. */
	public void addDayForecast(DayForecast oneDay)
	{
		if (oneDay != null)
		{
			forecasts.add(oneDay);
		}
	}
	
	/** Request a day's forecast: day 1 means first forecast (no day 0) */
	public DayForecast getDayForecast(int whichDay)
	{
		if (whichDay > 0 && whichDay <= forecasts.size() )
			return forecasts.get(whichDay-1);
		else
			return null;
	}
	
	/** Returns the name of the City that this CityForecast is for. */
	public String getCityName()
	{
		return cityName;
	}

}
