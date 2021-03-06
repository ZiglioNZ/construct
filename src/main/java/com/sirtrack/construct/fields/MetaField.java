package com.sirtrack.construct.fields;

import java.io.ByteArrayOutputStream;

import com.sirtrack.construct.Core.Construct;
import com.sirtrack.construct.interfaces.LengthFunc;
import com.sirtrack.construct.lib.ByteBufferWrapper;
import com.sirtrack.construct.lib.Containers.Container;

public class MetaField extends Construct {

  LengthFunc lengthfunc;

  /**
   * @param name
   *          name of the field
   * @param lengthfunc
   *          callable that takes a context and returns length as an int
   */
  public MetaField(String name, LengthFunc lengthfunc) {
    super(name);
    this.lengthfunc = lengthfunc;
    this._set_flag(FLAG_DYNAMIC);
  }

  @Override
  public Object _parse(ByteBufferWrapper stream, Container context) {
    return _read_stream(stream, lengthfunc.length(context));
  }

  @Override
  public void _build(Object obj, ByteArrayOutputStream stream,
      Container context) {
    _write_stream(stream, lengthfunc.length(context), obj);
  }

  @Override
  public int _sizeof(Container context) {
    return lengthfunc.length(context);
  }

}


