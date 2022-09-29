package melsec.scanner;

import melsec.net.EquipmentClient;
import melsec.bindings.PlcBinary;
import melsec.types.io.IORequest;

public class EquipmentScanner {

  //region Class members
  /**
   *
   */
  private EquipmentClient client;
  /**
   *
   */
  private Object syncObject;
  /**
   *
   */
  private boolean run;
  /**
   *
   */
  private IORequest request;
  //endregion

  //region Class initialization
  /**
   *
   * @param client
   * @param regs
   */
  public EquipmentScanner( EquipmentClient client, PlcBinary...regs ){
    this.client = client;

    syncObject = new Object();

    request = IORequest
      .builder()
      .read( regs )
      //.complete( recv results in scanner )
      .build();

    //var units = request.toUnits();
  }
  //endregion

  //region Class public methods
  /**
   *
   */
  public void start(){
    synchronized( syncObject  ) {
      if( run )
        return;

      run = true;

      client.exec( request );
    }
  }
  /**
   *
   */
  public void stop(){
    synchronized( syncObject ){
      if( !run )
        return;

      run = false;
    }
  }


  //endregion
}
