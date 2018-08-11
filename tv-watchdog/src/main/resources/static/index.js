document.getElementById("id01").innerHTML = "<img src=\"ajax-loader.gif\" alt=\"Loading... please wait..\">";

var shows;
var urlWithShowsString = "http://localhost:8080/?sort=";

/*
var url_string = window.location.href
var urlForSort = new URL(url_string);
var sortSwitch = urlForSort.searchParams.get("sort");

//var sortSwitch = "test"; // need to add to url (get from url)
//var sortSwitch = "alphabetical"; // need to add to url (get from url)
if (sortSwitch.localeCompare("alphabetical") == 0) {
	// have switch be to Right (checked)
	document.getElementById("sortSwitch").checked = true;
	urlWithShowsString += "alphabetical";
}
else {
	//switch to Left (unchecked)
	document.getElementById("sortSwitch").checked = false;
	urlWithShowsString += "default";
}

urlWithShowsString += "&shows=";
*/

var showsToAddList = [{"title": "Game of Thrones", "slug": "game-of-thrones"}, {"title": "The Walking Dead", "slug": "the-walking-dead"}, {"title": "The Big Bang Theory", "slug": "the-big-bang-theory"}, {"title": "Sherlock", "slug": "sherlock"}, {"title": "Arrow", "slug": "arrow"}, {"title": "Homeland", "slug": "homeland"}, {"title": "House of Cards", "slug": "house-of-cards"}, {"title": "Suits", "slug": "suits"}, {"title": "Supernatural", "slug": "supernatural"}, {"title": "Modern Family", "slug": "modern-family"}, {"title": "Stranger Things", "slug": "stranger-things"}, {"title": "Orange Is the New Black", "slug": "orange-is-the-new-black"}, {"title": "The Flash", "slug": "the-flash-2014"}, {"title": "Doctor Who", "slug": "doctor-who-2005"}, {"title": "Vikings", "slug": "vikings"}, {"title": "The Simpsons", "slug": "the-simpsons"}, {"title": "Marvel's Agents of S.H.I.E.L.D.", "slug": "marvel-s-agents-of-s-h-i-e-l-d"}, {"title": "True Detective", "slug": "true-detective"}, {"title": "American Horror Story", "slug": "american-horror-story"}, {"title": "Mr. Robot", "slug": "mr-robot"}, {"title": "Family Guy", "slug": "family-guy"}, {"title": "South Park", "slug": "south-park"}, {"title": "Marvel's Daredevil", "slug": "marvel-s-daredevil"}, {"title": "The Blacklist", "slug": "the-blacklist"}, {"title": "Westworld", "slug": "westworld"}, {"title": "The 100", "slug": "the-100"}, {"title": "Black Mirror", "slug": "black-mirror"}, {"title": "Gotham", "slug": "gotham"}, {"title": "Grey's Anatomy", "slug": "grey-s-anatomy"}, {"title": "Elementary", "slug": "elementary"}, {"title": "Rick and Morty", "slug": "rick-and-morty"}, {"title": "Fargo", "slug": "fargo"}, {"title": "Shameless", "slug": "shameless-2011"}, {"title": "Better Call Saul", "slug": "better-call-saul"}, {"title": "Arrested Development", "slug": "arrested-development"}, {"title": "Brooklyn Nine-Nine", "slug": "brooklyn-nine-nine"}, {"title": "Silicon Valley", "slug": "silicon-valley"}, {"title": "Marvel's Jessica Jones", "slug": "marvel-s-jessica-jones"}, {"title": "The X-Files", "slug": "the-x-files"}, {"title": "Archer", "slug": "archer"}, {"title": "Narcos", "slug": "narcos"}, {"title": "Top Gear", "slug": "top-gear"}, {"title": "Criminal Minds", "slug": "criminal-minds"}, {"title": "How to Get Away with Murder", "slug": "how-to-get-away-with-murder"}, {"title": "It's Always Sunny in Philadelphia", "slug": "it-s-always-sunny-in-philadelphia"}, {"title": "NCIS", "slug": "ncis"}, {"title": "Lucifer", "slug": "lucifer"}, {"title": "13 Reasons Why", "slug": "13-reasons-why"}, {"title": "American Dad!", "slug": "american-dad"}, {"title": "Attack on Titan", "slug": "attack-on-titan"}, {"title": "Luther", "slug": "luther"}, {"title": "Fear the Walking Dead", "slug": "fear-the-walking-dead"}, {"title": "The Originals", "slug": "the-originals"}, {"title": "Supergirl", "slug": "supergirl"}, {"title": "Twin Peaks", "slug": "twin-peaks"}, {"title": "MythBusters", "slug": "mythbusters"}, {"title": "Marvel's Luke Cage", "slug": "marvel-s-luke-cage"}, {"title": "Blindspot", "slug": "blindspot"}, {"title": "The Last Ship", "slug": "the-last-ship"}, {"title": "Bob's Burgers", "slug": "bob-s-burgers"}, {"title": "Ray Donovan", "slug": "ray-donovan"}, {"title": "Adventure Time", "slug": "adventure-time"}, {"title": "The Expanse", "slug": "the-expanse"}, {"title": "DC's Legends of Tomorrow", "slug": "dc-s-legends-of-tomorrow"}, {"title": "Cosmos: A Spacetime Odyssey", "slug": "cosmos-a-spacetime-odyssey"}, {"title": "Hawaii Five-0", "slug": "hawaii-five-0"}, {"title": "Peaky Blinders", "slug": "peaky-blinders"}, {"title": "Last Week Tonight with John Oliver", "slug": "last-week-tonight-with-john-oliver"}, {"title": "iZombie", "slug": "izombie"}, {"title": "Outlander", "slug": "outlander"}, {"title": "Marvel's Iron Fist", "slug": "marvel-s-iron-fist"}, {"title": "Legion", "slug": "legion"}, {"title": "The Daily Show", "slug": "the-daily-show"}, {"title": "One Piece", "slug": "one-piece"}, {"title": "Altered Carbon", "slug": "altered-carbon"}, {"title": "The Man in the High Castle", "slug": "the-man-in-the-high-castle"}, {"title": "Veep", "slug": "veep"}, {"title": "Marvel's The Punisher", "slug": "marvel-s-the-punisher"}, {"title": "Preacher", "slug": "preacher"}, {"title": "NCIS: Los Angeles", "slug": "ncis-los-angeles"}, {"title": "The Handmaid's Tale", "slug": "the-handmaid-s-tale"}, {"title": "American Gods", "slug": "american-gods"}, {"title": "BoJack Horseman", "slug": "bojack-horseman"}, {"title": "The OA", "slug": "the-oa"}, {"title": "One-Punch Man", "slug": "one-punch-man"}, {"title": "Curb Your Enthusiasm", "slug": "curb-your-enthusiasm"}, {"title": "Chicago Fire", "slug": "chicago-fire"}, {"title": "Quantico", "slug": "quantico"}, {"title": "Unbreakable Kimmy Schmidt", "slug": "unbreakable-kimmy-schmidt"}, {"title": "12 Monkeys", "slug": "12-monkeys"}, {"title": "Taboo", "slug": "taboo-2017"}, {"title": "Sword Art Online", "slug": "sword-art-online"}, {"title": "Mindhunter", "slug": "mindhunter"}, {"title": "The Magicians", "slug": "the-magicians-2015"}, {"title": "Star Trek: Discovery", "slug": "star-trek-discovery"}, {"title": "Billions", "slug": "billions"}, {"title": "This Is Us", "slug": "this-is-us"}, {"title": "The Night Manager", "slug": "the-night-manager"}, {"title": "Humans", "slug": "humans"}, {"title": "Master of None", "slug": "master-of-none"}, {"title": "Law & Order: Special Victims Unit", "slug": "law-order-special-victims-unit"}, {"title": "Big Little Lies", "slug": "big-little-lies"}, {"title": "Riverdale", "slug": "riverdale"}, {"title": "DARK", "slug": "dark"}, {"title": "American Crime Story", "slug": "american-crime-story"}, {"title": "The End of the F***ing World", "slug": "the-end-of-the-f-ing-world"}, {"title": "The Good Place", "slug": "the-good-place"}, {"title": "The Crown", "slug": "the-crown"}, {"title": "The Grand Tour", "slug": "the-grand-tour"}, {"title": "Strike Back", "slug": "strike-back"}, {"title": "A Series of Unfortunate Events", "slug": "a-series-of-unfortunate-events"}, {"title": "Z Nation", "slug": "z-nation"}, {"title": "Ozark", "slug": "ozark"}, {"title": "Shadowhunters", "slug": "shadowhunters"}, {"title": "Empire", "slug": "empire-2015"}, {"title": "Money Heist", "slug": "money-heist"}, {"title": "Chicago P.D.", "slug": "chicago-p-d"}, {"title": "Into the Badlands", "slug": "into-the-badlands"}, {"title": "Power", "slug": "power"}, {"title": "Blue Bloods", "slug": "blue-bloods"}, {"title": "Lethal Weapon", "slug": "lethal-weapon"}, {"title": "Jane the Virgin", "slug": "jane-the-virgin"}, {"title": "Colony", "slug": "colony"}, {"title": "Saturday Night Live", "slug": "saturday-night-live"}, {"title": "Mom", "slug": "mom"}, {"title": "Ballers", "slug": "ballers"}, {"title": "Red Dwarf", "slug": "red-dwarf"}, {"title": "You're the Worst", "slug": "you-re-the-worst"}, {"title": "Santa Clarita Diet", "slug": "santa-clarita-diet"}, {"title": "The Orville", "slug": "the-orville"}, {"title": "Atlanta", "slug": "atlanta"}, {"title": "Fairy Tail", "slug": "fairy-tail"}, {"title": "The Last Kingdom", "slug": "the-last-kingdom"}, {"title": "Survivor", "slug": "survivor-2000"}, {"title": "SpongeBob SquarePants", "slug": "spongebob-squarepants"}, {"title": "Broad City", "slug": "broad-city"}, {"title": "The Affair", "slug": "the-affair"}, {"title": "Nashville", "slug": "nashville"}, {"title": "Scream: The TV Series", "slug": "scream-the-tv-series-2015"}, {"title": "Robot Chicken", "slug": "robot-chicken"}, {"title": "Tokyo Ghoul", "slug": "tokyo-ghoul"}, {"title": "Pokémon", "slug": "pokemon-1997"}, {"title": "The Goldbergs", "slug": "the-goldbergs-2013"}, {"title": "Tosh.0", "slug": "tosh-0"}, {"title": "QI", "slug": "qi"}, {"title": "Killjoys", "slug": "killjoys"}, {"title": "Shooter", "slug": "shooter"}, {"title": "Travelers", "slug": "travelers-2016"}, {"title": "The Voice", "slug": "the-voice-2011"}, {"title": "Madam Secretary", "slug": "madam-secretary"}, {"title": "Young Justice", "slug": "young-justice"}, {"title": "Steven Universe", "slug": "steven-universe"}, {"title": "Hell's Kitchen", "slug": "hell-s-kitchen-2005"}, {"title": "Dragon Ball Super", "slug": "dragon-ball-super"}, {"title": "The Good Doctor", "slug": "the-good-doctor"}, {"title": "Whose Line is it Anyway? (US)", "slug": "whose-line-is-it-anyway-us-1998"}, {"title": "Fresh Off the Boat", "slug": "fresh-off-the-boat-2015"}, {"title": "The Gifted", "slug": "the-gifted"}, {"title": "The Venture Bros.", "slug": "the-venture-bros"}, {"title": "Lost in Space", "slug": "lost-in-space-2018"}, {"title": "Bosch", "slug": "bosch"}, {"title": "The Sinner", "slug": "the-sinner"}, {"title": "Breaking Bad (2008-2013)", "slug": "breaking-bad"}, {"title": "Dexter (2006-2013)", "slug": "dexter"}, {"title": "How I Met Your Mother (2005-2014)", "slug": "how-i-met-your-mother"}, {"title": "Friends (1994-2004)", "slug": "friends"}, {"title": "Lost (2004-2010)", "slug": "lost-2004"}, {"title": "House (2004-2012)", "slug": "house"}, {"title": "Fringe (2008-2013)", "slug": "fringe"}, {"title": "Prison Break (2005-2017)", "slug": "prison-break"}, {"title": "Firefly (2002-2003)", "slug": "firefly"}, {"title": "Community (2009-2015)", "slug": "community"}, {"title": "Person of Interest (2011-2016)", "slug": "person-of-interest"}, {"title": "True Blood (2008-2014)", "slug": "true-blood"}, {"title": "Futurama (1999-2013)", "slug": "futurama"}, {"title": "Once Upon a Time (2011-2018)", "slug": "once-upon-a-time"}, {"title": "Sons of Anarchy (2008-2014)", "slug": "sons-of-anarchy"}, {"title": "New Girl (2011-2018)", "slug": "new-girl"}, {"title": "Battlestar Galactica (2003-2009)", "slug": "battlestar-galactica-2003"}, {"title": "Hannibal (2013-2015)", "slug": "hannibal"}, {"title": "The Vampire Diaries (2009-2017)", "slug": "the-vampire-diaries"}, {"title": "Band of Brothers (2001-2001)", "slug": "band-of-brothers"}, {"title": "Parks and Recreation (2009-2015)", "slug": "parks-and-recreation"}, {"title": "Chuck (2007-2012)", "slug": "chuck"}, {"title": "The Office (2005-2013)", "slug": "the-office"}, {"title": "The Wire (2002-2008)", "slug": "the-wire"}, {"title": "Castle (2009-2016)", "slug": "castle"}, {"title": "Orphan Black (2013-2017)", "slug": "orphan-black"}, {"title": "Heroes (2006-2010)", "slug": "heroes"}, {"title": "Californication (2007-2014)", "slug": "californication"}, {"title": "Under the Dome (2013-2015)", "slug": "under-the-dome"}, {"title": "The Mentalist (2008-2015)", "slug": "the-mentalist"}, {"title": "Scrubs (2001-2010)", "slug": "scrubs"}, {"title": "The IT Crowd (2006-2010)", "slug": "the-it-crowd"}, {"title": "Spartacus (2010-2013)", "slug": "spartacus"}, {"title": "24 (2001-2003)", "slug": "24"}, {"title": "The Sopranos (1999-2007)", "slug": "the-sopranos"}, {"title": "White Collar (2009-2014)", "slug": "white-collar"}, {"title": "Two and a Half Men (2003-2015)", "slug": "two-and-a-half-men"}, {"title": "Grimm (2011-2017)", "slug": "grimm"}, {"title": "Mad Men (2007-2015)", "slug": "mad-men"}, {"title": "Falling Skies (2011-2015)", "slug": "falling-skies"}, {"title": "Bones (2005-2017)", "slug": "bones"}, {"title": "Boardwalk Empire (2010-2014)", "slug": "boardwalk-empire"}, {"title": "Avatar: The Last Airbender (2005-2008)", "slug": "avatar-the-last-airbender"}, {"title": "2 Broke Girls (2011-2017)", "slug": "2-broke-girls"}, {"title": "Seinfeld (1989-1998)", "slug": "seinfeld"}, {"title": "Teen Wolf (2011-2017)", "slug": "teen-wolf-2011"}, {"title": "Death Note (2006-2007)", "slug": "death-note"}, {"title": "Pretty Little Liars (2010-2017)", "slug": "pretty-little-liars"}, {"title": "Stargate SG-1 (1997-2007)", "slug": "stargate-sg-1"}, {"title": "Revolution (2012-2014)", "slug": "revolution"}, {"title": "Sense8 (2015-2018)", "slug": "sense8"}, {"title": "Revenge (2011-2015)", "slug": "revenge"}, {"title": "Buffy the Vampire Slayer (1997-2003)", "slug": "buffy-the-vampire-slayer"}, {"title": "Continuum (2012-2015)", "slug": "continuum"}, {"title": "Glee (2009-2015)", "slug": "glee"}, {"title": "Weeds (2005-2012)", "slug": "weeds"}, {"title": "The Newsroom (2012-2014)", "slug": "the-newsroom"}, {"title": "Misfits (2009-2013)", "slug": "misfits"}, {"title": "The Following (2013-2015)", "slug": "the-following"}, {"title": "Penny Dreadful (2014-2016)", "slug": "penny-dreadful"}, {"title": "30 Rock (2006-2013)", "slug": "30-rock"}, {"title": "The Americans (2013-2018)", "slug": "the-americans-2013"}, {"title": "Downton Abbey (2010-2015)", "slug": "downton-abbey"}, {"title": "Lie to Me (2009-2011)", "slug": "lie-to-me"}, {"title": "Smallville (2001-2011)", "slug": "smallville"}, {"title": "Star Trek: The Next Generation (1987-1994)", "slug": "star-trek-the-next-generation"}, {"title": "Bates Motel (2013-2017)", "slug": "bates-motel"}, {"title": "Banshee (2013-2016)", "slug": "banshee"}, {"title": "Black Sails (2014-2017)", "slug": "black-sails"}, {"title": "Eureka (2006-2012)", "slug": "eureka"}, {"title": "Entourage (2004-2011)", "slug": "entourage"}, {"title": "Stargate Atlantis (2004-2009)", "slug": "stargate-atlantis"}, {"title": "The Legend of Korra (2012-2014)", "slug": "the-legend-of-korra"}, {"title": "The Strain (2014-2017)", "slug": "the-strain"}, {"title": "Justified (2010-2015)", "slug": "justified"}, {"title": "Gossip Girl (2007-2012)", "slug": "gossip-girl"}, {"title": "The Good Wife (2009-2016)", "slug": "the-good-wife"}, {"title": "Almost Human (2013-2014)", "slug": "almost-human"}, {"title": "Warehouse 13 (2009-2014)", "slug": "warehouse-13"}, {"title": "Rome (2005-2007)", "slug": "rome"}, {"title": "Sleepy Hollow (2013-2017)", "slug": "sleepy-hollow"}, {"title": "Psych (2006-2014)", "slug": "psych"}, {"title": "Da Vinci's Demons (2013-2015)", "slug": "da-vinci-s-demons"}, {"title": "That '70s Show (1998-2006)", "slug": "that-70s-show"}, {"title": "Burn Notice (2007-2013)", "slug": "burn-notice"}, {"title": "The Killing (2011-2014)", "slug": "the-killing-2011"}, {"title": "The Leftovers (2014-2017)", "slug": "the-leftovers"}, {"title": "Scandal (2012-2018)", "slug": "scandal"}, {"title": "Veronica Mars (2004-2007)", "slug": "veronica-mars"}, {"title": "Gilmore Girls (2000-2007)", "slug": "gilmore-girls"}, {"title": "Marvel's Agent Carter (2015-2016)", "slug": "marvel-s-agent-carter"}, {"title": "Terra Nova (2011-2011)", "slug": "terra-nova"}, {"title": "Merlin (2008-2012)", "slug": "merlin"}, {"title": "Six Feet Under (2001-2005)", "slug": "six-feet-under"}, {"title": "Stargate Universe (2009-2011)", "slug": "stargate-universe"}, {"title": "Desperate Housewives (2004-2012)", "slug": "desperate-housewives"}, {"title": "Nikita (2010-2013)", "slug": "nikita"}, {"title": "Louie (2010-2015)", "slug": "louie"}, {"title": "Fullmetal Alchemist: Brotherhood (2009-2010)", "slug": "fullmetal-alchemist-brotherhood"}, {"title": "Star Trek: Voyager (1995-2001)", "slug": "star-trek-voyager"}, {"title": "Defiance (2013-2015)", "slug": "defiance"}, {"title": "Scorpion (2014-2018)", "slug": "scorpion"}, {"title": "The Pacific (2010-2010)", "slug": "the-pacific"}, {"title": "Skins (2007-2013)", "slug": "skins"}, {"title": "Limitless (2015-2016)", "slug": "limitless"}, {"title": "Constantine (2014-2015)", "slug": "constantine"}, {"title": "Broadchurch (2013-2017)", "slug": "broadchurch"}, {"title": "Dollhouse (2009-2010)", "slug": "dollhouse"}, {"title": "Deadwood (2004-2006)", "slug": "deadwood"}, {"title": "Freaks and Geeks (1999-2000)", "slug": "freaks-and-geeks"}, {"title": "V (2009-2011)", "slug": "v-2009"}, {"title": "The Shield (2002-2008)", "slug": "the-shield"}, {"title": "My Name Is Earl (2005-2009)", "slug": "my-name-is-earl"}, {"title": "Naruto Shippuden (2007-2017)", "slug": "naruto-shippuden"}, {"title": "Hell on Wheels (2011-2016)", "slug": "hell-on-wheels"}, {"title": "Planet Earth (2006-2006)", "slug": "planet-earth"}, {"title": "Malcolm in the Middle (2000-2006)", "slug": "malcolm-in-the-middle"}, {"title": "Leverage (2008-2012)", "slug": "leverage"}, {"title": "Torchwood (2006-2011)", "slug": "torchwood"}, {"title": "Dragon Ball Z (1989-1996)", "slug": "dragon-ball-z"}, {"title": "The Fresh Prince of Bel-Air (1990-1996)", "slug": "the-fresh-prince-of-bel-air"}, {"title": "Haven (2010-2015)", "slug": "haven"}, {"title": "Helix (2014-2015)", "slug": "helix"}, {"title": "Alphas (2011-2012)", "slug": "alphas"}, {"title": "Cowboy Bebop (1998-1999)", "slug": "cowboy-bebop"}, {"title": "Girls (2012-2017)", "slug": "girls"}, {"title": "Bachelor in Paradise", "slug": "bachelor-in-paradise"}, {"title": "GLOW", "slug": "glow"}, {"title": "The Disastrous Life of Saiki K", "slug": "the-disastrous-life-of-saiki-k"}];
autocomplete(document.getElementById("myInput"), showsToAddList);


