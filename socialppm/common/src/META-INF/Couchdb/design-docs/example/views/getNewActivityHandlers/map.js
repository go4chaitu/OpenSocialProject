function(doc){
  if(doc.type == "activity"){
      emit([doc.object.postedTime,doc.type], doc.object);
  }
}