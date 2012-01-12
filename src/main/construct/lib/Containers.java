package construct.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Containers{
  
	static public class Pair{
		public Object s;
		public Object o;
		
		public Pair( Object s, Object o){
			this.s = s;
			this.o = o;
		}
	}

  static public class Container
  {
  	HashMap<Object, Object> dict = new HashMap<Object, Object>();
  	
  	public Container( Pair... dict ){
  		for( Pair p : dict ){
  			this.dict.put( p.s, p.o);
  		}
  	}
  
  	public Container( Map<Object, Object> dict ){
  		this.dict.putAll( dict );
  	}
  
  	public <T>T get( Object name ){
  		Object o = dict.get(name);
  		return (T)dict.get(name);
  	}
  	
  	public void del( Object name ){
  		dict.remove( name );
  	}
  	
  	public void set( Object name, Object value ){
  		dict.put( name, value);
  	}
  	
  	public Set<Object> keys(){
  		return dict.keySet();
  	}
  	
  	public void update( Container other ){
  		for( Object s : other.keys() ){
  				set( s, other.get(s));
  		}
  	}
  	
  	public boolean contains( Object name ){
  		return dict.containsKey(name);
  	}
  	
  	@Override
  	public boolean equals( Object o ){
  		if( o instanceof Container)
  			return dict.equals(((Container)o).dict);
  		else
  			return false;
  	}
    
  	public Container clone(){
  		return new Container( dict );
  	}
  
    public final String toString() {
      return dict.toString();
  }
  
    public Container reverse(){
    	Container reverse = new Container();
    	for( Object key : this.keys() ){
    		reverse.set( this.get(key ), key );
    	}
    	return reverse;
    }
  }
  
  static public List<Object> ListContainer( Object... args ){
  	ArrayList<Object> l = new ArrayList<Object>();
  	for( Object o: args )
  		l.add(o);
  	return l;
  }
/*
  class ListContainer(list):
    """
    A container for lists.
    """

    __slots__ = ["__recursion_lock__"]

    @recursion_lock("[...]")
    def __str__(self):
        return pformat(self)
*/  
}