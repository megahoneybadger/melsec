package melsec.events;

public interface IEvent<T extends IEventArgs> {
  void executed( T args );
}
