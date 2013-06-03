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
                    loadmenu();
                  })
                })
          })
      });

  loadmenu = function()
  {
    var leftsidebar_url ="/niku/socialppm/container/claritysn/SocialNetwork/leftbar.html";
    $("#gadget_sidebar_li").load(leftsidebar_url, function(){
      $("#sn_myactivities").click(function(){
        var actionId = parent.document.getElementById("socialActionId").value = 'activitiesPage';
        var viewerId = parent.document.getElementById("socialViewerId").value;
        var token = viewerId+'%3A'+viewerId+'%3Aappid%3Acont%3Aurl%3A0%3Adefault&scrolling=no&nocache';
        var url = 'http://localhost/niku/socialppm/container/claritysn/SocialNetwork/gadgethome.xml';
        var ifrUrl = 'http://localhost/niku/gadgets/ifr' + '?url=' + url + '&st=' + token;
        $('#ppm_workspace', parent.document).html("<iframe width='100%' height='100%' style='border:0px;overflow-y: hidden;' src='"+ifrUrl+"'></iframe");
      });
      $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/js/leftbar_loadsocial.js").done(
      function(script, textStatus) {
        osapi.people.get({ userId : '@viewer', fields : ["id", "displayName"]}).execute(setPersonData);
        osapi.people.get({ userId : '@viewer', groupId: '@friends'}).execute(setFriendsData);
        osapi.groups.getFollowings({ userId: '@viewer'}).execute(setGroupsData);
      });
      $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/js/notificationbar.js").done(
        function(script, textStatus) {
          var url = "/niku/social/rest/activities/"+parent.document.getElementById("socialViewerId").value+"?st="+parent.document.getElementById("socialViewerId").value+"%3A"+parent.document.getElementById("socialViewerId").value+"%3Aappid%3Acont%3Aurl%3A0%3Adefault&scrolling=no&nocache&lastUpdatedTime=";
          (function poll() {
              setTimeout(function() {
                  $.ajax({
                      url: url + $("#feedsLastUpdateTime",parent.document).val(),
                      type: "GET",
                      success: function(data) {
                          //showNotificationBar("Test message!");
                          if( data.list && data.list.length > 0 )
                          {
                            for(var i=0; i<data.list.length; i++)
                            {
                              if( data.list[i].id )
                              {
                                var message = data.list[i].userId + " says "  + data.list[i].body;
                                showNotificationBar(message);
                              }
                            }
                          }
                      },
                      dataType: "json",
                      complete: poll,
                      timeout: 5000
                  });
              }, 5000);
          })();
        }
      );
    });
  };
});
