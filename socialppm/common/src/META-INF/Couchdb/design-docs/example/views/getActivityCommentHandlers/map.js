function(doc){
  if(doc.type == "activityComment"){
    emit([doc.parentActivityId,doc.type], doc.object);
  }
}