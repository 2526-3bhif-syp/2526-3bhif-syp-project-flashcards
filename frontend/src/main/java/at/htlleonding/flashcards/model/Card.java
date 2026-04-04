package at.htlleonding.flashcards.model;

import java.util.ArrayList;
import java.util.List;

public class Card {
    private String question;
    private String answer;
    private List<String> tags;
    private String frontImagePath;
    private String frontAudioPath;
    private String backImagePath;
    private String backAudioPath;

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

    public String getFrontImagePath() { return frontImagePath; }
    public void setFrontImagePath(String frontImagePath) { this.frontImagePath = frontImagePath; }

    public String getFrontAudioPath() { return frontAudioPath; }
    public void setFrontAudioPath(String frontAudioPath) { this.frontAudioPath = frontAudioPath; }

    public String getBackImagePath() { return backImagePath; }
    public void setBackImagePath(String backImagePath) { this.backImagePath = backImagePath; }

    public String getBackAudioPath() { return backAudioPath; }
    public void setBackAudioPath(String backAudioPath) { this.backAudioPath = backAudioPath; }
}
