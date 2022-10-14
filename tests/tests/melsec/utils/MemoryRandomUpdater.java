package melsec.utils;

import melsec.bindings.IPlcObject;
import melsec.simulation.EquipmentServer;
import melsec.types.exceptions.InvalidRangeException;

import java.util.ArrayList;
import java.util.List;

public class MemoryRandomUpdater {

  //region Class members
  /**
   *
   */
  private List<IPlcObject> workingSet;
  /**
   *
   */
  private List<IPlcObject> protos;
  /**
   *
   */
  private EquipmentServer server;
  /**
   *
   */
  private ArrayList<List<IPlcObject>> changes;
  //endregion

  //region Class properties
  /**
   *
   * @return
   */
  public List<List<IPlcObject>> getChanges(){
    return this.changes.stream().toList();
  }
  //endregion

  //region Class initialization
  /**
   *
   * @param prototypes
   */
  public MemoryRandomUpdater( EquipmentServer server, List<IPlcObject> prototypes ){
    workingSet = new ArrayList<>( prototypes );
    protos = new ArrayList<>( prototypes );

    changes = new ArrayList<>();

    var protoSorted = new ArrayList<>( prototypes );
    RandomFactory.sort( protoSorted );
    changes.add( protoSorted );

    this.server = server;
  }
  //endregion

  //region Class public methods
  /**
   *
   */
  public void update(){
    try {
      var countToChange = protos.size() / 2;

      var toWrite = RandomFactory.update( workingSet, countToChange );

      server.write( toWrite );

      workingSet = RandomFactory.merge( workingSet, toWrite );

      changes.add( toWrite );
    }
    catch( InvalidRangeException e ) {

    }
  }
  //endregion
}
