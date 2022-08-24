
import melsec.net.Endpoint;
import melsec.net.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) throws UnknownHostException {

    var logger = LogManager.getLogger("HelloWorld");

    LoggerContext context= (LoggerContext) LogManager.getContext();
    Configuration config= context.getConfiguration();




      //var conn = new Connection();
//
  }


}


