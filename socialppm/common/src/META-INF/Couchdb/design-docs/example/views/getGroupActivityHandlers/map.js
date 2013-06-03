function(doc){
  if(doc.type == "activity"){
    if( !doc.object.appId || doc.object.appId == "" || doc.object.appId == '@friends')
    {
      emit(['@friends',doc.object.userId,doc.type], doc.object);
      emit(['@friends',doc.object.userId,doc.type], doc.object);
    }
    else
    {
      emit([doc.object.appId,doc.object.userId,doc.type], doc.object);
      emit([doc.object.appId,doc.type], doc.object);
    }
  }
}