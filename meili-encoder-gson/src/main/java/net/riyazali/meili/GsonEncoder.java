package net.riyazali.meili;

import com.fatboyindustrial.gsonjavatime.ZonedDateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import okio.Okio;
import okio.Source;
import org.jetbrains.annotations.NotNull;

/**
 * {@linkplain Encoder} implementation backed by Gson
 *
 * @author Riyaz Ali (me@riyazali.net)
 */
public class GsonEncoder implements Encoder {

  // @formatter:off
  public static final Type ZONED_DATE_TIME_TYPE = new TypeToken<ZonedDateTime>() {}.getType();
  public static final Type UPDATE_STATUS_TYPE = new TypeToken<Update.Status>() {}.getType();
  // @formatter:on

  private final Gson gson;

  private GsonEncoder(@NotNull GsonBuilder builder) {
    builder.registerTypeAdapter(ZONED_DATE_TIME_TYPE, new ZonedDateTimeConverter());
    builder.registerTypeAdapter(UPDATE_STATUS_TYPE, new LowerCaseEnumTypeAdapter());
    this.gson = builder.create();
  }

  @Override public @NotNull <T> Source encode(@NotNull T object) {
    return Okio.source(new ByteArrayInputStream(gson.toJson(object).getBytes()));
  }

  @Override public <T> @NotNull T decode(@NotNull Source json, @NotNull Type type) {
    return gson.fromJson(new InputStreamReader(Okio.buffer(json).inputStream()), type);
  }

  // API returned status codes are in lower case but standard enum definitions are usually uppercase
  // This adapter allows us to do that transformation.
  private static class LowerCaseEnumTypeAdapter extends TypeAdapter<Update.Status> {

    @Override public void write(JsonWriter out, Update.Status value) throws IOException {
      out.value(value.name().toLowerCase());
    }

    @Override public Update.Status read(JsonReader in) throws IOException {
      return Update.Status.valueOf(in.nextString().toUpperCase());
    }
  }

  // Factories
  // ------- - - - - -

  public static @NotNull Encoder create() {
    return create(new GsonBuilder());
  }

  public static @NotNull Encoder create(@NotNull GsonBuilder builder) {
    return new GsonEncoder(builder);
  }
}
