package model;

/** A student row in the teacher monitoring table. */
public record StudentOverview(String name, String username, String track,
                              int progress, String lastActivity) {
}
