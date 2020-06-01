package net.riyazali.meili;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import static net.riyazali.meili.Precondition.checkNotNull;

/**
 * Page represents a single page from the result set
 *
 * @author Riyaz Ali (me@riyazali.net)
 */
@Accessors(fluent = true)
@Getter public class Page<T> implements Iterable<T> {

  // list containing the page's result
  private final List<T> results;

  // page's fetch configuration
  private final PageConfig config;

  /**
   * Create a new page from the given result set and configuration
   *
   * @param results results that this page contains
   * @param config  fetch configuration used
   */
  public Page(@NotNull List<T> results, @NotNull PageConfig config) {
    this.results = Collections.unmodifiableList(checkNotNull(results));
    this.config = checkNotNull(config);
  }

  @NotNull @Override public Iterator<T> iterator() {
    return results.iterator();
  }
}
