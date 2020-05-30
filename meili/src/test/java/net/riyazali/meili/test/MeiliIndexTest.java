package net.riyazali.meili.test;

import java.util.Iterator;
import java.util.List;
import net.riyazali.meili.GsonEncoder;
import net.riyazali.meili.Index;
import net.riyazali.meili.Meili;
import net.riyazali.meili.Remote;
import net.riyazali.meili.Update;
import net.riyazali.meili.test.utils.Movie;
import net.riyazali.meili.test.utils.StubResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MeiliIndexTest {

  // mock for external services
  @Mock Remote remote;

  @BeforeEach void setup() throws Exception {
    when(remote.get(any())).thenReturn(StubResponse.ok(Movie.processedUpdate()));
  }

  @DisplayName("verify index checks for required annotations on document class")
  @Test void verifyIndexDoesClassVerification() {
    assertDoesNotThrow(() -> Index.from(Movie.class));
    assertThrows(IllegalArgumentException.class, () -> Index.from(String.class));
  }

  @DisplayName("verify index retrieves a single document by id")
  @Test void verifyDocumentGet() throws Exception {
    // given
    Movie m = Movie.read().get(0);
    when(remote.get(any())).thenReturn(StubResponse.ok(m));

    // when
    Movie r = (new Meili(remote, GsonEncoder.create()).index(Movie.class)).get("0");

    // then
    assertEquals(m, r);
  }

  @DisplayName("verify index can fetch all the documents")
  @Test void verifyDocumentGetAll() throws Exception {
    // given
    List<Movie> movies = Movie.read();
    when(remote.get(any())).thenReturn(StubResponse.ok(movies));

    // when
    Iterable<Movie> r = (new Meili(remote, GsonEncoder.create()).index(Movie.class)).all();

    // then
    Iterator<Movie> it = r.iterator();
    for (Movie movie : movies) {
      assertEquals(movie, it.next());
    }
  }

  @DisplayName("verify index can insert documents")
  @Test void verifyDocumentInsert() throws Exception {
    // given
    when(remote.post(any())).thenReturn(StubResponse.accepted(Movie.enqueuedUpdate()));

    // when
    Update u = (new Meili(remote, GsonEncoder.create()))
        .index(Movie.class).insert(Movie.read().get(0));

    // then
    assertEquals(Update.Status.PROCESSED, u.status()); // will be processed because of setup step
    assertTrue(u.done());
  }

  @DisplayName("verify index can update documents")
  @Test void verifyDocumentUpdate() throws Exception {
    // given
    when(remote.put(any())).thenReturn(StubResponse.accepted(Movie.enqueuedUpdate()));

    // when
    Update u = (new Meili(remote, GsonEncoder.create()))
        .index(Movie.class).update(Movie.read().get(0));

    // then
    assertEquals(Update.Status.PROCESSED, u.status()); // will be processed because of setup step
    assertTrue(u.done());
  }

  @DisplayName("verify index can delete documents")
  @Test void verifyDocumentDelete() throws Exception {
    // given
    // post because batch delete uses a different endpoint
    when(remote.post(any())).thenReturn(StubResponse.accepted(Movie.enqueuedUpdate()));

    // when
    Update u = (new Meili(remote, GsonEncoder.create()))
        .index(Movie.class).delete(Movie.read().get(0));

    // then
    assertEquals(Update.Status.PROCESSED, u.status()); // will be processed because of setup step
    assertTrue(u.done());
  }

  @DisplayName("verify index can clear all documents")
  @Test void verifyDocumentClear() throws Exception {
    // given
    when(remote.delete(any())).thenReturn(StubResponse.accepted(Movie.enqueuedUpdate()));

    // when
    Update u = (new Meili(remote, GsonEncoder.create()))
        .index(Movie.class).clear();

    // then
    assertEquals(Update.Status.PROCESSED, u.status()); // will be processed because of setup step
    assertTrue(u.done());
  }
}
