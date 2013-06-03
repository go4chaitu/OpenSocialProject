// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.placeholders;

import org.apache.shindig.social.core.model.ActivityImpl;

public class ActivityHandler {
	private ActivityImpl object;
  private String parentActivityId;
	private String type;
	private String _id;
	private String _rev;
	public ActivityHandler()
	{
	}
	public ActivityHandler(ActivityImpl object_, String type_)
	{
		object = object_;
		type = type_;
	}
  public ActivityHandler(ActivityImpl object_, String parentActivityId_, String type_)
	{
		object = object_;
    parentActivityId = parentActivityId_;
		type = type_;
	}
	
	public ActivityImpl getActivityAObject(){
		return object;
	}
	public String getDocType(){
		return type;
	}
	public void setActivityObject(ActivityImpl object_){
		object = object_;
	}
	public void setDocType(String type_){
		type = type_;
	}
	public String getId(String id){
		return _id;
	}
	public void setId(String id_){
		_id = id_;
	}
	public String getRevId(){
		return _rev;
	}
	public void setRevId(String rev_){
		_rev = rev_;
	}

  public String getParentActivityId()
  {
    return parentActivityId;
  }

  public void setParentActivityId( String parentActivityId_ )
  {
    parentActivityId = parentActivityId_;
  }
}
