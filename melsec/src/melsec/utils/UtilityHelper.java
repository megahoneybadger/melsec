package melsec.utils;

import java.util.List;
import java.util.stream.StreamSupport;

public class UtilityHelper {
  public static <T> List<T> toList(final Iterable<T> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false)
      .toList();
  }
}
