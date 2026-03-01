package algo.winddata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Retrieves wind data from a weather station file.
 */
public class WindDataHandler {
    Map<String,String> observationByDate = new HashMap<>();
    ArrayList<String[]> rawData = new ArrayList<>();
    private final DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	/**
	 * Load wind data from file.
	 *
	 * @param filePath path to file with weather data
	 * @throws IOException if there is a problem while reading the file
	 */
	public void loadData(String filePath) throws IOException {
		List<String> fileData = Files.readAllLines(Paths.get(filePath));  //O(n)

        for(String line : fileData)
        { if (line.trim().isEmpty() || line.startsWith("#")) {continue;}

            String[] parts = line.split(";");
            String dataStr = parts[0];
            String timeStr = parts[1];

            String[] dataRow = new String[6];
            for(int i = 0; i<4; i++)
            {
                dataRow[0] = dataStr;
                dataRow[1] = timeStr;
                dataRow[i+2] = parts[2+i];
            }
            rawData.add(dataRow);



        }



		//TODO: Structure data and put it in appropriate data structure
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
	 */
	public List<String> averageWindSpeed(LocalDate dateFrom, LocalDate dateTo) {
        List<String> result = new ArrayList<String>();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate currentDate = dateFrom;
        double sum = 0;
        int count = 0;

        while(!currentDate.isAfter(dateTo)) {
            List<String[]> dayData = rawData;

            for (String[] row : dayData) {
                try {
                    double speed = Double.parseDouble(row[4]);
                    sum += speed;
                    count++;
                } catch (NumberFormatException e) {
                }
            }
            Double averageWindSpeed = count > 0 ? sum / count : 0;
            result.add(String.format("%s average wind speed: %.2f m/s ", currentDate.format(dateOnlyFormatter), averageWindSpeed));


             //O(1)
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
	 */
	public List<String> approvedValues(LocalDate dateFrom, LocalDate dateTo) {

		//TODO: Implement method
		return null;  //O(1)
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
	 */
	public List<String> highestWindSpeed(LocalDate dateFrom, LocalDate dateTo) {
		//TODO: Implement method
		return null;  //O(1)
	}
}