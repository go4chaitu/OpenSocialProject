function(doc){
  if(doc.type == "activity"){
    emit([doc.object.id,doc.object.userId,doc.type], doc.object);
  }
}