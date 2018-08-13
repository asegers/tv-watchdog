package segers.alex.tvwatchdog.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import org.bson.Document;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import segers.alex.tvwatchdog.beans.Show;

@Repository
public class ShowDao implements Dao<Show> {

	private static final String HOST_NAME = "localhost";
	private static final int PORT_NUMBER = 27017;
	private static final String DATABASE_NAME = "local";
	private static final String COLLECTION_NAME = "testeighty";


	
	@Override
	public Optional<Show> get(long id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
	
	public ArrayList<Show> getShowsBySlugNames(ArrayList<String> slugs) {
    	ArrayList<Show> shows = new ArrayList<>();

		try (MongoClient mongoClient = new MongoClient(HOST_NAME, PORT_NUMBER)) {
	    	MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
	    	MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
	    	
	    	for (String slug : slugs) {
	    		System.out.println("Searching db for... " + slug);
	    		if (null != collection.find(Filters.eq("slug", slug)).first()) {
	    			String json = collection.find(Filters.eq("slug", slug)).first().toJson();
	            	JSONObject obj = new JSONObject(json);
	            	Show show = jsonToShow(obj);
	            	shows.add(show);
	        	}
	    	}
		}
    	
		return shows;
	}

	@Override
	public ArrayList<Show> getAll() {
    	ArrayList<Show> allShows = new ArrayList<>();

		try (MongoClient mongoClient = new MongoClient(HOST_NAME, PORT_NUMBER)) {
	    	MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
	    	MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
	    	
	    	FindIterable<Document> docs = collection.find();
    	
			for (Document doc : docs) {
				JSONObject obj = new JSONObject(doc.toJson());
				System.out.println("Retrieved " + obj.getString("title") + "...");
				Show show = jsonToShow(obj);
				allShows.add(show);
			}
		}
		
		return allShows;
	}

	@Override
	public void save(Show t) {
		// TODO Auto-generated method stub
		
	}
	
	public void saveShows(ArrayList<Show> shows) {
		
		try (MongoClient mongoClient = new MongoClient(HOST_NAME, PORT_NUMBER)) {
	    	MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
	    	MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
			
	    	Document[] docs = showsToDocs(shows);
	    		
	    	for(Document doc : docs) {		
		    	collection.insertOne(doc);
	    	}
		}
	}

	@Override
	public void update(Show t, String[] params) {
		// TODO Auto-generated method stub
		
	}
	
	public void updateShows(ArrayList<Show> shows) {
		
		try (MongoClient mongoClient = new MongoClient(HOST_NAME, PORT_NUMBER)) {
	    	MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
	    	MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
	    	
	    	for(Show show : shows) {		
	    		Document doc = show.toDocument();
		    	UpdateResult result = collection.replaceOne(Filters.eq("slug", show.getIdSlug()), doc);
		    	if (result.getModifiedCount() == 1) {
		    		System.out.println("Mongo updated: " + show.getTitle());
		    	}
	    	}
		}
	}

	@Override
	public void delete(Show t) {
		// TODO Auto-generated method stub
		
	}

	private Show jsonToShow(JSONObject obj) {
		Show show = new Show();
		show.setCurrentSeason(obj.getInt("currentSeason"));
		show.setIdSlug(obj.getString("slug"));
		show.setIdTrakt(obj.getInt("idTrakt"));
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
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
		DateTimeFormatter dateFormatterSeconds = DateTimeFormatter.ofPattern(datePattern);
		show.setUpdatedAtTrakt(LocalDateTime.parse(obj.getString("traktUpdatedOn"), dateFormatterSeconds));
		show.setYearBegin(obj.getInt("yearBegin"));
		show.setYearLatest(obj.getInt("yearLatest"));
		
		return show;
	}
	
	private Document[] showsToDocs(ArrayList<Show> shows) {
		ArrayList<Document> listDocs = new ArrayList<>();
		for (Show show : shows) {
			listDocs.add(show.toDocument());
    	}
		Document[] docs = listDocs.toArray(new Document[listDocs.size()]);
		
		return docs;
	}

}
