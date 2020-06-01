package net.riyazali.meili;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.riyazali.meili.Precondition.checkNotNull;

@Getter @Accessors(fluent = true)
@SuperBuilder public class SearchConfig extends PageConfig {
  // the query string
  @NotNull private final String query;

  // attributes whose values have to be cropped
  @Nullable @Singular("crop") private final List<String> attributesToCrop;

  // number of characters to keep on each side
  @Builder.Default private final int cropLength = 200;

  // attributes whose values will contain highlighted matching query words.
  @Nullable @Singular("highlight") private final List<String> attributesToHighlight;

  // filters to be used with the query
  @Nullable private final String filters;

  // defines whether an object that contains information about the matches should be returned or not.
  private final boolean matches;

  /**
   * Generate a Map&lt;String, String&gt; from the config
   */
  @Override public @NotNull Map<String, String> map() {
    Map<String, String> result = super.map();
    result.put("q", checkNotNull(query()));
    result.put("cropLength", Integer.toString(cropLength()));
    if (attributesToCrop() != null) {
      result.put("attributesToCrop", String.join(",", checkNotNull(attributesToCrop())));
    }
    if (attributesToHighlight != null) {
      result.put("attributesToHighlight", String.join(",", checkNotNull(attributesToHighlight())));
    }
    if (filters() != null) {
      result.put("filters", filters());
    }
    if (matches) {
      result.put("matches", Boolean.toString(matches()));
    }
    return result;
  }
}
