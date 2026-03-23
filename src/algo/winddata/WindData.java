package algo.winddata;

import java.time.LocalDate;

public class WindData
{
    private final LocalDate dateTime;
    private final LocalDate timeStr;
    private final Double windSpeed;
    private final Double windDirection;
    private final String qualityControl1;
    private final String qualityControl2;

    public WindData(LocalDate dateTime, LocalDate timeStr, Double windSpeed, String qualityControl2, Double windDirection, String qualityControl) {
        this.dateTime = dateTime;
        this.timeStr = timeStr;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.qualityControl1 = qualityControl;
        this.qualityControl2 = qualityControl2;
    }

    public LocalDate getTimeStr() {
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

