package model;

/** A test question with the track it belongs to, used by the teacher view. */
public record TeacherQuestion(String track, String trackLabel, TestQuestion question) {
}
