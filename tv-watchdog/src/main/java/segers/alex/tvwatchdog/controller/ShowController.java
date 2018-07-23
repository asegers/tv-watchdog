package segers.alex.tvwatchdog.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import segers.alex.tvwatchdog.beans.Show;
import segers.alex.tvwatchdog.service.MongoService;
import segers.alex.tvwatchdog.service.TraktService;


@RestController
//@Component
public class ShowController {

	public MongoService mongoSvc = new MongoService();
	public TraktService traktSvc = new TraktService();
    
    @RequestMapping("/getShows")
    public String getShows( @RequestParam(value="sort", defaultValue="best") String sort, @RequestParam(value="shows", defaultValue="game-of-thrones,breaking-bad") ArrayList<String> slugs) throws IOException, JSONException {

    	ArrayList<String> showSlugs = slugs; 
    	
		System.out.println(slugs.toString());
		System.out.println(sort);
		
		String showsJson = mongoSvc.getShowsFromMongo(showSlugs);
    	return showsJson;
    }
  
    @RequestMapping("/dailyDatabaseUpdate")
    public void updateDatabaseViaTrakt()  {
    	// To run daily, need to schedule somehow... via Jenkins?
    	dailyDatabaseUpdates();
    }
	
    @RequestMapping("/updateDatabaseWithTop200")
    public void updateDatabaseWithTop200()  {
    	traktSvc.updateDatabaseWithTopShows2();
    }

