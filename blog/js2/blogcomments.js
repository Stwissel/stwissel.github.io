var destinationURL = "/blog/comments";

function getCommentForm(recaptchaid, parentId) {
	var params = {};
	params.recaptchaid = recaptchaid;
	params.parentId = parentId;
	var result = {};
	$.ajax({
         url:    '/blog/js2/comment.mustache',
         dataType : 'text',
         success: function(template) {
                    result = Mustache.render(template, params);
                  },
         async:   false
    });
	return result;
}

function addComment(form, recaptchaid, parentId) {
	$('#commentsubmit').hide();
	$('#commentcontrol').hide();
	$('#captchadiv').hide();
	$("#alertContainer").html("One moment please, submitting comment...")
			.show();

	$.postJSON({
		url : destinationURL,
		data : {
			Commentor : this.Commentor.value,
			eMail : this.Email.value,
			webSite : this.webSite.value,
			Body : this["wmd-input"].value,
			rChallenge : this.recaptcha_challenge_field.value,
			rResponse : this.recaptcha_response_field.value,
			parentId : parentId
		},
		success : function(result) {
			$("#alertContainer").html(result).addClass("alert-error").delay(
					2000).hide(200, function() {
				resetComment(recaptchaid, parentId);
			});
		},
		error : function(err) {
			$("#alertContainer").html(err.responseText).addClass("alert-error")
					.delay(1000).hide(200, function() {
						resetComment(recaptchaid, parentId);
					});
		}
	});
	return false;
}

function resetComment(recaptchaid, parentId) {
	var hasSuccess = $("div.commentsuccess");
	if (hasSuccess.length == 0) {
		// It didn't work!
		$("#alertContainer").show();
		$('#commentsubmit').show();
		$('#commentcontrol').show();
		$('#captchadiv').show();
	} else {
		renderComment(recaptchaid, parentId);
	}
}

function renderComment(recaptchaid, parentId) {
	var fid = "#commentform_" + parentId;
	var form = getCommentForm(recaptchaid, parentId);
	$(fid).empty().append(form);
	if (Recaptcha) {
		var theDiv = document.getElementById("captchadiv");
		Recaptcha.create(recaptchaid, theDiv, {
			tabindex : 1,
			theme : "clean" /*
							 * , callback : Recaptcha.focus_response_field
							 */
		});
	}

	// Markdown
	var converter1 = Markdown.getSanitizingConverter();
	var editor1 = new Markdown.Editor(converter1);
	editor1.run();

	// Comments that are not yet static
	$.ajax({
		url: destinationURL + "?parentid=" + parentId,
		type: 'GET',
		async: true,
		success: function(data){
			if (data && data.length > 0) {
				$("li.dynamicComments").remove();
				$("#nocomments").remove();
				$("#commentList").append(data);
			}
		},
		error: function(data) {
			// Crude error handling
			$("#alertContainer").html(data).show();
		}
	});
}

jQuery.extend({
	postJSON : function(params) {
		return jQuery.ajax(jQuery.extend(params, {
			type : "POST",
			data : JSON.stringify(params.data),
			dataType : "json",
			contentType : "application/json",
			processData : false
		}));
	}
});
