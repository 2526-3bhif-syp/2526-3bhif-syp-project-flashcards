package at.htlleonding.flashcards.model;

import java.time.Instant;

public class StudyRecord {
    private String rating;
    private String timestamp;

    public StudyRecord() {}

    public StudyRecord(String rating) {
        this.rating = rating;
        this.timestamp = Instant.now().toString();
    }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
