package melsec.bindings;

import java.text.MessageFormat;

public enum DeviceCode {

    X( ( byte )0x9C, true, false ),
    Y( ( byte )0x9D, true, false ),
    M( ( byte )0x90, true, true ),
    L( ( byte )0x92, true, true),
    S( ( byte )0x98, true, true ),
    B( ( byte )0xA0, true, false ),
    F( ( byte )0x93, true, true ),
    D( ( byte )0xA8, false, true ),
    W( ( byte )0xB4, false, false ),
    R( ( byte )0xB0, false, true );

    private byte value;
    private boolean isBit;
    private boolean isDec;

    public byte value(){
        return value;
    }

    public boolean isBit(){
        return this.isBit;
    }

    public boolean isWord(){
        return !isBit();
    }

    public int getMaxSectionSize(){
        return isBit() ?  15360 : 960;
    }

    public boolean isDecimalAddress() {
        return isDec;
    }

    public boolean isHexademicalAddress(){
        return !isDecimalAddress();
    }

    public int getAddressRadix(){
        return isDecimalAddress() ? 10 : 16;
    }

    public void ensureBit() throws InvalidDeviceCodeException {
        if( !this.isBit() )
            throw new InvalidDeviceCodeException( this );
    }

    public void ensureWord() throws InvalidDeviceCodeException {
        if( !this.isWord() )
            throw new InvalidDeviceCodeException( this );
    }

    DeviceCode( byte v, boolean isBitNature, boolean isDec ){
        this.value = v;
        this.isBit = isBitNature;
        this.isDec = isDec;
    }

    public String toString(){
        return MessageFormat.format( "{0}{1}{2}",
          super.toString(), isBit ? "b" : "w", isDec ? "d" : "h" );
    }

    public String toStringAddress( int a ){
        return isDecimalAddress() ?
          Integer.toString( a ):
          MessageFormat.format( "0x{0}", Integer.toString( a, 16 ) );
    }

}
