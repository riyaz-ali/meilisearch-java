package net.riyazali.meili;

import java.io.IOException;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.riyazali.meili.Precondition.checkNotNull;

/**
 * {@linkplain Remote} implementation that uses okhttp under the hood
 *
 * @author Riyaz Ali (me@riyazali.net)
 */
public class HttpRemote implements Remote {

  // all REST API methods on meili support only application/json
  private static final MediaType MEDIA_TYPE_JSON =
      checkNotNull(MediaType.parse("application/json"));

  private final HttpUrl base;
  private final OkHttpClient client;

  private HttpRemote(
      @NotNull final String endpoint, @Nullable final String token, @NotNull OkHttpClient client) {
    this.base = checkNotNull(HttpUrl.parse(endpoint));
    if (token != null) {
      Interceptor interceptor = chain -> chain.proceed(
          chain.request().newBuilder()
              .addHeader("X-Meili-API-Key", token)
              .build()
      );
      client = client.newBuilder().addInterceptor(interceptor).build();
    }
    this.client = client;
  }

  @Override public @NotNull Response execute(
      @NotNull String method, @NotNull Request request) throws IOException {
    return execute(
        new okhttp3.Request.Builder()
            .url(buildUrl(request))
            .method(method, buildBody(request))
            .build());
  }

  private @NotNull HttpUrl buildUrl(@NotNull Request request) {
    HttpUrl.Builder url = checkNotNull(base.newBuilder(request.path()));
    if (request.query() != null) {
      for (Map.Entry<String, String> entry : request.query().entrySet()) {
        url.addQueryParameter(entry.getKey(), entry.getValue());
      }
    }
    return url.build();
  }

  private @Nullable RequestBody buildBody(@NotNull Request request) {
    return request.body() != null ?
        SourceRequestBody.create(request.body(), MEDIA_TYPE_JSON) : null;
  }

  private @NotNull Response execute(okhttp3.Request request) throws IOException {
    return new Okhttp3ResponseWrapper(client.newCall(request).execute());
  }

  // RequestBody that reads from Source directly to sink
  private static final class SourceRequestBody extends RequestBody {

    private final MediaType mediaType;
    private final Source source;

    private SourceRequestBody(MediaType mediaType, Source source) {
      this.mediaType = checkNotNull(mediaType);
      this.source = checkNotNull(source);
    }

    @Nullable @Override public MediaType contentType() {
      return mediaType;
    }

    @Override public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
      bufferedSink.writeAll(source);
    }

    public static @NotNull RequestBody create(@NotNull Source source,
        @NotNull MediaType mediaType) {
      return new SourceRequestBody(mediaType, source);
    }
  }

  // Response implementation that wraps okhttp3.Response
  private static final class Okhttp3ResponseWrapper extends Response {
    private final okhttp3.Response response;

    Okhttp3ResponseWrapper(okhttp3.Response response) {
      this.response = checkNotNull(response);
    }

    @Override public int status() {
      return response.code();
    }

    @Override public @Nullable Source body() {
      return response.body() != null ? checkNotNull(response.body()).source() : null;
    }

    @Override public void close() throws Exception {
      response.close();
    }
  }

  // Factories
  // ------ - - - - -

  public static @NotNull Remote create(@NotNull String endpoint) {
    return create(endpoint, (String) null);
  }

  public static @NotNull Remote create(@NotNull String endpoint, @Nullable String token) {
    return create(endpoint, token, new OkHttpClient.Builder().build());
  }

  public static @NotNull Remote create(@NotNull String endpoint, @NotNull OkHttpClient client) {
    return create(endpoint, null, client);
  }

  public static @NotNull Remote create(@NotNull String endpoint, @Nullable String token,
      @NotNull OkHttpClient client) {
    return new HttpRemote(endpoint, token, client);
  }
}
