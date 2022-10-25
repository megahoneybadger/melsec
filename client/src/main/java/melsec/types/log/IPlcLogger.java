package melsec.types.log;

import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;

public interface IPlcLogger {
  LogLevel level();

  void create( ConfigurationBuilder builder );
}
