$(document).ready(function(){

  var txt = $('#addcomment_ta');
	var hiddenDiv = $(document.createElement('div'));
	var content = null;

	txt.addClass('txtstuff');
	hiddenDiv.addClass('hiddendiv comment');

	$('#addcomment_textfield').append(hiddenDiv);

	$(".txtstuff").keyup(function () {

		content = $(this).val();
		content = content.replace(/\n/g, '<br>');
		hiddenDiv.html(content + '<br class="lbr">');

		$(this).css('height', hiddenDiv.height() > 0? hiddenDiv.height() : 20);

	});

  $("#addComment_button").click(function(){
    alert('post the contents!');
  })
});