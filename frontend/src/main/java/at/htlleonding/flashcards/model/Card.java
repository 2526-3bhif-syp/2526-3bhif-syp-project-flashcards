package at.htlleonding.flashcards.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Card {
    private String id;
    private String question;
    private String answer;
    private List<String> tags;

    // Audio fields for front side (Question)
    private String frontAudioData; // Base64
    private String frontAudioName;
    private String frontAudioDuration;

    // Audio fields for back side (Answer)
    private String backAudioData; // Base64
    private String backAudioName;
    private String backAudioDuration;

    // Image fields for front side (Question)
    private String frontImageData; // Base64
    private String frontImageName;

    // Image fields for back side (Answer)
    private String backImageData; // Base64
    private String backImageName;

    public Card() {
        this.id = UUID.randomUUID().toString();
        this.tags = new ArrayList<>();
    }

    public Card(String question, String answer) {
        if (question == null || question.trim().isEmpty() || answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Question and answer are mandatory.");
        }
        this.id = UUID.randomUUID().toString();
        this.question = question;
        this.answer = answer;
        this.tags = new ArrayList<>();
    }

    public String getId() { 
        if (id == null) id = UUID.randomUUID().toString();
        return id; 
    }
    public void setId(String id) { this.id = id; }

    // Getters and Setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getFrontAudioData() { return frontAudioData; }
    public void setFrontAudioData(String frontAudioData) { this.frontAudioData = frontAudioData; }

    public String getFrontAudioName() { return frontAudioName; }
    public void setFrontAudioName(String frontAudioName) { this.frontAudioName = frontAudioName; }

    public String getFrontAudioDuration() { return frontAudioDuration; }
    public void setFrontAudioDuration(String frontAudioDuration) { this.frontAudioDuration = frontAudioDuration; }

    public String getBackAudioData() { return backAudioData; }
    public void setBackAudioData(String backAudioData) { this.backAudioData = backAudioData; }

    public String getBackAudioName() { return backAudioName; }
    public void setBackAudioName(String backAudioName) { this.backAudioName = backAudioName; }

    public String getBackAudioDuration() { return backAudioDuration; }
    public void setBackAudioDuration(String backAudioDuration) { this.backAudioDuration = backAudioDuration; }

    public String getFrontImageData() { return frontImageData; }
    public void setFrontImageData(String frontImageData) { this.frontImageData = frontImageData; }

    public String getFrontImageName() { return frontImageName; }
    public void setFrontImageName(String frontImageName) { this.frontImageName = frontImageName; }

    public String getBackImageData() { return backImageData; }
    public void setBackImageData(String backImageData) { this.backImageData = backImageData; }

    public String getBackImageName() { return backImageName; }
    public void setBackImageName(String backImageName) { this.backImageName = backImageName; }
}
