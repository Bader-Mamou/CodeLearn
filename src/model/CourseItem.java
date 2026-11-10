package model;

/** A course stored in the shared catalogue and editable by teachers. */
public record CourseItem(String id, String track, String title, String description) {
}
