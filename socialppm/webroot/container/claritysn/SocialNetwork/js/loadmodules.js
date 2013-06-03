$(document).ready(function(){
  $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/js/jquery-1.9.1.js").done(
      function(script, textStatus) {
        $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/js/jquery-ui.js").done(
          function(script, textStatus) {
            $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/components/statusbar/jquery.form.js").done(
              function(script, textStatus) {
                $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/js/jquery.timeago.js").done(
                  function(script, textStatus) {
                    $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/js/loaderDefinitions.js").done(
                      function(script, textStatus) {
                        loadModules();
                      })
                  })
                })
          })
      });

  loadModules = function()
  {
    var header_url ="/niku/socialppm/container/claritysn/SocialNetwork/header.html";
    $("#sn_header").load(header_url);
    var leftsidebar_url ="/niku/socialppm/container/claritysn/SocialNetwork/leftbar.html";
    var contentbar_homeurl ="/niku/socialppm/container/claritysn/SocialNetwork/contentbar.html";
    var contentbar_profileurl ="/niku/socialppm/container/claritysn/SocialNetwork/components/profileDiv/profileDiv.html";
    var actionId = 'viewerProfile';
    if( parent.document.getElementById("socialActionId") )
    {
      actionId = parent.document.getElementById("socialActionId").value;
    }
    if( actionId == 'activitiesPage')
    {
      $("#sn_centerbar").load(contentbar_homeurl, function(){
        $("#sn_centerbar_statusupdate").load('/niku/socialppm/container/claritysn/SocialNetwork/components/statusbar/statusbar.html', function(){
          $.getScript("/niku/socialppm/container/claritysn/SocialNetwork/components/statusbar/statusbar.js");
        });
        loadActivities("sn_centerbar_activities");
      });
    }
    else if( actionId == 'viewerProfile')
    {
      $("#sn_centerbar").load(contentbar_profileurl, function(){
         var profileId = parent.document.getElementById("socialViewerId").value;
         loadProfile(profileId);
      });
    }
    else if( actionId == 'friendProfile')
    {
      $("#sn_centerbar").load(contentbar_profileurl, function(){
         var profileId = parent.document.getElementById("socialFriendId").value;
         loadProfile(profileId);
      });
    }

    var rightbar_url ="/niku/socialppm/container/claritysn/SocialNetwork/rightbar.html";
    $("#sn_rightbar").load(rightbar_url);
  };
});
