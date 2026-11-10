package model;

import java.util.List;

/** A multiple-choice question used by the test page. */
public record TestQuestion(String prompt, String hint, List<String> options,
                           int correctIndex) {

    public TestQuestion {
        options = List.copyOf(options);
    }

    public TestQuestion(String prompt, String hint, List<String> options) {
        this(prompt, hint, options, 0);
    }
}
