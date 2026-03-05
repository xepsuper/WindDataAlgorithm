package algo.winddata;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Retrieves wind data from a weather station file.
 * ArrayList according to the datastructure, the timecomplexity should at both worst and best case scenario O(n).
 */
public class WindDataHandler {
    private final ArrayList<String[]> rawData = new ArrayList<>();
    private final DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
	 * Load wind data from file.
	 *
	 * @param filePath path to file with weather data
	 * @throws IOException if there is a problem while reading the file
     *
     * The enhanced for-loop should return with time complexity O(n) since it's an iteration
     * Problem with this iteration is that I am using an array to store specific row with data which should technically use O(n^2)
     * In this case it's not optimal, however it could've been better if I have chosen better datastructures like treemap since it stores with timecomplexity of O(logn).
     * But I am not obligated to optimize the datastructure as long I keep it below O(n^3).
	 */
	public void loadData(String filePath) throws IOException {
		List<String> fileData = Files.readAllLines(Paths.get(filePath));  //O(n)


        for(String line : fileData)
        { if (line.trim().isEmpty() || line.startsWith("#")) {continue;}

            String[] parts = line.split(";");
            String calenderDate = parts[0];
            String timeStr = parts[1];
            String[] dataRow = new String[6];

            dataRow[0] = calenderDate;
            dataRow[1] = timeStr;
            System.arraycopy(parts, 2, dataRow, 2, 4);

            rawData.add(dataRow);



        }
        System.out.println("Loaded " + rawData.size() + " observations\n");

    }
	/**
	 * Search for average wind speed for dates. Result is sorted by date (ascending).
	 * When searching from 2000-01-01 to 2000-01-03 the result should be:
	 * 2000-01-01 average wind speed: 4.29 m/s
	 * 2000-01-02 average wind speed: 6.48 m/s
	 * 2000-01-03 average wind speed: 5.74 m/s
	 *
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo   end date (YYYY-MM-DD) inclusive
	 * @return average wind speed for each date, sorted by date
     *
     * In this method I am using an iteration to loop through my database, and it should have timecomplexity of O(n).
     * Because I am using enhanced for-loop it'll multiply the timecomplexity to O(n^2) with tree map it could've been more efficient, eta O(nlogn).
     * I also added an if check so it's checking the correct date and try catch so it doesn't receive invalid data.
     * I calculated the average speed for the entire duration of a day. So it recieves the average for thar specific day.
     * Thereafter, I used a simplified if-check to secure the correct data. The main principle is that it should take the sum of the day and divide with how many times it has to go through that specific day.
	 */
    public List<String> averageWindSpeed(LocalDate dateFrom, LocalDate dateTo) {
        List<String> result = new ArrayList<>();

        String data = "";
        for (LocalDate date = dateFrom; !date.isAfter(dateTo); date = date.plusDays(1)) {
            double sum = 0;
            int count = 0;

            for (String[] row : rawData) {
                LocalDate rowDate = LocalDate.parse(row[0], dateOnlyFormatter);

                if (rowDate.equals(date)) {
                    try {
                        double speed = Double.parseDouble(row[4]);
                        sum += speed;
                        count++;
                    } catch (NumberFormatException e) {
                        // Skip invalid numbers
                    }
                }
            }

           double averageWindSpeed = count > 0 ? sum / count : 0;
            data = date.format(dateOnlyFormatter) +  " Average wind speed: "+ BigDecimal.valueOf(averageWindSpeed).setScale(2, RoundingMode.HALF_UP) + " m/s";
            result.add(data);
        }



        return result;
    }

	/**
	 * Search for percentage of approved values (for both wind speed and wind direction) for dates.
	 * When searching from 2000-01-01 to 2000-01-03 the result should be:
	 * 2000-01-01: 33.33 % approved values
	 * 2000-01-02: 34.78 % approved values
	 * 2000-01-03: 34.78 % approved values
	 *
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo   end date (YYYY-MM-DD) inclusive
	 * @return approved values for each date, sorted by date
     *
     * In this method I am using an iteration to loop through my database, and it should have timecomplexity of O(n).
     * Because I am using enhanced for-loop it'll multiply the timecomplexity to O(n^2) with tree map it could've been more efficient, eta O(nlogn).
	 */
	public List<String> approvedValues(LocalDate dateFrom, LocalDate dateTo) {
        List<String> result = new ArrayList<>();
        int approvedWinds = 0;
        int count = 0;
        String data = "";


      for(LocalDate date = dateFrom; !date.isAfter(dateTo); date = date.plusDays(1))
      {
          for(String[] row : rawData) {
              LocalDate rowDate = LocalDate.parse(row[0], dateOnlyFormatter);
              if(rowDate.equals(date)) {
                  try {
                      if (row[5].equalsIgnoreCase("G")) {
                          approvedWinds++;
                      }
                      count++;
                  }catch(NumberFormatException _){}
              }
          }
          double percentage = count > 0 ? (double) approvedWinds / count : 0;
          data = "Percentage " + BigDecimal.valueOf(percentage*100).setScale(2, RoundingMode.HALF_UP) + "% " + " From: " + date.format(dateOnlyFormatter);
          result.add(data);
        }


		return result;  //O(1)
	}

	/**
	 * Search for highest wind speed for dates.
	 * When searching from 2000-01-01 to 2000-01-03 the result should be:
	 * 2000-01-01 05:00: 5.0 m/s
	 * 2000-01-02 11:00: 9.0 m/s
	 * 2000-01-03 17:00: 9.0 m/s
	 *
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo   end date (YYYY-MM-DD) inclusive
	 * @return highest wind speed for each date, sorted by date
     *
     * In this method I am using an iteration to loop through my database, and it should have timecomplexity of O(n).
     * Because I am using enhanced for-loop it'll multiply the timecomplexity to O(n^2) with tree map it could've been more efficient, eta O(nlogn).
	 */
	public List<String> highestWindSpeed(LocalDate dateFrom, LocalDate dateTo) {
		List<String> result = new ArrayList<>();
        String data = "";
        String timeHour = "";
        for(LocalDate date = dateFrom; !date.isAfter(dateTo); date = date.plusDays(1)) {
            double maxSpeed = -1;
            for(String[] row : rawData){
                LocalDate rowDate = LocalDate.parse(row[0], dateOnlyFormatter);
                if(rowDate.equals(date)) {
                    try {
                        double windSpeed = Double.parseDouble(row[4]);

                        if (windSpeed > maxSpeed) {
                            maxSpeed = windSpeed;
                            timeHour = row[1];
                        }
                    } catch (NumberFormatException e) {

                    }
                }
                }
                if(maxSpeed > 0) {
                    data = date.format(dateOnlyFormatter) + " " + timeHour.substring(0, timeHour.length() - 3)+ ": " + BigDecimal.valueOf(maxSpeed).setScale(2, RoundingMode.HALF_UP) + " m/s";
                    result.add(data);
                }

            }
        return result;
    }
}