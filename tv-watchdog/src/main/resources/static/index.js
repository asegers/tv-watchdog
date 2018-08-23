document.getElementById("id01").innerHTML = "<img src=\"ajax-loader.gif\" alt=\"Loading... please wait..\">";

var showsToAddList = setAutoCompleteShowsList();

var url = "http://localhost:8080/getShows?shows=";


var currentSort = getUserSortMethodFromLocalStorage();

if (currentSort.localeCompare("alphabetical") == 0) {
	// have switch be to the Right (checked)
	document.getElementById("sortSwitch").checked = true;
}
else {
	document.getElementById("sortSwitch").checked = false;
}

var myShows = getUserShowsFromLocalStorage();

for (var i = 0; i < myShows.length; i++) {
	url += myShows[i];
	if (i < myShows.length - 1)
		url += ",";
}

var xmlhttp = new XMLHttpRequest();
xmlhttp.onreadystatechange = function() {
	if (this.readyState == 4 && this.status == 200) {
		var myArr = JSON.parse(this.responseText);
		console.log(myArr);
		myFunction(myArr);
	}
};
xmlhttp.open("GET", url, true);
xmlhttp.send();

function getUserShowsFromLocalStorage() {
	var stored = localStorage['myShowsJson'];
	if (stored) {
		myShows = JSON.parse(stored);
	} else {
		myShows = [ "breaking-bad", "westworld" ]; // default list
		localStorage['myShowsJson'] = JSON.stringify(myShows);
	}
	return myShows;
}

function getUserSortMethodFromLocalStorage() {
	var stored = localStorage['mySortMethod'];
	if (stored) {
		currentSort = stored;
	} else {
		currentSort = "best"; // default sort
		localStorage['mySortMethod'] = JSON.stringify(currentSort);
	}
	return currentSort;
}

function myFunction(arr) {
	shows = arr.shows;

	var out = "";
	if (currentSort.localeCompare("alphabetical") == 0) {
		console.log(shows);
		out = buildDisplaySortedAlpha(shows);
	} else {
		out = buildDisplaySortedBest(shows);
	}
    
	document.getElementById("id01").innerHTML = out;
}

function buildDisplaySortedAlpha(shows) {
	console.log(shows);
	shows = sortShowsByTitle(shows);
	console.log(shows);

	var out = addShowstoOutput(shows);
	return out;
}

function buildDisplaySortedBest(shows) {
	//neat sort: (currentlyAiring) "New Ep airs in...", (newSeasonHasDate) "New season premieres in...", [Ready for binging in...], 
	// (seasonEnded, < 1.5yr since lastEp) "Ready to binge:", (seasonAnnounced) "News:", (seasonEnded, >1.5yr) "Awaiting updates", (seriesEnded) "Ended series:"
	//temp workaround (re-assign each show's detail.. may want to move this to Java, or possibly move original detail assignment to js also)
	var newEpAirings = new Array();
	var newSeasonDates = new Array();
	var recentBingables = new Array();
	var news = new Array();
	var awaitingUpdates = new Array();
	var ended = new Array();
	for (var i = 0; i < shows.length; i++) {
		var status = shows[i].status;
		var show = shows[i];
		switch (status) {

		case "seasonCurrentlyAiring":
			var detail = show.detail;
			var newDetail = detail.replace(" till new episode", "");
			show.detail = newDetail;
			newEpAirings.push(show);
			break;

		case "newSeasonHasPremiereDate": // once interest levels are implemented, will need to update this logic (i.e. whether a series shows as "x days till new season" or "recently bingable"/etc.)
			var detail = show.detail;
			var newDetail = detail.replace(" premiere", "");
			show.detail = newDetail;
			newSeasonDates.push(show);
			break;

		case "seasonEnded":
			//if less than 1y
			// TO DO: group by recency (<3 mos., 3-6 mos, 6mos-year)
			var dateLastEp = new Date(show.latestEpisodeDate);
			var dateToday = new Date().getTime();
			var millisecondsSinceLastEpisode = dateToday - dateLastEp;
			var yearInMillisecs = 31540000000;
			if (millisecondsSinceLastEpisode <= yearInMillisecs) {
				var dateString = (dateLastEp.toDateString()).substr(4,
						(dateLastEp.toDateString()).length);
				show.detail = " - Season " + show.currentSeason + " (as of "
						+ dateString + ")";
				recentBingables.push(show);
			} else {
				show.detail = "";
				awaitingUpdates.push(show);
			}
			break;

		case "newSeasonAnnounced":
			show.detail = " - Season " + show.currentSeason
					+ " announced! (date TBA)";
			news.push(show);
			break;

		case "seriesEnded":
			ended.push(show);
			break;
		}
	}
	
	var out = "";
	
	if (null != newEpAirings[0]) {
		newEpAirings = sortShowsByDateAsc(newEpAirings);
		out += "New Episode airs in...<br>";
		out += addShowstoOutput(newEpAirings);
		out += "<br>";
	}

	if (null != newSeasonDates[0]) {
		newSeasonDates = sortShowsByDateAsc(newSeasonDates);
		out += "New Season premieres in...<br>";
		out += addShowstoOutput(newSeasonDates);
		out += "<br>";
	}

	if (null != recentBingables[0]) {
		recentBingables = sortShowsByDateDesc(recentBingables);
		out += "Ready to Binge:<br>";
		out += addShowstoOutput(recentBingables);
		out += "<br>";
	}

	if (null != news[0]) {
		news = sortShowsByTitle(news);
		out += "News:<br>";
		out += addShowstoOutput(news);
		out += "<br>";
	}

	if (null != awaitingUpdates[0]) {
		awaitingUpdates = sortShowsByTitle(awaitingUpdates);
		out += "Awaiting Updates...<br>";
		out += addShowstoOutput(awaitingUpdates);
		out += "<br>";
	}

	if (null != ended[0]) {
		ended = sortShowsByTitle(ended);
		out += "Completed Series:<br>";
		out += addShowstoOutput(ended);
	}
	return out;
}

