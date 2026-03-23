package algo.winddata;

import java.time.LocalDate;

public class WindData
{
    private final LocalDate dateTime;
    private final String timeStr;
    private final Double windSpeed;
    private final Double windDirection;
    private final String qualityControl1;
    private final String qualityControl2;

    public WindData(LocalDate dateTime, String timeStr, Double windDirection, String qualityControl2, Double windSpeed, String qualityControl) {
        this.dateTime = dateTime;
        this.timeStr = timeStr;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.qualityControl1 = qualityControl;
        this.qualityControl2 = qualityControl2;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public Double getWindDirection() {
        return windDirection;
    }

    public String getQualityControl1() {
        return qualityControl1;
    }

    public LocalDate getDateTime() {
        return dateTime;
    }

    public String getQualityControl2() {
        return qualityControl2;
    }
}

