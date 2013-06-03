// Copyright (c) 2013, CA Inc.  All rights reserved.
$(document).ready(function(){
  $.ajaxSetup({
  data:{
    'st':"sarch04:sarch04:appid:cont:url:0:default&scrolling=no&nocache"
  }
});
    $("#hello").load("http://localhost/niku/gadgets/ifr?url=http://localhost/niku/socialppm/container/claritysn/SocialNetwork/gadgethome.xml&st=sarch04:sarch04:appid:cont:url:0:default&scrolling=no&nocache");

//  $.ajax({
//    url: 'http://localhost/niku/gadgets/ifr?url=http://localhost/niku/socialppm/container/claritysn/SocialNetwork/gadgethome.xml&st=sarch04:sarch04:appid:cont:url:0:default&scrolling=no&nocache'
//   })
});