function sortShowsByTitle(shows){
	shows.sort(function(a, b){
    	// if title doesn't start with "A " or "The ", then go ahead, else, compare without first word.
    	aTitle = (a.title).toUpperCase(); 
    	if(aTitle.startsWith("A ")) aTitle = aTitle.substr(2, aTitle.length - 1);
    	if(aTitle.startsWith("THE ")) aTitle = aTitle.substr(4, aTitle.length - 1);
    	
    	bTitle = (b.title).toUpperCase();
    	if(bTitle.startsWith("A ")) bTitle = bTitle.substr(2, bTitle.length - 1);
    	if(bTitle.startsWith("THE ")) bTitle = bTitle.substr(4, bTitle.length - 1);
    	
	  	return aTitle > bTitle;
	});
	return shows;
}

function sortShowsByDateAsc(shows) {
	shows.sort(function(a, b) {
		aDate = new Date(a.nextEpisodeDate).getTime();
		bDate = new Date(b.nextEpisodeDate).getTime();
		return aDate > bDate;
	});
	return shows;
}

function sortShowsByDateDesc(shows) {
	shows.sort(function(a, b) {
		aDate = new Date(a.nextEpisodeDate).getTime();
		bDate = new Date(b.nextEpisodeDate).getTime();
		return aDate < bDate;
	});
	return shows;
}

function addShowstoOutput(shows) {
	var tempOut = "";

	for (var i = 0; i < shows.length; i++) {
		var singleShowOutput = addSingleShowToTempOutputString(shows[i]);
		tempOut += singleShowOutput;

		// next line
		tempOut += "<br>";

		// check for extra break for alphabetical sort
		   if (currentSort.localeCompare("alphabetical") == 0 && null != shows[i+1]) {
		   	var currentTitle = shows[i].title.toUpperCase();
		   	var nextTitle = shows[i+1].title.toUpperCase();
		   	if(currentTitle.startsWith("A ")) currentTitle = currentTitle.substr(2, currentTitle.length - 1);
		   	if(currentTitle.startsWith("THE ")) currentTitle = currentTitle.substr(4, currentTitle.length - 1);
		   	if(nextTitle.startsWith("A ")) nextTitle = nextTitle.substr(2, nextTitle.length - 1);
		   	if(nextTitle.startsWith("THE ")) nextTitle = nextTitle.substr(4, nextTitle.length - 1);
		   	if (nextTitle.substr(0,1) > currentTitle.substr(0,1)) {
		   		tempOut += "<br>";
		   	}
		   } 
	}
	return tempOut;
}

function addSingleShowToTempOutputString(show) {
	var out = "";

	// TITLE
	showTitle = "<b>" + show.title + "</b>" + show.detail;

	// add calendar icon/date tooltip if it's a countdown detail.
	if ((show.status).localeCompare("seasonCurrentlyAiring") == 0
			|| (show.status).localeCompare("newSeasonHasPremiereDate") == 0) {
		out += showTitle
				+ " <div class=\"tooltip\"> <i class=\"fa fa-calendar\"></i> <span class=\"tooltiptext tooltip-right\">"
				+ show.hoverDate + "</span></div>";
	} else {
		out += showTitle;
	}
	return out;
}

function updateSort() {
	var aToZ = document.getElementById("sortSwitch");
	var url_string = window.location.href
	if (aToZ.checked) {
		localStorage['mySortMethod'] = "alphabetical";
	} else {
		localStorage['mySortMethod'] = "best";
	}
	setTimeout(refreshPage(), 1000);
}

function addShowAndRefresh(element) {
	// TO DO: Fix issue with adding a show with apostrophe (e.g. Marvel's Jessica Jones adds Luke Cage instead..)
	var searchTitle = document.getElementById("myInput").value;
	var slug = "";

	for (var i = 0; i < showsToAddList.length; i++) {
		if (showsToAddList[i].title.startsWith(searchTitle)) {
			slug = showsToAddList[i].slug;
			break;
		}
	}

	// TO DO: If no matches in the Top 200 list, then need to search Mongodb, have modal pop-up, user select correct show, etc.

	// Getting user's shows from local storage...
	var stored = localStorage['myShowsJson'];
	if (stored) {
		myShows = JSON.parse(stored);
	} else {
		myShows = new Array();
	}

	myShows.push(slug);
	localStorage['myShowsJson'] = JSON.stringify(myShows);

	refreshPage();
}

