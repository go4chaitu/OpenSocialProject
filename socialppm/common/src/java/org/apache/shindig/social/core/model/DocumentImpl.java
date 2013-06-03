// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.core.model;

import org.apache.shindig.social.opensocial.model.Document;

import java.io.InputStream;

public class DocumentImpl implements Document
{
  private InputStream inputStream;
  private String attachmentId;
  private String contentType;
  private String attachmentName;
  private String docType = "document";

  @Override
  public void setDocType( String docType_ )
  {
    docType = docType_;
  }
  @Override
  public String getDocType()
  {
    return docType;
  }
  @Override
  public void setContentType( String contentType_ )
  {
    this.contentType = contentType_;
  }
  @Override
  public String getContentType()
  {
    return contentType;
  }
  @Override
  public void setInputStream(InputStream inputStream_)
  {
    this.inputStream = inputStream_;
  }
  public InputStream getInputStream()
  {
    return inputStream;
  }
  public void setAttachmentId( String docId_ )
  {
    this.attachmentId = docId_;
  }
  public String getAttachmentId()
  {
    return this.attachmentId;
  }
  public void setAttachmentName( String attachmentName_ )
  {
    this.attachmentName = attachmentName_;
  }
  public String getAttachmentName()
  {
    return attachmentName;
  }
}
