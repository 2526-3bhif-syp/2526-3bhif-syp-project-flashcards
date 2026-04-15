package at.htlleonding.flashcards.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Persistence {
    private static final String DEFAULT_FILE = "decks.json";
    private final ObjectMapper mapper;

    public Persistence() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Loads decks from the internal application storage.
     */
    public List<Deck> loadDecks() {
        File file = new File(DEFAULT_FILE);
        if (!file.exists()) return new ArrayList<>();
        
        try {
            return mapper.readValue(file, new TypeReference<List<Deck>>() {});
        } catch (IOException e) {
            System.err.println("Error loading decks: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Saves decks to the internal application storage.
     */
    public void saveDecks(List<Deck> decks) {
        try {
            mapper.writeValue(new File(DEFAULT_FILE), decks);
        } catch (IOException e) {
            System.err.println("Error saving decks: " + e.getMessage());
        }
    }

    /**
     * Exports a deck to a JSON file.
     */
    public void exportToJSON(Deck deck, File file) throws IOException {
        mapper.writeValue(file, deck);
    }

    /**
     * Exports a deck to a CSV file.
     * Format: Question,Answer,Tags(comma separated and quoted)
     */
    public void exportToCSV(Deck deck, File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            // Header
            pw.println("Question,Answer,Tags");
            
            for (Card card : deck.getCards()) {
                String q = escapeCSV(card.getQuestion());
                String a = escapeCSV(card.getAnswer());
                String tags = escapeCSV(String.join(";", card.getTags())); // Use ; for tags inside CSV
                
                pw.printf("%s,%s,%s%n", q, a, tags);
            }
        }
    }

    /**
     * Imports a deck from a JSON file.
     */
    public Deck importFromJSON(File file) throws IOException {
        return mapper.readValue(file, Deck.class);
    }

    /**
     * Imports a deck from a CSV file.
     */
    public Deck importFromCSV(File file) throws IOException {
        Deck deck = new Deck(file.getName().replace(".csv", ""));
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length >= 2) {
                    Card card = new Card(parts[0], parts[1]);
                    if (parts.length >= 3 && !parts[2].isEmpty()) {
                        card.setTags(Arrays.asList(parts[2].split(";")));
                    }
                    deck.addCard(card);
                }
            }
        }
        return deck;
    }

    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    sb.append('\"'); // escaped quote
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString());
        return result.toArray(new String[0]);
    }

    private String escapeCSV(String text) {
        if (text == null) return "";
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}
