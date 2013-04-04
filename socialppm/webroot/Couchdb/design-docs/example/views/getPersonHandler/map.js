function(doc){
  if(doc.type == "person"){
    emit([doc.object.id,doc.type], doc.object);
  }
}