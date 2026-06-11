package at.htlleonding.flashcards.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreakData {
    private List<String> streakDates = new ArrayList<>();
    private Map<String, String> recentDecks = new HashMap<>();

    public List<String> getStreakDates() {
        return streakDates;
    }

    public void setStreakDates(List<String> streakDates) {
        this.streakDates = streakDates;
    }

    public Map<String, String> getRecentDecks() {
        return recentDecks;
    }

    public void setRecentDecks(Map<String, String> recentDecks) {
        this.recentDecks = recentDecks;
    }
}
