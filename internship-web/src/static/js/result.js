function showUnregCars(iteration) {
	var temp = "unregTable" + iteration;
	var unregCarsPannel = document.getElementById(temp);
	if (unregCarsPannel.style.display === 'none') {
		unregCarsPannel.style.display = 'block';
		
	} else {
		unregCarsPannel.style.display = 'none'
	}
}

function showErrors(iteration) {
	var temp = "errorTable" + iteration;
	var errorTable = document.getElementById(temp);
	if (errorTable.style.display === 'none') {
		errorTable.style.display = 'block';
	} else {
		errorTable.style.display = 'none'
	}
}