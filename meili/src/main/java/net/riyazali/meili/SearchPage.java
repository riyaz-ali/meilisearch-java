package net.riyazali.meili;

import java.time.Duration;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

/**
 * SearchPage represents a single page from the search result set
 *
 * @author Riyaz Ali (me@riyazali.net)
 */
@Accessors(fluent = true)
public class SearchPage<T> extends Page<T> {

  // total elements in resultset reported by the api
  @Getter private final long count;

  // is the total number reported exhaustive?
  @Getter private final boolean isCountExhaustive;

  // query duration / time taken for execution
  @Getter private final Duration processingTime;

  /**
   * Create a new search page from the given result set and configuration
   *
   * @param response response that this page contains
   * @param config   fetch configuration used
   */
  SearchPage(@NotNull Response<T> response, @NotNull SearchConfig config) {
    super(response.hits(), config);

    this.count = response.nbHits();
    this.isCountExhaustive = response.exhaustiveNbHits();
    this.processingTime = Duration.ofMillis(response.processingTimeMs());
  }

  @Override public SearchConfig config() {
    return (SearchConfig) super.config();
  }

  // Response represents the response we get from the api
  // this class is for use with encoder.decode method which might reflectively
  // access the fields here to de-serialize the response
  @Getter public static class Response<T> {
    private final List<T> hits;
    private long nbHits;
    private boolean exhaustiveNbHits;
    private long processingTimeMs;

    @TestOnly public Response(List<T> hits) {
      this.hits = hits;
    }
  }
}
