package algo.winddata;

import java.time.LocalDate;

public record WindData(LocalDate dateTime, String timeStr, Double windDirection, String qualityControl2,
                       Double windSpeed, String qualityControl1) {
}

