package melsec.types.log;

import melsec.utils.UtilityHelper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.text.MessageFormat;

public record ConsoleLogger( LogLevel level ) implements IPlcLogger {
  public final static String NAME = "stdout";

  public ConsoleLogger( String id ){
    this( LogLevel.DEBUG );
  }

  public void create( ConfigurationBuilder builder, String id ){
    id = UtilityHelper.coalesce( id, "melsec" );
    id = MessageFormat.format( "[{0}]", id );

    var layout = builder
      .newLayout( PatternLayout.class.getSimpleName() )
      .addAttribute( "disableAnsi", false )
      .addAttribute( "pattern", id + "%highlight{[%-5level][%d{HH:mm:ss.sss}] %msg%n}{FATAL=red, ERROR=red, WARN=yellow bold, INFO=Normal, DEBUG=Normal}" );

    // Fix for win10: REG ADD HKCU\CONSOLE /f /v VirtualTerminalLevel /t REG_DWORD /d 1

    var appender = builder
      .newAppender( NAME, "Console" )
      .add(layout);

    builder.add(appender);

    var logger = builder
      .newRootLogger( getNativeLevel())
      .add( builder.newAppenderRef( NAME ));

    builder.add(logger);
  }

  private Level getNativeLevel(){
    return switch ( this.level ){
      case INFO -> Level.INFO;
      case DEBUG -> Level.DEBUG;
      case ERROR -> Level.ERROR;
      case NET -> Level.getLevel( String.valueOf( LogLevel.NET ) );
      case SCAN -> Level.getLevel( String.valueOf( LogLevel.SCAN ) );
      default -> Level.OFF;
    };
  }

  @Override
  public void create( ConfigurationBuilder builder ){
    create( builder, null );

  }
}