	private void dailyDatabaseUpdates() {
		// get ALL records as Shows... sort into lists: apiSeasonsCall, apiNextEpCall, apiGetShowCall
		MongoClient mongoClient = new MongoClient("localhost", 27017);
    	MongoDatabase database = mongoClient.getDatabase("local");
    	MongoCollection<Document> collection = database.getCollection("testeighty");
    	
		ArrayList<Show> seasonsApiCallShows = new ArrayList<>();
		ArrayList<Show> nextEpApiCallShows = new ArrayList<>();
		ArrayList<Show> getShowApiCallShows = new ArrayList<>();
		
		ArrayList<Show> mongoUpdateShows = new ArrayList<>();
    	
		FindIterable<Document> docs = collection.find();
		for (Document doc : docs) {
			JSONObject obj = new JSONObject(doc.toJson());
			System.out.println("Retrieved " + obj.getString("title") + "...");
			Show show = buildShowFromJson(obj);
			
			String status = show.getStatus();
			switch(status) {
			case "seasonEnded":
				seasonsApiCallShows.add(show);
				break;
				
			case "seriesEnded":
				getShowApiCallShows.add(show);
				break;
				
			default:
				nextEpApiCallShows.add(show);
				break;
			}
		}
    	mongoClient.close();
    	
		// iterate apiSeasonsCall (status = seasonEnded), then API-getShowSeasons
			// if current season (max of returned..) > show.latestEpisodesSeasonNum
				// update to seasonAnnounced (& traktUpdatedOn), add to LIST (apiNextEpCall)
			// else do nothing (update traktUpdatedOn date)
    	for (Show show : seasonsApiCallShows) {
    		int currentSeason = traktSvc.getLatestSeasonNumber(show);
    		int latestEpisodeSeason = show.getLatestEpsSeasonNumber();
    		if (currentSeason > latestEpisodeSeason) {
    			System.out.println("Updated: " + show.getTitle() + ";\t\t status (tent.) changed: " + show.getStatus() + " --> " + "newSeasonAnnounced");
    			show.setStatus("newSeasonAnnounced");
    			nextEpApiCallShows.add(show);
    			System.out.println("Update: New Season Announced (temp) - " + show.getTitle());
    		}
    		// can't update traktUpdatedOn bc don't get the date from Api call...
    	}
		
		// iterate apiNextEpCall
			// if nextEp resp != null
				// case status = Announced
					// update to newSeasonHasPremiereDate (& traktUpdatedOn)
				// case status = newSeasonHasPremiereDate || currentlyAiring
					//check if matches current value, update if doesn't (either way, update traktUpdatedOn)
			// else (nextEp == null)
				// case status = Announced
					// nothing
				// case else
					// update to seasonEnded, add to apiGetShowCall
    	for (Show show : nextEpApiCallShows) {
    		Show showUpdated = new Show();
    		showUpdated = traktSvc.getNextEpisodeProper(show.getIdSlug());
    		if (null != showUpdated.getNextEpDate()) {
    			switch(show.getStatus()) {
    			
    			case "newSeasonAnnounced":
    				show.setStatus("newSeasonHasPremiereDate");
    				show.setNextEpDate(showUpdated.getNextEpDate());
    				show.setNextEpNumber(showUpdated.getNextEpNumber());
	    			System.out.println("Updated: " + show.getTitle() + ";\t\t status changed: " + "newSeasonAnnounced" + " --> " + "newSeasonHasPremiereDate");
	    			System.out.println("Updated: " + show.getTitle() + ";\t\t next episode date changed: " + "null" + " --> " + showUpdated.getNextEpDate().toString());
	    			System.out.println("Updated: " + show.getTitle() + ";\t\t next episode # changed: " + "null" + " --> " + showUpdated.getNextEpNumber());

    				mongoUpdateShows.add(show);
    				break;
    				
				case "newSeasonHasPremiereDate":
				case "seasonCurrentlyAiring":
				    LocalDateTime dbNextEpDate = show.getNextEpDate();
				    LocalDateTime currentNextEpDate = showUpdated.getNextEpDate();
				    boolean updatedFlag = false;
				    if (!currentNextEpDate.isEqual(dbNextEpDate)) {	//need to check for null?
				    	show.setNextEpDate(currentNextEpDate);
		    			System.out.println("Updated: " + show.getTitle() + ";\t\t next episode date changed: " + dbNextEpDate.toString() + " --> " + currentNextEpDate.toString());
				    	updatedFlag = true;
				    }
				    
				    int dbNextEpNum = show.getNextEpNumber();
				    int currentNextEpNum = showUpdated.getNextEpNumber();
				    if (currentNextEpNum != dbNextEpNum) {
				    	show.setNextEpNumber(currentNextEpNum);
		    			System.out.println("Updated: " + show.getTitle() + ";\t\t next episode # changed: " + dbNextEpNum + " --> " + currentNextEpNum);
				    	updatedFlag = true;
				    }
				    
				    if (updatedFlag) {
				    	mongoUpdateShows.add(show);
				    }
				    // TO DO: ALSO NEED TO UPDATE LATEST EPISODE! Isn't always accurate when it's been >1 day since last update
				    break;
    			}
    		}
    		else {	// Next Episode Date response is null/empty
    			if (!show.getStatus().equals("newSeasonAnnounced")) {
    				show.setStatus("seasonEnded");
    				getShowApiCallShows.add(show);
        			System.out.println("Updated: " + show.getTitle() + ";\t\t status (tent.) changed: " + "newSeasonAnnounced" + " --> " + "seasonEnded");
    			}
    		}
    		// can't update traktUpdatedOn bc don't get the date from Api call...
    	}
		
		// iterate apiGetShowCall
			// case traktStatus = canceled/ended
				// check matches, if not then update
			// case traktStatus = returning, else
				// check matches, if not then update
    	for (Show show : getShowApiCallShows) {
    		Show showUpdate = traktSvc.getSingleShow(show.getIdSlug());
    		
    		String currentTraktStatus = showUpdate.getStatusTrakt();
    		String prevTraktStatus = show.getStatusTrakt();

    		boolean updatedFlag = false;
    		if (!currentTraktStatus.equals(prevTraktStatus)) {
    			show.setStatusTrakt(showUpdate.getStatusTrakt());
    			System.out.println("Updated: " + show.getTitle() + ";\t\t trakt status changed: " + prevTraktStatus + " --> " + currentTraktStatus);
    			updatedFlag = true;
    		}
    		String prevStatus = show.getStatus();
    		String currentStatus = show.determineAndSetMyStatus();
    		if (!currentStatus.equals(prevStatus)) {
    			updatedFlag = true;
    			System.out.println("Updated: " + show.getTitle() + ";\t\t status changed: " + prevStatus + " --> " + currentStatus);
    		}
    		
    		if (updatedFlag) {
				mongoUpdateShows.add(show);
    		}
    	}
    	// ADD "LOGGING"
    	// update MongoDB with shows...
		MongoClient mongoClient2 = new MongoClient("localhost", 27017);
    	MongoDatabase database2 = mongoClient2.getDatabase("local");
    	MongoCollection<Document> collection2 = database2.getCollection("testeighty");
    	for (Show show : mongoUpdateShows) {
    		Document doc = new Document("title", show.getTitle())	
	    			.append("slug", show.getIdSlug())	
	    			.append("status", show.getStatus())	
	    			.append("yearBegin", show.getYearBegin())	
	    			.append("yearLatest", show.getYearLatest())	
	    			.append("currentSeason", show.getCurrentSeason());
	    	
	    	if (null != show.getNextEpDate()) {
    			doc
    			.append("nextEpisodeDate", show.getNextEpDate().toString())	
    			.append("nextEpisodeNum", show.getNextEpNumber());
	    	}
	    	else {
	    		doc
    			.append("nextEpisodeDate", "")	
    			.append("nextEpisodeNum", "");
	    	}
	    	if (null != show.getLatestEpDate()) {
    			doc
    			.append("latestEpisodeDate", show.getLatestEpDate().toString())	
    			.append("latestEpisodeSeasonNum", show.getLatestEpsSeasonNumber());
	    	}
	    	else {
	    		doc
    			.append("latestEpisodeDate", "")	
    			.append("latestEpisodeSeasonNum", "");
	    	}
	    	doc
    			.append("idTrakt", show.getIdTrakt())
    			.append("statusTrakt", show.getStatusTrakt());
	    	
	    	if (null != show.getUpdatedAtTrakt()) {
    			doc.append("traktUpdatedOn", show.getUpdatedAtTrakt().toString());
	    	}
	    	else {
	    		doc.append("traktUpdatedOn", "");
	    	}
	    	
	    	
	    	UpdateResult result = collection2.replaceOne(Filters.eq("slug", show.getIdSlug()), doc);
	    	if (result.getModifiedCount() == 1) {
	    		System.out.println("Mongo updated: " + show.getTitle());
	    	}
    	}
    	mongoClient2.close();
	}
	
