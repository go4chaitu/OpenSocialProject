function(doc){
  if( doc.docType=='document'){
    emit(doc.attachmentId, doc.attachmentName );
  }
}