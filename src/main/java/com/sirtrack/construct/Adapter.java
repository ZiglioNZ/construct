package com.sirtrack.construct;

import java.io.ByteArrayOutputStream;

import com.sirtrack.construct.Adapters.MappingAdapter;
import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.Core.Subconstruct;
import com.sirtrack.construct.lib.ByteBufferWrapper;
import com.sirtrack.construct.lib.Containers.Container;

/**
 * """ Abstract adapter: calls _decode for parsing and _encode for building. """
 * 
 */
public abstract class Adapter<T extends Construct, V> extends Subconstruct<T> implements AdapterEncoder<V>, AdapterDecoder<V> {
	/**
	 * @param name
	 * @param subcon
	 *          the construct to wrap
	 */
	public Adapter(T subcon) {
		super(subcon);
	}

	@Override
	public Object _parse( ByteBufferWrapper stream, Container context) {
		return decode(subcon._parse( stream, context ), context);
	}

  @Override
	public void _build( Object obj, ByteArrayOutputStream stream, Container context) {
		subcon._build(encode((V) obj, context), stream, context);
	}

//  @Override
//  public T get(){
//    return subcon;
//  }
//
//  @Override
//  public void set( Object val ){
//    subcon.set(val);
//  }
	
	abstract public V decode(Object obj, Container context);
	abstract public Object encode(V obj, Container context);

}
