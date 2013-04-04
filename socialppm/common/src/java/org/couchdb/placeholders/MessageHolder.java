// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.placeholders;

import org.apache.shindig.social.core.model.MessageImpl;

public class MessageHolder
{
	private MessageImpl object;
	private String type;
	private String _id;
	private String _rev;
	public MessageHolder()
	{
	}
	public MessageHolder( MessageImpl object_, String type_ )
	{
		object = object_;
		type = type_;
	}
	
	public MessageImpl getDocObject(){
		return object;
	}
	public String getDocType(){
		return type;
	}
	public void setDocObject(MessageImpl object_){
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
}
