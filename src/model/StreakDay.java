package model;

/** One day in the student's current learning-streak week. */
public record StreakDay(String label, boolean completed, boolean today) {
}
