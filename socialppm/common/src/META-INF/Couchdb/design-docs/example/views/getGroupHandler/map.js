function(doc){
  if(doc.type == "group" && doc.object.id){
    if( doc.object.id.objectId.objectId.localId )
    {
      emit([doc.object.id.objectId.objectId.localId,doc.type], doc.object);
    }
  }
}