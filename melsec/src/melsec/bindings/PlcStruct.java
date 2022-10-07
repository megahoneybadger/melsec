package melsec.bindings;

import melsec.types.DataType;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;
import melsec.utils.ByteConverter;
import melsec.utils.Copier;


import java.util.ArrayList;
import java.util.List;

import static melsec.utils.UtilityHelper.EMPTY_STRING;

public final class PlcStruct implements IPlcWord {

  //region Class members
  private WordDeviceCode device = WordDeviceCode.W;
  private int address = 0;
  private String id = EMPTY_STRING;
  private List<IPlcWord> items = new ArrayList<>();
  //endregion

  //region Class properties
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

  @Override
  public String id() {
    return id;
  }

  public int count(){
    return items.size();
  }

  public boolean isEmpty(){
    return items.isEmpty();
  }

  public List<IPlcWord> items(){
    return new ArrayList<>( items );
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
  public boolean equals( Object o ){
    if (null == o)
      return false;

    if (this == o)
      return true;

    if (!(o instanceof PlcStruct))
      return false;

    var st = ( PlcStruct )o;

//    if( st.count() != size() )
//      return false;

    for( var i = 0; i < st.count(); ++i ){
      if( !st.items.get( i ).equals( items.get( i ) ) )
        return false;
    }

    return true;
  }
  //endregion

  //region Class internal structs
  public static class Builder{

    //region Class members
    private int baseAddress = 0;
    private int address = 0;
    private String id;
    private WordDeviceCode device = WordDeviceCode.W;
    private ArrayList<IPlcWord> items = new ArrayList<>();
    //endregion

    //region Class public methods
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
      id = ( null == s ) ? EMPTY_STRING : s;

      return this;
    }

    public Builder u2( int value ) {
      return u2( value, EMPTY_STRING );
    }

    public Builder u2( String id ) {
      return u2( 0, id );
    }

    public Builder u2(){
      return u2( 0 );
    }

    public Builder u2( int value, String id ) {
      var o = new PlcU2( device, address, value, id );

      items.add( o );
      address += ByteConverter.getPointsCount( o );

      return this;
    }


    public Builder i2( short value ) {
      return i2( value, EMPTY_STRING );
    }

    public Builder i2( String id ) {
      return i2( ( short )0, id );
    }

    public Builder i2(){
      return i2( ( short )0 );
    }

    public Builder i2( short value, String id ) {
      var o = new PlcI2( device, address, value, id );

      items.add( o );
      address += ByteConverter.getPointsCount( o );

      return this;
    }

    public Builder i4( int value ) {
      return i4( value, EMPTY_STRING );
    }

    public Builder i4( String id ) {
      return i4( 0, id );
    }

    public Builder i4(){
      return i4( 0 );
    }

    public Builder i4( int value, String id ) {
      var o = new PlcI4( device, address, value, id );

      items.add( o );
      address += ByteConverter.getPointsCount( o );

      return this;
    }

    public Builder u4( long value ) {
      return this.u4( value, EMPTY_STRING );
    }

    public Builder u4( String id ) {
     return this.u4( 0l, id );
    }

    public Builder u4( long value, String id ) {
      var o = new PlcU4( device, address, value, id );

      items.add( o );
      address += ByteConverter.getPointsCount( o );

      return this;
    }

    public Builder string( int size ) {
      return string( size, EMPTY_STRING, EMPTY_STRING );
    }

    public Builder string( int size, String value ) {
      return string( size, value, EMPTY_STRING );
    }

    public Builder string( int size, String value, String id ) {
      var o = new PlcString( device, address, size, value, id );

      items.add( o );
      address += ByteConverter.getPointsCount( o );

      return this;
    }

    public Builder offset( int points ){
      address += points;

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

    public PlcStruct with( PlcStruct proto, List<IPlcWord> items ) {
      var s = new PlcStruct();

      s.address = proto.address;
      s.device = proto.device;
      s.id = proto.id;
      s.items = new ArrayList<>( items );

      return s;
    }

    public PlcStruct without(PlcStruct proto){
      var s = new PlcStruct();

      s.address = proto.address;
      s.device = proto.device;
      s.id = proto.id;

      s.items = proto
        .items
        .stream()
        .map( x -> ( IPlcWord )Copier.withoutValue( x ))
        .toList();

      return s;
    }
    //endregion
  }
  //endregion
}