var xmlhttp = new XMLHttpRequest();
var url = "http://localhost:8080/getShows?shows="; //sort=" + "" + "&

// Getting user's shows from local storage...
var stored = localStorage['myShowsJson'];
if (stored) {
	myShows = JSON.parse(stored);
}
else {
	myShows = ["breaking-bad", "westworld"]; // default list
	localStorage['myShowsJson'] = JSON.stringify(myShows);
}

for (var i = 0; i < myShows.length; i++) {
	url += myShows[i];
	if(i < myShows.length - 1) url += ",";
}


xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText); //edit?
        console.log(myArr);
  //      document.getElementById("id01").innerHTML = myArr[0].title.toString();
//        setSortMethod()
        myFunction(myArr);
    }
};
xmlhttp.open("GET", url, true);
xmlhttp.send();

function myFunction(arr) {
	shows = arr.shows;
	
	/*if (sortSwitch.localeCompare("alphabetical") == 0) {
		// sort
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
	} */
	/*else {*/
		//neat sort: (currentlyAiring) "New Ep airs in...", (newSeasonHasDate) "New season premieres in...", [Ready for binging in...], 
		// (seasonEnded, < 1.5yr since lastEp) "Ready to binge:", (seasonAnnounced) "News:", (seasonEnded, >1.5yr) "Awaiting updates", (seriesEnded) "Ended series:"
		
		//temp workaround (re-assign each show's detail.. may want to move this to Java, or possibly move original detail assignment to js also)
		var newEpAirings = new Array(); newSeasonDates = new Array(); recentBingables = new Array(); news = new Array(); awaitingUpdates = new Array(); ended = new Array();
		for (var i = 0; i < shows.length; i++) {
			var status = shows[i].status;
			var show = shows[i];
			switch(status) {
			
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
				if(millisecondsSinceLastEpisode <= yearInMillisecs) {
					var dateString = (dateLastEp.toDateString()).substr(4,(dateLastEp.toDateString()).length);
					show.detail = " - Season " + show.currentSeason + " (as of " + dateString + ")";
					recentBingables.push(show);
				}
				else {
					show.detail = "";
					awaitingUpdates.push(show);
				}
				break;
			
			case "newSeasonAnnounced":
				show.detail = " - Season " + show.currentSeason + " announced! (date TBA)";
				news.push(show);
				break;
				
			case "seriesEnded":
				ended.push(show);
				break;
			}
		}
	/*}*/

// display...
	var showTitle = "";
	var out = "";
	var dateLoc = 0;
//    var i;
    var showJsonString = "";
    /* var urlWithShowsString = "http://localhost:8080/?shows="; */
    
    /*if (sortSwitch.localeCompare("alphabetical") == 0) {		// ALPHA-SORT:
    	out = addShowstoOutput(shows);
    } 
    else {		*/// BEST SORT:
    	
    	if (null != newEpAirings[0]) {
    		newEpAirings.sort(function(a, b){
    	    	aDate = new Date(a.nextEpisodeDate).getTime(); 
    	    	bDate = new Date(b.nextEpisodeDate).getTime(); 
    		  	return aDate > bDate;
    		});
    		if (!urlWithShowsString.endsWith("=")) urlWithShowsString += ",";
	    	out += "New Episode airs in...<br>";
	    	out += addShowstoOutput(newEpAirings);
	    	out += "<br>";
    	}
    	
    	if (null != newSeasonDates[0]) {
    		newSeasonDates.sort(function(a, b){
    	    	aDate = new Date(a.latestEpisodeDate); 
    	    	bDate = new Date(b.latestEpisodeDate); 
    		  	return aDate > bDate;
    		});
    		if (!urlWithShowsString.endsWith("=")) urlWithShowsString += ",";
	    	out += "New Season premieres in...<br>";
	    	out += addShowstoOutput(newSeasonDates);
	    	out += "<br>";
    	}
    	
    	if (null != recentBingables[0]) {
    		recentBingables.sort(function(a, b){
    	    	aDate = new Date(a.latestEpisodeDate); 
    	    	bDate = new Date(b.latestEpisodeDate); 
    		  	return aDate < bDate;
    		});
    		if (!urlWithShowsString.endsWith("=")) urlWithShowsString += ",";
	    	out += "Ready to Binge:<br>";
	    	out += addShowstoOutput(recentBingables);
	    	out += "<br>";
    	}
    	
    	if (null != news[0]) {
    		news.sort(function(a, b){
    	    	// if title doesn't start with "A " or "The ", then go ahead, else, compare without first word.
    	    	aTitle = (a.title).toUpperCase(); 
    	    	if(aTitle.startsWith("A ")) aTitle = aTitle.substr(2, aTitle.length - 1);
    	    	if(aTitle.startsWith("THE ")) aTitle = aTitle.substr(4, aTitle.length - 1);
    	    	
    	    	bTitle = (b.title).toUpperCase();
    	    	if(bTitle.startsWith("A ")) bTitle = bTitle.substr(2, bTitle.length - 1);
    	    	if(bTitle.startsWith("THE ")) bTitle = bTitle.substr(4, bTitle.length - 1);
    	    	
    		  	return aTitle > bTitle;
    		});
    		if (!urlWithShowsString.endsWith("=")) urlWithShowsString += ",";
	    	out += "News:<br>";
	    	out += addShowstoOutput(news);
	    	out += "<br>";
    	}
    	
    	if (null != awaitingUpdates[0]) {
    		awaitingUpdates.sort(function(a, b){
    	    	// if title doesn't start with "A " or "The ", then go ahead, else, compare without first word.
    	    	aTitle = (a.title).toUpperCase(); 
    	    	if(aTitle.startsWith("A ")) aTitle = aTitle.substr(2, aTitle.length - 1);
    	    	if(aTitle.startsWith("THE ")) aTitle = aTitle.substr(4, aTitle.length - 1);
    	    	
    	    	bTitle = (b.title).toUpperCase();
    	    	if(bTitle.startsWith("A ")) bTitle = bTitle.substr(2, bTitle.length - 1);
    	    	if(bTitle.startsWith("THE ")) bTitle = bTitle.substr(4, bTitle.length - 1);
    	    	
    		  	return aTitle > bTitle;
    		});
    		if (!urlWithShowsString.endsWith("=")) urlWithShowsString += ",";
	    	out += "Awaiting Updates...<br>";
	    	out += addShowstoOutput(awaitingUpdates);
	    	out += "<br>";
    	}
    	
    	if (null != ended[0]) {
    		ended.sort(function(a, b){
    	    	// if title doesn't start with "A " or "The ", then go ahead, else, compare without first word.
    	    	aTitle = (a.title).toUpperCase(); 
    	    	if(aTitle.startsWith("A ")) aTitle = aTitle.substr(2, aTitle.length - 1);
    	    	if(aTitle.startsWith("THE ")) aTitle = aTitle.substr(4, aTitle.length - 1);
    	    	
    	    	bTitle = (b.title).toUpperCase();
    	    	if(bTitle.startsWith("A ")) bTitle = bTitle.substr(2, bTitle.length - 1);
    	    	if(bTitle.startsWith("THE ")) bTitle = bTitle.substr(4, bTitle.length - 1);
    	    	
    		  	return aTitle > bTitle;
    		});
    		if (!urlWithShowsString.endsWith("=")) urlWithShowsString += ",";
	    	out += "Completed Series:<br>";
	    	out += addShowstoOutput(ended);
    	}
    /*}*/
//    if (urlWithShowsString.endsWith(",")) urlWithShowsString = urlWithShowsString.substr(0, urlWithShowsString.length - 1);
    
    document.getElementById("id01").innerHTML = out;
}
function addShowstoOutput(shows)  {
	var tempOut = "";
	
	for(var i = 0; i < shows.length; i++) {
		var singleShowOutput = addSingleShowToTempOutputString(shows[i]);
		tempOut += singleShowOutput;
		
		urlWithShowsString += shows[i].slug;
		if (i < (shows.length - 1)) {
			urlWithShowsString += ",";
		}
		
	    // next line
		tempOut += "<br>";
	    
	    // check for extra break for alphabetical sort
	 /*   if (sortSwitch.localeCompare("alphabetical") == 0 && null != shows[i+1]) {
	    	var currentTitle = shows[i].title.toUpperCase();
	    	var nextTitle = shows[i+1].title.toUpperCase();
	    	if(currentTitle.startsWith("A ")) currentTitle = currentTitle.substr(2, currentTitle.length - 1);
	    	if(currentTitle.startsWith("THE ")) currentTitle = currentTitle.substr(4, currentTitle.length - 1);
	    	if(nextTitle.startsWith("A ")) nextTitle = nextTitle.substr(2, nextTitle.length - 1);
	    	if(nextTitle.startsWith("THE ")) nextTitle = nextTitle.substr(4, nextTitle.length - 1);
	    	if (nextTitle.substr(0,1) > currentTitle.substr(0,1)) {
	    		tempOut += "<br>";
	    	}
	    } */
	}
	return tempOut;
}

