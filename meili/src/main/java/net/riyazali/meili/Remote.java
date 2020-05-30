package net.riyazali.meili;

import java.io.IOException;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Remote represents a service capable of communicating to Meilisearch server over a network.
 *
 * @author Riyaz Ali (me@riyazali.net)
 * @apiNote The interface defines method which maps directly to Meili's REST API actions. Most of
 * the methods define a synchronous way of communication (no callbacks / promises / futures),
 * similar to RPC-styled services, since the client doesn't handle any multi-threading /
 * asynchronous pattern by itself allowing the consumer to adapt whatever is best of their use.
 * Subclasses of this interface must ensure thread-safety and should be designed for concurrent
 * usage.
 */
public interface Remote {

  /**
   * Perform a GET operation on the given resource
   */
  @NotNull Response get(@NotNull Request request) throws IOException;

  /**
   * Perform a POST operation on the given resource
   */
  @NotNull Response post(@NotNull Request request) throws IOException;

  /**
   * Perform a PUT operation on the given resource
   */
  @NotNull Response put(@NotNull Request request) throws IOException;

  /**
   * Perform a DELETE operation on the given resource
   */
  @NotNull Response delete(@NotNull Request request) throws IOException;

  // ------------ - - - - -
  // Common data transfer objects for request / response types
  // ------------ - - - - -

  /* Request class represents a single REST API call */
  @Accessors(fluent = true)
  @Getter @Builder final class Request {
    private final String path;
    private final Map<String, String> query;
    private final Source body;
  }

  /* Response class represents a single REST API response */
  abstract class Response implements AutoCloseable {

    /**
     * Get the HTTP status of the request
     */
    public abstract int status();

    /**
     * Get a reference to the underlying source stream
     */
    public abstract @Nullable Source body();
  }
}
