package algo.winddata;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
/**
 * Command based UI for a simple Weather Data application.
 */
public class WindDataUI {
	private WindDataHandler windData = null;
	private Scanner _scanner;
	/**
	 * Construct a Weather Data UI.
	 * 
	 * @param weatherData logic for weather data
	 */
	public WindDataUI(WindDataHandler weatherData) {
		windData = weatherData;
	}
	/**
	 * Start Weather Data UI.
	 */
	public void startUI() {
		_scanner = new Scanner(System.in);
        int input;
        boolean quit = false;

        System.out.println("** Weather Data **");

        while (!quit) {
            input = getNumberInput(_scanner, 1, 4, getMainMenu());

            switch (input) {
                case 1:
                	averageWindSpeed();
                    break;
                case 2:
                    approvedValues();
                    break;
                case 3:
                	highestWindSpeed();
                    break;
                case 4:               	
                    quit = true;
            }
        }
        //Close scanner to free resources
        _scanner.close();
	}
	/**
     * Get input and translate it to a valid number.
     *
     * @param scanner the Scanner we use to get input
     * @param min the lowest valid number
     * @param max the highest valid number
     * @param message message to user
     * @return input translated to valid number
     */
    private int getNumberInput(Scanner scanner, int min, int max, String message) {
        int input = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.println(message);
            try {
                input = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid input. Enter a number between " + min + " and " + max);
            }
            if (input < min || input > max) {
                System.out.println("Invalid input. Enter a number between " + min + " and " + max);
            }
            else {
                validInput = true;
            }
        }
        return input;
    }
    /**
     * Get date from user formatted as YYYY-MM-DD.
     * 
     * @return date provided by user
     */
    private LocalDate getDateInput() {
    	String input = null;
    	LocalDate date = null;
    	boolean isCorrect = false;

        while (!isCorrect) {
            System.out.println("Enter date (YYYY-MM-DD): ");
            input = _scanner.nextLine().trim();
            
            try {
            	date = LocalDate.parse(input);
            	isCorrect = true;
            } catch (DateTimeParseException e) {
            	System.out.println("Invalid date");
            }            
        }
        return date;    	
    }
    /**
     * Query user for two dates and present the average wind speed for each date
     * in the period, sorted by date (ascending).
     */
    private void averageWindSpeed() {
        System.out.println("Average wind speed");
    	System.out.print("Start date (will be included)\n");
        LocalDate dateFrom = getDateInput();
        System.out.print("End date (will be included)\n");
        LocalDate dateTo = getDateInput();
        presentResult(windData.averageWindSpeed(dateFrom, dateTo));

    }
    /**
     * Query user for the percentage of approved values (for both wind direction
     * and wind speed) for each date, sorted by date (ascending).
     */
    private void approvedValues() {
        System.out.println("Percentage of approved values (for both wind direction and wind speed)");
    	System.out.print("Start date (will be included)\n");
    	LocalDate dateFrom = getDateInput();
        System.out.print("End date (will be included)\n");
        LocalDate dateTo = getDateInput();
                       
        presentResult(windData.approvedValues(dateFrom, dateTo));
    }   
    /**
     * Query user for two dates and present the highest wind speed for each date
     * in the period, sorted by date (ascending).
     */
    private void highestWindSpeed() {
        System.out.println("Highest wind speed");
    	System.out.print("Start date (will be included)\n");
    	LocalDate dateFrom = getDateInput();
        System.out.print("End date (will be included)\n");
        LocalDate dateTo = getDateInput();
                       
        presentResult(windData.highestWindSpeed(dateFrom, dateTo));
    }    
    /**
     * Present search result.
     *
     * @param result the result to present
     */
    private void presentResult(List<String> result) {
    	if(result.isEmpty()) {
    		System.out.println("No matching values for the provided query.");
    	}
        int count = 0;
    	for(String s : result) {
    		System.out.println(s);
            count++;
    	}
        System.out.println("\nThis many operations ran: "+count);
    }
    /**
     * Return the main menu text.
     *
     * @return the main menu text
     */
    private String getMainMenu() {
        return "-------------------\n"
                + "1. Average wind speed\n"
                + "2. Approved values\n"
                + "3. Highest wind speed\n"
                + "-------------------\n"
                + "4. Quit";
    }
}