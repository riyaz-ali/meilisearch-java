package net.riyazali.meili;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Handy utility class to provide Guava-like precondition checks
final class Precondition {

  public static <T> @NotNull T checkNotNull(@Nullable T instance) {
    if (instance == null) {
      throw new NullPointerException();
    }
    return instance;
  }

  public static void checkArgument(boolean expression) {
    if (!expression) {
      throw new IllegalArgumentException();
    }
  }

  public static void checkState(boolean expression) {
    if (!expression) {
      throw new IllegalStateException();
    }
  }
}
