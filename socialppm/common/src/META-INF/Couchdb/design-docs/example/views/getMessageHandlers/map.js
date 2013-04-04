function(doc){
  if(doc.type == "message"){

    if(doc.object && doc.object.recipients){
      for(var idx in doc.object.recipients){
        emit(doc.object.recipients[idx], doc.object);
        emit([doc.object.recipients[idx],doc.object.type], doc.object);
        emit([doc.object.recipients[idx],doc.object.type,doc.object.id], doc.object);
      }
    }
  }
}