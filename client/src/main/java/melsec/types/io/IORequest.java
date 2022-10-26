package melsec.types.io;

import melsec.bindings.IPlcObject;
import melsec.types.events.IOCompleteEvent;
import melsec.types.exceptions.ConnectionNotEstablishedException;
import melsec.types.exceptions.InvalidRangeException;
import melsec.utils.UtilityHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IORequest {

  //region Class members
  private String id;
  /**
   *
   */
  private List<IORequestItem> list = new ArrayList<>();
  /**
   *
   */
  private IOCompleteEvent completeHandler;
  //endregion

  //region Class properties
  /**
   *
   * @return
   */
  public IOCompleteEvent completeHandler(){
    return completeHandler;
  }
  /**
   *
   * @return
   */
  public Iterable<IORequestItem> items(){
    return new ArrayList<>( list );
  }
  /**
   *
   * @return
   */
  public static Builder builder() {
    return new Builder();
  }
  /**
   *
   * @return
   */
  public String id() {
    return id;
  }
  //endregion

  //region Class utility methods
  /**
   *
   * @param t
   * @throws InvalidRangeException
   */
  public void fail( Throwable t )  {
    if( null != completeHandler ){
      try {
        completeHandler.complete( IOResponse.fromError( this, t ) );
      }
      catch( InvalidRangeException e ) {

      }
    }
  }
  //endregion

  //region Class internal structs
  /**
   *
   */
  public static class Builder{

    //region Class members
    /**
     *
     */
    private List<IORequestItem> list = new ArrayList<>();
    /**
     *
     */
    private IOCompleteEvent eventHandler;
    //endregion

    //region Class properties
    /**
     *
     * @return
     */
    public int count(){
      return list.size();
    }
    //endregion

    //region Class public methods
    /**
     *
     * @param o
     * @return
     */
    public Builder read( IPlcObject o ){
      return add( new IORequestItem( IOType.Read, o ) );
    }
    /**
     *
     * @param arr
     * @return
     */
    public Builder read( IPlcObject... arr ){
      return read( List.of( arr ) );
    }
    /**
     *
     * @param list
     * @return
     */
    public Builder read( List<IPlcObject> list ){
      list.forEach( x -> read( x ) );

      return this;
    }
    /**
     *
     * @param o
     * @return
     */
    public Builder write( IPlcObject o ){
      return add( new IORequestItem( IOType.Write, o ) );
    }
    /**
     *
     * @param arr
     * @return
     */
    public Builder write( IPlcObject... arr ){
      return write( List.of( arr ) );
    }
    /**
     *
     * @param list
     * @return
     */
    public Builder write( List<IPlcObject> list ){
      list.forEach( x -> write( x ) );

      return this;
    }
    /**
     *
     * @param item
     * @return
     */
    public Builder add( IORequestItem item ){
      list.add( item );

      return this;
    }
    /**
     *
     * @param items
     * @return
     */
    public Builder add( Iterable<IORequestItem> items ){
      list.addAll( UtilityHelper.toList( items ) );

      return this;
    }
    /**
     *
     * @param e
     * @return
     */
    public Builder complete( IOCompleteEvent e){
      eventHandler = e;

      return this;
    }
    /**
     *
     * @return
     */
    public IORequest build(){
      var res = new IORequest();
      res.list = new ArrayList<IORequestItem>( list );
      res.completeHandler = eventHandler;
      res.id = UUID.randomUUID().toString();

      return res;
    }
    //endregion

  }
  //endregion
}