function addSingleShowToTempOutputString(show){
	var out = "";
	
	/*if (sortSwitch.localeCompare("alphabetical") == 0) {
		// since you've now done this, perhaps you don't need all the a/the checks for sorting, etc down below...
		// only need this for alpha sort? "A/The" looks fine when "best" sort?
		var tempTitle = show.title;
		if((show.title.toUpperCase()).startsWith("A ")) show.title = (show.title).substr(2, (show.title).length - 1) + ", " + tempTitle.substr(0);
		if((show.title.toUpperCase()).startsWith("THE ")) show.title = (show.title).substr(4, (show.title).length - 1) + ", " + tempTitle.substr(0,3);
	}*/
	// TITLE
	showTitle = "<b>" + show.title + "</b>" + show.detail;
 
	// add calendar icon/date tooltip if it's a countdown detail.
    if ((show.status).localeCompare("seasonCurrentlyAiring") == 0 || (show.status).localeCompare("newSeasonHasPremiereDate") == 0) {
    	out += showTitle + " <div class=\"tooltip\"> <i class=\"fa fa-calendar\"></i> <span class=\"tooltiptext tooltip-right\">" 
    	+ show.hoverDate + "</span></div>";
    } 
    else {
    	out += showTitle;
    }
    return out;
}

function updateSort() {
	var aToZ = document.getElementById("sortSwitch");
	var url_string = window.location.href
	var urlForSort = new URL(urlWithShowsString);
	if (aToZ.checked) {
		urlForSort.searchParams.set("sort", "alphabetical");
	}
	else {
		urlForSort.searchParams.set("sort", "default");
	}
//	bookmarkString = urlForSort.toString();
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
	}
	else {
		myShows = new Array(); // change this to what exactly?
	}

	console.log(myShows);
	myShows.push(slug);
	console.log(myShows);
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
	      if (!val) { return false;}
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
	        if (arr[i].title.substr(0, val.length).toUpperCase() == val.toUpperCase()) {
	          /*create a DIV element for each matching element:*/
	          b = document.createElement("DIV");
	          /*make the matching letters bold:*/
	          b.innerHTML = "<strong>" + arr[i].title.substr(0, val.length) + "</strong>";
	          b.innerHTML += arr[i].title.substr(val.length);
	          /*insert a input field that will hold the current array item's value:*/
	          b.innerHTML += "<input type='hidden' value='" + arr[i].title + "'>";
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
	      if (x) x = x.getElementsByTagName("div");
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
	          if (x) x[currentFocus].click();
	        }
	      }
	  });
	  function addActive(x) {
	    /*a function to classify an item as "active":*/
	    if (!x) return false;
	    /*start by removing the "active" class on all items:*/
	    removeActive(x);
	    if (currentFocus >= x.length) currentFocus = 0;
	    if (currentFocus < 0) currentFocus = (x.length - 1);
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
	document.addEventListener("click", function (e) {
	    closeAllLists(e.target);
	});
	}
	
/* 	function launchModal(tvShow) {
		var jsonShow = JSON.parse(tvShow);
		var modal = new tingle.modal({
		    footer: true,
		    stickyFooter: false,
		    closeMethods: ['overlay', 'button', 'escape'],
		    closeLabel: "Close",
		 //   cssClass: ['custom-class-1', 'custom-class-2'],
		    onOpen: function() {
		        console.log('modal open');
		    },
		    onClose: function() {
		        console.log('modal closed');
		    },
		    beforeClose: function() {
		        // here's goes some logic
		        // e.g. save content before closing the modal
		        //alert('test');
		        return true; // close the modal
		        return false; // nothing happens
		    }
		});
	
		// set content
		modal.setContent('<h1>' + jsonShow.show.title + '</h1>');
	
		// add a button
		modal.addFooterBtn('Button label', 'tingle-btn tingle-btn--primary', function() {
		    // here goes some logic
		    console.log('sup');
		    modal.close();
		});
		 modal.open();
	}; */
	
// 	 var button = document.getElementById("clickable-title");
// 	button.onClick = function(){
// 		launchModal(/* show */);
// 	}
	