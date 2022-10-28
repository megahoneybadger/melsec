
## General
A java library which implements MELSEC Communication protocol (QnA compatible 3E Binary).

Releases are available via Maven central. To add a dependency to melsec-client, use:

    <dependency>
        <groupId>io.github.megahoneybadger</groupId>
        <artifactId>melsec-client</artifactId>
        <version>0.1</version>
    </dependency>

## Content
Project consists of the following modules:

- **client** - the main library you need to communicate with PLC device
- **eqp** - equipment simulator that can be used for tests
- **monitor** - console application which allows to connect to a device and test read/write commands
- **tests** - contains unit and integration tests

## How to build
Firstly get the sources:

    git clone https://github.com/megahoneybadger/melsec.git
    cd melsec

Secondly compile the project (you will need maven):

    mvn compile

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
        .complete( x -> x.items().forEach( y -> System.out.println( y ) ) )  
        .build();  

	 // let's establish a connection	   
     Thread.sleep( 500 );  
   
     client.exec( request );  

     new Scanner( System.in ).nextLine();

*Output #1*

    [melsec][INFO ][10:45:51.051] Client started
    [melsec][DEBUG][10:45:51.051] Connection#234 trying to connect to 127.0.0.1:8000
    [melsec][INFO ][10:45:51.051] Connection#234 established
    [melsec][DEBUG][10:45:52.052] Enqueue mbbr#762 [2w|1b]
    [melsec][DEBUG][10:45:52.052] Process mbbr#762 [2w|1b]
    [melsec][DEBUG][10:45:52.052] Complete mbbr#762 [2w|1b]
    Read [OK] U2 [D100]  125
    Read [OK] U2 [D200] 231
    Read [OK] bit [M0] 1
        

*Example #2*

    // omit client creation for brevity...
    
    var request = IORequest  
        .builder()  
        .read( new PlcU2( WordDeviceCode.D, -500/*bad address*/ ) )  
        .complete( x -> x.items().forEach( y -> System.out.println( y ) ) )  
        .build();  
    
    // let's establish a connection	   
    Thread.sleep( 500 );  
    
    client.exec( request );  

*Output #2*

    [melsec][INFO ][10:53:06.006] Client started
    [melsec][DEBUG][10:53:06.006] Connection#816 trying to connect to 127.0.0.1:8000
    [melsec][INFO ][10:53:06.006] Connection#816 established
    [melsec][DEBUG][10:53:06.006] Enqueue mbbr#50b [1w]
    [melsec][DEBUG][10:53:06.006] Process mbbr#50b [1w]
    [melsec][ERROR][10:53:06.006] Failed to complete mbbr#50b [1w]. Failed to encode mbbr#50b [1w]. Invalid device address
    Read [NG] U2 [D-500] -> Failed to encode mbbr#50b [1w]. Invalid device address



*Example #3*

    // omit client creation for brevity...

    var w1 = new PlcString( WordDeviceCode.D, 0, 10, "Hello word" );  
    var b1 = new PlcBit( BitDeviceCode.B, 500, true );  
    var w2 = new PlcU2( WordDeviceCode.D, 100, "Pressure" );  
    var b2 = new PlcBit( BitDeviceCode.B, 501, false, "Reply Bit" );  
      
    var request = IORequest  
        .builder()  
        // we want the client to stick to
        // the order: write -> read -> write
        .write( w1, b1 )  
        .read( w2 )  
        .write( b2 )  
        .complete( x -> x.items().forEach( y -> System.out.println( y ) ) )  
        .build();
      
    // let's establish a connection	   
    Thread.sleep( 500 );  
  
    client.exec( request ); 

*Output #3*

    [melsec][INFO ][10:56:05.005] Client started
    [melsec][DEBUG][10:56:05.005] Connection#635 trying to connect to 127.0.0.1:8000
    [melsec][INFO ][10:56:05.005] Connection#635 established
    [melsec][DEBUG][10:56:06.006] Enqueue mbbw#2d0 [1w], rw#261 [1], mbbr#9d7 [1w], rw#b53 [1]
    [melsec][DEBUG][10:56:06.006] Process mbbw#2d0 [1w]
    [melsec][DEBUG][10:56:06.006] Complete mbbw#2d0 [1w]
    [melsec][DEBUG][10:56:06.006] Process rw#261 [1]
    [melsec][DEBUG][10:56:06.006] Complete rw#261 [1]
    [melsec][DEBUG][10:56:06.006] Process mbbr#9d7 [1w]
    [melsec][DEBUG][10:56:06.006] Complete mbbr#9d7 [1w]
    [melsec][DEBUG][10:56:06.006] Process rw#b53 [1]
    [melsec][DEBUG][10:56:06.006] Complete rw#b53 [1]
    Write [OK] A10 [D0] Hello word
    Write [OK] bit [Bx01F4] 1
    Read [OK] U2 [D100 Pressure] 2500
    Write [OK] bit [Bx01F5 Reply Bit] 0

