package melsec.types.log;

import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;

public record FileLogger(LogLevel level, String folder ) implements IPlcLogger {

  @Override
  public void create( ConfigurationBuilder builder ){
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
