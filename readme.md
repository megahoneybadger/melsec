
## General
A java library which implements MELSEC Communication protocol (QnA compatible 3E Binary).

## Project content
Project consists of the following modules:

- **client** - main library you will need to communicate with PLC devices.
- **eqp** - equipment simulator that can be used for tests
- **monitor** - console application which allows to connect to a device and test read/write commands
- **tests** - contains unit and integration tests

## Main concepts
### Bindings
To communicate with a remote device you will use so-called bindings: typed values that reflect and interpret memory blocks. In other words we are not working with a raw memory (words or bits). Available bindings are:
- *PlcU2*	- unsigned short
- *PlcI2*	- signed short
- *PlcU4*	- unsigned integer
- *PlcI4*	- signed integer
- *PlcString* - ASCII string
- *PlcStruct* - structure
- *PlcBit* - boolean

Every binding contains target device code and address. In addition, it may have  optional  value and id.

    var a = new PlcU2( WordDeviceCode.D, 100 );  
    var b = new PlcI4( WordDeviceCode.W, 0x200, 200 );  
    
    // You can specify optional id  to differentiate objects
    var c = new PlcBit( BitDeviceCode.B, 0, true, "GLASS_EVENT_BIT" );

	// Bits and words use different device code families  
    var d = new PlcBit( BitDeviceCode.M, 0 );  
      
    // String must specify a size in BYTES  
    var e = new PlcString( WordDeviceCode.W, 300, 10, "This is a long string", "GLASS_DESCRIPTION" );  
      
    var f = PlcStruct  
      .builder( WordDeviceCode.W, 0x100, "Glass" )  
      .u2( 101 )  
      .u2( 27894 )  
      .u2( 31254 ) 
      // we may have offsets in WORDS   
      .offset( 3 )  
      .i2( ( short )-1456 )  
      .offset( 1 ) 
      .i2( ( short )5567 )  
      .string( 4, "helloworld" )  
      .build();


### Equipment client
Primary object that is responsible for communication is an Equipment Client. To make it work you have to create a proper configuration which sets remote device's IP and port.

    var config = ClientOptions  
        .builder()  
        .address( "127.0.0.1" )  
        .port( 8000 )  
        .loggers(  
        new ConsoleLogger( LogLevel.DEBUG ))  
        .build();  
      
    var client = new EquipmentClient( config ); 

	// This line initiates connection
    client.start();
### IO Requests
To read or/and write binding you will need to pack them into the requests. Every request may contain a chain of IO operations. Then complete you will receive a response with detailed result information for every binding.

*Example #1*

    var config = ClientOptions  
        .builder()  
        .address( "127.0.0.1" )  
        .port( 8000 )  
        .loggers(  
          new ConsoleLogger( LogLevel.DEBUG ))  
        .build();  
      
     var client = new EquipmentClient( config );  
     client.start();  
   
     var a = new PlcU2( WordDeviceCode.D, 100 );  
     var b = new PlcU2( WordDeviceCode.D, 200 );  
   
     var request = IORequest  
        .builder()  
        .read( a, b, new PlcBit( BitDeviceCode.M, 0 ) )  
        .complete( x -> x.items().forEach( y -> Console.print( y ) ) )  
        .build();  

	 // let's establish a connection	   
     Thread.sleep( 500 );  
   
     client.exec( request );  

     new Scanner( System.in ).nextLine();

![](.resources/images/image1.png?raw=true)

*Example #2*

    // omit client creation for brevity
    var b = new PlcU2( WordDeviceCode.D, -500/*bad address*/ );  
    
    var request = IORequest  
        .builder()  
        .read( b )  
        .complete( x -> x.items().forEach( y -> Console.print( y ) ) )  
        .build();  
    
    // let's establish a connection	   
    Thread.sleep( 500 );  
    
    client.exec( request );  

![](.resources/images/image2.png?raw=true)

*Example #3*

    var w1 = new PlcString( WordDeviceCode.D, 0, 10, "Hello word" );  
    var b1 = new PlcBit( BitDeviceCode.B, 500, true );  
    var w2 = new PlcU2( WordDeviceCode.D, 100, "Pressure" );  
    var b2 = new PlcBit( BitDeviceCode.B, 501, false, "Reply Bit" );  
      
    var request = IORequest  
        .builder()  
        .write( w1, b1 )  
        .read( w2 )  
        .write( b2 )  
        .complete( x -> x.items().forEach( y -> Console.print( y ) ) )  
        .build();
      
    // let's establish a connection	   
    Thread.sleep( 500 );  
  
    client.exec( request ); 


![](.resources/images/image3.png?raw=true)

*Example #4*

    var w1 = new PlcString( WordDeviceCode.D, 0, 2000/*to many points*/, "Hello word" );  
    var w2 = new PlcU2( WordDeviceCode.D, 100, "Pressure" );  
      
    var request = IORequest  
        .builder()  
        .write( w1 )  
        .read( w2 )  
        .complete( x -> x.items().forEach( y -> Console.print( y ) ) )  
        .build();  
      
    Thread.sleep( 500 );  
      
    client.exec( request );

![](.resources/images/image4.png?raw=true)

*Example #4*

    `var st = PlcStruct
        .builder( WordDeviceCode.W, 0x100, "Employee" )
        .u2( "Age" )
        .u2( "Weight" )
        .u2( "Salary" )
        .offset( 3 )
        .string( 20, "Name" )
        .build();

    var request = IORequest
        .builder()
        .read( st )
        .complete( x -> x.items().forEach( y -> Console.print( y ) ) )
        .build();

    Thread.sleep( 500 );

    client.exec( request );

![](.resources/images/image5.png?raw=true)
![](.resources/images/image6.png?raw=true)