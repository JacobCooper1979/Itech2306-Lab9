package week9;
/**
 * Objects of this class represent the 4-day forecast for a city.
 * @author Shane Moore
 * @author Jacob Cooper
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.NotSerializableException;

public class MainProgram {
	private Scanner keyboardScanner;

	private ArrayList<CityForecast> cityForecasts;

	public MainProgram()
	{
		keyboardScanner = new Scanner(System.in);		// Prepare the scanner that reads keyboard input from the user.
		cityForecasts = new ArrayList<CityForecast>();	// Prepare the ArrayList to receive any 'add' method calls.
	}

	public void begin()
	{
		int userChoice;
		System.out.println("The Weather Forecast Program");
		do {
			System.out.println("What do you want to do?");
			System.out.println("(1) Read Text Data Files");
			System.out.println("(2) Display a city's weather forecast");
			System.out.println("(3) Write a day's forecasts to a text file");
			System.out.println("(4) Write data to Serialized (Binary) Data File");
			System.out.println("(5) Read Serialized (Binary) Data File");
			System.out.println("(0) quit");
			userChoice = keyboardScanner.nextInt();
			keyboardScanner.nextLine();	
			switch(userChoice)
			{
			case 1:
				readTextFiles();
				break;
			case 2:
				showForecast();
				break;
			case 3:
				makeDayForecastTextFile();
				break;
			case 4:
				writeSerializedFile();
				break;
			case 5:
				readSerializedFiles();
				break;
			}
		} while (userChoice != 0);
	}


	/**
	 * Public Method to read txt files and import the data.
	 *
	 * Loops through the current folder and lists all .txt files in the project directory.
	 * Then prompts the user to select the one they want to load.
	 * Reads the first line of the file as the city's name and creates the CityForecast object.
	 * Then reads up to 4 days of forecast data from the file:
	 * Each days data includes a summary line max temp min temp and a short description.
	 * followed by a detailed description on the next line.
	 * A DayForecast object is created for each day and added to the CityForecast.
	 * the fully loaded CityForecast is added to the main cityForecasts list.
	 * The method also includes exception handling to let me know if what is potentially going wrong.
	 */
	public void readTextFiles() {
		File directory = new File("."); 
		File[] textFiles = directory.listFiles((dir, name) -> name.endsWith(".txt"));
		if (textFiles == null || textFiles.length == 0) {
			System.out.println("Sorry but their are no text files in the current project folder.");
			return;
		}
		System.out.println("Here are the weather txt files you can choose from i am only showing the .txt files in the folder:");
		for (int index = 0; index < textFiles.length; index++) {
			System.out.println("(" + (index + 1) + ") " + textFiles[index].getName());
		}
		int selectedIndex;
		do {
			System.out.print("Please type the number of the file you would like to load: ");
			while (!keyboardScanner.hasNextInt()) {
				System.out.print("Sorry but Thats not a correct number please enter the number next to the file you would like to load.");
				keyboardScanner.next(); 
			}
			selectedIndex = keyboardScanner.nextInt();
			keyboardScanner.nextLine(); 
		} while (selectedIndex < 1 || selectedIndex > textFiles.length);
		String selectedFileName = textFiles[selectedIndex - 1].getName();
		try (Scanner fileScanner = new Scanner(new java.io.File(selectedFileName))) {
			String cityName = fileScanner.nextLine();
			System.out.println("Load the file susessfully Weather data for: " + cityName + " has not been loading into the program");
			CityForecast forecast = new CityForecast(cityName);
			for (int day = 1; day <= 4; day++) {
				if (!fileScanner.hasNextLine()) {
					System.out.println("Sorry but it looks like their are only " + (day - 1) + " days of data were found their needs to be 4 days of data.");
					System.out.println("Stopping the file reading early as their is data missing.");
					break;
				}
				String summaryLine = fileScanner.nextLine();
				Scanner summaryScanner = new Scanner(summaryLine);
				try {
					int maximumTemperature = summaryScanner.nextInt();
					int minimumTemperature = summaryScanner.nextInt();
					String briefDescription = summaryScanner.hasNextLine() ? summaryScanner.nextLine().trim() : "";
					summaryScanner.close();

					if (!fileScanner.hasNextLine()) {
						System.out.println("Sorry but this txt file seems to be missing the detailed description for day " + day + ".");
						break;
					}
					String detailedDescription = fileScanner.nextLine();
					DayForecast forecastForDay = new DayForecast(maximumTemperature, minimumTemperature, briefDescription, detailedDescription);
					forecast.addDayForecast(forecastForDay);
				} catch (InputMismatchException invalidTemperatureInput) {
				    System.out.println("Sorry but this txt file seems to be missiing the temperature values for day: " + day + " or were were not valid numbers. Please check the numbers in the file.");
				    summaryScanner.close();
				    break;
				} catch (NoSuchElementException missingSummaryData) {
				    System.out.println("Sorry but this txt file is data in the summary line for in the text file for the day " + day + ". Please make sure each line has a city name max temp min temp and a description.");
				    summaryScanner.close();
				    break;
				}
			}
			cityForecasts.add(forecast);
			System.out.println("The weather forecast .txt file for " + cityName + " was successfully loaded :)");
		} catch (FileNotFoundException fileNotFoundException) {
			System.out.println("Sorry but the file doesnt seem to exist: " + selectedFileName);
		} catch (IOException inputOutputErrorException) {
			System.out.println("Sorry something went wrong while reading the file. Input or Output Error): " + inputOutputErrorException.getMessage());
		} catch (IllegalStateException illegalStateException) {
			System.out.println("Sorry their seems to be a Scanner Error: " + illegalStateException.getMessage());
		} catch (Exception generalException) {
			System.out.println("Sorry their is something unexpected happened while the file was loading: " + generalException.getMessage());
		}

	}

	/**
	 * Public Method to displays the weather forecast for a selected city.
	 *
	 * Checks if any CityForecast data has been loaded into the program.
	 * pPrompts the user to load data if none is found.
	 * If their is data loaded in the program it will. 
	 * the program will list all loaded cities and prompt the user to select a city by a number.
	 * Retrieves the selected CityForecast object and displays all of its forecast data.
	 * Loops through the 4 days of weather data and dispalys it to the user:
	 * The Minimum temperature.
	 * The Maximum temperature.
	 * The Detailed forecast description.
	 * If a days data is missing the user will be displayed a messgae.
	 * The method also includes exception handling to let me know if what is potentially going wrong.
	 */
	public void showForecast() {
	    try {
	        if (cityForecasts.isEmpty()) {
	            System.out.println("Sorry but their is no data in this program.\n To see weather reports please import in one of the Weather txt files.");
	            return;
	        }
	        System.out.println("These are the Cities that are currently loaded into the program:");
	        for (int cityIndex = 0; cityIndex < cityForecasts.size(); cityIndex++) {
	            System.out.println("(" + (cityIndex + 1) + ") " + cityForecasts.get(cityIndex).getCityName());
	        }
	        int chosenCityNumber = -1;
	        do {
	            System.out.print("Please enter the number of the city you would like to view the forecast for: ");
	            if (keyboardScanner.hasNextInt()) {
	                chosenCityNumber = keyboardScanner.nextInt();
	                keyboardScanner.nextLine();
	            } else {
	                System.out.println("Sorry that was an invalid input. Please enter the number that is next to the city.");
	                keyboardScanner.nextLine(); 
	            }
	        } while (chosenCityNumber < 1 || chosenCityNumber > cityForecasts.size());
	        CityForecast selectedCityForecast = cityForecasts.get(chosenCityNumber - 1);
	        System.out.println("\nForecast for " + selectedCityForecast.getCityName() + ":");
	        for (int forecastDayNumber = 1; forecastDayNumber <= 4; forecastDayNumber++) {
	            DayForecast oneDayForecast = selectedCityForecast.getDayForecast(forecastDayNumber);
	            if (oneDayForecast != null) {
	                System.out.println("The Day " + forecastDayNumber + ":");
	                System.out.println("  The Minimum expected Temperature: " + oneDayForecast.getMinimum());
	                System.out.println("  The Maximum expected Temperature: " + oneDayForecast.getMaximum());
	                System.out.println("  The detailed Forecast for the day: " + oneDayForecast.getDetail());
	            } else {
	                System.out.println("Day " + forecastDayNumber + ": Sorry but we couldnt find any information about this day");
	            }
	        }
	    } catch (IndexOutOfBoundsException errorOutOfBounds) {
	        System.out.println("Sorry an error has occured while tring to access a forecast outside the valid ranges.");
	    } catch (InputMismatchException inputError) {
	        System.out.println("Sorry their has been an input error it was not a valid number.");
	    } catch (Exception unknownError) {
	        System.out.println("Sorry but an unexpected error occurred while showing the forecast: " + unknownError.getMessage());
	    }
	}

	/**
	 * Public method to create a text report for the weather forecast of a specific days 1 to 4.
	 *
	 * Prompts the usser to enter a day number 1 to 4 to generate a report for the selected days.
	 * Promts the user to provide a name they would like to call the outputed text file.
	 * Creates and writes a formatted table into the specified file.
	 * Each row contains: City name minimum temperature maximum temperature and a the weather desscription.
	 * Unsures the file is created as a text.
	 * If forecast data is unavailable for a city on the selected day N/A is displayed.
	 * Informs the user whether the file was successfully written or if an error occurred.
	 * The method also includes exception handling to let me know if what is potentially going wrong.
	 */
	public void makeDayForecastTextFile() {
		int dayNumberToReport;
		String outputFileName;
		do {
			System.out.print("Please enter the days you would like to make a report on 1 - 4: ");
			dayNumberToReport = keyboardScanner.nextInt();
			keyboardScanner.nextLine(); 
		} while (dayNumberToReport < 1 || dayNumberToReport > 4);
		System.out.print("Please enter the name of the report: ");
		outputFileName = keyboardScanner.nextLine();
		if (!outputFileName.endsWith(".txt")) {
			outputFileName += ".txt";
		}
		File outputFile = new File(outputFileName);
		try (java.io.PrintWriter writer = new java.io.PrintWriter(outputFile)) {
			writer.println("Th weather forecast for Day " + dayNumberToReport + " is:");
			writer.println();
			writer.printf("%-20s %-15s %-15s %-20s\n", "The Cities Name", "The Min Temperature", "The Max Temperature", "The full Desciprtion");
			for (CityForecast forecast : cityForecasts) {
				DayForecast forecastForDay = forecast.getDayForecast(dayNumberToReport);
				if (forecastForDay != null) {
					writer.printf("%-20s %-15d %-15d %-20s\n",
							forecast.getCityName(),
							forecastForDay.getMinimum(),
							forecastForDay.getMaximum(),
							forecastForDay.getBriefForecast());
				} else {
					writer.printf("%-20s %-15s %-15s %-20s\n",
							forecast.getCityName(),
							"N/A",
							"N/A",
							"Sorry but their was no forecast data available for that day.");
				}
			}
			System.out.println("The report has been successfully saved to " + outputFileName + " :)");
		} catch (FileNotFoundException fileError) {
	        System.out.println("Sorry but an error occurred while creating the report: " + fileError.getMessage());
	    } catch (IOException inputOutputErrorException) {
	        System.out.println("Sorry a  general input and out error has occurred: " + inputOutputErrorException.getMessage());
	    } catch (Exception unknownError) {
	        System.out.println("An unexpected error occurred: " + unknownError.getMessage());
	    }
	}

	/**
	 * Public mMethod to serialises and writes the list of weather infomration from the CityForecast objects to a txt file.
	 *
	 * Promptss the user to enter a filename for storing serialized data.
	 * Automatically appends .ser if the user does not include the extensns.
	 * Createss the output file in the current projects directory.
	 * Uses FileOutputStream and ObjectOutputStream to write the entire cityForecastss list to the text file..
	 * Closes streams properly to help ensure resource leaks are avoided.
	 * Displays a message to the user depending on successful completion or reports any Input or Ouput error encountered.
	 * The method also includes exception handling to let me know if what is potentially going wrong.
	 */
	public void writeSerializedFile() {
	    String filename;
	    System.out.print("Enter a name for the file to place serialised objects into: ");
	    filename = keyboardScanner.nextLine();

	    if (!filename.endsWith(".ser")) {
	        filename += ".ser";
	    }
	    File outputFile = new File(".", filename);
	    try {
	        FileOutputStream fileStream = new FileOutputStream(outputFile);
	        ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
	        objectStream.writeObject(cityForecasts);
	        objectStream.close();
	        fileStream.close();
	        System.out.println("The cities weather data has been successfully written to the txt file " + outputFile.getName() + " :)");
	    } catch (NotSerializableException serialError) {
	        System.out.println("Sorry but one or more objects being saved does not support serialisation: " + serialError.getMessage());
	    } catch (FileNotFoundException fileError) {
	        System.out.println("Sorry the specified file path was not found: " + fileError.getMessage());
	    } catch (IOException inputOutputErrorException) {
	        System.out.println("Sorry an error has occurred while writing the serialized data to a file: " + inputOutputErrorException.getMessage());
	    } catch (Exception unknownError) {
	        System.out.println("Sorry an unexpected error has occurred: " + unknownError.getMessage());
	    }
	}


	/**
	 * Public Method to reads serialised CityForecast objects from a selected .ser file.
	 *
	 * Searches the current directory of the project for files ending with the ".ser" extension.
	 * Lists available serialised files for the user to choose from.
	 * Promptss the user to select a file by entering the number next to the files name.
	 * Validates input to ensure a valid file index is chosen.
	 * Usess ObjectInputStream to read and deserialise the ArrayList of CityForecasst objects.
	 * Replaces the current cityForecasts list with the data from the selected text file.
	 * Displays a message to the user if a successful load of the serialised file or displays an error if an exception occurs.
	 * The method also includes exception handling to let me know if what is potentially going wrong.
	 * 
	 */
	public void readSerializedFiles() {
	    File currentDirectory = new File(".");
	    File[] serializedFiles = currentDirectory.listFiles((dir, name) -> name.endsWith(".ser"));
	    if (serializedFiles == null || serializedFiles.length == 0) {
	        System.out.println("Sorry there are no serialised files found in the current project directory. Please create one or change the path.");
	        return;
	    }
	    System.out.println("These are the available serialized files:");
	    for (int index = 0; index < serializedFiles.length; index++) {
	        System.out.println("(" + (index + 1) + ") " + serializedFiles[index].getName());
	    }
	    int selectedIndex;
	    do {
	        System.out.print("Please select a file you would like to deserialize by its number: ");
	        while (!keyboardScanner.hasNextInt()) {
	            System.out.print("Please enter a valid number the numbers are next to the files: ");
	            keyboardScanner.next(); 
	        }
	        selectedIndex = keyboardScanner.nextInt();
	        keyboardScanner.nextLine();
	    } while (selectedIndex < 1 || selectedIndex > serializedFiles.length);
	    String filename = serializedFiles[selectedIndex - 1].getName();
	    try (ObjectInputStream inputFromSerializedFile = new ObjectInputStream(new FileInputStream(filename))) {
	        cityForecasts = (ArrayList<CityForecast>) inputFromSerializedFile.readObject();
	        System.out.println("City forecast data has been successfully loaded from the file: " + filename + " :)");
	    } catch (NotSerializableException serialisationProblem) {
	        System.out.println("Sorry it seems that one of the classes used in the forecast data allowed to be serialisable.");
	        System.out.println("It seems one or more classes can not be serialised: " + serialisationProblem.getMessage());
	        serialisationProblem.printStackTrace();  // should hopefully print the stack trace of what one or more objects is not serialisable.
	    } catch (IOException fileInputOutputIssue) {
	        System.out.println("Something went wrong while reading the file from disk.");
	        System.out.println("inputOutputErrorException Error details: " + fileInputOutputIssue.getMessage());
	    } catch (ClassNotFoundException missingClassDefinition) {
	        System.out.println("The program couldn't find a class definition that matches the saved data.");
	        System.out.println("Their seems to be a missing Class: " + missingClassDefinition.getMessage());
	    } catch (ClassCastException invalidTypeCasting) {
	        System.out.println("The file seems to contain a different type of data than expected.");
	        System.out.println("Expetion error message: " + invalidTypeCasting.getMessage());
	    } catch (Exception unexpectedProblem) {
	        System.out.println("An unexpected error happened while loading the forecast data.");
	        System.out.println("Unexpected progblem: " + unexpectedProblem.getMessage());
	    }
	}

	/**
	 * PROGRAM START POINT.
	 * @param args
	 */
	public static void main(String[] args)
	{
		MainProgram theProgram = new MainProgram();
		theProgram.begin();
	}
}
