package melsec.types.events;

public interface IEvent<T extends IEventArgs> {
  void executed( T args );
}
