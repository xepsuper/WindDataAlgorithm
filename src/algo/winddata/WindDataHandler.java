package algo.winddata;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Retrieves wind data from a weather station file.
 * ArrayList according to the datastructure, the timecomplexity should at both worst and best case scenario O(n^2).
 * This datastructure is an algorithmic method called brute force which is a method that use computing power to solve problems quickly rather than optimizing the code.
 * It makes it easier to find the solution, and it's also the first step to optimize the code.
 * It does could use a better method but in this case it doesn't matter since we're not using large amount of data to play with.
 */
public class WindDataHandler {
    private final ArrayList<WindData> rawData = new ArrayList<>();

    private final DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Load wind data from file.
     *
     * @param filePath path to file with weather data
     * @throws IOException if there is a problem while reading the file
     *                     <p>
     *                     The enhanced for-loop should return with time complexity O(n) since it's an iteration
     *                     Problem with this iteration is that I am using an array to store specific row with data which should technically use O(n^2)
     *                     In this case it's not optimal, however it could've been better if I have chosen better datastructures like treemap since it stores with timecomplexity of O(logn).
     *                     But I am not obligated to optimize the datastructure as long I keep it below O(n^3).
     */
    public void loadData(String filePath) throws IOException {
        List<String> fileData = Files.readAllLines(Paths.get(filePath));  //O(n)


        for (String line : fileData) {
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split(";");
            // parsed values
            try {
                LocalDate calenderDate = LocalDate.parse(parts[0]);
                String timeStr = parts[1];
                Double windDirection = Double.parseDouble(parts[2]);
                String qualityControl2 = parts[3];
                Double windSpeed = Double.parseDouble(parts[4]);
                String qualityControl1 = parts[5];
                rawData.add(new WindData(calenderDate, timeStr, windDirection, qualityControl1, windSpeed, qualityControl2));
            }catch (Exception e){
                throw new IllegalArgumentException("Invalid datatype");
            }


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
     * <p>
     * In this method I am using an iteration to loop through my database, and it should have timecomplexity of O(n).
     * Because I am using enhanced for-loop it'll multiply the timecomplexity to O(n^2) with tree map it could've been more efficient, eta O(nlogn).
     * I also added an if check so it's checking the correct date and try catch so it doesn't receive invalid data.
     * I calculated the average speed for the entire duration of a day. So it recieves the average for that specific day.
     * Thereafter, I used a simplified if-check to secure the correct data. The main principle is that it should take the sum of the day and divide with how many times it has to go through that specific day.
     */
    public List<String> averageWindSpeed(LocalDate dateFrom, LocalDate dateTo) {
        // checks and new arraylists
        checkDate(dateFrom, dateTo);
        List<String> result = new ArrayList<>();

        // sublist
        int toIndex = getToIndex(dateTo);
        int fromIndex = getFromIndex(dateFrom);
        final List<WindData> sublist = rawData.subList(fromIndex, toIndex);

        //variables
        double averageWindSpeed = 0;
        String dataStr = "";

        LocalDate currentDay = null;
        double dailySum = 0;
        int dailyCount = 0;

        for (WindData data : sublist) {
            LocalDate dataDate = data.getDateTime();

            if (currentDay == null) {
                currentDay = dataDate;
            }

            if (!dataDate.equals(currentDay)) {
                if (dailyCount > 0) {
                    averageWindSpeed = dailySum / dailyCount;
                    dataStr = currentDay.format(dateOnlyFormatter) +
                            " average wind speed: " +
                            BigDecimal.valueOf(averageWindSpeed).setScale(2, RoundingMode.HALF_UP) +
                            " m/s (" + dailyCount + " records)";
                    result.add(dataStr);
                }
                currentDay = dataDate;
                dailySum = 0;
                dailyCount = 0;
            }

            dailySum += data.getWindSpeed();
            dailyCount++;
        }

        if (currentDay != null && dailyCount > 0) {
            averageWindSpeed = dailySum / dailyCount;
            dataStr = currentDay.format(dateOnlyFormatter) +
                    " average wind speed: " +
                    BigDecimal.valueOf(averageWindSpeed).setScale(2, RoundingMode.HALF_UP) +
                    " m/s (" + dailyCount + " records)";
            result.add(dataStr);
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
     * In this method I am using an iteration to loop through my database, and it should have timecomplexity of O(n).
     * Because I am using enhanced for-loop it'll multiply the timecomplexity to O(n^2) with tree map it could've been more efficient, eta O(nlogn).
     * This algorithm is trying to find the approvedValues of each winddata in each row. The ones that has "G" within the row are approved while the ones that are "Y" are denied.
     * I counted how many times it loops through on the same day and check the ones that are considered approved. Thereafter, I divide approved winddata with count to get the percentage value.
     * In the end I used the percentage value and converted and simplifed it by converting it to natural numbers.
	 */
	public List<String> approvedValues(LocalDate dateFrom, LocalDate dateTo) {
        //checks and arraylists
        checkDate(dateFrom, dateTo);
        List<String> result = new ArrayList<>();

        // sublist
        int toIndex = getToIndex(dateTo);
        int fromIndex = getFromIndex(dateFrom);
        final List<WindData> sublist = rawData.subList(fromIndex, toIndex);

        //variables
        double averageWindSpeed = 0;
        String dataStr = "";


//        List<String> result = new ArrayList<>();
//        int approvedWinds = 0;
//        int count = 0;
//        String data = "";
//
//
//      for(LocalDate date = dateFrom; !date.isAfter(dateTo); date = date.plusDays(1))
//      {
//          for(WindData row : rawData) {
//              LocalDate rowDate = LocalDate.parse(row[0], dateOnlyFormatter);
//              if(rowDate.equals(date)) {
//                  try {
//                      if (row[5].equalsIgnoreCase("G")) {
//                          approvedWinds++;
//                      }
//                      count++;
//                  }catch(NumberFormatException _){}
//              }
//          }
//          double percentage = count > 0 ? (double) approvedWinds / count : 0;
//          data = "Percentage " + BigDecimal.valueOf(percentage*100).setScale(2, RoundingMode.HALF_UP) + "% " + " From: " + date.format(dateOnlyFormatter);
//          result.add(data);
//        }
//
//
//		return result;  //O(1)
        return null;
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
     * In this method I am using an iteration to loop through my database, and it should have timecomplexity of O(n).
     * Because I am using enhanced for-loop it'll multiply the timecomplexity to O(n^2) with tree map it could've been more efficient, eta O(nlogn).
     * In this algorithm I am trying to select the highest wind speed on each day. I do that by using an if-check which checks for the value that's greater than its predecessor.
     * I also made sure to add checks so the value doesn't receive anything that's irrelevant besides double.
     * In the end I made sure that the data it prints it should be the same as the ones as listed above.
	 */
	public List<String> highestWindSpeed(LocalDate dateFrom, LocalDate dateTo) {
        //check and arraylists
        checkDate(dateFrom, dateTo);
        List<String> result = new ArrayList<>();

        // sublist
        int fromIndex = getFromIndex(dateFrom);
        int toIndex = getToIndex(dateTo);
        final List<WindData> sublist = rawData.subList(fromIndex, toIndex);//O(k)

        //variables
        LocalDate currentDate = null;
        double highWindSpeed = 0;
        String dataStr = "";
        String currentTime = "";



        for (WindData data : sublist) {  //O(k)
            LocalDate localDate = data.getDateTime();
            if(currentDate == null){
                currentDate = localDate;
            }
            if(!localDate.equals(currentDate))
            {
                if(highWindSpeed < data.getWindSpeed())
                {
                    highWindSpeed = data.getWindSpeed();
                    currentTime = data.getTimeStr();
                    dataStr = currentDate.format(dateOnlyFormatter) + " " + currentTime +
                            " " + BigDecimal.valueOf(highWindSpeed).setScale(2, RoundingMode.HALF_UP)
                            +  " m/s";
                    result.add(dataStr);
                }

            }
        }
        if(currentDate != null && highWindSpeed > 0){
            dataStr = currentDate.format(dateOnlyFormatter) + " " + currentDate +
                    " " + BigDecimal.valueOf(highWindSpeed).setScale(2, RoundingMode.HALF_UP)
                    +  " m/s";
            result.add(dataStr);
        }

        return result;
    }



    //        String data = "";
//        String timeHour = "";
//        for(LocalDate date = dateFrom; !date.isAfter(dateTo); date = date.plusDays(1)) {
//            double maxSpeed = -1;
//            for(String[] row : rawData){
//                LocalDate rowDate = LocalDate.parse(row[0], dateOnlyFormatter);
//                if(rowDate.equals(date)) {
//                    try {
//                        double windSpeed = Double.parseDouble(row[4]);
//
//                        if (windSpeed > maxSpeed) {
//                            maxSpeed = windSpeed;
//                            timeHour = row[1];
//                        }
//                    } catch (NumberFormatException _) {
//
//                    }
//                }
//                }
//                if(maxSpeed > 0) {
//                    data = date.format(dateOnlyFormatter) + " " + timeHour.substring(0, timeHour.length() - 3)+ ": " + BigDecimal.valueOf(maxSpeed).setScale(2, RoundingMode.HALF_UP) + " m/s";
//                    result.add(data);
//                }
//
//            }
//        return result;



    // Help methods
    private void checkDate(LocalDate dateFrom, LocalDate dateTo) {
        if(dateFrom.isAfter(dateTo) || dateTo == null){
            System.out.println("Skriv in värde och skriv datumet korrekt");
        }
    }
    //index methods
    private int getToIndex(LocalDate dateTo) {
        int toIndex = Collections.binarySearch(rawData, new WindData(dateTo, null, null, null, null, null),
                Comparator.comparing(WindData::getDateTime));

        if (toIndex < 0) {
            toIndex = -toIndex - 1;  // Insertion point
        } else {
            // Move to the last occurrence of this date
            while (toIndex < rawData.size() - 1 && rawData.get(toIndex + 1).getDateTime().equals(dateTo)) {
                toIndex++;
            }
            toIndex = toIndex + 1;  // Exclusive end index
        }
        return toIndex;
    }

    private int getFromIndex(LocalDate dateFrom){
        int fromIndex = Collections.binarySearch(rawData, new WindData(dateFrom, null, null, null, null, null),
                Comparator.comparing(WindData::getDateTime));

        if (fromIndex < 0) {
            fromIndex = -fromIndex - 1;  // Insertion point
        } else {
            // Move to the first occurrence of this date
            while (fromIndex > 0 && rawData.get(fromIndex - 1).getDateTime().equals(dateFrom)) {
                fromIndex--;
            }
        }
        return fromIndex;
        }
}

