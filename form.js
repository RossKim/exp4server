function checkCookie() {
	var enabledCookie = navigator.cookieEnabled || ("cookie" in document && (document.cookie.length > 0 || (document.cookie = "test").indexOf.call(document.cookie, "test") > -1));
	if(!enabledCookie) {
		var key = document.getElementById('key').firstChild;
		var sessionKey = null;
		if(key) {
			sessionKey = key.value;
		}
		if(!sessionKey) {
			$.ajax({
				type: 'POST',
				url: '/sessionKey.json',
				dataType: 'json',
				success: function(data) {
					if(data) {
						document.getElementById('key').innerHTML = "<input type='hidden' name='sessionKey' value='" + data.key + "' />";
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown) {
					alert("Fail to get sessionKey");
				}
			});
		}
	}
}

$(function() {
	$.ajax({
		type: 'GET',
		url: '/auth',
		error:function(XMLHttpRequest, textStatus, errorThrown) {
			document.body.innerHTML = "<h1>Need Authorization</h1>";
		}
	});
});