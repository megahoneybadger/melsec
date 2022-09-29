package melsec.types.io;

import melsec.bindings.IPlcObject;
import melsec.utils.UtilityHelper;

import java.util.ArrayList;
import java.util.List;

public class IORequest {

  //region Class members
  /**
   *
   */
  private List<IORequestItem> list = new ArrayList<>();
  /**
   *
   */
  private IOCompleteEventHandler completeHandler;
  //endregion

  //region Class properties
  /**
   *
   * @return
   */
  public IOCompleteEventHandler getCompleteHandler(){
    return completeHandler;
  }
  /**
   *
   * @return
   */
  public Iterable<IORequestItem> getItems(){
    return new ArrayList<>( list );
  }
  //endregion

  //region Class properties
  /**
   *
   * @return
   */
  public static Builder builder() {
    return new Builder();
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
    private IOCompleteEventHandler eventHandler;
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
      for( var o : arr ){
        read( o );
      }

      return this;
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
    public Builder complete( IOCompleteEventHandler e){
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

      return res;
    }
    //endregion

  }
  //endregion
}




