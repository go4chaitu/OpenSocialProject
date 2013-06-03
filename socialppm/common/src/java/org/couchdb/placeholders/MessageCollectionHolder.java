// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.placeholders;

import org.apache.shindig.social.core.model.MessageCollectionImpl;

public class MessageCollectionHolder
{
  private MessageCollectionImpl object;
  private String type;
  private String _id;
  private String _rev;
  private String userId;

  public MessageCollectionHolder()
  {
  }

  public MessageCollectionHolder( MessageCollectionImpl object_, String userId_, String type_ )
  {
    object = object_;
    userId = userId_;
    type = type_;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId( String userId_ )
  {
    userId = userId_;
  }

  public MessageCollectionImpl getDocObject()
  {
    return object;
  }

  public String getDocType()
  {
    return type;
  }

  public void setDocObject( MessageCollectionImpl object_ )
  {
    object = object_;
  }

  public void setDocType( String type_ )
  {
    type = type_;
  }

  public String getId( String id )
  {
    return _id;
  }

  public void setId( String id_ )
  {
    _id = id_;
  }

  public String getRevId()
  {
    return _rev;
  }

  public void setRevId( String rev_ )
  {
    _rev = rev_;
  }
}