*Example #4*

    var w1 = new PlcString( WordDeviceCode.D, 0, 2000/*to many points*/, "Hello word" );  
    var w2 = new PlcU2( WordDeviceCode.D, 100, "Pressure" );  
      
    var request = IORequest  
        .builder()  
        .write( w1 )  
        .read( w2 )  
        .complete( x -> x.items().forEach( y -> System.out.println( y ) ) )  
        .build();  
      
    Thread.sleep( 500 );  
      
    client.exec( request );

*Output #4*

    [melsec][INFO ][10:58:59.059] Client started
    [melsec][DEBUG][10:58:59.059] Connection#816 trying to connect to 127.0.0.1:8000
    [melsec][INFO ][10:58:59.059] Connection#816 established
    [melsec][DEBUG][10:58:59.059] Enqueue mbbw#5ca [1w], mbbr#9d9 [1w]
    [melsec][DEBUG][10:58:59.059] Process mbbw#5ca [1w]
    [melsec][ERROR][10:58:59.059] Failed to complete mbbw#5ca [1w]. Failed to encode mbbw#5ca [1w]. Too many points
    [melsec][DEBUG][10:58:59.059] Process mbbr#9d9 [1w]
    [melsec][DEBUG][10:58:59.059] Complete mbbr#9d9 [1w]
    Write [NG] A2000 [D0] Hello word -> Failed to encode mbbw#5ca [1w]. Too many points
    Read [OK] U2 [D100 Pressure]

*Example #5*

    var st = PlcStruct
        .builder( WordDeviceCode.W, 0x100, "Employee" )
        .u2( "Age" )
        .u2( "Weight" )
        .u2( "Salary" )
        // pay attention to the offset between fields
        .offset( 3 )
        .string( 20, "Name" )
        .build();

    var request = IORequest
        .builder()
        .read( st )
        .complete( x -> x.items().forEach( y -> System.out.println( y ) ) )
        .build();

    Thread.sleep( 500 );

    client.exec( request );

*Output #5*

    [melsec][INFO ][11:00:37.037] Client started
    [melsec][DEBUG][11:00:38.038] Connection#402 trying to connect to 127.0.0.1:8000
    [melsec][INFO ][11:00:38.038] Connection#402 established
    [melsec][DEBUG][11:00:38.038] Enqueue mbbr#69b [1w]
    [melsec][DEBUG][11:00:38.038] Process mbbr#69b [1w]
    [melsec][DEBUG][11:00:38.038] Complete mbbr#69b [1w]
    Read [OK] struct [Wx0100 Employee]
    	U2 [Wx0100 Age] 25
    	U2 [Wx0101 Weight] 75
    	U2 [Wx0102 Salary] 1000
    	A20 [Wx0106] John Smith

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

## Monitor
Project contains a monitor tool which can be used for debug purposes.
Run the following command from the root folder:

`mvn -pl monitor compile exec:java -Dexec.mainClass="Main" -Dexec.arguments="127.0.0.1:8000"`

It will try to connect to device at 127.0.0.1:8000. If connected you can 
send read and write commands:

    [melsec][INFO ][16:29:09.009] Client started
    [melsec][INFO ][16:29:10.010] Connection#129 established
    read b100
    Read [OK] bit [Bx0100] 1
    write b100 true
    Write [OK] bit [Bx0100] 1
    read w10 a10, w50 i2, w70 u4
    Read [OK] A10 [Wx0010] ring
    Read [OK] I2 [Wx0050]
    Read [OK] U4 [Wx0070]
    write b100 true, b200 false, d150 u2 456, d200 i4 -6758
    Write [OK] bit [Bx0100] 1
    Write [OK] bit [Bx0200] 0
    Write [OK] U2 [D150] 456
    Write [OK] I4 [D200] -6758

When reading data specify device, address and binding.
In case of writing also specify value you want to write.

## Equipment simulator
In case if you do not have a physical PLC device you can play around 
with equipment simulator.
Run two terminals from the root folder.

The first one will be a server:

`mvn -pl eqp compile exec:java -Dexec.mainClass="Main" -Dexec.arguments="127.0.0.1:8000"`

The second one will be a server:

`mvn -pl monitor compile exec:java -Dexec.mainClass="Main" -Dexec.arguments="127.0.0.1:8000"`


For the sake of simplicity let's set a few values on the server
first and after that try to read  it from the client (you can read/write data on client/server in any order). 

Server terminal:

    [eqp.sim][DEBUG][10:58:31.031] Equipment is listening at 127.0.0.1:8000
    [eqp.sim][DEBUG][10:58:52.052] Client#660 connected
    write b100 true
    write w200 a100 hello-world-long-string

Client terminal:

    [melsec][INFO ][10:59:10.010] Client started  
    [melsec][INFO ][10:59:10.010] Connection#770 established  
    read b100  
    Read [OK] bit [Bx0100] 1  
    read b101  
    Read [OK] bit [Bx0101] 0  
    read w200 a10  
    Read [OK] A10 [Wx0200] hello-worl  
    