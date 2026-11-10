package model;

/** A level in the student learning path. */
public record LevelInfo(String level, String name, String description,
                        String status, boolean unlocked) {
}
