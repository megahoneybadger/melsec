package melsec.bindings;

public interface IPlcNumber<T extends  Number> extends IPlcWord {
  T value();
}
