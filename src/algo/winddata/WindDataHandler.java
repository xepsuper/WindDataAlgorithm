package algo.winddata;

import java.io.IOException;
import java.math.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Retrieves wind data from a weather station file.
 */
public class WindDataHandler {
    private final ArrayList<WindData> rawData = new ArrayList<>();

    private final DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Load wind data from file.
     *
     * @param filePath path to file with weather data
     * @throws IOException if there is a problem while reading the file
     * <p>
     * Timecomplexity is O(n)
     */
    public void loadData(String filePath) throws IOException { //O(n)
        List<String> fileData = Files.readAllLines(Paths.get(filePath));


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
     * Timecomplexity is O(k)
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
        double averageWindSpeed;
        String dataStr;

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
                    dataStr = currentDay.format(dateOnlyFormatter) + " average wind speed: " + BigDecimal.valueOf(averageWindSpeed).setScale(2, RoundingMode.HALF_UP);
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
            dataStr = currentDay.format(dateOnlyFormatter) + " average wind speed: " + BigDecimal.valueOf(averageWindSpeed).setScale(2, RoundingMode.HALF_UP);
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
     * Time complexity: O(k) where k is the number of records in the date range
     */
    public List<String> approvedValues(LocalDate dateFrom, LocalDate dateTo) { //O(k)
        // checks and arraylists
        checkDate(dateFrom, dateTo);
        List<String> result = new ArrayList<>();

        // sublist
        int toIndex = getToIndex(dateTo);
        int fromIndex = getFromIndex(dateFrom);
        final List<WindData> sublist = rawData.subList(fromIndex, toIndex);

        // variables
        LocalDate currentDate = null;
        int approvedCount = 0;
        int totalCount = 0;

        for (WindData data : sublist) {
            LocalDate localDate = data.getDateTime();
            String localApproveValue1 = data.getQualityControl1();
            String localApproveValue2 = data.getQualityControl2();

            // Check if both quality control values are approved ('G')
            boolean isApproved = "G".equalsIgnoreCase(localApproveValue1) && "G".equalsIgnoreCase(localApproveValue2);

            // Initialize for the first record
            if (currentDate == null) {
                currentDate = localDate;
            }

            // When date changes, calculate percentage for the previous day
            if (!localDate.equals(currentDate)) {
                // Calculate percentage for the completed day
                double percentage = (totalCount > 0) ? (approvedCount * 100.0 / totalCount) : 0.0;
                String dataStr = currentDate.format(dateOnlyFormatter) + ": " + BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP) + " % approved values";
                result.add(dataStr);

                // Reset for new day
                currentDate = localDate;
                approvedCount = 0;
                totalCount = 0;
            }

            // Count records for current day
            totalCount++;
            if (isApproved) {
                approvedCount++;
            }
        }

        // Check the last day after loop completes
        if (currentDate != null && totalCount > 0) {
            double percentage = (approvedCount * 100.0 / totalCount);
            String dataStr = currentDate.format(dateOnlyFormatter) + ": " + BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP) + " % approved values";
            result.add(dataStr);
        }

        return result;
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
     * Time complexity: O(k) where k is the number of records in the date range
     */
    public List<String> highestWindSpeed(LocalDate dateFrom, LocalDate dateTo) { //O(k)
        // check and arraylists
        checkDate(dateFrom, dateTo);
        List<String> result = new ArrayList<>();

        // sublist
        int fromIndex = getFromIndex(dateFrom);
        int toIndex = getToIndex(dateTo);
        final List<WindData> sublist = rawData.subList(fromIndex, toIndex);

        // variables
        LocalDate currentDate = null;
        double maxWindSpeed = -1;
        String maxWindSpeedTime = "";
        double currentWindSpeed;

        for (WindData data : sublist) {
            LocalDate localDate = data.getDateTime();
            currentWindSpeed = data.getWindSpeed();

            // Initialize for the first record
            if (currentDate == null) {
                currentDate = localDate;
                maxWindSpeed = currentWindSpeed;
                maxWindSpeedTime = data.getTimeStr();
                continue;
            }

            // When date changes, save the max for the previous day
            if (!localDate.equals(currentDate)) {
                // Add the max wind speed for the completed day
                String dataStr = currentDate.format(dateOnlyFormatter) + " " + maxWindSpeedTime + ": " + maxWindSpeed + " m/s";
                result.add(dataStr);

                // Reset for new day
                currentDate = localDate;
                maxWindSpeed = currentWindSpeed;
                maxWindSpeedTime = data.getTimeStr();
            }
            // Same day - check if current wind speed is higher
            else if (currentWindSpeed > maxWindSpeed) {
                maxWindSpeed = currentWindSpeed;
                maxWindSpeedTime = data.getTimeStr();
            }
        }

        // Check the last day after loop completes
        if (currentDate != null) {
            String dataStr = currentDate.format(dateOnlyFormatter) + " " + maxWindSpeedTime + ": " + maxWindSpeed + " m/s";
            result.add(dataStr);
        }

        return result;
    }

    // Help methods
    private void checkDate(LocalDate dateFrom, LocalDate dateTo) { //O(1)
        if(dateFrom.isAfter(dateTo) || dateTo == null){
            throw new IllegalArgumentException("Skriv in datumet korrekt (X-datum måste vara större än Y-datum");

        }
    }
    //index methods
    private int getToIndex(LocalDate dateTo) { //O(logn)
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

    private int getFromIndex(LocalDate dateFrom){ //O(logn)
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

