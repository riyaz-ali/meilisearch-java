package net.riyazali.meili.test.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import net.riyazali.meili.Document;
import net.riyazali.meili.Index;
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
}
