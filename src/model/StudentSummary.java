package model;

import java.util.List;

/** Everything the student home page needs to render itself. */
public record StudentSummary(String greeting, String subtitle,
                             String eyebrow, String heroTitle, String heroSubtitle,
                             String currentLevel, int globalProgress,
                             StreakInfo streak, List<LevelInfo> levels,
                             List<StatInfo> stats, List<ActivityInfo> activities) {
}
