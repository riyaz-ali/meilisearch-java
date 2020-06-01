package net.riyazali.meili;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@SuperBuilder @Getter public class PageConfig {

  /* Max number of elements to retrieve in a page */
  @Builder.Default private final int limit = 20;

  /* Offset to begin with */
  @Builder.Default private final int offset = 0;

  /* List of attributes to fetch */
  @Builder.Default private final List<String> attributes = Collections.emptyList();

  /**
   * Returns the default page configuration
   *
   * <p>
   * The default configuration is limit=20, offset=0, attributes=["*"]
   *
   * @return PageConfiguration with default values
   */
  public static @NotNull PageConfig getDefault() {
    return builder().build();
  }

  /**
   * Generate a Map&lt;String, String&gt; from the config
   */
  public @NotNull Map<String, String> map() {
    Map<String, String> result = new HashMap<>();
    result.put("limit", Integer.toString(limit()));
    result.put("offset", Integer.toString(offset()));
    if (!attributes().isEmpty()) result.put("attributes", String.join(",", attributes()));
    return result;
  }
}
