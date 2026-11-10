package model;

/** An exercise available to the student. */
public record ExerciseInfo(String title, String description, String level, Tone tone,
                           boolean completed) {

    public ExerciseInfo(String title, String description, String level, Tone tone) {
        this(title, description, level, tone, false);
    }
}
