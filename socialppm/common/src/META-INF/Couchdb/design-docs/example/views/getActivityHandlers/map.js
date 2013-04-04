function(doc){
  if(doc.type == "activity"){
    emit([doc.object.userId,doc.type], doc.object);
  }
}