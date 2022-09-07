package melsec.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.layout.PatternLayout;

public record ConsoleLogger(LogLevel level ) implements IPlcLogger {
  public final static String NAME = "stdout";

  public ConsoleLogger(){
    this( LogLevel.DEBUG );
  }

  @Override
  public void create( ConfigurationBuilder builder ){
    var layout = builder
      .newLayout( PatternLayout.class.getSimpleName() )
      .addAttribute( "disableAnsi", false )
      .addAttribute( "pattern", "[melsec]%highlight{[%-5level][%d{HH:mm:ss.sss}] %msg%n}{FATAL=red, ERROR=red, WARN=yellow bold, INFO=Normal, DEBUG=Normal}");

    // Fix for win10: REG ADD HKCU\CONSOLE /f /v VirtualTerminalLevel /t REG_DWORD /d 1

    var appender = builder
      .newAppender( NAME, "Console")
      .add(layout);

    builder.add(appender);

    var logger = builder
      .newRootLogger(getNativeLevel())
      .add( builder.newAppenderRef( NAME ));

    builder.add(logger);
  }

  private Level getNativeLevel(){
    return switch ( this.level ){
      case INFO -> Level.INFO;
      case DEBUG -> Level.DEBUG;
      case ERROR -> Level.ERROR;
      default -> Level.OFF;
    };
  }
}
