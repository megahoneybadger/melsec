package melsec.bindings;

import types.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public final class PlcStruct implements IPlcWord {

  private WordDeviceCode device = WordDeviceCode.W;
  private int address = 0;
  private String id = EMPTY_STRING;
  private List<IPlcWord> items = new ArrayList<>();

  @Override
  public DataType type() {
    return DataType.Struct;
  }

  @Override
  public IDeviceCode device() {
    return device;
  }

  @Override
  public int address() {
    return address;
  }

  public int count(){
    return items.size();
  }

  @Override
  public int size(){
    var last = items.get( items.size() - 1 );

    return last.address() + last.size() - address;
  }

  public boolean isEmpty(){
    return items.isEmpty();
  }

  @Override
  public String id() {
    return null;
  }

  public Iterable<IPlcWord> items(){
    return items.stream().toList();
  }

  public static Builder builder(){
    return builder( WordDeviceCode.W );
  }

  public static Builder builder( WordDeviceCode device ){
    return builder( device, 0 );
  }

  public static Builder builder( WordDeviceCode device, int address ){
    return builder( device, address, EMPTY_STRING );
  }

  public static Builder builder( WordDeviceCode device, int address, String id ){
    return new Builder()
      .device( device )
      .address( address )
      .id( id );
  }

  @Override
  public String toString(){
    var sb = new StringBuilder();

    var id = this.id.isEmpty() ? EMPTY_STRING : " " + this.id;

    sb.append( MessageFormat.format( "struct [{0}@{1}{2}]",
      device(), device.toStringAddress( address ), id ) );

    for( var item: items ){
      sb.append( System.lineSeparator() + "\t" );

      id = item.id().isEmpty() ? EMPTY_STRING : " " + item.id();

      if( item instanceof IPlcNumber<?> n){
        sb.append( MessageFormat.format( "[{0}@{1}{3}] {2}",
          n.type(), n.device().toStringAddress( n.address() ), n.value(), id ));
      } else if( item instanceof PlcString s ){
        sb.append( MessageFormat.format( "[A{0}@{1}{3}] {2}",
          s.size(), s.device().toStringAddress( s.address() ), s.value(), id ));
      }
    }

    return sb.toString();
  }

  @Override
  public boolean equals( Object o ){
    if (null == o)
      return false;

    if (this == o)
      return true;

    if (!(o instanceof PlcStruct))
      return false;

    var st = ( PlcStruct )o;

    ((PlcStruct) o).id = "fuck u";

    if( st.count() != size() )
      return false;

    for( var i = 0; i < st.count(); ++i ){
      if( !st.items.get( i ).equals( items.get( i ) ) )
        return false;
    }

    return true;
  }

  public static class Builder{
    private int baseAddress = 0;
    private int address = 0;
    private String id;

    private WordDeviceCode device = WordDeviceCode.W;
    private ArrayList<IPlcWord> items = new ArrayList<>();

    public Builder address( int addr ){
      baseAddress = address = addr;

      items = new ArrayList<>();

      return this;
    }

    public Builder device( WordDeviceCode d ) {
      device = d;

      items = new ArrayList<>();
      address = baseAddress;

      return this;
    }

    public Builder id( String s ) {
      id = s;

      return this;
    }

    public Builder u1( int value ) {
      return this.u1( value, EMPTY_STRING );
    }

    public Builder u1( int value, String id ) {
      items.add( new PlcU1( device, address, value, id ) );
      address += 1;

      return this;
    }

    public Builder u2( int value ) {
      return u2( value, EMPTY_STRING );
    }

    public Builder u2( int value, String id ) {
      items.add( new PlcU2( device, address, value, id ) );
      address += 2;

      return this;
    }

    public Builder u4( long value ) {
      return this.u4( value, EMPTY_STRING );
    }

    public Builder u4( long value, String id ) {
      items.add( new PlcU4( device, address, value, id ) );
      address += 4;

      return this;
    }

    public Builder f4( float value ) {
      return this.f4( value, EMPTY_STRING );
    }

    public Builder f4( float value, String id ) {
      items.add( new PlcF4( device, address, value, id ) );
      address += 4;

      return this;
    }

    public Builder f8( double value ) {
      return this.f8( value, EMPTY_STRING );
    }

    public Builder f8( double value, String id ) {
      items.add( new PlcF8( device, address, value, id ) );
      address += 8;

      return this;
    }

    public Builder string( int size, String value ) {
      return string( size, value, EMPTY_STRING );
    }

    public Builder string( int size, String value, String id ) {
      items.add( new PlcString( device, address, size, value, id ) );
      address += size;

      return this;
    }

    public Builder offset( int size ){
      address += size;

      return this;
    }

    public PlcStruct build() {
      var s = new PlcStruct();

      s.address = baseAddress;
      s.device = device;
      s.id = id;
      s.items = new ArrayList<>( items );

      return s;
    }
  }
}
