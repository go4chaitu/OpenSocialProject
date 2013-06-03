// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.placeholders;

import org.apache.shindig.social.core.model.GroupImpl;

import java.util.List;

public class GroupHandler
{
  private GroupImpl object;
  private String type;
  private String _id;
  private String _rev;
  private List<String> groupmembers;
  private String owner;

  public GroupImpl getGroupObject()
  {
    return object;
  }

  public String getDocType()
  {
    return type;
  }

  public void setGroupObject( GroupImpl object_ )
  {
    object = object_;
  }

  public void setDocType( String type_ )
  {
    type = type_;
  }

  public String getId()
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

  public List<String> getGroupmembers()
  {
    return groupmembers;
  }

  public void setGroupmembers( List<String> groupmembers_ )
  {
    groupmembers = groupmembers_;
  }

  public String getGroupOwner()
  {
    return owner;
  }

  public void setGroupOwner( String owner_ )
  {
    owner = owner_;
  }
}
