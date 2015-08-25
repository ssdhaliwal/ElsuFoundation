package elsu.database.rowset;

/**
 *
 * @author ss.dhaliwal
 * http://javarevisited.blogspot.com/2012/08/how-to-write-parametrized-class-method-Generic-example.html
 */
public class DbColumn<T> {
    private T _item;
    private boolean _changed = false;
    
    public T getItem(){
        return this._item;
    }
    
    public void setItem(T item) {
        this._item = item;
        this._changed = true;
    }
    
    public boolean isChanged() {
        return this._changed;
    }
}
