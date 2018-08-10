package segers.alex.tvwatchdog.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import segers.alex.tvwatchdog.beans.Show;
import segers.alex.tvwatchdog.dao.ShowDao;

public class TraktService {
	
	ShowDao daoShow;

	public ArrayList<JSONObject> getShowsDataFromTrakt(ArrayList<String> showSlugs) throws JSONException {
		// this is the logic for downloading all possible info for a show upon first Search query to Trakt (via API)
		// add search query api here next... in place of showSlugs array.. or check if showSlugs is null, then call search...? separate method?
		
		ArrayList<Show> shows = new ArrayList<Show>();
		
		for (String slug : showSlugs) {
			Show show = populateShowFromSlug(slug);
	    	shows.add(show);
		}
		
		daoShow.saveShows(shows);
		
		ArrayList<JSONObject> jsonShows = new ArrayList<>();
		for (Show show : shows) {
			// turn into JSONObject, etc. return that instead
			JSONObject jsonShow = buildJson(show);
			jsonShows.add(jsonShow);
		}
		return jsonShows;
	}
	

	private Show getLastEpisode(Show show) {
		String lastEpisodeUrl = "https://private-anon-266658202a-trakt.apiary-proxy.com/shows/"
		    	+ show.getIdSlug() +
		    	"/last_episode?extended=full";
	    	Response lastEpisodeData = RestAssured.given()
	    			.header("Content-Type", "application/json")
	    			.header("trakt-api-version", "2")
	    			.header("trakt-api-key", "a86f75ae6e256af79ca34cd35462228b1d25f02a6ce1552e83bf967a1dff0ff0")
	    			.when().get(lastEpisodeUrl);
	    	
	    	LocalDateTime lastEpisode = null;
	    	int lastEpisodeSeason = -1;
	    	int lastEpisodeYear = -1;
	    	if (lastEpisodeData.statusCode() == 200) {
		    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		    	lastEpisode = LocalDateTime.parse(lastEpisodeData.jsonPath().getString("first_aired"), formatter);
		    	lastEpisodeSeason = lastEpisodeData.jsonPath().getInt("season");
		    	lastEpisodeYear = Integer.parseInt(lastEpisode.toString().substring(0, 4));
	    	}
	    	else if (lastEpisodeData.statusCode() == 429) {
	    		// if Rate Limit exceeded, then wait a few seconds and try again...
	    		try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		System.out.println("429 Response. Retrying...");
	    		show = getLastEpisode(show);
	    	}

    		show.setLatestEpDate(lastEpisode);
    		show.setLatestEpsSeasonNumber(lastEpisodeSeason);
    		show.setYearLatest(lastEpisodeYear);
	    	
	    	return show;
	}

	private Show populateShowFromSlug(String slug) throws JSONException {
		// need to clean this up.. prob not supposed to be show = method(show);
		Show show = getSingleShow(slug);
		show = getNextEpisode(show);
		show = getLastEpisode(show);
		show.setCurrentSeason(getLatestSeasonNumber(show));
		show.determineAndSetMyStatus();
//		show = setDetails(show); //look into this... need most of this code now that setting detail/hover on the fly??
		
		return show;
	}
	
	public Show populateShow(Show show) throws JSONException {
		// need to clean this up.. prob not supposed to be show = method(show);
		show.setStatus("");
		show = getNextEpisode(show);
		show = getLastEpisode(show);
		show.setCurrentSeason(getLatestSeasonNumber(show));
		show.determineAndSetMyStatus();
		
		return show;
	}

	public int getLatestSeasonNumber(Show show) throws JSONException {
		String seasonsUrl = "https://private-anon-266658202a-trakt.apiary-proxy.com/shows/"
		    	+ show.getIdSlug() +
		    	"/seasons";
	    	Response seasonsData = RestAssured.given()
	    			.header("Content-Type", "application/json")
	    			.header("trakt-api-version", "2")
	    			.header("trakt-api-key", "a86f75ae6e256af79ca34cd35462228b1d25f02a6ce1552e83bf967a1dff0ff0")
	    			.when().get(seasonsUrl);
	    	
	    	int currentSeason = -1;
	    	if (seasonsData.statusCode() == 200) {
	    	
	    		JSONArray arrayJson = new JSONArray(seasonsData.asString());
	    		
	    		int maxSeason = 0;
	    		for (int i = 0; i < arrayJson.length() ; i++) {
	    			int objSeasonNumber = arrayJson.getJSONObject(i).getInt("number");
	    			if (objSeasonNumber > maxSeason) maxSeason = objSeasonNumber;
	    		}
	    		currentSeason = maxSeason;
	    	}
	    	
	    	return currentSeason;
	}

