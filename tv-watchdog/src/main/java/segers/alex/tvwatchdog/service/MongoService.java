package segers.alex.tvwatchdog.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import segers.alex.tvwatchdog.beans.Show;

public class MongoService {

	TraktService traktSvc;
	
	public String getShowsFromMongo(ArrayList<String> showSlugs) throws JSONException {
    	MongoClient mongoClient = new MongoClient("localhost", 27017);
    	MongoDatabase database = mongoClient.getDatabase("local");
    	MongoCollection<Document> collection = database.getCollection("testeighty");
    	
    	JSONArray arrayShows = new JSONArray();
    	ArrayList<String> slugsNotFoundInDb = new ArrayList<String>();
    	
    	for (String slug : showSlugs) {
    		System.out.println("Searching db for... " + slug);
    		if (null != collection.find(Filters.eq("slug", slug)).first()) {
    			String json = collection.find(Filters.eq("slug", slug)).first().toJson();
            	JSONObject obj = new JSONObject(json);
            	arrayShows.put(obj);
        	}
        	else {
        		slugsNotFoundInDb.add(slug);
        	}        	
    	}
    	mongoClient.close();
    	
    	if (!slugsNotFoundInDb.isEmpty()) {
    		ArrayList<JSONObject> newShows = traktSvc.getShowsDataFromTrakt(slugsNotFoundInDb);
    		for (JSONObject newShowJson : newShows) {
    			arrayShows.put(newShowJson);
    		}
    	}
    	
    	// for calculating each show's details...
    	for (int i = 0; i < arrayShows.length(); i++) {
    		JSONObject obj = setDetails(arrayShows.getJSONObject(i));
    		arrayShows.put(i, obj);
    	}
    	
    	String jsonString = new JSONObject()
    			.put("shows", arrayShows).toString();
    	
    	return jsonString;
	}
	
	public void mongoAddShows(ArrayList<Show> shows) throws JSONException {
		
		MongoClient mongoClient = new MongoClient("localhost", 27017);
    	MongoDatabase database = mongoClient.getDatabase("local");
    	MongoCollection<Document> collection = database.getCollection("testeighty");
		
    	for (Show show : shows) {
			
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
    			
	    	collection.insertOne(doc);
    	}
    	mongoClient.close();
	}
	
	private JSONObject setDetails(JSONObject obj) throws JSONException {
		String detail = "";
		String hoverDetail = "";
		
		// Temporary fix... need to restructure DB so that saving all info on show, not just the data sent to html page.
		// Prob also need to serialize, etc, etc, so that Show obj is reflected in MongoDB, etc. Best practices, etc etc
		
		// ADD SPEECH FOR IF PREMIERE, NEW EPISODE IS TODAY!!!! ALSO ADD "Newest season released recently!" for Netflix shows, etc.
		
		String nextEpDateFromObj = obj.getString("nextEpisodeDate");

		String status = obj.getString("status");
		switch(status){
			
			case "newSeasonHasPremiereDate":
				int season = obj.getInt("currentSeason");
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
				LocalDateTime epDate = LocalDateTime.parse(nextEpDateFromObj, dateFormatter);
				int daysTill = 1 + (int) ChronoUnit.DAYS.between(LocalDateTime.now(), epDate);
				String daysOrDay = "days";
				if (daysTill == 1) daysOrDay = "day";
				detail = " - " + daysTill + " " + daysOrDay + " till Season " + season + " premiere!";
				// (Friday) June 22, 2018.. position of (Friday) depends on how if daysTill < 7
				String dayOfWeekUpper = epDate.getDayOfWeek().toString();
				String dayOfWeek = 	dayOfWeekUpper.substring(0, 1) + dayOfWeekUpper.substring(1).toLowerCase();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
				String releaseDate = epDate.format(formatter);
				if (daysTill < 7) {
					hoverDetail = "(" + dayOfWeek + ") " + releaseDate;
				}
				else {
					hoverDetail = releaseDate + " (" + dayOfWeek + ")";
				}
				obj.put("detail", detail);
				obj.put("hoverDate", hoverDetail);
				break;
				
			case "seasonCurrentlyAiring":
				int iSeasonNum = obj.getInt("currentSeason");
				String strSeasonNum = String.valueOf(iSeasonNum);
				if (iSeasonNum < 10) strSeasonNum = "0" + strSeasonNum;
				
				int iEpisodeNum = obj.getInt("nextEpisodeNum");
				String strEpisodeNum = String.valueOf(iEpisodeNum);
				if (iEpisodeNum < 10) strEpisodeNum = "0" + strEpisodeNum;
				
				DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
				LocalDateTime epDate2 = LocalDateTime.parse(nextEpDateFromObj, dateFormatter2);
				int daysTill2 = 1 + (int) ChronoUnit.DAYS.between(LocalDateTime.now(), epDate2);
				String daysOrDay2 = "days";
				if (daysTill2 == 1) daysOrDay2 = "day";
				detail = " - " + daysTill2 + " " + daysOrDay2 + " till new episode. (s" + strSeasonNum + "e" + strEpisodeNum + ")";
				String dayOfWeekUpper2 = epDate2.getDayOfWeek().toString();
				String dayOfWeek2 = 	dayOfWeekUpper2.substring(0, 1) + dayOfWeekUpper2.substring(1).toLowerCase();
				DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MMM dd, yyyy");
				String releaseDate2 = epDate2.format(formatter2);
				if (daysTill2 < 7) {
					hoverDetail = "(" + dayOfWeek2 + ") " + releaseDate2;
				}
				else {
					hoverDetail = releaseDate2 + " (" + dayOfWeek2 + ")";
				}
				obj.put("detail", detail);
				obj.put("hoverDate", hoverDetail);
				break;
				
			case "seriesEnded":
				int yearBegin = obj.getInt("yearBegin");
				int yearEnded = obj.getInt("yearLatest");
				obj.put("detail", " (" + yearBegin + "-" + yearEnded + ")");
				break;
				
			case "newSeasonAnnounced":
				obj.put("detail", " - Season " + (obj.getInt("latestEpisodeSeasonNum") + 1) + " announced! Premiere date TBA.");
				break;
				
			case "seasonEnded":
				obj.put("detail",  " - " + "awaiting news on next season.");
				break;

		}
		return obj;
	}
	
	
	
}
