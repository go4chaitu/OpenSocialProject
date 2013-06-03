$(document).ready(function(){

  activityCommentscallback = function(resp){
    if( resp && resp.list && resp.list.length > 0 )
    {
      $("#"+resp.list[0].parentId + " .activitycomments").removeAttr("style");
      for(var i=0; i<resp.list.length; i++)
      {
        if( resp.list[i].id ){
          var commentObj = $("#activityEntryTemplate #activitycommentsTemplate #usercommentTemplate").clone();
          commentObj.attr("id", resp.list[i].id );
          commentObj.find("#commentedUserImg img").attr("src",resp.list[i].userId);
          commentObj.find("#commentText #commentedUserId").html(resp.list[i].userId);
          commentObj.find("#commentText #commentedUserText").html(resp.list[i].body);
          commentObj.find("#comments_footer .comment_timestamp").html("3 secs ago!");
          $("#"+resp.list[i].parentId + " #comments_header").append( commentObj );
          osapi.people.get({ userId : resp.list[i].userId, fields : ["id", "displayName"]}).execute(function(resp){
              if( resp && resp.id && resp.photos ){
                $("#commentedUserImg img[src="+resp.id+"]").attr("src",resp.photos[0].value)
              }
          });
        }
      }
    }
  }

  initComments = function( root ){
      var txt = root.find('#addcomment_ta');
      var hiddenDiv = $(document.createElement('div'));
      var content = null;

      txt.addClass('txtstuff');
      hiddenDiv.addClass('hiddendiv comment');

      root.find('#addcomment_textfield').append(hiddenDiv);

      $(".txtstuff").keyup(function () {

        content = $(this).val();
        content = content.replace(/\n/g, '<br>');
        hiddenDiv.html(content + '<br class="lbr">');

        $(this).css('height', hiddenDiv.height() > 30? hiddenDiv.height() : 30);

      });

      root.find("#addComment_button").click(function(){
        var bodyText = $(this).parent().find("#addcomment_ta").val();
        if( bodyText && bodyText.length > 0 )
        {
          var activityDiv = $(this).parent().parent();
          var activityId = activityDiv.attr("id")
          var bodyText = $(this).parent().find("#addcomment_ta").val();
          var viewerId = parent.document.getElementById("socialViewerId").value;
          var timestamp = new Date().getTime();
          var params = {userId: '@viewer', activityId: activityId, activity: {body: bodyText,title:'', userId:viewerId, postedTime: timestamp}};
          osapi.activities.addComment(params).execute(function(resp){
            loadActivities(activityDiv.parent().attr("id"),$("#activityGroupId").val() );
          });
        }
        else
        {
          alert('Please enter a comment!');
        }
      })
  };

  activitycallback = function(resp){
    var divIdToLoad = $("#divIdToLoad").val();
    if( resp && resp.list && resp.list.length > 0 )
    {
      for(var i=0; i<resp.list.length; i++)
      {
        if( resp.list[i].id ){
          var mainActivityDiv = $( "#activityEntryTemplate" ).clone();
          mainActivityDiv.attr("id", resp.list[i].id);
          mainActivityDiv.find(".activitycontent .activitycontent_inside .userImg img").attr("src", resp.list[i].userId);
          mainActivityDiv.find(".activitycontent .activitycontent_inside .userText .userName").html(resp.list[i].userId);
          mainActivityDiv.find(".activitycontent .activitycontent_inside .userText .spanUserText").html(resp.list[i].body);
          mainActivityDiv.find(".activitycontent").attr("id","");
          mainActivityDiv.find(".activitycomments").attr("id","");
          osapi.people.get({ userId : resp.list[i].userId, fields : ["id", "displayName"]}).execute(function(resp){
            if( resp && resp.id && resp.photos ){
              $(".activitycontent .activitycontent_inside .userImg img[src="+resp.id+"]").attr("src",resp.photos[0].value)
            }
          });
          var params = {activityId: resp.list[i].id};
          osapi.activities.getActivityComments(params).execute(activityCommentscallback);
          $( "#"+divIdToLoad ).append( mainActivityDiv );
          initComments(mainActivityDiv);
          //$("#feedsLastUpdateTime",parent.document).val(resp.list[i].postedTime);
        }
      }
      //$("#feedsLastUpdateTime",parent.document).val(resp.list[0].postedTime + 1);
      var notify_bar = $('#notification-bar',parent.document);
      notify_bar.css({"display":"none"});
      $("#feedsLastUpdateTime",parent.document).val(new Date().getTime());
    }
  }
});
