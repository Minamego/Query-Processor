
var xMLHttpRequest = new XMLHttpRequest();
function suggestion(txt){

    console.log("I'm typing");
    xMLHttpRequest.open("POST","/AutoComplete?query="+txt,true);
	xMLHttpRequest.onreadystatechange = addSuggestions;
	xMLHttpRequest.send();
}

function addSuggestions() {
	console.log("addSuggestions ....");
	if(xMLHttpRequest.readyState == 4 && xMLHttpRequest.status == 200){
		var JSONSuggestObject = eval('('+ xMLHttpRequest.responseText + ')');
		var table = document.getElementById("tableSuggestions");
		table.innerHTML = "";
		table.style.width = "80%";
		table.style.position = "fixed";
		table.style.backgroundColor = "white";
		var headRow = table.insertRow(0);
		var headcell = headRow.insertCell(0);
		headcell.style.backgroundColor = "lightblue";
		headcell.innerHTML = JSONSuggestObject.query.name;
		var suggestions = JSONSuggestObject.query.suggestions;
		var i =0;
		while (i < suggestions.length){
			row = table.insertRow(i+1);
			cell = row.insertCell(0);
			cell.innerHTML = suggestions[i++].name;
		}
	}
}

function viewSignUpForm() {

	document.getElementById('id01').style.display = 'none';
	document.getElementById('id02').style.display = 'block';
}
