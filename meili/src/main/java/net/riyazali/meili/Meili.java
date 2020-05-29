package net.riyazali.meili;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;

import static net.riyazali.meili.Precondition.checkNotNull;

/**
 * Meili is the client that handles all the interaction with the meili server.
 *
 * <p>
 * Most operations in meili are defined on an {@link Index index}. Use this client class to get
 * reference to an index object.
 *
 * <p>
 * Instances of this class are thread-safe and can be safely used concurrently
 *
 * @author Riyaz Ali (me@riyazali.net)
 */
public final class Meili {

  // external / provided service class references
  private final Remote remote;
  private final Encoder encoder;

  /**
   * Create a new meili client
   *
   * <p>
   * The client is the primary interface to the remote meilisearch service. Most methods defined on
   * client operate on global context (ie. indexes, settings, stats, etc.)
   *
   * @param remote  an implementation of {@linkplain Remote} capable of communicating with the
   *                meilisearch service
   * @param encoder an implementation of {@linkplain Encoder} which can handle encoding/decoding of
   *                JSON data
   */
  public Meili(@NotNull Remote remote, @NotNull Encoder encoder) {
    this.remote = checkNotNull(remote);
    this.encoder = checkNotNull(encoder);
  }

  /**
   * Get or create new index on meilisearch
   *
   * @param klass index's document type
   * @throws IllegalArgumentException if no sensible document properties can be deduced from the
   *                                  given class type
   */
  public <T> @NotNull Index<T> index(Class<T> klass) throws IOException {
    return index(klass, true);
  }

  /**
   * Get or create new index (if autoCreate is set) on meilisearch backend
   *
   * @param klass      index's document type
   * @param autoCreate whether or not we should create / update the index automatically
   * @throws IllegalArgumentException if no sensible document properties can be deduced from the
   *                                  given class type
   */
  public <T> @NotNull Index<T> index(Class<T> klass, boolean autoCreate) throws IOException {
    Index<T> index = Index.from(klass);
    index.remote(remote);
    index.encoder(encoder);

    if (autoCreate && !index.exists()) {
      index.create();
    }

    return index;
  }
}
