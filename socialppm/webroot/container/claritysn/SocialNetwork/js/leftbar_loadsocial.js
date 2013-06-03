setPersonData = function( resp )
{
  if( resp )
  {
    if( resp.aboutMe )
    {
      aboutMe = resp.aboutMe;
    }
    if( resp.displayName )
    {
      displayName = resp.displayName;
    }
    if( resp.photos && resp.photos[0] )
    {
      profileUrl = resp.photos[0].value;
    }
    if( resp.name && resp.name.formatted )
    {
      formatted = resp.name.formatted;
    }
    if( resp.jobInterests )
    {
      jobInterests = resp.jobInterests;
    }

    $( "#sn_picture_dtls #name" ).text( formatted );
    $( "#sn_sidebar_sections #sn_picture img" ).attr( "src", profileUrl );
    $( "#sn_picture_dtls #designation" ).text( jobInterests );
    $( "#snfrnds_header_2" ).text( "0 Friends" );
    $( "#viewerId" ).attr( "value", resp.id );
    $( "#sn_sidebar_sections #sn_picture img" ).click(function(){
      var viewerId = parent.document.getElementById("socialViewerId").value;
      var actionId = parent.document.getElementById("socialActionId").value = 'viewerProfile';
      var token = viewerId+'%3A'+viewerId+'%3Aappid%3Acont%3Aurl%3A0%3Adefault&scrolling=no&nocache';
  	  var url = 'http://localhost/niku/socialppm/container/claritysn/SocialNetwork/gadgethome.xml';
      var ifrUrl = 'http://localhost/niku/gadgets/ifr' + '?url=' + url + '&st=' + token;
      $('#ppm_workspace', window.parent.document).html("<iframe width='100%' height='100%' style='border:0px;overflow-y: hidden;' src='"+ifrUrl+"'></iframe");
    });
  }
};

setFriendsData = function( resp )
{
  var totalFriends = 0;
  var friends = null;
  if( resp )
  {
    if( resp.list && resp.list.length > 0 )
    {
      totalFriends = resp.list.length;
      friends = resp.list;
      $( "#snfrnds_header_2" ).text( totalFriends + " Friends" );
      var length = 7;
      if( resp.list.length < 7 )
      {
        length = resp.list.length;
      }
      for( var i = 0; i < length; i++ )
      {
        var newObject = $( "#sn_frnds_content #sn_friend_img #friendImgTemplate" ).clone();
        var srcUrl = "/niku/socialppm/container/claritysn/SocialNetwork/imgs/friend1.png";
        var userId = "defaultUserId";

        if( resp.list[i].photos && resp.list[i].photos[0].value )
        {
          srcUrl = resp.list[i].photos[0].value;
        }
        if( resp.list[i].id )
        {
          userId = resp.list[i].id;
        }
        if( resp.list[i].name && resp.list[i].name.formatted )
        {
          userName = resp.list[i].name.formatted;
        }
        var imgObject = newObject.find( 'img' );
        newObject.attr( "id", "imgSpan" );
        newObject.removeAttr( "style" );
        imgObject.attr( "src", srcUrl );
        imgObject.attr( "userid", userId );
        imgObject.attr( "title", userName );
        imgObject.attr( "id", "friendImg" );
        newObject.click(function(){
          var userId = $(this).find( 'img' ).attr("userid");
          var viewerId = parent.document.getElementById("socialViewerId").value;
          parent.document.getElementById("socialFriendId").value = userId;
          var actionId = parent.document.getElementById("socialActionId").value = 'friendProfile';
          var token = viewerId+'%3A'+userId+'%3Aappid%3Acont%3Aurl%3A0%3Adefault&scrolling=no&nocache';
          var url = 'http://localhost/niku/socialppm/container/claritysn/SocialNetwork/gadgethome.xml';
          var ifrUrl = 'http://localhost/niku/gadgets/ifr' + '?url=' + url + '&st=' + token;
          $('#ppm_workspace', window.parent.document).html("<iframe width='100%' height='100%' style='border:0px;overflow-y: hidden;' src='"+ifrUrl+"'></iframe");
        });
        $( "#sn_frnds_content #sn_friend_img" ).append( newObject );
      }
    }
  }
};

setGroupsData = function( resp )
{
  if( resp && resp.list && resp.list.length > 0 )
  {
    var length = 3;
    if( resp.list.length < 3 )
    {
      length = resp.list.length;
    }
    for( var i = 0; i < length; i++ )
    {
      if( resp.list[i].id )
      {
        var groupTitle = resp.list[i].title;
        var groupId = resp.list[i].id.split("localId=")[1].split("}")[0];
        var communityIdDiv = $( "#communityIdTemplate" ).clone();
        communityIdDiv.attr("id","communityId");
        communityIdDiv.attr("data_groupId",groupId);
        communityIdDiv.text(groupTitle);
        $( "#sn_followings_span" ).append( communityIdDiv );
        communityIdDiv.click(function(){
          var actionId = parent.document.getElementById("socialActionId").value = 'activitiesPage';
          var viewerId = parent.document.getElementById("socialViewerId").value;
          var token = viewerId+'%3A'+viewerId+'%3Aappid%3Acont%3Aurl%3A0%3Adefault&scrolling=no&nocache';
          var groupId = $(this).attr("data_groupId");
          var objectCodeFromGroupId = groupId.split("_")[0];
          var instanceIdFromGroupId = groupId.split("_")[1];
          $("#listViewObjectCode", parent.document).val(objectCodeFromGroupId);
          $("#listViewInstanceId", parent.document).val(instanceIdFromGroupId);
          var url = 'http://localhost/niku/socialppm/container/claritysn/SocialNetwork/gadgetObjectContext.xml';
          var ifrUrl = 'http://localhost/niku/gadgets/ifr' + '?url=' + url + '&st=' + token;
          $('#ppm_workspace', parent.document).html("<iframe width='100%' height='100%' style='border:0px;overflow-y: hidden;' src='"+ifrUrl+"'></iframe");
          $('.ppm_tabs',parent.document).css({"height":"0"});
        });
      }
    }
  }
}