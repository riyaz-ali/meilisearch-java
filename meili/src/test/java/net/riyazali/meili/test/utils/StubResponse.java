package net.riyazali.meili.test.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayInputStream;
import net.riyazali.meili.Remote.Response;
import okio.Okio;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StubResponse extends Response {
  private static final Gson gson = new GsonBuilder().create();

  private final int status;
  private final Source body;

  public StubResponse(int status, Source body) {
    this.status = status;
    this.body = body;
  }

  @Override public int status() {
    return status;
  }

  @Override public @Nullable Source body() {
    return body;
  }

  @Override public void close() throws Exception {
    // no-op
  }

  // Factories
  // ------- - - - - -

  public static @NotNull Response notFound() {
    return new StubResponse(404, null);
  }

  public static <T> @NotNull Response ok(T object) {
    return new StubResponse(200,
        Okio.source(new ByteArrayInputStream(gson.toJson(object).getBytes())));
  }
}
