package model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Mutable learning state of one student: completed courses and exercises,
 * test scores and the recent activity feed.
 */
public final class StudentProgress {

    private final Set<String> completedCourses = new LinkedHashSet<>();
    private final Set<String> completedExercises = new LinkedHashSet<>();
    private final List<Integer> testScores = new ArrayList<>();
    private final List<ActivityInfo> activities = new ArrayList<>();

    public Set<String> completedCourses() {
        return completedCourses;
    }

    public Set<String> completedExercises() {
        return completedExercises;
    }

    public List<Integer> testScores() {
        return testScores;
    }

    public List<ActivityInfo> activities() {
        return activities;
    }

    public boolean completeCourse(String key, ActivityInfo activity) {
        boolean added = completedCourses.add(key);
        if (added) {
            addActivity(activity);
        }
        return added;
    }

    public boolean completeExercise(String key, ActivityInfo activity) {
        boolean added = completedExercises.add(key);
        if (added) {
            addActivity(activity);
        }
        return added;
    }

    public void addTestScore(int score, ActivityInfo activity) {
        testScores.add(score);
        addActivity(activity);
    }

    public void addActivity(ActivityInfo activity) {
        if (activity == null) {
            return;
        }
        activities.add(0, activity);
        while (activities.size() > 8) {
            activities.remove(activities.size() - 1);
        }
    }

    public int averageScore() {
        if (testScores.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (int score : testScores) {
            total += score;
        }
        return total / testScores.size();
    }

    public int passedTests() {
        int passed = 0;
        for (int score : testScores) {
            if (score >= 60) {
                passed++;
            }
        }
        return passed;
    }

    /** Weighted completion used by the progress ring and profile level. */
    public int percent() {
        int value = completedCourses.size() * 20
                + completedExercises.size() * 10
                + passedTests() * 15
                + averageScore() / 10;
        return Math.max(0, Math.min(100, value));
    }

    public String totalTime() {
        int minutes = completedCourses.size() * 45
                + completedExercises.size() * 20
                + testScores.size() * 15;
        if (minutes < 60) {
            return minutes + "min";
        }
        return (minutes / 60) + "h " + (minutes % 60) + "min";
    }

    public int levelNumber() {
        int percent = percent();
        if (percent >= 80) {
            return 3;
        }
        if (percent >= 50) {
            return 2;
        }
        return 1;
    }
}
