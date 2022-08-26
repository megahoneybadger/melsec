package melsec.config;

import melsec.net.Endpoint;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PlcConfig {
  private InetAddress address;
  private int port;

  public Endpoint endpoint(){
    return new Endpoint( address, port );
  }

  public static Builder builder(){
    return new Builder();
  }

  public static class Builder{
    private InetAddress address;
    private int port;
    private ConfigurationBuilder logConfigBuilder;

    public Builder address( InetAddress addr ){
      address = addr;

      return this;
    }

    public Builder address( String s ) throws UnknownHostException {
      return address( InetAddress.getByName( s ) );
    }

    public Builder port( int p ){
      port = p;

      return this;
    }

    public PlcConfig build(){
      var c = new PlcConfig();
      c.address = address;
      c.port = port;
      // todo: logger

      configureLog4J();

      return c;
    }

    private void configureLog4J() {
      logConfigBuilder = ConfigurationBuilderFactory.newConfigurationBuilder();

      createAppenders();

      var logger = logConfigBuilder
        .newRootLogger( Level.ALL )
        .add( logConfigBuilder.newAppenderRef( "stdout" ));
//
//      var logger2 = builder
//        .newLogger( "l2", Level.DEBUG )
//        .add( builder.newAppenderRef( "stdout" ));
//
//      var logger3 = builder
//        .newLogger( "l1", Level.ALL )
//        .add( builder.newAppenderRef( "rolling" ));/*
//      .addAttribute("additivity", false );*/
//
//
//      // configure the root logger
      logConfigBuilder.add( logger );
//      builder.add( logger2 );
//      builder.add( logger3 );
//
//      // apply the configuration
      Configurator.initialize( (Configuration) logConfigBuilder.build());
    }

    private void createAppenders(){
      var layout = logConfigBuilder
        .newLayout( PatternLayout.class.getSimpleName())
        .addAttribute( "pattern", "[%d{HH:mm:ss.sss}@%logger] %-5level %msg%n" );

      createConsoleAppender( layout );

      createRollingFileAppender( layout );
    }

    private void createConsoleAppender( LayoutComponentBuilder layout ){
      var appender =  logConfigBuilder
        .newAppender( "stdout", "Console" )
        .add( layout );

//      var filter = builder
//        .newFilter( "RegexFilter", Filter.Result.DENY, Filter.Result.NEUTRAL)
//        .addAttribute( "regex", ".*test.*");
//
//      appender.add( filter );

      // configure a console appender
      logConfigBuilder.add( appender );
    }

    private void createRollingFileAppender( LayoutComponentBuilder layout ){
//      var triggeringPolicy = builder
//        .newComponent( "Policies" )
//        .addComponent(
//          builder
//            .newComponent( "CronTriggeringPolicy" )
//            .addAttribute( "schedule", "0 0 0 * * ?"))
//        .addComponent(
//          builder
//            .newComponent( "SizeBasedTriggeringPolicy" )
//            .addAttribute("size", "100M" ));
//
//      var appender = builder
//        .newAppender( "rolling", "RollingFile" )
//        .addAttribute( "fileName", "${baseDir}/app.log")
//        .addAttribute( "filePattern", "${baseDir}/$${date:yyyy-MM}/app-%d{yyyyMMdd}.log.gz")
//        .add( layout )
//        .addComponent( triggeringPolicy );

      // configure a console appender
      //logConfigBuilder.add( appender );
    }

  }

}
