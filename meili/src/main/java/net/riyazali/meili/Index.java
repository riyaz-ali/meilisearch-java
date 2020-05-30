package net.riyazali.meili;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.riyazali.meili.Remote.Request;
import net.riyazali.meili.Remote.Response;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.riyazali.meili.Precondition.checkNotNull;

/**
 * Index class represents a single index in Meilisearch.
 *
 * <p>
 * Index is a central component in Meilisearch as most operations are defined on a single index.
 * Common operations include working with {@link Document documents} and querying the index to
 * retrieve those.
 *
 * @author Riyaz Ali (me@riyazali.net)
 */
@Accessors(fluent = true, chain = false)
@ToString public final class Index<T> {

  // The index's uid
  @Getter private final String uid;

  // The name of the index's primary key
  @Getter private final String primaryKey;

  // external / provided service class references
  @ToString.Exclude
  @Setter(AccessLevel.PACKAGE)
  private transient Remote remote;

  @ToString.Exclude
  @Setter(AccessLevel.PACKAGE)
  private transient Encoder encoder;

  // type information of the document class
  @ToString.Exclude
  private transient Class<T> documentType;

  @ToString.Exclude
  private transient Class<T> documentArrayType;

  // see: Index.from(...) method below for details
  private Index(String uid, String primaryKey, Class<T> documentType) {
    this.uid = checkNotNull(uid);
    this.primaryKey = checkNotNull(primaryKey);
    documentType(checkNotNull(documentType));
  }

  // set the type of document class used
  // see: https://stackoverflow.com/a/4901192/6611700
  @SuppressWarnings("unchecked") private void documentType(Class<T> documentType) {
    try {
      this.documentType = documentType;
      this.documentArrayType = (Class<T>) Class.forName("[L" + documentType.getName() + ";");
    } catch (ClassNotFoundException cnfe) {
      throw new RuntimeException(cnfe);
    }
  }

  // some handy lifecycle operations
  // ------------- - - - - -

  /* returns true if the index exists on meili server */
  boolean exists() throws Exception {
    Request request = Request.builder().path(String.format("/indexes/%s", uid())).build();
    try (Response response = remote.get(request)) {
      return response.status() != 404;
    }
  }

  void create() throws Exception {
    Source json = encoder.encode(this);
    remote.post(Request.builder().path("indexes").body(json).build()).close();
  }

  // Public API
  // ------- - - - - -

  // TODO: add better error handling support

  /**
   * Get a single document identified by it's primary key
   *
   * @param id the document's primary key
   * @return the document instance if found else {@code null}
   */
  public @Nullable final T get(@NotNull String id) throws Exception {
    Request request =
        Request.builder().path(String.format("/indexes/%s/documents/%s", uid(), id)).build();

    try (Response response = remote.get(request)) {
      return response.status() == 200 ? encoder.decode(response.body(), documentType) : null;
    }
  }

  /**
   * All returns an iterable using which you can iterate over all the records in the index. It
   * transparently handles the pagination details so that it doesn't load a (potentially) large
   * dataset into memory at once.
   *
   * @return Iterable that returns instances if type T
   */
  public @NotNull final Iterable<T> all() throws Exception {
    // TODO: add pagination support
    Request request = Request.builder().path(String.format("/indexes/%s/documents", uid())).build();
    try (Response response = remote.get(request)) {
      T[] docs = encoder.decode(response.body(), documentArrayType);
      return Arrays.asList(docs);
    }
  }

  /**
   * Add a list of documents or replace them if they already exist.
   *
   * <p>
   * If you send an already existing document (same id) the whole existing document will be
   * overwritten by the new document. Fields previously in the document not present in the new
   * document are removed.
   *
   * @param documents list of documents to add or update
   * @see #update(T... documents) for partial updates
   */
  @SafeVarargs
  public @NotNull final Update insert(T... documents) throws Exception {
    Source json = encoder.encode(Arrays.asList(documents));
    Request request = Request.builder()
        .path(String.format("/indexes/%s/documents", uid())).body(json).build();

    try (Response response = remote.post(request)) {
      if (response.status() != 202) {
        throw new RuntimeException("failed to insert documents");
      }

      return makeUpdate(response);
    }
  }

  /**
   * Add a list of documents and update them if they already.
   *
   * <p>
   * If you send an already existing document (same id) the old document will be only partially
   * updated according to the fields of the new document. Thus, any fields not present in the new
   * document are kept and remained unchanged.
   *
   * @param documents list of documents to add or update
   * @see #insert(T... documents) for complete update
   */
  @SafeVarargs
  public final @NotNull Update update(T... documents) throws Exception {
    Source json = encoder.encode(Arrays.asList(documents));
    Request request = Request.builder()
        .path(String.format("/indexes/%s/documents", uid())).body(json).build();

    try (Response response = remote.put(request)) {
      if (response.status() != 202) {
        throw new RuntimeException("failed to insert documents");
      }

      return makeUpdate(response);
    }
  }

  /**
   * Delete the documents in the current index.
   *
   * @param documents list of documents to delete
   */
  @SafeVarargs
  public final @NotNull Update delete(T... documents) throws Exception {
    List<?> ids;
    try {
      Field primaryKeyField = documentType.getDeclaredField(primaryKey());
      primaryKeyField.setAccessible(true);
      ids = Arrays.stream(documents)
          .map(doc -> {
            try {
              return primaryKeyField.get(doc);
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }).collect(Collectors.toList());
    } catch (NoSuchFieldException ex) {
      throw new RuntimeException(ex);
    }

    Source json = encoder.encode(checkNotNull(ids));
    Request request = Request.builder()
        .path(String.format("/indexes/%s/documents/delete-batch", uid())).body(json).build();

    try (Response response = remote.post(request)) {
      if (response.status() != 202) {
        throw new RuntimeException("failed to insert documents");
      }

      return makeUpdate(response);
    }
  }

  /**
   * Delete all documents in the current index
   */
  public final @NotNull Update clear() throws Exception {
    Request request = Request.builder()
        .path(String.format("/indexes/%s/documents", uid())).build();

    try (Response response = remote.delete(request)) {
      if (response.status() != 202) {
        throw new RuntimeException("failed to insert documents");
      }

      return makeUpdate(response);
    }
  }

  // Helpers
  // ------- - - - -

  @NotNull private Update makeUpdate(Response response) throws Exception {
    Update update = encoder.decode(response.body(), Update.class);
    update.index(this);
    update.remote(remote);
    update.encoder(encoder);
    return update.refresh();
  }

  // Factories
  // ------ - - - -

  /**
   * Returns a new index instance for the given document class type
   *
   * @param klass class type of the document
   * @return new index instance
   * @throws IllegalArgumentException if no sensible document properties can be deduced from the
   *                                  given class type
   */
  public static <T> @NotNull Index<T> from(Class<T> klass) {
    Document document = klass.getAnnotation(Document.class);
    if (document == null) {
      throw new IllegalArgumentException(
          String.format("class %s does not contain annotation of type %s",
              klass.getCanonicalName(), Document.class.getCanonicalName())
      );
    }

    return new Index<>(document.index(), document.primaryKey(), klass);
  }
}
