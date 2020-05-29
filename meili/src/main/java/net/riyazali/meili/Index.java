package net.riyazali.meili;

import java.io.IOException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.riyazali.meili.Remote.Request;
import org.jetbrains.annotations.NotNull;

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
  boolean exists() throws IOException {
    return remote.get(
        Request.builder().path(String.format("/indexes/%s", uid())).build()
    ).status() != 404;
  }

  void create() throws IOException {
    String json = encoder.encode(this);
    remote.post(Request.builder().path("indexes").body(json).build());
  }

  // Public API
  // ------- - - - - -

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
