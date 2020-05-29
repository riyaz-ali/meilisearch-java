package net.riyazali.meili;

import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;

/**
 * Encoder represents a service responsible to encode/decode java objects to JSON representation.
 *
 * @author Riyaz Ali (me@riyazali.net)
 */
public interface Encoder {

  /**
   * Encode encodes the given object of type T into it's JSON representation
   *
   * @param object object to encode
   * @return JSON string representation of the object
   */
  @NotNull <T> String encode(@NotNull T object);

  /**
   * Decode decodes the given json string and returns an object of type T
   *
   * @param json json string to decode
   * @return object of type T
   */
  @NotNull <T> T decode(@NotNull String json, @NotNull Type type);
}