function refreshPage() {
	window.location.reload(true);
}

function clearMyShows() {
	localStorage.removeItem("myShowsJson");
	refreshPage();
}

function autocomplete(inp, arr) {
	/*the autocomplete function takes two arguments,
	the text field element and an array of possible autocompleted values:*/
	var currentFocus;
	/*execute a function when someone writes in the text field:*/
	inp.addEventListener("input", function(e) {
		var a, b, i, val = this.value;
		/*close any already open lists of autocompleted values*/
		closeAllLists();
		if (!val) {
			return false;
		}
		currentFocus = -1;
		/*create a DIV element that will contain the items (values):*/
		a = document.createElement("DIV");
		a.setAttribute("id", this.id + "autocomplete-list");
		a.setAttribute("class", "autocomplete-items");
		/*append the DIV element as a child of the autocomplete container:*/
		this.parentNode.appendChild(a);
		/*for each item in the array...*/
		for (i = 0; i < arr.length; i++) {
			/*check if the item starts with the same letters as the text field value:*/
			if (arr[i].title.substr(0, val.length).toUpperCase() == val
					.toUpperCase()) {
				/*create a DIV element for each matching element:*/
				b = document.createElement("DIV");
				/*make the matching letters bold:*/
				b.innerHTML = "<strong>" + arr[i].title.substr(0, val.length)
						+ "</strong>";
				b.innerHTML += arr[i].title.substr(val.length);
				/*insert a input field that will hold the current array item's value:*/
				b.innerHTML += "<input type='hidden' value='" + arr[i].title
						+ "'>";
				/*execute a function when someone clicks on the item value (DIV element):*/
				b.addEventListener("click", function(e) {
					/*insert the value for the autocomplete text field:*/
					inp.value = this.getElementsByTagName("input")[0].value;
					/*close the list of autocompleted values,
					(or any other open lists of autocompleted values:*/
					closeAllLists();
				});
				a.appendChild(b);
			}
		}
	});
	/*execute a function presses a key on the keyboard:*/
	inp.addEventListener("keydown", function(e) {
		var x = document.getElementById(this.id + "autocomplete-list");
		if (x)
			x = x.getElementsByTagName("div");
		if (e.keyCode == 40) {
			/*If the arrow DOWN key is pressed,
			increase the currentFocus variable:*/
			currentFocus++;
			/*and and make the current item more visible:*/
			addActive(x);
		} else if (e.keyCode == 38) { //up
			/*If the arrow UP key is pressed,
			decrease the currentFocus variable:*/
			currentFocus--;
			/*and and make the current item more visible:*/
			addActive(x);
		} else if (e.keyCode == 13) {
			/*If the ENTER key is pressed, prevent the form from being submitted,*/
			e.preventDefault();
			if (currentFocus > -1) {
				/*and simulate a click on the "active" item:*/
				if (x)
					x[currentFocus].click();
			}
		}
	});
	function addActive(x) {
		/*a function to classify an item as "active":*/
		if (!x)
			return false;
		/*start by removing the "active" class on all items:*/
		removeActive(x);
		if (currentFocus >= x.length)
			currentFocus = 0;
		if (currentFocus < 0)
			currentFocus = (x.length - 1);
		/*add class "autocomplete-active":*/
		x[currentFocus].classList.add("autocomplete-active");
	}
	function removeActive(x) {
		/*a function to remove the "active" class from all autocomplete items:*/
		for (var i = 0; i < x.length; i++) {
			x[i].classList.remove("autocomplete-active");
		}
	}
	function closeAllLists(elmnt) {
		/*close all autocomplete lists in the document,
		except the one passed as an argument:*/
		var x = document.getElementsByClassName("autocomplete-items");
		for (var i = 0; i < x.length; i++) {
			if (elmnt != x[i] && elmnt != inp) {
				x[i].parentNode.removeChild(x[i]);
			}
		}
	}
	/*execute a function when someone clicks in the document:*/
	document.addEventListener("click", function(e) {
		closeAllLists(e.target);
	});
}	

