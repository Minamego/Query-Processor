function hello() {
	console.log("Hell Bell");
}
console.log("Script");

var np;       // number of pages
var nrpp = 6; // nubmber of results per page
var totalNoRes = 30; // total number of results
var nrLstP = totalNoRes % nrpp;
var curPage; // current active page
var id="r";

function updateButtons(nextCliked){

  if(nextCliked == 1){
    var newVal = curPage;
    document.getElementById("page"+(curPage%10)).style.background = 'orange';
    document.getElementById("page"+(curPage%10)).style.color = 'white';
    for (var i=1; i<= 10; i++) {
      document.getElementById("page"+i).innerHTML = newVal++;
      if(newVal > np){
      	for (i++; i<= 10; i++){
      		document.getElementById("page"+i).innerHTML = newVal++;
      		document.getElementById("page"+i).style.background = "gray";
      	}	
      	break;
      }
    } 
  }else{
  	var newVal = curPage;
  	for (i = 1; i<= 10; i++){
      	document.getElementById("page"+i).style.background = "white";
    }
    document.getElementById("page"+10).style.background = 'orange';
    document.getElementById("page"+10).style.color = 'white';
    for (var i=10; i> 0; i--) {
      document.getElementById("page"+i).innerHTML = newVal--;
    } 
  }

}

function removeCurPage(){

	var i = (curPage-1)*nrpp + 1;
	if(curPage == np && nrLstP != 0){
		for (var j = 1; j <= nrLstP; j++) {
			document.getElementById(id + i).style.display = "none";
			i++;
		}
	}else{
		for (var j = 1; j <= nrpp; j++) {
			document.getElementById(id + i).style.display = "none";
			i++;
		}
	}
}

function viewPage(pn) {
	var i = (pn-1)*nrpp + 1;
	if(pn == np && nrLstP){
		for (var j = 1; j <= nrLstP; j++) {
			document.getElementById(id + i).style.display = "block";
			i++;
		}
	}else{
		for (var j = 1; j <= nrpp; j++) {
			document.getElementById(id + i).style.display = "block";
			i++;
		}
	}
}

function moveToPage(bid){
	console.log("moveToPage #"+bid);
	if(document.getElementById("page"+bid).innerHTML <= np){

		var tmp;
		if(curPage%10 == 0)tmp=10;
		else tmp = curPage%10;
		if(document.getElementById("page"+tmp).innerHTML != document.getElementById("page"+bid).innerHTML) {
			document.getElementById("page"+tmp).style.background = 'white';
			document.getElementById("page"+tmp).style.color = 'black';
			removeCurPage();
			curPage = document.getElementById("page"+bid).innerHTML;
			viewPage(curPage);
			if(curPage%10 == 0)tmp=10;
			else tmp = curPage%10;
			document.getElementById("page"+tmp).style.background = 'orange';
			document.getElementById("page"+tmp).style.color = 'white';
			window.scrollTo(0, 0);
		}
	}
}

window.onload = function () {

	var x = document.getElementById("page1");
	console.log(x);
	x.style.background = 'orange';
	x.style.color = 'white';
  	curPage = 1;
  for (var i = 1; i <= totalNoRes; i++) {
    x = document.getElementById(id + i);
    if(i>=1 && i<= nrpp){
       x.style.display = "block";
    }else{
       x.style.display = "none";
    }
  }
  np = Math.ceil(totalNoRes/nrpp);

  if(np == 1){
  	document.getElementById("pagingBar").style.display = "none";
  }

  for(var i = np+1; i <= 10; i++) {
    document.getElementById("page"+i).style.background = "gray";
  } 
}

window.onclick = function(event) {

var nextButton = document.getElementById("nxtbtn");
var PreviousButton = document.getElementById("prvbtn");

  
	if (event.target == nextButton) {

	    for (var i = 1; i <= totalNoRes; i++) {
		    
		    var x = document.getElementById(id + i);
		    if(x.style.display == "block" && curPage != np) {
		        for (var j = 1; j <= nrpp; j++) {
		          x.style.display = "none";
		          i++;
		          if(i > totalNoRes)break;
		          x = document.getElementById(id + i);
		        }
		        if(i <= totalNoRes){
		        for (var j = 1; j <= nrpp; j++) {
		          x.style.display = "block";
		          i++;
		          if(i > totalNoRes)break;
		          x = document.getElementById(id + i);
		        }
		      }
		    } 
	    }

	    if(curPage < np){
	    	var tmp;
	    	if(curPage%10 == 0)tmp=10;
	    	else tmp = curPage%10;
	    	document.getElementById("page"+tmp).style.background = 'white';
	   	    document.getElementById("page"+tmp).style.color = 'black';
	    	curPage++;
	    	tmp++;
	    	if ((curPage-1)%10 == 0) {
	    		updateButtons(1);
	    	}
	    	if(tmp <= 10){
		    	document.getElementById("page"+tmp).style.background = 'orange';
		   	    document.getElementById("page"+tmp).style.color = 'white';
		    	window.scrollTo(0, 0);
	    	}
	    }
	    console.log("curPage= " + curPage);

	}else if(event.target == PreviousButton){

	  if(curPage == np && nrLstP != 0){
	  	x = document.getElementById(id + totalNoRes);
	  	var i = totalNoRes;
	  	for (var j = 1; j <= nrLstP; j++){
	          x.style.display = "none"; 
	          i--;
	          if(j+1 > nrLstP)break;
	          x = document.getElementById(id + i);
	    }
	    if(i <= totalNoRes - nrLstP){
		    x = document.getElementById(id + i);
		    for (var j = 1; j <= nrpp; j++){
		          x.style.display = "block"; 
		          i--;
		          if(j+1 > nrpp)break;
		          x = document.getElementById(id + i);
		    }
		}
	  } else {

		  for (var i = totalNoRes; i >= 1; i--) {
		    x = document.getElementById(id + i);
		    if(x.style.display == "block" && curPage != 1){
		        for (var j = 1; j <= nrpp; j++){
		          x.style.display = "none"; 
		          i--;
		          x = document.getElementById(id + i);
		        }
		        for (var j = 1; j <= nrpp; j++){
		          x.style.display = "block"; 
		          i--;
		          x = document.getElementById(id + i);
		        }
		        break;
		    }
		  }
		}
	  	if(curPage  >= 2){

	  		var tmp;
	    	if(curPage%10 == 0)tmp=10;
	    	else tmp = curPage%10;
	  		document.getElementById("page"+tmp).style.background = 'white';
	   	    document.getElementById("page"+tmp).style.color = 'black';
	    	curPage--;
	    	if (curPage%10 == 0) {
	    		updateButtons(0);
	    	}
	    	tmp--;
	    	if(tmp >= 1){
		    	document.getElementById("page"+tmp).style.background = 'orange';
		   	    document.getElementById("page"+tmp).style.color = 'white';
				window.scrollTo(0, 0);
			}
	  	}
	    console.log("curPage= " + curPage);
	    
	}
}
