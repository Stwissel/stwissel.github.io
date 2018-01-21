var destinationURL = "https://senseicomments.herokuapp.com/blogcomments";

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

	var postData = {};
	postData.Commentor = this.Commentor.value;
	postData.eMail = this.Email.value;
	postData.webSite = this.webSite.value;
	postData.Body = this["wmd-input"].value;
	postData.captcha = grecaptcha.getResponse();
	postData.parentId = parentId;

	$.postJSON({
		url : destinationURL,
		data : postData,
		success : function(result) {
			// TODO: Render result from JSON!
			$("#alertContainer").html("<pre>"+JSON.stringify(result)+"</pre>").addClass("alert-error").delay(
					5000).hide(200, function() {
				resetComment(recaptchaid, parentId);
			});
		},
		error : function(err) {
			var realError = (err.responseText) ? err.responseText : "Something went wrong";
			$("#alertContainer").html("<pre>"+JSON.stringify(realError)+"</pre>").addClass("alert-error")
					.delay(5000).hide(200, function() {
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
	if (grecaptcha) {
		var theDiv = document.getElementById("captchadiv");
		$(theDiv).empty();
		grecaptcha.render(theDiv, {
			sitekey : recaptchaid,
			theme: "light"
		});
	}

	// Markdown
	var converter1 = Markdown.getSanitizingConverter();
	var editor1 = new Markdown.Editor(converter1);
	editor1.run();
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