	private Show buildShowFromJson(JSONObject obj) {
		Show show = new Show();
		show.setCurrentSeason(obj.getInt("currentSeason"));
		show.setIdSlug(obj.getString("slug"));
		show.setIdTrakt(obj.getInt("idTrakt"));
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");//"MMM dd, yyyy HH:mm");
		if (!obj.getString("latestEpisodeDate").isEmpty()) {
			show.setLatestEpDate(LocalDateTime.parse(obj.getString("latestEpisodeDate"), dateFormatter));
		}
		if (!obj.get("latestEpisodeSeasonNum").toString().isEmpty() ) {
			show.setLatestEpsSeasonNumber(obj.getInt("latestEpisodeSeasonNum"));
		}
		if (!obj.getString("nextEpisodeDate").isEmpty()) {
			show.setNextEpDate(LocalDateTime.parse(obj.getString("nextEpisodeDate"), dateFormatter));
		} 
		if ( !obj.get("nextEpisodeNum").toString().isEmpty() ){
			show.setNextEpNumber(obj.getInt("nextEpisodeNum"));
		}
		show.setStatus(obj.getString("status"));
		show.setStatusTrakt(obj.getString("statusTrakt"));
		show.setTitle(obj.getString("title"));
		String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
		if (obj.getString("traktUpdatedOn").length() != 19 ) {
			datePattern = "yyyy-MM-dd'T'HH:mm";
		}
		DateTimeFormatter dateFormatterSeconds = DateTimeFormatter.ofPattern(datePattern);//"MMM dd, yyyy HH:mm");
		show.setUpdatedAtTrakt(LocalDateTime.parse(obj.getString("traktUpdatedOn"), dateFormatterSeconds));
		show.setYearBegin(obj.getInt("yearBegin"));
		show.setYearLatest(obj.getInt("yearLatest"));
		
		return show;
	}

}
