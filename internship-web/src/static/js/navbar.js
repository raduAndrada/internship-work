window.onload = function() {
	console.log($("a[href*='" + location.pathname + "']")[0].pathname);
	var path = $("a[href*='" + location.pathname + "']")[0].pathname;
	var liNodes = $('#nav li');

	var len = liNodes.length;
	console.log(liNodes);
	console.log(len);
	for (var i = 0; i < len; i++) {
		if (liNodes[i].id === path){
			console.log(liNodes[i]);
			liNodes[i].className += " active-page";
		}
	};
};