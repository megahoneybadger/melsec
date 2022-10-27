package melsec.simulation;

import melsec.net.ClientOptions;
import melsec.types.Endpoint;
import melsec.types.log.ConsoleLogger;
import melsec.types.log.LogLevel;
import melsec.simulation.events.IClientDisconnectedEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousSocketChannel;

public record ServerOptions(Memory memory,
                            InetAddress address,
                            int port,
                            boolean useLogger) {

  public static Builder builder() {
    return new Builder();
  }

  public ChannelParams toChannelParams(AsynchronousSocketChannel s,
                                       IClientDisconnectedEvent handler) {
    return new ChannelParams(memory, s, handler);
  }

  public static class Builder {
    //region Class members
    /**
     *
     */
    private Memory memory = new Memory();
    /**
     *
     */
    private InetAddress address;
    /**
     *
     */
    private int port = 8000;
    /**
     *
     */
    private boolean useLogger = true;
    /**
     *
     */
    private ConfigurationBuilder logNativeBuilder;
    //endregion

    public Builder port(int p) {
      port = p;
      return this;
    }

    public Builder address( InetAddress addr) {
      address = addr;

      return this;
    }

    public Builder address( String s) throws UnknownHostException {
      return address(InetAddress.getByName(s));
    }

    public Builder endpoint( Endpoint ep){
      address( ep.address() );
      port( ep.port() );

      return this;
    }

    public Builder memory(Memory m) {
      memory = m;
      return this;
    }

    public Builder logger(boolean logger) {
      useLogger = logger;
      return this;
    }

    //region Class Log4j settings
    /**
     *
     */
    private void initLog4J() {
      if(!useLogger)
        return;

      logNativeBuilder = ConfigurationBuilderFactory.newConfigurationBuilder();

      var appender = new ConsoleLogger(LogLevel.DEBUG);
      appender.create(logNativeBuilder, "eqp.sim");

      Configurator.initialize((Configuration) logNativeBuilder.build());
    }
    //endregion

    public ServerOptions build() {
      initLog4J();

      return new ServerOptions(memory, address, port, useLogger);
    }
  }
}