	private Show getNextEpisode(Show show) {
		String nextEpisodeUrl = "https://private-anon-266658202a-trakt.apiary-proxy.com/shows/"
		    	+ show.getIdSlug() +
		    	"/next_episode?extended=full";
	    	Response nextEpisodeData = RestAssured.given()
	    			.header("Content-Type", "application/json")
	    			.header("trakt-api-version", "2")
	    			.header("trakt-api-key", "a86f75ae6e256af79ca34cd35462228b1d25f02a6ce1552e83bf967a1dff0ff0")
	    			.when().get(nextEpisodeUrl);
	    	
	    	LocalDateTime nextEpisode = null;
	    	int nextEpisodeNumber = -1;
	    	if (nextEpisodeData.statusCode() == 200) {
	    	
		    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		    	nextEpisode = LocalDateTime.parse(nextEpisodeData.jsonPath().getString("first_aired"), formatter);
		    	nextEpisodeNumber = nextEpisodeData.jsonPath().getInt("number");
	    	}
	    	else if (nextEpisodeData.statusCode() == 429) {
	    		// if Rate Limit exceeded, then wait a few seconds and try again...
	    		try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		System.out.println("429 Response. Retrying...");
	    		show = getNextEpisode(show);
	    	}

    		show.setNextEpDate(nextEpisode);
    		show.setNextEpNumber(nextEpisodeNumber);
	    	
	    	return show;
	}

	public Show getSingleShow(String slug) {
		String showUrl = "https://private-anon-266658202a-trakt.apiary-proxy.com/shows/"
    		+slug+
    		"?extended=full";
    	Response showData = RestAssured.given()
    			.header("Content-Type", "application/json")
    			.header("trakt-api-version", "2")
    			.header("trakt-api-key", "a86f75ae6e256af79ca34cd35462228b1d25f02a6ce1552e83bf967a1dff0ff0")
    			.when().get(showUrl);
    
    	Show show = new Show();
    
    	if (showData.statusCode() == 200) {
	    	show.setTitle(showData.jsonPath().getString("title"));
	    	show.setYearBegin(showData.jsonPath().getInt("year"));
	    	show.setIdTrakt(showData.jsonPath().getInt("ids.trakt"));
	    	show.setIdSlug(showData.jsonPath().getString("ids.slug"));
	    	show.setStatusTrakt(showData.jsonPath().getString("status"));
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	    	show.setUpdatedAtTrakt(LocalDateTime.parse(showData.jsonPath().getString("updated_at"), formatter));
	    	show.setStatus("");
    	}
    	else if (showData.statusCode() == 429) {
    		// if Rate Limit exceeded, then wait a few seconds and try again...
    		try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		System.out.println("429 Response. Retrying...");
    		show = getSingleShow(slug);
    	}
    	
    	
    	return show;
	}
	
