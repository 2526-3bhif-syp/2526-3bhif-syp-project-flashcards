package at.htlleonding.flashcards.model;

import java.util.ArrayList;
import java.util.List;

public class Card {
    private String question;
    private String answer;
    private List<String> tags;

    public Card() {
        this.tags = new ArrayList<>();
    }

    public Card(String question, String answer) {
        if (question == null || question.trim().isEmpty() || answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Question and answer are mandatory.");
        }
        this.question = question;
        this.answer = answer;
        this.tags = new ArrayList<>();
    }

    // Getters and Setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
