package model;

/** Result of submitting a multiple-choice test. */
public record TestResult(boolean correct, int score, String message) {
}
