function(doc){
  if(doc.type == "group"){

    if(doc.object && doc.groupmembers){
      for(var idx in doc.groupmembers){
        emit(doc.groupmembers[idx], doc.object);
      }
    }
    if(doc.owner){
    emit(doc.owner, doc.object);
    }
  }
}