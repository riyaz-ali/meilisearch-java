package net.riyazali.meili;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * Document represents a single document instance in meilisearch.
 *
 * <p>
 * In Meilisearch, documents are objects composed of fields which can store any type of data. <br />
 * The only required field in a document is it's document id / primary key. Meili enforces a strict
 * pattern check on a document's primary key and it can only contain alphanumeric values with dash
 * (-) and under-score (_)
 *
 * <p>
 * Refer to the <a href="https://docs.meilisearch.com/guides/main_concepts/documents.html">documents
 * article</a> in Meilisearch's documentation to know more.
 *
 * @author Riyaz Ali (me@riyazali.net)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Document {

  // regex to test the primary key value against
  Pattern PRIMARY_KEY_REGEX = Pattern.compile("A-Za-z0-9_-");

  /**
   * The document's primary key field name
   */
  @NotNull String primaryKey();

  /**
   * The document's index name
   */
  @NotNull String index();
}
