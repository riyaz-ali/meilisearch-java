package net.riyazali.meili.test;

import net.riyazali.meili.Encoder;
import net.riyazali.meili.Meili;
import net.riyazali.meili.Remote;
import net.riyazali.meili.test.utils.Movie;
import net.riyazali.meili.test.utils.StubResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeiliClientTest {

  // mock for external services
  @Mock Remote remote;
  @Mock Encoder encoder;

  @DisplayName("verify that index is auto-created by default")
  @Test void verifyIndexIsAutoCreated() throws Exception {
    // given
    when(remote.get(any())).thenReturn(StubResponse.notFound());
    when(remote.post(any())).thenReturn(StubResponse.ok(Movie.index()));

    // when
    (new Meili(remote, encoder)).index(Movie.class);

    // then
    verify(remote, times(1)).get(any());
    verify(remote, times(1)).post(any());
  }
}
