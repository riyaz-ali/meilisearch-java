package net.riyazali.meili;

import java.io.IOException;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.riyazali.meili.Remote.Request;
import net.riyazali.meili.Remote.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.riyazali.meili.Precondition.checkNotNull;

/**
 * Update represents result of an asynchronous action that would resolve / complete at a later time
 * in the future.
 *
 * <p>
 * Most of MeiliSearch's endpoint are asynchronous, that is, operations are put in a queue and will
 * be executed in turn (asynchronously). In this case, the server response contains the identifier
 * to track the execution of the operation.
 *
 * @author Riyaz Ali (me@riyazali.net)
 */
@Accessors(fluent = true)
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter public final class Update {

  // update's identifier
  private long updateId;

  // update's status
  private Status status;

  // update's type
  // contains information such as update's name and number
  private Type type;

  // the error message if status == FAILED
  @Nullable private String error;

  // execution duration in seconds
  private double duration;

  // timestamp of when the operation was enqueued
  private ZonedDateTime enqueuedAt;

  // timestamp of when the operation was processed
  private ZonedDateTime processedAt;

  // valid status values
  public enum Status {
    ENQUEUED, PROCESSED, FAILED
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Getter public static final class Type {
    // type of update
    private String name;
    // number / position of update
    private long number;
  }

  // transient internal fields required to poll update status from remote
  // ------ - - - - -

  @ToString.Exclude
  @Setter(AccessLevel.PACKAGE)
  @Nullable private transient Index<?> index;

  @ToString.Exclude
  @Setter(AccessLevel.PACKAGE)
  @Nullable private transient Remote remote;

  @ToString.Exclude
  @Setter(AccessLevel.PACKAGE)
  @Nullable private transient Encoder encoder;

  // copy copies the details from other into self
  private @NotNull Update copy(@NotNull Update other) {
    this.updateId = other.updateId();
    this.status = other.status();
    this.type = other.type();
    this.error = other.error();
    this.duration = other.duration();
    this.enqueuedAt = other.enqueuedAt();
    this.processedAt = other.processedAt();
    return this;
  }

  // Public API
  // -------- - - - - -

  /**
   * Fetch the updated information from the API and check if status is either {@linkplain
   * Status#PROCESSED} or {@linkplain Status#FAILED}
   *
   * @return true if the update is complete
   */
  public boolean done() {
    return status() == Status.PROCESSED || status() == Status.FAILED;
  }

  /**
   * Refresh fetches / updates the update from the remote service
   */
  public @NotNull Update refresh() throws IOException {
    Index<?> index = checkNotNull(this.index);
    Remote remote = checkNotNull(this.remote);
    Encoder encoder = checkNotNull(this.encoder);

    Response response = remote.get(
        Request.builder().path(
            String.format("/indexes/%s/updates/%s", index.uid(), updateId())
        ).build()
    );

    if (response.status() != 200) {
      throw new RuntimeException("error fetching update details");
    }

    return copy(encoder.decode(response.body(), Update.class));
  }
}
