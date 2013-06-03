// Copyright (c) 2013, CA Inc.  All rights reserved.
function(doc){
  if(doc.type == "messageCollection")
  {
    emit([doc.userId, doc.type], doc.object);
    emit([doc.userId, doc.object.id, doc.type], doc.object);
  }
}