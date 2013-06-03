$(document).ready(function(){

	textAreaTemplate = function( textarea_li_id )
	{
		var txt = $('#'+textarea_li_id+"_ta");
		var hiddenDiv = $(document.createElement('div'));
		var content = null;

		txt.addClass('txtstuff');
		hiddenDiv.addClass('hiddendiv common');

		$('#'+textarea_li_id).append(hiddenDiv);

		txt.on('keyup', function () {

			content = $(this).val();

			content = content.replace(/\n/g, '<br>');
			hiddenDiv.html(content + '<br class="lbr">');

			$(this).css('height', hiddenDiv.height());

		});
	}
	textAreaTemplate("workitem_name");
	textAreaTemplate("workitem_desc");
	
	(function ($) {
		$('div[data-placeholder]').on('keydown keypress input', function() {
			if (this.textContent) {
				this.dataset.divPlaceholderContent = 'true';
				$(this).css({"color":"#a9a9a9"});
			}
			else {
				delete(this.dataset.divPlaceholderContent);
				$(this).css({"color":"black"});
			}
		});
	})(jQuery);	
	
	$(function(){
		$('.text_value').each(function(){
			if($(this).val() == ''){
				$(this).css({"color":"#a9a9a9"});
				$(this).val($(this).data('placeholder'));				
			}
		});
	});
	$('input').focusin(function(){
		if($(this).val() == $(this).data('placeholder')){
			$(this).css({"color":"black"});
			$(this).val('');
		}
	}).focusout(function(){    
		if(!$(this).val() || $(this).val().length <= 0){
			$(this).css({"color":"#a9a9a9"});
			$(this).val($(this).data('placeholder'));
		}
	});
	
	$("#workitem_lead #clear, #workitem_on_object #clear, #workitem_duedate #clear").click(function(){
		var inputField = $(this).parent().find("input");
		$(inputField).css({"color":"#a9a9a9"});		
		inputField.val(inputField.data('placeholder'));
	});
	
	$('#workitem_duedate input').change(function() {
		$(this).css({"color":"black"});
	});

	var allUsers = [
      "Emma",
      "Madison",
      "Tommy",
      "Taylor"
    ];
	
	$( "#workitem_lead_input" ).autocomplete({
		autoFocus: true,
		source: allUsers
	}).keyup(function() {
		var isValid = false;
		for (i in allUsers) {
			if (allUsers[i].toLowerCase().match(this.value.toLowerCase())) {
				isValid = true;
			}
		}
		if (!isValid) {
			this.value = previousValue
		} else {
			previousValue = this.value;
		}
	});
	
	$("#workitem_duedate .text_value").datepicker();
});