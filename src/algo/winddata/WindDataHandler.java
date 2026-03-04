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
            String calenderDate = parts[0];
            String timeStr = parts[1];

            String[] dataRow = new String[6];
            dataRow[0] = calenderDate;
            dataRow[1] = timeStr;
            for(int i = 0; i<4; i++)
            {
                dataRow[i+2] = parts[2+i];
            }

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
            data = dateFrom.format(dateOnlyFormatter) +  " Average wind speed: "+ Math.round(averageWindSpeed/10);
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
	 */
	public List<String> approvedValues(LocalDate dateFrom, LocalDate dateTo) {
        List<String> result = new ArrayList<>();
        int approvedWinds = 0;
        int count = 0;
        String data = "";
      for(LocalDate date = dateFrom; !date.isAfter(dateTo); date = date.plusDays(1))
      {
            List<String[]> dataRow = rawData;
            for(String[] row : dataRow)
            {
                if(row[5].equalsIgnoreCase("G") || row[3].equalsIgnoreCase("G")){
                    approvedWinds++;
                }
                count++;
            }
            double percentage = count > 0 ? (double) approvedWinds /count : 0;
          data = "Percentage "+ Math.round(percentage*100)+"% " + " From: "+ dateFrom.format(dateOnlyFormatter);
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
	 */
	public List<String> highestWindSpeed(LocalDate dateFrom, LocalDate dateTo) {
		List<String> result = new ArrayList<>();
        while(!dateFrom.isAfter(dateTo)) {
            List<String[]> dataRow = rawData;
           double maxSpeed = -1;
            for(String[] row : dataRow){
                try {
                    double windSpeed = Double.parseDouble(row[4]);
                    if (windSpeed > maxSpeed) {
                        maxSpeed = windSpeed;
                    }
                }catch(NumberFormatException e)
                    {

                    }

                }
                if(maxSpeed > 0) {
                    result.add(String.format("%s average wind speed: %.2f m/s ", dateFrom.format(dateOnlyFormatter), maxSpeed));
                }

            }
        return result;
    }
}