package melsec.exceptions;

import melsec.types.ErrorCode;
import melsec.types.IDeviceCode;

import java.text.MessageFormat;

public class InvalidRangeException extends BaseException {
  public InvalidRangeException( IDeviceCode device, int l, int r, int max ){

    super( ErrorCode.InvalidRange, MessageFormat.format(
      "Requested range [{1}-{2}] violates {0} acceptable range [0-{3}]",
      device,
      device.toStringAddress( l ),
      device.toStringAddress( r ),
      device.toStringAddress( max ) ) );

  }
}
