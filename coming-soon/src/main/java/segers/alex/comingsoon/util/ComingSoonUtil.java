package segers.alex.comingsoon.util;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class ComingSoonUtil {
	
	private void generateTop200List() throws JSONException {
    	
    	MongoClient mongoClient = new MongoClient("localhost", 27017);
    	MongoDatabase database = mongoClient.getDatabase("local");
    	MongoCollection<Document> collection = database.getCollection("testeighty");
    	
    	
    	String listString = "[";
    	
    	MongoCursor<Document> cursor = collection.find().iterator();
    	
    	String title = "";
    	String slug = "";
    	try {
    	    while (cursor.hasNext()) {
    	    	String strShow = cursor.next().toJson();
    	    	JSONObject jsonShow = new JSONObject(strShow);
    	    	title = jsonShow.getString("title");
    	    	if (jsonShow.getString("status").equals("seriesEnded")) title += (" (" + jsonShow.getInt("yearBegin") + "-" + jsonShow.getInt("yearLatest") + ")");
    	    	slug = jsonShow.getString("slug");
//    	        System.out.println(cursor.next().toJson());
    	    	listString += "{\"title\": \"" + title + "\", \"slug\": \"" + slug + "\"}, ";
    	    }
    	} finally {
    	    cursor.close();
    	}
    	
    	mongoClient.close();
    	
    	listString = listString.substring(0, listString.length()) + "]";
    	System.out.println(listString);
	}
	
	
}
