package net.riyazali.meili.test.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.Objects;
import net.riyazali.meili.Document;
import net.riyazali.meili.Index;
import net.riyazali.meili.Update;
import org.jetbrains.annotations.NotNull;

// model class to use during tests
// see: resources/movies.json
@Document(index = "movies", primaryKey = "id")
public class Movie {
  private String id;
  private String title;
  private String poster;
  private String overview;
  private long releaseDate;

  @Override public String toString() {
    return "Movie{" +
        "id='" + id + '\'' +
        ", title='" + title + '\'' +
        ", poster='" + poster + '\'' +
        ", overview='" + overview + '\'' +
        ", releaseDate=" + releaseDate +
        '}';
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Movie movie = (Movie) o;
    return id.equals(movie.id);
  }

  @Override public int hashCode() {
    return Objects.hash(id);
  }

  public static @NotNull List<Movie> read() throws Exception {
    try (InputStream in = Movie.class.getClassLoader().getResourceAsStream("movies.json")) {
      // @formatter:off
      return (new GsonBuilder().create()).fromJson(
          new InputStreamReader(in), new TypeToken<List<Movie>>() {}.getType());
      // @formatter:on
    }
  }

  public static @NotNull Index<Movie> index() throws Exception {
    try (InputStream in = Movie.class.getClassLoader().getResourceAsStream("index.json")) {
      // @formatter:off
      return (new GsonBuilder().create()).fromJson(
          new InputStreamReader(in), new TypeToken<Index<Movie>>() {}.getType());
      // @formatter:on
    }
  }

  public static @NotNull Update enqueuedUpdate() {
    // @formatter:off
    return (new GsonBuilder().create()).fromJson(
        new StringReader("{\"updateId\": 0, \"status\":  \"ENQUEUED\"}"),
        new TypeToken<Update>() {}.getType()
    );
    // @formatter:on
  }

  public static @NotNull Update failedUpdate() {
    // @formatter:off
    return (new GsonBuilder().create()).fromJson(
        new StringReader("{\"updateId\": 0, \"status\":  \"FAILED\"}"),
        new TypeToken<Update>() {}.getType()
    );
    // @formatter:on
  }

  public static @NotNull Update processedUpdate() {
    // @formatter:off
    return (new GsonBuilder().create()).fromJson(
        new StringReader("{\"updateId\": 0, \"status\":  \"PROCESSED\"}"),
        new TypeToken<Update>() {}.getType()
    );
    // @formatter:on
  }
}
