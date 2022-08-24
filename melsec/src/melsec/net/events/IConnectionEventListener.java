package melsec.net.events;

public interface IConnectionEventListener {

  default void initialized( ConnectionEventArgs args ){

  };

  default void connecting( ConnectionEventArgs args ){

  };

  default void established( ConnectionEventArgs args ){

  };

  default void dropped( ConnectionEventArgs args ){

  };

  default void disposed( ConnectionEventArgs args ){

  };
}
