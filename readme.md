
## General
A java library which implements MELSEC Communication protocol (QnA compatible 3E Binary).

## Project content
Project consists of the following modules:

- **client** - the main library you need to communicate with PLC device
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
Primary object responsible for communication is an Equipment Client. To make it work you have to create a proper configuration which sets remote device's IP and port.

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
To read and write bindings you will need to pack them into the requests. Every request may contain a chain of IO operations. Then complete you will receive a response with detailed result information for every binding.

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

*Example #5*

    var st = PlcStruct
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

### Events
You may subscribe to a number of events.
    
    var config = ClientOptions
        .builder()
        .address( "127.0.0.1" )
        .port( 8000 )
        .loggers(
            new ConsoleLogger( LogLevel.DEBUG ))
        .build();

    var client = new EquipmentClient( config );

    client.events().subscribe( ( IClientStartedEvent ) x -> System.out.println( "client started" ) );
    client.events().subscribe( ( IClientStoppedEvent ) x -> System.out.println( "client stopped" ) );

    client.events().subscribe( ( IConnectionConnectingEvent ) x -> System.out.println( "connecting" ) );
    client.events().subscribe( ( IConnectionEstablishedEvent ) x -> System.out.println( "connected" ) );
    client.events().subscribe( ( IConnectionDisposedEvent ) x -> System.out.println( "disconnected" ) );

    client.start();

Hence, if you want to guarantee that you are sending requests only after establishing a connection, you can use this code snippet:

    var client = new EquipmentClient( config );

    client.events().subscribe( ( IConnectionEstablishedEvent ) x -> {
      client.exec( IORequest
        .builder()
        .read( new PlcBit( BitDeviceCode.M, 0 ) )
        .complete( y -> y.items().forEach( z -> Console.print( z ) ) )
        .build());
    } );

    client.start();

### Equipment Scanner
In addition to discrete IO request we can organize continuous data reading:
1. Define interested regions within a memory
2. Specify timeout between consecutive reads
3. Provide bindings for specified regions
4. When binding changes you will be notified via event

*Example #1*

    var config = ClientOptions
        .builder()
        .address( "127.0.0.1" )
        .port( 8000 )
        .loggers(
            new ConsoleLogger( LogLevel.SCAN ))
        .build();

    var client = new EquipmentClient( config );
    client.start();

    Thread.sleep( 500 );

     EquipmentScanner
        .builder()
        .changed( x -> x.changes().forEach( y -> System.out.println( y ) ) )
        .binding(
            new PlcBit( BitDeviceCode.M, 10, "GlassRecvEventBit" ),
            new PlcBit( BitDeviceCode.M, 12, "AlarmSetBit" ),
            new PlcBit( BitDeviceCode.M, 27, "FDCSetBit " ))
        .region( BitDeviceCode.M, 0, 30000 /*scan M0-M29999*/ )
        .region( WordDeviceCode.D, 0, 2000 /*scan M0-M1999*/ )
        .region( BitDeviceCode.M, 0, 50000 )
        .region( WordDeviceCode.W, 0, 300 /*scan Wx0-Wx12b*/ )
        .timeout( 20 )
        .build( client );

    new Scanner( System.in ).nextLine();

*Example #2*

    EquipmentScanner
        .builder()
        .changed( x -> x.changes().forEach( y -> System.out.println( y ) ) )
        // read bindings from the file: we can pass thousands of items here 
        .binding( BindingDeserializer.read( "bits.xml" ) )
        .region( BitDeviceCode.M, 0, 1000 )
        .build( client );

This is a sample xml file containing bindings:

    <Bindings>
    
        <Bit device="M" address="200" />
        
        <I2 device="w" address="0x2000" value="-4568" id="temperature" />
        
        <I4 device="d" address="20" id="pressure" />	
        
        <String device="w" address="300" size="10" value="hello-world" />
        
        <Struct id="HOST_GLASS_DATA_REQUEST_STRUCT" device="d" address="500">
            <U2 id="CST_SEQ_NO" />
            <U2 id="CHIP_POSITION" />
            <U2 id="PRODUCT_TYPE" />
            <U2 id="SUBSTRATE_TYPE" />
            <U2 id="CIM_MODE" />
            <U2 id="CST_OPERATION_MODE" />
            <U2 id="JOB_TYPE" />
            <U2 id="DUMMY_TYPE" />
            <U2 id="JOB_JUDGE" />
            <U2 id="JOB_GRADE" />
            <String id="PPID" size="25" />
            <String id="GLASS_CHIP_ID" size="8" />
        </Struct>
    
    </Bindings>