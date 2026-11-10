package model;

/** A course available to the student. */
public record CourseInfo(String id, String title, String description, String state, Tone tone) {

    public CourseInfo(String title, String description, String state, Tone tone) {
        this(null, title, description, state, tone);
    }
}
