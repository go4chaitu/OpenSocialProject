// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.shindig.social.core.model.ListFieldImpl;
import org.apache.shindig.social.opensocial.model.ListField;

import java.lang.reflect.Type;

/**
 * Created by IntelliJ IDEA.
 * User: sarch04
 * Date: 5/11/13
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListFieldAdapter implements JsonDeserializer, JsonSerializer<ListField>
{
  public ListField deserialize( JsonElement jsonElement_, Type type_,
                                JsonDeserializationContext jsonDeserializationContext_ )
    throws JsonParseException
  {
    ListField field = new ListFieldImpl();
    JsonObject jsonObject = jsonElement_.getAsJsonObject();
    field.setType( jsonObject.get( "type" ) != null ? jsonObject.get( "type" ).getAsString() : "" );
    field.setValue( jsonObject.get( "value" ) != null ? jsonObject.get( "value" ).getAsString() : "" );
    field.setPrimary( jsonObject.get( "primary" ) != null ? jsonObject.get( "primary" ).getAsBoolean() : false );
    return field;
  }

  @Override
  public JsonElement serialize( ListField t_, Type type_, JsonSerializationContext jsonSerializationContext_ )
  {
//    Gson gson = new GsonBuilder().create();
//    return new JsonPrimitive( gson.toJson( t_ ) );
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty( "type", t_.getType() );
    jsonObject.addProperty( "value", t_.getValue() );
    jsonObject.addProperty( "primary", t_.getPrimary() );
    return jsonObject;
  }
}
