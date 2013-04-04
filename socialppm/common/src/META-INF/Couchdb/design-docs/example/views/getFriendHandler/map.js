// Copyright (c) 2013, CA Inc.  All rights reserved.
function(doc){
  if(doc.type == "friend"){
    emit([doc.userId,doc.type], doc);
  }
}