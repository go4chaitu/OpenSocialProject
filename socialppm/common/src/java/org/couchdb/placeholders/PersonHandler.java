// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.placeholders;

import org.apache.shindig.social.core.model.PersonImpl;

public class PersonHandler {
	private PersonImpl object;
	private String type;
	private String _id;
	private String _rev;
  public PersonHandler()
	{
	}
	public PersonHandler(PersonImpl object_, String type_)
	{
		object = object_;
		type = type_;
	}
	
	public PersonImpl getPersonObject(){
		return object;
	}
	public String getDocType(){
		return type;
	}
	public void setPersonObject(PersonImpl object_){
		object = object_;
	}
	public void setDocType(String type_){
		type = type_;
	}
	public String getId(){
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
}
