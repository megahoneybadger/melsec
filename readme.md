## General
This is an open source library which implements MELSEC Communication protocol. 

### Building

To build the library, you will need:

* [JDK 8+](http://www.oracle.com/technetwork/java/javase/downloads/index.html) - Oracle or OpenJDK
* [maven](http://maven.apache.org/) - Version 3 recommended

After installing these tools simply run 'mvn clean package' and find the jar in the target folder.

Other Useful maven lifecycles:

* clean - remove binaries, docs and temporary build files
* compile - compile the library
* test - run all unit tests
* package - package compiled code into a jar