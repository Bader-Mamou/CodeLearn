package model;

import java.util.List;

/** Everything the teacher dashboard needs to render itself. */
public record TeacherSummary(String name, List<StatInfo> stats, List<ActivityInfo> activities,
                             List<LanguageInfo> languages, List<CourseItem> courses,
                             List<StudentOverview> students) {

    public TeacherSummary {
        stats = List.copyOf(stats);
        activities = List.copyOf(activities);
        languages = List.copyOf(languages);
        courses = List.copyOf(courses);
        students = List.copyOf(students);
    }

    public TeacherSummary(List<StatInfo> stats, List<ActivityInfo> activities) {
        this("Enseignant", stats, activities, List.of(), List.of(), List.of());
    }
}
