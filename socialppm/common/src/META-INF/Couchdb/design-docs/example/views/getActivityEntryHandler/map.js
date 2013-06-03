// Copyright (c) 2013, CA Inc.  All rights reserved.
function(doc){
  if(doc.type == "activityEntry"){
    emit([doc.object.id,doc.userId,doc.type], doc.object);
    emit([doc.object.id,doc.type], doc.object);
  }
}