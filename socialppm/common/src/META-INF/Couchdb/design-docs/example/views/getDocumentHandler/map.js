function(doc){
  if(doc.type == "document"){
    emit(doc.attachmentName, doc.attachmentId);
  }
}