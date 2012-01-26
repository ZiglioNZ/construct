package com.sirtrack.construct;

//from construct import Struct, MetaField, StaticField, FormatField
//from construct import Container, Byte
//from construct import FieldError, SizeofError

import static com.sirtrack.construct.Adapters.*;
import static com.sirtrack.construct.Core.*;
import static com.sirtrack.construct.Macros.*;
import static com.sirtrack.construct.lib.Containers.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.sirtrack.construct.lib.Containers.Container;


public class AdaptersTest  
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void BitIntegerAdapterTest() {
    Adapter ba;

    ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8 );
    assertEquals( 255, ba.parse( ByteArray(1,1,1,1,1,1,1,1) ));

    ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8, false, true );
    assertEquals( -1, ba.parse( ByteArray(1,1,1,1,1,1,1,1) ));

    ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8, true, false, 4 );
    assertEquals( 0x0f, ba.parse( ByteArray(1,1,1,1,0,0,0,0) ));

    ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8 );
    assertArrayEquals( ByteArray(1,1,1,1,1,1,1,1), ba.build(255) );

    ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8, false, true );
    assertArrayEquals( ByteArray(1,1,1,1,1,1,1,1), ba.build(-1) );

    ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8, true, false, 4 );
    assertArrayEquals( ByteArray(1,1,1,1,0,0,0,0), ba.build(0x0f) );

    exception.expect( BitIntegerError.class );
    ba = BitIntegerAdapter( Field("bitintegeradapter", 8), 8 );
    assertEquals( null, ba.build(-1) );
  }

  @Test
  public void oneOfTest(){
  	Adapter oneOf = OneOf(UBInt8("foo"), ListContainer(4, 5, 6, 7));
  	assertEquals( 5, oneOf.parse( ByteArray(5)));

  	assertArrayEquals( ByteArray(5), oneOf.build(5) );

    exception.expect( ValidationError.class );
    oneOf.build( 9 );

    exception.expect( ValidationError.class );
    oneOf.parse( ByteArray(8));
  }

  @Test
  public void MappingAdapterTest(){
  	Adapter ma;
  	
  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( 2,"x", 3,"y"), Container( "x",2, "y",3), null, null);
  	assertEquals( "y", ma.parse(ByteArray(3)));
  	
  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( 2,"x", 3,"y"), Container( "x",2, "y",3), "foo", null );
  	assertEquals( "foo", ma.parse(ByteArray(4)));

  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( 2,"x", 3,"y"), Container( "x",2, "y",3), Pass, null );
  	assertEquals( 4, ma.parse(ByteArray(4)));

  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( 2,"x", 3,"y"), Container( "x",2, "y",3), null, null);
  	assertArrayEquals( ByteArray(3), ma.build("y"));

  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( 2,"x", 3,"y"), Container( "x",2, "y",3), null, 17);
  	assertArrayEquals( ByteArray(17), ma.build("foo"));

  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( 2,"x", 3,"y"), Container( "x",2, "y",3), null, Pass);
  	assertArrayEquals( ByteArray(4), ma.build(4));
  	
  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( 2,"x", 3,"y"), Container( "x",2, "y",3), null, null);
    exception.expect( MappingError.class );
  	ma.build("z");

  	ma = MappingAdapter( UBInt8("mappingadapter"), Container( 2,"x", 3,"y"), Container( "x",2, "y",3), null, null);
    exception.expect( MappingError.class );
  	ma.parse(ByteArray(4));

  }

  @Test
  public void PaddingAdapterTest(){
  	assertArrayEquals( "abcd".getBytes(), (byte[])PaddingAdapter( Field("paddingadapter", 4) ).parse("abcd"));

    exception.expect( PaddingError.class );
  	assertArrayEquals( "abcd".getBytes(), (byte[])PaddingAdapter( Field("paddingadapter", 4), (byte)0x00, true ).parse("abcd"));

  	assertArrayEquals( ByteArray(0,0,0,0), (byte[])PaddingAdapter( Field("paddingadapter", 4), (byte)0x00, true ).parse( ByteArray(0,0,0,0)));

  	assertArrayEquals( ByteArray(0,0,0,0), (byte[])PaddingAdapter( Field("paddingadapter", 4) ).build("abcd"));
  	
  }
  
  @Test
  public void ExprAdapterTest(){
  	Adapter exprAdapter = ExprAdapter( UBInt8("expradapter"),
  																		 new AdapterEncoder() {
																				public Object encode(Object obj, Container context) {
																					return (Integer)obj / 7;
																				}
																			},
																			new AdapterDecoder() {
																				public Integer decode(Object obj, Container context) {
																					return (Integer)obj * 7;
																				}
																			});
  	assertEquals( 42, exprAdapter.parse( ByteArray( 6 )));
  	assertArrayEquals( ByteArray(6), exprAdapter.build( 42 ));
  }

  @Test 
  public void IpAddressTest() throws UnknownHostException{
  	Adapter adapter;
  	
  	adapter = IpAddress( "v4addr" );
  	InetAddress addrA = InetAddress.getByAddress(ByteArray( 192, 168, 0, 1 ));
  	InetAddress addrB = adapter.decode(ByteArray( 192, 168, 0, 1 ), null);
  	assertEquals( addrA, addrB );
  }


  @Test 
  public void BeanAdapterTest() throws UnknownHostException{
  	Pojo pojo = new Pojo ( 100, 1 );
  	Container c1 = Container( "id", 100, "val", 1 );
  	
  	Adapter adapter = BeanAdapter( Pojo.class, Pass );
  	assertEquals( pojo, adapter.decode( c1, null));
  	
  	Construct ps1 =  Struct( "Pojo", UBInt8("id"), UBInt8( "val") );
  	assertEquals( c1, ps1.parse(ByteArray( 100, 1 )) );
  	
  	Construct ps2 =  BeanAdapter( Pojo.class, Struct( "Pojo", UBInt8("id"), UBInt8( "val") ));
  	assertEquals( pojo, ps2.parse(ByteArray( 100, 1 )));
  	
  }

}

