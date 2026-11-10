package model;

import java.util.List;

/** The student's daily learning streak and current-week activity. */
public record StreakInfo(String title, int currentDays, int bestDays, String message,
                         String weekCaption, List<StreakDay> week) {
}
