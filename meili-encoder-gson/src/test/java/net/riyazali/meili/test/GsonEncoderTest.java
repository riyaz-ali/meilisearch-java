package net.riyazali.meili.test;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Objects;
import net.riyazali.meili.Encoder;
import net.riyazali.meili.GsonEncoder;
import net.riyazali.meili.Update;
import okio.Buffer;
import okio.ByteString;
import okio.Okio;
import okio.Source;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GsonEncoderTest {

  final Model ALPHA = Model.from(ByteString.decodeBase64(
      "rO0ABXNyAC1uZXQucml5YXphbGkubWVpbGkudGVzdC5Hc29uRW5jb2RlclRlc3QkTW9kZWy/F+IIS4qgJAIAAUwAAmlkdAASTGphdmEvbGFuZy9TdHJpbmc7eHB0AAVhbHBoYQ=="));

  final String ALPHA_JSON = "{\"id\":\"alpha\"}";

  @DisplayName("verify encoder encodes the value to json")
  @Test void verifyEncoderEncodes() throws Exception {
    Source s = GsonEncoder.create().encode(ALPHA);
    Buffer b = new Buffer();
    b.writeAll(s);

    assertEquals(new ByteString(ALPHA_JSON.getBytes()), b.readByteString());
  }

  @DisplayName("verify encoder decodes the json to object")
  @Test void verifyEncoderDecodes() throws Exception {
    Buffer b = new Buffer();
    b.writeString(ALPHA_JSON, Charset.defaultCharset());

    Model m = GsonEncoder.create().decode(Okio.source(b.inputStream()), Model.class);

    assertEquals(ALPHA, m);
  }

  @DisplayName("verify encoder handles enums properly")
  @Test void verifyEnumHandledProperly() throws Exception {
    Encoder e = GsonEncoder.create();

    Source s = e.encode(Update.Status.ENQUEUED);
    assertEquals(new ByteString("\"enqueued\"".getBytes()), Okio.buffer(s).readByteString());

    Buffer b = new Buffer();
    b.writeString("\"enqueued\"", Charset.defaultCharset());
    Update.Status status = e.decode(b, Update.Status.class);
    assertEquals(Update.Status.ENQUEUED, status);
  }

  // dummy model class to use during tests
  static final class Model implements Serializable {
    private final String id;

    public Model(String id) {
      this.id = id;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Model model = (Model) o;
      return id.equals(model.id);
    }

    @Override public int hashCode() {
      return Objects.hash(id);
    }

    public static Model from(ByteString byteString) {
      Buffer buffer = new Buffer();
      buffer.write(byteString);
      try (ObjectInputStream objectIn = new ObjectInputStream(buffer.inputStream())) {
        return (Model) objectIn.readObject();
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