function setAutoCompleteShowsList() {
	var showsToAddList = [ {
		"title" : "Game of Thrones",
		"slug" : "game-of-thrones"
	}, {
		"title" : "The Walking Dead",
		"slug" : "the-walking-dead"
	}, {
		"title" : "The Big Bang Theory",
		"slug" : "the-big-bang-theory"
	}, {
		"title" : "Sherlock",
		"slug" : "sherlock"
	}, {
		"title" : "Arrow",
		"slug" : "arrow"
	}, {
		"title" : "Homeland",
		"slug" : "homeland"
	}, {
		"title" : "House of Cards",
		"slug" : "house-of-cards"
	}, {
		"title" : "Supernatural",
		"slug" : "supernatural"
	}, {
		"title" : "Suits",
		"slug" : "suits"
	}, {
		"title" : "Stranger Things",
		"slug" : "stranger-things"
	}, {
		"title" : "Modern Family",
		"slug" : "modern-family"
	}, {
		"title" : "Orange Is the New Black",
		"slug" : "orange-is-the-new-black"
	}, {
		"title" : "The Flash",
		"slug" : "the-flash-2014"
	}, {
		"title" : "Vikings",
		"slug" : "vikings"
	}, {
		"title" : "Doctor Who",
		"slug" : "doctor-who-2005"
	}, {
		"title" : "The Simpsons",
		"slug" : "the-simpsons"
	}, {
		"title" : "Marvel's Agents of S.H.I.E.L.D.",
		"slug" : "marvel-s-agents-of-s-h-i-e-l-d"
	}, {
		"title" : "True Detective",
		"slug" : "true-detective"
	}, {
		"title" : "American Horror Story",
		"slug" : "american-horror-story"
	}, {
		"title" : "Mr. Robot",
		"slug" : "mr-robot"
	}, {
		"title" : "Prison Break",
		"slug" : "prison-break"
	}, {
		"title" : "South Park",
		"slug" : "south-park"
	}, {
		"title" : "Family Guy",
		"slug" : "family-guy"
	}, {
		"title" : "Marvel's Daredevil",
		"slug" : "marvel-s-daredevil"
	}, {
		"title" : "The Blacklist",
		"slug" : "the-blacklist"
	}, {
		"title" : "Westworld",
		"slug" : "westworld"
	}, {
		"title" : "The 100",
		"slug" : "the-100"
	}, {
		"title" : "Black Mirror",
		"slug" : "black-mirror"
	}, {
		"title" : "Gotham",
		"slug" : "gotham"
	}, {
		"title" : "Grey's Anatomy",
		"slug" : "grey-s-anatomy"
	}, {
		"title" : "Elementary",
		"slug" : "elementary"
	}, {
		"title" : "Rick and Morty",
		"slug" : "rick-and-morty"
	}, {
		"title" : "Shameless",
		"slug" : "shameless-2011"
	}, {
		"title" : "Fargo",
		"slug" : "fargo"
	}, {
		"title" : "Better Call Saul",
		"slug" : "better-call-saul"
	}, {
		"title" : "Brooklyn Nine-Nine",
		"slug" : "brooklyn-nine-nine"
	}, {
		"title" : "Arrested Development",
		"slug" : "arrested-development"
	}, {
		"title" : "Silicon Valley",
		"slug" : "silicon-valley"
	}, {
		"title" : "Marvel's Jessica Jones",
		"slug" : "marvel-s-jessica-jones"
	}, {
		"title" : "Archer",
		"slug" : "archer"
	}, {
		"title" : "Narcos",
		"slug" : "narcos"
	}, {
		"title" : "Top Gear",
		"slug" : "top-gear"
	}, {
		"title" : "How to Get Away with Murder",
		"slug" : "how-to-get-away-with-murder"
	}, {
		"title" : "Criminal Minds",
		"slug" : "criminal-minds"
	}, {
		"title" : "It's Always Sunny in Philadelphia",
		"slug" : "it-s-always-sunny-in-philadelphia"
	}, {
		"title" : "NCIS",
		"slug" : "ncis"
	}, {
		"title" : "13 Reasons Why",
		"slug" : "13-reasons-why"
	}, {
		"title" : "Lucifer",
		"slug" : "lucifer"
	}, {
		"title" : "American Dad!",
		"slug" : "american-dad"
	}, {
		"title" : "Attack on Titan",
		"slug" : "attack-on-titan"
	}, {
		"title" : "Fear the Walking Dead",
		"slug" : "fear-the-walking-dead"
	}, {
		"title" : "Luther",
		"slug" : "luther"
	}, {
		"title" : "Supergirl",
		"slug" : "supergirl"
	}, {
		"title" : "Marvel's Luke Cage",
		"slug" : "marvel-s-luke-cage"
	}, {
		"title" : "MythBusters",
		"slug" : "mythbusters"
	}, {
		"title" : "The Expanse",
		"slug" : "the-expanse"
	}, {
		"title" : "Blindspot",
		"slug" : "blindspot"
	}, {
		"title" : "Bob's Burgers",
		"slug" : "bob-s-burgers"
	}, {
		"title" : "The Last Ship",
		"slug" : "the-last-ship"
	}, {
		"title" : "Adventure Time",
		"slug" : "adventure-time"
	}, {
		"title" : "Ray Donovan",
		"slug" : "ray-donovan"
	}, {
		"title" : "DC's Legends of Tomorrow",
		"slug" : "dc-s-legends-of-tomorrow"
	}, {
		"title" : "Peaky Blinders",
		"slug" : "peaky-blinders"
	}, {
		"title" : "Hawaii Five-0",
		"slug" : "hawaii-five-0"
	}, {
		"title" : "Last Week Tonight with John Oliver",
		"slug" : "last-week-tonight-with-john-oliver"
	}, {
		"title" : "iZombie",
		"slug" : "izombie"
	}, {
		"title" : "Marvel's Iron Fist",
		"slug" : "marvel-s-iron-fist"
	}, {
		"title" : "Outlander",
		"slug" : "outlander"
	}, {
		"title" : "Legion",
		"slug" : "legion"
	}, {
		"title" : "Altered Carbon",
		"slug" : "altered-carbon"
	}, {
		"title" : "The Daily Show",
		"slug" : "the-daily-show"
	}, {
		"title" : "The Handmaid's Tale",
		"slug" : "the-handmaid-s-tale"
	}, {
		"title" : "One Piece",
		"slug" : "one-piece"
	}, {
		"title" : "Marvel's The Punisher",
		"slug" : "marvel-s-the-punisher"
	}, {
		"title" : "The Man in the High Castle",
		"slug" : "the-man-in-the-high-castle"
	}, {
		"title" : "Preacher",
		"slug" : "preacher"
	}, {
		"title" : "Veep",
		"slug" : "veep"
	}, {
		"title" : "American Gods",
		"slug" : "american-gods"
	}, {
		"title" : "BoJack Horseman",
		"slug" : "bojack-horseman"
	}, {
		"title" : "NCIS: Los Angeles",
		"slug" : "ncis-los-angeles"
	}, {
		"title" : "One-Punch Man",
		"slug" : "one-punch-man"
	}, {
		"title" : "The OA",
		"slug" : "the-oa"
	}, {
		"title" : "Curb Your Enthusiasm",
		"slug" : "curb-your-enthusiasm"
	}, {
		"title" : "Unbreakable Kimmy Schmidt",
		"slug" : "unbreakable-kimmy-schmidt"
	}, {
		"title" : "Chicago Fire",
		"slug" : "chicago-fire"
	}, {
		"title" : "Mindhunter",
		"slug" : "mindhunter"
	}, {
		"title" : "Taboo",
		"slug" : "taboo-2017"
	}, {
		"title" : "Sword Art Online",
		"slug" : "sword-art-online"
	}, {
		"title" : "Star Trek: Discovery",
		"slug" : "star-trek-discovery"
	}, {
		"title" : "The Magicians",
		"slug" : "the-magicians-2015"
	}, {
		"title" : "Billions",
		"slug" : "billions"
	}, {
		"title" : "This Is Us",
		"slug" : "this-is-us"
	}, {
		"title" : "Star Wars: The Clone Wars",
		"slug" : "star-wars-the-clone-wars"
	}, {
		"title" : "Big Little Lies",
		"slug" : "big-little-lies"
	}, {
		"title" : "DARK",
		"slug" : "dark"
	}, {
		"title" : "Master of None",
		"slug" : "master-of-none"
	}, {
		"title" : "Humans",
		"slug" : "humans"
	}, {
		"title" : "The Night Manager",
		"slug" : "the-night-manager"
	}, {
		"title" : "Riverdale",
		"slug" : "riverdale"
	}, {
		"title" : "The End of the F***ing World",
		"slug" : "the-end-of-the-f-ing-world"
	}, {
		"title" : "Law & Order: Special Victims Unit",
		"slug" : "law-order-special-victims-unit"
	}, {
		"title" : "Making a Murderer",
		"slug" : "making-a-murderer"
	}, {
		"title" : "Money Heist",
		"slug" : "money-heist"
	}, {
		"title" : "American Crime Story",
		"slug" : "american-crime-story"
	}, {
		"title" : "The Good Place",
		"slug" : "the-good-place"
	}, {
		"title" : "The Crown",
		"slug" : "the-crown"
	}, {
		"title" : "Ozark",
		"slug" : "ozark"
	}, {
		"title" : "A Series of Unfortunate Events",
		"slug" : "a-series-of-unfortunate-events"
	}, {
		"title" : "The Grand Tour",
		"slug" : "the-grand-tour"
	}, {
		"title" : "Shadowhunters",
		"slug" : "shadowhunters"
	}, {
		"title" : "Strike Back",
		"slug" : "strike-back"
	}, {
		"title" : "Z Nation",
		"slug" : "z-nation"
	}, {
		"title" : "Power",
		"slug" : "power"
	}, {
		"title" : "Into the Badlands",
		"slug" : "into-the-badlands"
	}, {
		"title" : "Empire",
		"slug" : "empire-2015"
	}, {
		"title" : "Chicago P.D.",
		"slug" : "chicago-p-d"
	}, {
		"title" : "Lethal Weapon",
		"slug" : "lethal-weapon"
	}, {
		"title" : "Blue Bloods",
		"slug" : "blue-bloods"
	}, {
		"title" : "Jane the Virgin",
		"slug" : "jane-the-virgin"
	}, {
		"title" : "Timeless",
		"slug" : "timeless-2016"
	}, {
		"title" : "Saturday Night Live",
		"slug" : "saturday-night-live"
	}, {
		"title" : "Santa Clarita Diet",
		"slug" : "santa-clarita-diet"
	}, {
		"title" : "Ballers",
		"slug" : "ballers"
	}, {
		"title" : "Mom",
		"slug" : "mom"
	}, {
		"title" : "Red Dwarf",
		"slug" : "red-dwarf"
	}, {
		"title" : "The Orville",
		"slug" : "the-orville"
	}, {
		"title" : "You're the Worst",
		"slug" : "you-re-the-worst"
	}, {
		"title" : "Atlanta",
		"slug" : "atlanta"
	}, {
		"title" : "The Last Kingdom",
		"slug" : "the-last-kingdom"
	}, {
		"title" : "Fairy Tail",
		"slug" : "fairy-tail"
	}, {
		"title" : "SpongeBob SquarePants",
		"slug" : "spongebob-squarepants"
	}, {
		"title" : "Survivor",
		"slug" : "survivor-2000"
	}, {
		"title" : "The Affair",
		"slug" : "the-affair"
	}, {
		"title" : "Broad City",
		"slug" : "broad-city"
	}, {
		"title" : "Scream: The TV Series",
		"slug" : "scream-the-tv-series-2015"
	}, {
		"title" : "Tokyo Ghoul",
		"slug" : "tokyo-ghoul"
	}, {
		"title" : "Last Man Standing",
		"slug" : "last-man-standing-2011"
	}, {
		"title" : "Robot Chicken",
		"slug" : "robot-chicken"
	}, {
		"title" : "Pokémon",
		"slug" : "pokemon-1997"
	}, {
		"title" : "The Goldbergs",
		"slug" : "the-goldbergs-2013"
	}, {
		"title" : "Killjoys",
		"slug" : "killjoys"
	}, {
		"title" : "Shooter",
		"slug" : "shooter"
	}, {
		"title" : "Tosh.0",
		"slug" : "tosh-0"
	}, {
		"title" : "QI",
		"slug" : "qi"
	}, {
		"title" : "Travelers",
		"slug" : "travelers-2016"
	}, {
		"title" : "Lost in Space",
		"slug" : "lost-in-space-2018"
	}, {
		"title" : "The Good Doctor",
		"slug" : "the-good-doctor"
	}, {
		"title" : "Steven Universe",
		"slug" : "steven-universe"
	}, {
		"title" : "Young Justice",
		"slug" : "young-justice"
	}, {
		"title" : "Madam Secretary",
		"slug" : "madam-secretary"
	}, {
		"title" : "The Voice",
		"slug" : "the-voice-2011"
	}, {
		"title" : "Dragon Ball Super",
		"slug" : "dragon-ball-super"
	}, {
		"title" : "Hell's Kitchen",
		"slug" : "hell-s-kitchen-2005"
	}, {
		"title" : "Whose Line is it Anyway?",
		"slug" : "whose-line-is-it-anyway-1998"
	}, {
		"title" : "The Sinner",
		"slug" : "the-sinner"
	}, {
		"title" : "The Gifted",
		"slug" : "the-gifted"
	}, {
		"title" : "Fresh Off the Boat",
		"slug" : "fresh-off-the-boat-2015"
	}, {
		"title" : "My Hero Academia",
		"slug" : "my-hero-academia"
	}, {
		"title" : "Bosch",
		"slug" : "bosch"
	}, {
		"title" : "The Venture Bros.",
		"slug" : "the-venture-bros"
	}, {
		"title" : "Bitten",
		"slug" : "bitten"
	}, {
		"title" : "Breaking Bad (2008-2013)",
		"slug" : "breaking-bad"
	}, {
		"title" : "Dexter (2006-2013)",
		"slug" : "dexter"
	}, {
		"title" : "How I Met Your Mother (2005-2014)",
		"slug" : "how-i-met-your-mother"
	}, {
		"title" : "Friends (1994-2004)",
		"slug" : "friends"
	}, {
		"title" : "Lost (2004-2010)",
		"slug" : "lost-2004"
	}, {
		"title" : "House (2004-2012)",
		"slug" : "house"
	}, {
		"title" : "Fringe (2008-2013)",
		"slug" : "fringe"
	}, {
		"title" : "Firefly (2002-2003)",
		"slug" : "firefly"
	}, {
		"title" : "Community (2009-2015)",
		"slug" : "community"
	}, {
		"title" : "Person of Interest (2011-2016)",
		"slug" : "person-of-interest"
	}, {
		"title" : "True Blood (2008-2014)",
		"slug" : "true-blood"
	}, {
		"title" : "Futurama (1999-2013)",
		"slug" : "futurama"
	}, {
		"title" : "Once Upon a Time (2011-2018)",
		"slug" : "once-upon-a-time"
	}, {
		"title" : "Sons of Anarchy (2008-2014)",
		"slug" : "sons-of-anarchy"
	}, {
		"title" : "New Girl (2011-2018)",
		"slug" : "new-girl"
	}, {
		"title" : "Battlestar Galactica (2004-2009)",
		"slug" : "battlestar-galactica-2004"
	}, {
		"title" : "Hannibal (2013-2015)",
		"slug" : "hannibal"
	}, {
		"title" : "The Vampire Diaries (2009-2017)",
		"slug" : "the-vampire-diaries"
	}, {
		"title" : "Band of Brothers (2001-2001)",
		"slug" : "band-of-brothers"
	}, {
		"title" : "The Office (2005-2013)",
		"slug" : "the-office"
	}, {
		"title" : "Parks and Recreation (2009-2015)",
		"slug" : "parks-and-recreation"
	}, {
		"title" : "Chuck (2007-2012)",
		"slug" : "chuck"
	}, {
		"title" : "The Wire (2002-2008)",
		"slug" : "the-wire"
	}, {
		"title" : "Castle (2009-2016)",
		"slug" : "castle"
	}, {
		"title" : "Orphan Black (2013-2017)",
		"slug" : "orphan-black"
	}, {
		"title" : "Heroes (2006-2010)",
		"slug" : "heroes"
	}, {
		"title" : "Californication (2007-2014)",
		"slug" : "californication"
	}, {
		"title" : "Under the Dome (2013-2015)",
		"slug" : "under-the-dome"
	}, {
		"title" : "Scrubs (2001-2010)",
		"slug" : "scrubs"
	}, {
		"title" : "The Mentalist (2008-2015)",
		"slug" : "the-mentalist"
	}, {
		"title" : "The IT Crowd (2006-2010)",
		"slug" : "the-it-crowd"
	}, {
		"title" : "Spartacus (2010-2013)",
		"slug" : "spartacus"
	}, {
		"title" : "24 (2001-2003)",
		"slug" : "24"
	}, {
		"title" : "The Sopranos (1999-2007)",
		"slug" : "the-sopranos"
	}, {
		"title" : "White Collar (2009-2014)",
		"slug" : "white-collar"
	}, {
		"title" : "The X-Files (1993-2018)",
		"slug" : "the-x-files"
	}, {
		"title" : "Two and a Half Men (2003-2015)",
		"slug" : "two-and-a-half-men"
	}, {
		"title" : "Grimm (2011-2017)",
		"slug" : "grimm"
	}, {
		"title" : "Mad Men (2007-2015)",
		"slug" : "mad-men"
	}, {
		"title" : "Falling Skies (2011-2015)",
		"slug" : "falling-skies"
	}, {
		"title" : "Bones (2005-2017)",
		"slug" : "bones"
	}, {
		"title" : "Avatar: The Last Airbender (2005-2008)",
		"slug" : "avatar-the-last-airbender"
	}, {
		"title" : "Boardwalk Empire (2010-2014)",
		"slug" : "boardwalk-empire"
	}, {
		"title" : "2 Broke Girls (2011-2017)",
		"slug" : "2-broke-girls"
	}, {
		"title" : "Seinfeld (1989-1998)",
		"slug" : "seinfeld"
	}, {
		"title" : "Death Note (2006-2007)",
		"slug" : "death-note"
	}, {
		"title" : "Teen Wolf (2011-2017)",
		"slug" : "teen-wolf-2011"
	}, {
		"title" : "Pretty Little Liars (2010-2017)",
		"slug" : "pretty-little-liars"
	}, {
		"title" : "Sense8 (2015-2018)",
		"slug" : "sense8"
	}, {
		"title" : "Stargate SG-1 (1997-2007)",
		"slug" : "stargate-sg-1"
	}, {
		"title" : "Revolution (2012-2014)",
		"slug" : "revolution"
	}, {
		"title" : "Revenge (2011-2015)",
		"slug" : "revenge"
	}, {
		"title" : "Buffy the Vampire Slayer (1997-2003)",
		"slug" : "buffy-the-vampire-slayer"
	}, {
		"title" : "Continuum (2012-2015)",
		"slug" : "continuum"
	}, {
		"title" : "Glee (2009-2015)",
		"slug" : "glee"
	}, {
		"title" : "Weeds (2005-2012)",
		"slug" : "weeds"
	}, {
		"title" : "Misfits (2009-2013)",
		"slug" : "misfits"
	}, {
		"title" : "The Newsroom (2012-2014)",
		"slug" : "the-newsroom"
	}, {
		"title" : "The Following (2013-2015)",
		"slug" : "the-following"
	}, {
		"title" : "Penny Dreadful (2014-2016)",
		"slug" : "penny-dreadful"
	}, {
		"title" : "30 Rock (2006-2013)",
		"slug" : "30-rock"
	}, {
		"title" : "The Americans (2013-2018)",
		"slug" : "the-americans-2013"
	}, {
		"title" : "Downton Abbey (2010-2015)",
		"slug" : "downton-abbey"
	}, {
		"title" : "Lie to Me (2009-2011)",
		"slug" : "lie-to-me"
	}, {
		"title" : "Smallville (2001-2011)",
		"slug" : "smallville"
	}, {
		"title" : "Star Trek: The Next Generation (1987-1994)",
		"slug" : "star-trek-the-next-generation"
	}, {
		"title" : "Bates Motel (2013-2017)",
		"slug" : "bates-motel"
	}, {
		"title" : "Banshee (2013-2016)",
		"slug" : "banshee"
	}, {
		"title" : "Black Sails (2014-2017)",
		"slug" : "black-sails"
	}, {
		"title" : "Eureka (2006-2012)",
		"slug" : "eureka"
	}, {
		"title" : "The Originals (2013-2018)",
		"slug" : "the-originals"
	}, {
		"title" : "Entourage (2004-2011)",
		"slug" : "entourage"
	}, {
		"title" : "Stargate Atlantis (2004-2009)",
		"slug" : "stargate-atlantis"
	}, {
		"title" : "The Legend of Korra (2012-2014)",
		"slug" : "the-legend-of-korra"
	}, {
		"title" : "The Strain (2014-2017)",
		"slug" : "the-strain"
	}, {
		"title" : "Gossip Girl (2007-2012)",
		"slug" : "gossip-girl"
	}, {
		"title" : "Justified (2010-2015)",
		"slug" : "justified"
	}, {
		"title" : "The Good Wife (2009-2016)",
		"slug" : "the-good-wife"
	}, {
		"title" : "Almost Human (2013-2014)",
		"slug" : "almost-human"
	}, {
		"title" : "Warehouse 13 (2009-2014)",
		"slug" : "warehouse-13"
	}, {
		"title" : "Rome (2005-2007)",
		"slug" : "rome"
	}, {
		"title" : "Sleepy Hollow (2013-2017)",
		"slug" : "sleepy-hollow"
	}, {
		"title" : "Psych (2006-2014)",
		"slug" : "psych"
	}, {
		"title" : "That '70s Show (1998-2006)",
		"slug" : "that-70s-show"
	}, {
		"title" : "Da Vinci's Demons (2013-2015)",
		"slug" : "da-vinci-s-demons"
	}, {
		"title" : "Twin Peaks (1990-2017)",
		"slug" : "twin-peaks"
	}, {
		"title" : "Burn Notice (2007-2013)",
		"slug" : "burn-notice"
	}, {
		"title" : "The Killing (2011-2014)",
		"slug" : "the-killing-2011"
	}, {
		"title" : "The Leftovers (2014-2017)",
		"slug" : "the-leftovers"
	}, {
		"title" : "Scandal (2012-2018)",
		"slug" : "scandal"
	}, {
		"title" : "Marvel's Agent Carter (2015-2016)",
		"slug" : "marvel-s-agent-carter"
	}, {
		"title" : "Gilmore Girls (2000-2007)",
		"slug" : "gilmore-girls"
	}, {
		"title" : "Veronica Mars (2004-2007)",
		"slug" : "veronica-mars"
	}, {
		"title" : "Terra Nova (2011-2011)",
		"slug" : "terra-nova"
	}, {
		"title" : "Merlin (2008-2012)",
		"slug" : "merlin"
	}, {
		"title" : "Six Feet Under (2001-2005)",
		"slug" : "six-feet-under"
	}, {
		"title" : "Desperate Housewives (2004-2012)",
		"slug" : "desperate-housewives"
	}, {
		"title" : "Stargate Universe (2009-2011)",
		"slug" : "stargate-universe"
	}, {
		"title" : "Cosmos: A Spacetime Odyssey (2014-2014)",
		"slug" : "cosmos-a-spacetime-odyssey"
	}, {
		"title" : "Nikita (2010-2013)",
		"slug" : "nikita"
	}, {
		"title" : "Fullmetal Alchemist: Brotherhood (2009-2010)",
		"slug" : "fullmetal-alchemist-brotherhood"
	}, {
		"title" : "Louie (2010-2015)",
		"slug" : "louie"
	}, {
		"title" : "Star Trek: Voyager (1995-2001)",
		"slug" : "star-trek-voyager"
	}, {
		"title" : "Scorpion (2014-2018)",
		"slug" : "scorpion"
	}, {
		"title" : "Defiance (2013-2015)",
		"slug" : "defiance"
	}, {
		"title" : "The Pacific (2010-2010)",
		"slug" : "the-pacific"
	}, {
		"title" : "Limitless (2015-2016)",
		"slug" : "limitless"
	}, {
		"title" : "Skins (2007-2013)",
		"slug" : "skins"
	}, {
		"title" : "Constantine (2014-2015)",
		"slug" : "constantine"
	}, {
		"title" : "Broadchurch (2013-2017)",
		"slug" : "broadchurch"
	}, {
		"title" : "Dollhouse (2009-2010)",
		"slug" : "dollhouse"
	}, {
		"title" : "Deadwood (2004-2006)",
		"slug" : "deadwood"
	}, {
		"title" : "Freaks and Geeks (1999-2000)",
		"slug" : "freaks-and-geeks"
	}, {
		"title" : "Naruto Shippuden (2007-2017)",
		"slug" : "naruto-shippuden"
	}, {
		"title" : "The Shield (2002-2008)",
		"slug" : "the-shield"
	}, {
		"title" : "V (2009-2011)",
		"slug" : "v-2009"
	}, {
		"title" : "My Name Is Earl (2005-2009)",
		"slug" : "my-name-is-earl"
	}, {
		"title" : "Planet Earth (2006-2006)",
		"slug" : "planet-earth"
	}, {
		"title" : "Hell on Wheels (2011-2016)",
		"slug" : "hell-on-wheels"
	}, {
		"title" : "Malcolm in the Middle (2000-2006)",
		"slug" : "malcolm-in-the-middle"
	}, {
		"title" : "Dragon Ball Z (1989-1996)",
		"slug" : "dragon-ball-z"
	}, {
		"title" : "The Fresh Prince of Bel-Air (1990-1996)",
		"slug" : "the-fresh-prince-of-bel-air"
	}, {
		"title" : "Torchwood (2006-2011)",
		"slug" : "torchwood"
	}, {
		"title" : "Leverage (2008-2012)",
		"slug" : "leverage"
	}, {
		"title" : "Haven (2010-2015)",
		"slug" : "haven"
	}, {
		"title" : "Helix (2014-2015)",
		"slug" : "helix"
	}, {
		"title" : "Cowboy Bebop (1998-1999)",
		"slug" : "cowboy-bebop"
	}, {
		"title" : "Alphas (2011-2012)",
		"slug" : "alphas"
	} ];
	autocomplete(document.getElementById("myInput"), showsToAddList);
	return showsToAddList;
}