package net.riyazali.meili.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

// Utility class containing methods to deal with reflection
public final class ReflectionUtils {
  private ReflectionUtils() {
    throw new AssertionError("no instances allowed");
  }

  public static @NotNull Type getParameterized(Type owner, Type raw, Type... arguments) {
    return new ParameterizedTypeImpl(owner, raw, arguments);
  }

  // a very basic implementation of ParameterizedType
  // sufficient for our use case
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static class ParameterizedTypeImpl implements ParameterizedType {
    // ownerType represents the enclosing class type
    // eg. X<Y>.A<B> => X<Y> would be the owner type for A<B>
    private final Type ownerType;

    // raw represents the top-level raw type
    // eg. A<B> => A would be the raw type
    private final Type raw;

    // typeArguments represent the generic type arguments passed to the type
    // eg. A<B> => B would be the type argument
    private final Type[] typeArguments;

    @Override public Type[] getActualTypeArguments() {
      return typeArguments.clone();
    }

    @Override public Type getRawType() {
      return raw;
    }

    @Override public Type getOwnerType() {
      return ownerType;
    }
  }
}