    public void updateDatabaseWithTopShows2() throws JSONException {
    	
    	String popularShowsUrl = "https://private-anon-266658202a-trakt.apiary-proxy.com/shows/popular"
        		+ "?extended=full" + "&limit=200";
        	Response popularShowsData = RestAssured.given()
        			.header("Content-Type", "application/json")
        			.header("trakt-api-version", "2")
        			.header("trakt-api-key", "a86f75ae6e256af79ca34cd35462228b1d25f02a6ce1552e83bf967a1dff0ff0")
        			.when().get(popularShowsUrl);
		
        	// for each show returned...
        	ArrayList<JSONObject> array = popularShowsData.jsonPath().get();
        	
        	ArrayList<Show> showsToAddToMongo = new ArrayList<Show>();
        	
        	for (int i = 0; i < array.size(); i++) {
        		Object objShow = array.get(i);
            	HashMap hashJson = (HashMap) objShow;
            	String status = (String) hashJson.get("status");
            	if(status.equals("ended") || status.equals("canceled")) {
	            	System.out.println(hashJson.get("title"));
	            	
	            	Show show = new Show();
	            	show.setTitle((String) hashJson.get("title"));
	            	show.setYearBegin((int) hashJson.get("year"));
	            	HashMap hashIds = (HashMap) hashJson.get("ids");
	            	show.setIdTrakt((int) hashIds.get("trakt"));
	            	show.setIdSlug((String) hashIds.get("slug"));
	            	show.setStatusTrakt((String) hashJson.get("status"));
	            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	            	show.setUpdatedAtTrakt(LocalDateTime.parse((String) hashJson.get("updated_at"), formatter));
	            	
	            	show = populateShow(show);
	            	
	        		showsToAddToMongo.add(show);
            	}
        	}
        	int count = 1;
        	for (Show show : showsToAddToMongo) {
        		System.out.println(count + "\t\t" + show.getTitle() + " (" + show.getYearLatest() +")");
        		count++;
        	}
        	daoShow.saveShows(showsToAddToMongo);
	}
    
    private JSONObject buildJson(Show show) throws JSONException {
    	JSONObject showJson = null;

		showJson = new JSONObject()
                .put("title", show.getTitle())
                .put("slug", show.getIdSlug())
                .put("status", show.getStatus())
                .put("yearBegin", show.getYearBegin())
                .put("yearLatest", show.getYearLatest())
                .put("currentSeason", show.getCurrentSeason());
		if (null != show.getNextEpDate()) {
			showJson.put("nextEpisodeDate", show.getNextEpDate().toString())	
			.put("nextEpisodeNum", show.getNextEpNumber());
    	}
		else {
			showJson.put("nextEpisodeDate", "")	
			.put("nextEpisodeNum", "");
    	}
		
		if (null != show.getLatestEpDate()) {
			showJson.put("latestEpisodeDate", show.getLatestEpDate().toString())	
			.put("latestEpisodeSeasonNum", show.getLatestEpsSeasonNumber());
    	}
    	else {
    		showJson.put("latestEpisodeDate", "")	
			.put("latestEpisodeSeasonNum", "");
    	}
		
		showJson
                .put("idTrakt", show.getIdTrakt())
                .put("statusTrakt", show.getStatusTrakt());
		
        if (null != show.getUpdatedAtTrakt()) {
			showJson.put("traktUpdatedOn", show.getUpdatedAtTrakt().toString());
    	}
    	else {
    		showJson.put("traktUpdatedOn", "");
    	}

		return showJson;
	}
	
    public Show getNextEpisodeProper(String slugId) {
		String nextEpisodeUrl = "https://private-anon-266658202a-trakt.apiary-proxy.com/shows/"
		    	+ slugId +
		    	"/next_episode?extended=full";
	    	Response nextEpisodeData = RestAssured.given()
	    			.header("Content-Type", "application/json")
	    			.header("trakt-api-version", "2")
	    			.header("trakt-api-key", "a86f75ae6e256af79ca34cd35462228b1d25f02a6ce1552e83bf967a1dff0ff0")
	    			.when().get(nextEpisodeUrl);
	    	
	    	LocalDateTime nextEpisode = null;
	    	int nextEpisodeNumber = -1;
	    	if (nextEpisodeData.statusCode() == 200) {
	    	
		    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		    	nextEpisode = LocalDateTime.parse(nextEpisodeData.jsonPath().getString("first_aired"), formatter);
		    	nextEpisodeNumber = nextEpisodeData.jsonPath().getInt("number");
	    	}
	    	else if (nextEpisodeData.statusCode() == 429) {
	    		// if Rate Limit exceeded, then wait a few seconds and try again...
	    		try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		System.out.println("429 Response. Retrying...");
	    		Show show = getNextEpisodeProper(slugId); // why did I assign to a show? is this the cause of an issue?
	    	}
	    	

	    	Show show = new Show();
	    	
    		show.setNextEpDate(nextEpisode);
    		show.setNextEpNumber(nextEpisodeNumber);
	    	
	    	return show;
	}
    
}
