package com.learnings.ai.service;

import com.learnings.ai.models.Movie;
import com.learnings.ai.models.Show;
import org.springframework.ai.document.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MovieBookingService {

    private static final Logger log = LoggerFactory.getLogger(MovieBookingService.class);

    private final VectorStore vectorStore;
    private final ObjectMapper json = new ObjectMapper();

    public MovieBookingService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Tool(description = "Search available movies by city and date; returns shows from the vector store with IDs")
    public List<Map<String, Object>> searchShows(
            @ToolParam(description = "City where user wants to watch the movie") String city,
            @ToolParam(description = "Date in ISO format YYYY-MM-DD") String date) {
        // Semantic query (avoid metadata filters until Cassandra schema is enriched)
        String semanticQuery = city + " " + date + " show movie";
        SearchRequest request = SearchRequest.builder()
                .query(semanticQuery)
                .topK(100)
                .build();

        try {
            List<Document> docs = vectorStore.similaritySearch(request);
            List<Map<String, Object>> results = new ArrayList<>();
            if (docs != null) {
                for (Document d : docs) {
                    String content = safeContent(d);
                    Movie movieAgg = parseMovieJson(content);
                    if (movieAgg != null && movieAgg.getShows() != null) {
                        for (Show show : movieAgg.getShows()) {
                            if (!city.equalsIgnoreCase(show.getCity()) || !date.equals(show.getDate())) {
                                continue;
                            }
                            Map<String, Object> m = new HashMap<>();
                            m.put("showId", show.getShowId());
                            m.put("movie", show.getMovie());
                            m.put("city", show.getCity());
                            m.put("date", show.getDate());
                            m.put("time", show.getTime());
                            m.put("screen", show.getScreen());
                            results.add(m);
                        }
                        continue;
                    }

                    // Back-compat: handle when content is a single Show JSON
                    Show show = parseShowJson(content);
                    if (show != null) {
                        if (!city.equalsIgnoreCase(show.getCity()) || !date.equals(show.getDate())) {
                            continue;
                        }
                        Map<String, Object> m = new HashMap<>();
                        m.put("showId", show.getShowId());
                        m.put("movie", show.getMovie());
                        m.put("city", show.getCity());
                        m.put("date", show.getDate());
                        m.put("time", show.getTime());
                        m.put("screen", show.getScreen());
                        results.add(m);
                    }
                }
            }
            if (!results.isEmpty()) {
                return results;
            }
        } catch (Exception e) {
            log.warn("Vector search failed; returning fallback shows. Reason: {}", e.getMessage(), e);
        }

        LocalDate d = LocalDate.parse(date);
        return List.of(
                Map.of("showId", "S-1001", "movie", "Interstellar", "city", city, "date", d.toString(), "time", "18:00", "screen", "Screen 3"),
                Map.of("showId", "S-1002", "movie", "Inception", "city", city, "date", d.toString(), "time", "21:00", "screen", "Screen 1")
        );
    }

    @Tool(description = "Get seat layout for a show; returns a dummy summary with a few available seats")
    public Map<String, Object> getSeatLayout(
            @ToolParam(description = "Show identifier returned from searchShows") String showId) {
        return Map.of(
                "showId", showId,
                "totalSeats", 120,
                "available", List.of("A1", "A2", "A3", "B5", "C10")
        );
    }

    @Tool(description = "Book seats for a show; returns a dummy bookingId and status")
    public Map<String, Object> bookSeats(
            @ToolParam(description = "Show identifier") String showId,
            @ToolParam(description = "List of seat labels, e.g., ['A1','A2']") List<String> seats,
            @ToolParam(description = "User name for the booking") String user) {
        return Map.of(
                "bookingId", "BK-" + Math.abs(new Random().nextInt(90000) + 10000),
                "showId", showId,
                "seats", seats,
                "user", user,
                "status", "CONFIRMED"
        );
    }

    @Tool(description = "Cancel a booking; returns a dummy cancellation confirmation")
    public Map<String, Object> cancelBooking(
            @ToolParam(description = "Booking identifier from bookSeats response") String bookingId) {
        return Map.of(
                "bookingId", bookingId,
                "status", "CANCELLED",
                "refund", "INITIATED"
        );
    }

    // Seeder utility: populate the Cassandra vector store with one document per show (small payloads, better embedding)
    public void seedMoviesIntoVectorStore(int count) {
        String[] movies = curatedMovies();
        int limit = Math.max(1, count);

        List<Document> batch = new ArrayList<>(200);
        int produced = 0;
        outer:
        for (int i = 0; i < movies.length; i++) {
            String title = movies[i];
            List<Show> shows = generateShowsForMovie(title, i);
            for (Show show : shows) {
                String content = buildShowJson(show);
                Document doc = Document.builder()
                        .id(show.getShowId())
                        .text(content)
                        .build();
                batch.add(doc);
                produced++;
                if (batch.size() == 200) {
                    try {
                        vectorStore.add(batch);
                    } catch (Exception e) {
                        log.warn("Failed to add batch to vector store: {}", e.getMessage(), e);
                    }
                    batch.clear();
                }
                if (produced >= limit) {
                    break outer;
                }
            }
        }
        if (!batch.isEmpty()) {
            try {
                vectorStore.add(batch);
            } catch (Exception e) {
                log.warn("Failed to add final batch to vector store: {}", e.getMessage(), e);
            }
        }
    }

    private static String[] curatedMovies() {
        return new String[]{
                "Oppenheimer", "Inception", "Interstellar", "The Dark Knight", "Tenet",
                "Dune", "Dune: Part Two", "Avatar", "Avatar: The Way of Water",
                "RRR", "Baahubali: The Beginning", "Baahubali 2: The Conclusion", "KGF: Chapter 1", "KGF: Chapter 2",
                "Pushpa: The Rise", "Pushpa 2: The Rule", "Salaar", "Kantara",
                "Kalki 2898 AD", "Leo", "Vikram", "Jailer",
                "3 Idiots", "PK", "Dangal", "Chhichhore",
                "Drishyam", "Drishyam 2", "Andhadhun", "Gangs of Wasseypur",
                "Spider-Man: No Way Home", "Avengers: Endgame", "Guardians of the Galaxy Vol. 3",
                "Mad Max: Fury Road", "John Wick: Chapter 4", "Mission: Impossible – Fallout",
                "Parasite", "The Shawshank Redemption", "The Godfather"
        };
    }

    private static List<Show> generateShowsForMovie(String title, int seed) {
        String[] cities = {"Hyderabad", "Bengaluru", "Chennai", "Mumbai", "Delhi", "Pune", "Kolkata"};
        String[] screens = {"Screen 1", "Screen 2", "Screen 3", "IMAX", "4DX"};
        String[] genres = {"Sci-Fi", "Action", "Drama", "Thriller", "Adventure"};

        Random rnd = new Random(seed + 12345);
        String genre = genres[rnd.nextInt(genres.length)];
        List<Show> shows = new ArrayList<>();
        int showCounter = 0;
        for (int day = 0; day < 30; day++) {
            LocalDate date = LocalDate.now().plusDays(day);
            for (int c = 0; c < 3; c++) { // 3 cities per movie
                String city = cities[(seed + c) % cities.length];
                for (String timeStr : List.of("12:00", "15:30", "19:00", "21:30")) {
                    String screen = screens[(seed + showCounter) % screens.length];
                    shows.add(new Show(
                            "S-" + (seed * 1000 + showCounter),
                            title,
                            genre,
                            city,
                            date.toString(),
                            timeStr,
                            screen
                    ));
                    showCounter++;
                }
            }
        }
        return shows;
    }

    private String buildMovieJson(Movie movie) {
        try {
            return json.writeValueAsString(movie);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize movie to JSON", e);
        }
    }

    private String buildShowJson(Show show) {
        try {
            return json.writeValueAsString(show);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize show to JSON", e);
        }
    }

    private Show parseShowJson(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        try {
            return json.readValue(content, Show.class);
        } catch (Exception e) {
            // Not JSON? try key=value fallback
            Map<String, String> kv = parseKeyValueContent(content);
            if (kv.isEmpty()) return null;
            return new Show(
                    kv.getOrDefault("showId", ""),
                    kv.getOrDefault("movie", ""),
                    kv.getOrDefault("genre", ""),
                    kv.getOrDefault("city", ""),
                    kv.getOrDefault("date", ""),
                    kv.getOrDefault("time", ""),
                    kv.getOrDefault("screen", "")
            );
        }
    }

    private Movie parseMovieJson(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        try {
            return json.readValue(content, Movie.class);
        } catch (Exception e) {
            return null;
        }
    }
    private static Map<String, String> parseKeyValueContent(String content) {
        Map<String, String> map = new HashMap<>();
        if (content == null) return map;
        String[] parts = content.split(";\\s*");
        for (String p : parts) {
            int i = p.indexOf('=');
            if (i > 0 && i < p.length() - 1) {
                String key = p.substring(0, i).trim();
                String value = p.substring(i + 1).trim();
                map.put(key, value);
            }
        }
        return map;
    }

    private static String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static String safeContent(Document d) {
        try {
            String s = d.toString();
            if (s == null) return "";
            // Prefer exact JSON slice: look for the first occurrence of a JSON object start
            int jsonStart = s.indexOf("{\"");
            if (jsonStart >= 0) {
                int jsonEnd = s.lastIndexOf('}');
                if (jsonEnd >= jsonStart) {
                    return s.substring(jsonStart, jsonEnd + 1);
                }
            }
            // Try to extract from text='...'
            String[] keys = {"text='", "content='"};
            for (String key : keys) {
                int i = s.indexOf(key);
                if (i >= 0) {
                    int start = i + key.length();
                    int end = s.indexOf("'", start);
                    if (end > start) {
                        return s.substring(start, end);
                    }
                }
            }
            return s;
        } catch (Exception e) {
            return "";
        }
    }
}

