package melsec;

import melsec.log.IPlcLogger;
import melsec.net.Endpoint;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class Config {

  //region Class members
  private InetAddress address;
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
    private InetAddress address;
    private int port;
    private List<IPlcLogger> appenders;
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
    public Config build() {
      var c = new Config();
      c.address = address;
      c.port = port;

      mapLoggersToLog4J();

      return c;
    }
    //endregion

    //region Class Log4j settings
    private void mapLoggersToLog4J() {
      if( null == appenders || 0 == appenders.size() )
        return;

      logNativeBuilder = ConfigurationBuilderFactory.newConfigurationBuilder();

      appenders.forEach( a -> a.create( logNativeBuilder ) );

      Configurator.initialize((Configuration) logNativeBuilder.build());
    }
    //endregion

  }

  //endregion
}
