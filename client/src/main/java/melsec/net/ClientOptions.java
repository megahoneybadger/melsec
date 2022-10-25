package melsec.net;

import melsec.types.Endpoint;
import melsec.types.log.IPlcLogger;
import melsec.types.log.LogLevel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class ClientOptions {

  //region Class members
  /**
   *
   */
  private InetAddress address;
  /**
   *
   */
  private int port;
  //endregion

  //region Class properties
  /**
   * @return
   */
  public Endpoint endpoint() {
    return new Endpoint(address, port);
  }
  /**
   * @return
   */
  public static Builder builder() {
    return new Builder();
  }
  //endregion

  //region Class internal structs
  /**
   *
   */
  public final static class Builder {

    //region Class members
    /**
     *
     */
    private InetAddress address;
    /**
     *
     */
    private int port;
    /**
     *
     */
    private List<IPlcLogger> appenders;
    /**
     *
     */
    private ConfigurationBuilder logNativeBuilder;
    //endregion

    //region Class properties
    public Builder address(InetAddress addr) {
      address = addr;

      return this;
    }

    public Builder address(String s) throws UnknownHostException {
      return address(InetAddress.getByName(s));
    }

    public Builder port(int p) {
      port = p;

      return this;
    }

    public Builder loggers( IPlcLogger... arr ){
      appenders = Arrays.asList( arr );
      return this;
    }
    //endregion

    //region Class public methods
    public ClientOptions build() {
      var c = new ClientOptions();
      c.address = address;
      c.port = port;

      mapLoggersToLog4J();

      return c;
    }
    //endregion

    //region Class Log4j settings
    /**
     *
     */
    private void mapLoggersToLog4J() {
//      if( null == appenders || 0 == appenders.size() )
//        return;

      Level.forName( String.valueOf( LogLevel.NET ), 550 );
      Level.forName( String.valueOf( LogLevel.SCAN ), 450 );

      logNativeBuilder = ConfigurationBuilderFactory.newConfigurationBuilder();

      var noCustomAppenders = ( null == appenders || appenders.size() == 0 );

      if( !noCustomAppenders ){
        appenders.forEach( a -> a.create( logNativeBuilder ) );
      }

      Configurator.initialize((Configuration) logNativeBuilder.build());

      if( noCustomAppenders ){
        // Remove default console appender
        var logger  = LoggerContext
          .getContext(false)
          .getRootLogger();

         logger
          .getAppenders()
          .entrySet()
          .forEach( x -> logger.removeAppender( x.getValue() ) );
      }
    }
    //endregion
  }
  //endregion
}
