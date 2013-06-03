$(document).ready(function(){
	setProfileData = function(resp){
		if( resp )
		  {
		  	$("#profile_pic img").attr("src","")
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
		   	$("#profile_pic img").attr("src", profileUrl);
		   	$("#profile_nameheader span").text(formatted);
		   	$("#profile_namedesc #designation").text(jobInterests);
        $("#profile_namedesc #aboutme").text(aboutMe);
        $("#contactdetails_mobile").text("09989388943");
        $("#contactdetails_email").text(resp.id+"@ca.com");
        $("#contactdetails_address").text("CA Hyderabad");
        if( resp.interests && resp.interests.length > 0 )
        {
          for(var i=0; i<resp.interests.length; i++)
          {
            var profileskill = $("#profileskills_span_template").clone();
            profileskill.css({"display":""});
            profileskill.attr("id","profileskills_span");
            profileskill.text(resp.interests[i]);
            $("#profileskills").append(profileskill);
          }
		    }

        loadActivities("profileactivities_content");
		  }
	}
});
