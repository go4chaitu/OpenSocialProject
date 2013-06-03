// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.couchdb.placeholders;

import org.apache.shindig.social.core.model.DocumentImpl;

public class DocumentHandler
{
	private DocumentImpl object;
	private String type;
	private String _id;
	private String _rev;
  public DocumentHandler()
	{
	}
	public DocumentHandler( DocumentImpl object_, String type_ )
	{
		object = object_;
		type = type_;
	}
	
	public DocumentImpl getPersonObject(){
		return object;
	}
	public String getDocType(){
		return type;
	}
	public void setDocumentObject(DocumentImpl object_){
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
