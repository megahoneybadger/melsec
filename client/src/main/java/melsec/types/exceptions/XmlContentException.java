package melsec.types.exceptions;

import melsec.bindings.files.BindingReader;
import melsec.types.ErrorCode;

import java.io.File;
import java.text.MessageFormat;

public class XmlContentException extends BaseException {
  public XmlContentException( BindingReader r, String message, Object ...args ){

    super( ErrorCode.InvalidDeserialization,
      MessageFormat.format( "File [{0}], line {1}. ",
        new File( r.getPath()).getAbsolutePath(),
        r.getNativeReader().getLocation().getLineNumber()) +
      MessageFormat.format( message, args ) );

  }
}
