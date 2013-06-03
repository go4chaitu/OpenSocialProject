// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.opensocial.model;

import com.google.inject.ImplementedBy;
import org.apache.shindig.protocol.model.Exportablebean;
import org.apache.shindig.social.core.model.AccountImpl;
import org.apache.shindig.social.core.model.DocumentImpl;

import java.io.InputStream;

@ImplementedBy(DocumentImpl.class)
@Exportablebean
public interface Document
{
  void setInputStream(InputStream inputStream_);
  InputStream getInputStream();
  void setDocType( String docType_ );
  String getDocType();
  void setContentType( String contentType_ );
  String getContentType();
  void setAttachmentId( String docId_ );
  String getAttachmentId();
  void setAttachmentName( String attachmentName_ );
  String getAttachmentName();
}
