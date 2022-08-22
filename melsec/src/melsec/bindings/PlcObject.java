package melsec.bindings;

abstract class PlcObject {

  protected int address;
  protected DeviceCode device;

  protected String name;
  protected int localNo;

  public String name(){
    return name;
  }

  public int address(){
    return address;
  }

  public DeviceCode device(){
    return device;
  }

  public int localNo(){
    return localNo;
  }

  public abstract PlcDataType type();

  protected PlcObject( DeviceCode d, int addr ) throws InvalidDeviceCodeException {
    device = d;
    address = addr;
    name = "";
    localNo = 1;

    validateDevice( d );
    validateAddress( address );
  }

  protected abstract void validateDevice(DeviceCode device ) throws InvalidDeviceCodeException;


  protected void validateAddress(int address) {

  }

  public boolean equals( Object o ){
    if( null == o )
      return false;

    if( this == o )
      return true;

    if( !( o instanceof PlcObject) )
      return false;

    PlcObject p = ( PlcObject )o;

//    if( !id.equalsIgnoreCase( p.id() ) )
//      return false;

    if( !device.equals( p.device() ) )
      return false;

    if( address != p.address() )
      return false;

//    if( localNo != p.localNo() )
//      return false;

//    if( name.equalsIgnoreCase( p.name ) )
//      return false;

    return true;
  }
}
