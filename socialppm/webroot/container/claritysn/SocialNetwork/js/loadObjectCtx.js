// Copyright (c) 2013, CA Inc.  All rights reserved.
$(document).ready(function(){
  $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/js/jquery-1.9.1.js").done(
      function(script, textStatus) {
        $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/js/jquery-ui.js").done(
          function(script, textStatus) {
            $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/components/statusbar/jquery.form.js").done(
              function(script, textStatus) {
                $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/js/loaderDefinitions.js").done(
                  function(script, textStatus) {
                    $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/js/follow_unfollow.js").done(
                      function(script, textStatus) {
                        loadObjectCtx();
                      })
                  })
                })
            })
        });

  loadObjectCtx = function()
  {
    $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/components/objectContextDiv/js/objectCtx.js").done(
      function(script, textStatus) {

      });
   }
});

