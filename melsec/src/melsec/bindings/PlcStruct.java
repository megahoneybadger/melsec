package melsec.bindings;

import java.text.MessageFormat;
import java.util.ArrayList;

public class PlcStruct extends PlcObject {

  protected ArrayList<PlcObject> items;

  public Iterable<PlcObject> children(){
    return items.stream().toList();
  }

  public static Builder builder(){
    return new Builder();
  }

  @Override
  public PlcDataType type(){
    return PlcDataType.Struct;
  }

  private PlcStruct() throws InvalidDeviceCodeException {
    super( DeviceCode.W, 0 );

  }

  protected void validateDevice( DeviceCode device ) throws InvalidDeviceCodeException {
    device.ensureWord();
  }

  @Override
  public String toString(){
    var sb = new StringBuilder();

    sb.append( MessageFormat.format( "struct [{0}@{1}] {2}",
      device.toStringAddress( address ), device() ) );

    for( var item: items ){
      sb.append( System.lineSeparator() );

//      MessageFormat.format( "[{0}@{1}] [{2}]",
//        super.device, device.toStringAddress( address ) );

    }

    return sb.toString();
  }

  public static class Builder{
    private int baseAddress = 0;
    private int address = 0;

    private DeviceCode device = DeviceCode.W;
    private ArrayList<PlcObject> items = new ArrayList<>();

    public Builder address( int addr ){
      baseAddress = address = addr;

      items = new ArrayList<>();

      return this;
    }

    public Builder device( DeviceCode d )
      throws InvalidDeviceCodeException {

      device = d;
      d.ensureWord();
      items = new ArrayList<>();

      return this;
    }

    public Builder u1( int value )
      throws InvalidNumberException, InvalidDeviceCodeException {

      items.add( new PlcU1( device, address, value ) );

      address += 1;

      return this;
    }

    public Builder u2( int value )
      throws InvalidNumberException, InvalidDeviceCodeException {

      items.add( new PlcU2( device, address, value ) );

      address += 2;

      return this;
    }

    public Builder u4( int value )
      throws InvalidNumberException, InvalidDeviceCodeException {

      items.add( new PlcU4( device, address, value ) );

      address += 4;

      return this;
    }

    public Builder string( int size, String value )
      throws InvalidNumberException, InvalidDeviceCodeException {

      items.add( new PlcString( device, address, size, value ) );

      address += size;

      return this;
    }

    public Builder offset( int size )
      throws InvalidDeviceCodeException {

      items.add( new PlcOffset( device, address, size ) );

      address += size;

      return this;
    }

    public PlcStruct build() throws InvalidDeviceCodeException {
      var s = new PlcStruct();

      s.address = address;
      s.device = device;
      s.items = new ArrayList<>( items );

      return s;
    }
  }
}
