package net.riyazali.meili;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@Builder @Getter public class PageConfig {

  /* Max number of elements to retrieve in a page */
  @Builder.Default private final int limit = 20;

  /* Offset to begin with */
  @Builder.Default private final int offset = 0;

  /* List of attributes to fetch */
  @Builder.Default private final List<String> attributes = Collections.singletonList("*");

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
}
