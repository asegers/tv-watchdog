package segers.alex.tvwatchdog.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import segers.alex.tvwatchdog.beans.Show;

@Component
public class TvWatchdogUtil {

	final static Logger logger = LogManager.getLogger(TvWatchdogUtil.class);

	private static final String NEW_SEASON_ANNOUNCED = "newSeasonAnnounced";
	private static final String NEW_SEASON_HAS_PREMIERE_DATE = "newSeasonHasPremiereDate";
	private static final String SEASON_CURRENTLY_AIRING = "seasonCurrentlyAiring";
	private static final String SEASON_ENDED = "seasonEnded";
	private static final String SERIES_ENDED = "seriesEnded";

	public void generateTop200List() {

		try (MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://asegers:TestPassword123@ds229732.mlab.com:29732/tvwatchdog1"))) {
			MongoDatabase database = mongoClient.getDatabase("tvwatchdog1");
			MongoCollection<Document> collection = database.getCollection("shows");

			StringBuilder bldListString = new StringBuilder();
			bldListString.append("[");

			MongoCursor<Document> cursor = collection.find().iterator();


			try {
				while (cursor.hasNext()) {
					String title = "";
					String slug = "";
					String strShow = cursor.next().toJson();
					JSONObject jsonShow = new JSONObject(strShow);
					title = jsonShow.getString("title");
					if (jsonShow.getString("status").equals(SERIES_ENDED)) {
						StringBuilder bld = new StringBuilder();
						bld.append(title)
						.append(" (")
						.append(Integer.toString(jsonShow.getInt("yearBegin")))
						.append("-")
						.append(Integer.toString(jsonShow.getInt("yearLatest")))
						.append(")");
						title = bld.toString();
					}
					slug = jsonShow.getString("slug");
					bldListString.append("{\"title\": \"")
					.append(title)
					.append("\", \"slug\": \"")
					.append(slug)
					.append("\"}, ");
				}
			} finally {
				cursor.close();
			}

			String listString = bldListString.toString();
			listString = listString.substring(0, listString.length()) + "]";
			logger.info(bldListString.toString());
			System.out.println(listString);
		}
	}

	public String convertShowsToJsonArrayString(ArrayList<Show> shows) {
		JSONArray arrayJsonShows = new JSONArray();
		for (Show show : shows) {
			arrayJsonShows.put(show.toJson());
		}

		return new JSONObject().put("shows", arrayJsonShows).toString();
	}

	// Temporarily being stored in this class... need another Service?
	public Show calculateShowDetailsForToday(Show show) {
		String detail = "";
		String hoverDetail = "";

		// ADD SPEECH FOR IF PREMIERE, NEW EPISODE IS TODAY!!!! ALSO ADD "Newest
		// season released recently!" for Netflix shows, etc.

		LocalDateTime nextEpDate = show.getNextEpDate();
		int daysTill = 0;
		String daysOrDay = "";
		String dayOfWeekUpper = "";
		String dayOfWeek = "";
		String releaseDate = "";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

		String status = show.getStatus();
		switch (status) {

		case NEW_SEASON_HAS_PREMIERE_DATE:
			int season = show.getCurrentSeason();
			daysTill = 1 + (int) ChronoUnit.DAYS.between(LocalDateTime.now(), nextEpDate);
			daysOrDay = "days";
			if (daysTill == 1)
				daysOrDay = "day";
			detail = " - " + daysTill + " " + daysOrDay + " till Season " + season + " premiere!";
			// (Friday) June 22, 2018.. position of (Friday) depends on how if
			// daysTill < 7
			dayOfWeekUpper = nextEpDate.getDayOfWeek().toString();
			dayOfWeek = dayOfWeekUpper.substring(0, 1) + dayOfWeekUpper.substring(1).toLowerCase();
			releaseDate = nextEpDate.format(formatter);
			if (daysTill < 7) {
				hoverDetail = "(" + dayOfWeek + ") " + releaseDate;
			} else {
				hoverDetail = releaseDate + " (" + dayOfWeek + ")";
			}
			show.setDetail(detail);
			show.setHoverDetail(hoverDetail);
			break;

		case SEASON_CURRENTLY_AIRING:
			int iSeasonNum = show.getCurrentSeason();
			String strSeasonNum = String.valueOf(iSeasonNum);
			if (iSeasonNum < 10)
				strSeasonNum = "0" + strSeasonNum;

			int iEpisodeNum = show.getNextEpNumber();
			String strEpisodeNum = String.valueOf(iEpisodeNum);
			if (iEpisodeNum < 10)
				strEpisodeNum = "0" + strEpisodeNum;

			daysTill = 1 + (int) ChronoUnit.DAYS.between(LocalDateTime.now(), nextEpDate);
			daysOrDay = "days";
			if (daysTill == 1)
				daysOrDay = "day";
			detail = " - " + daysTill + " " + daysOrDay + " till new episode. (s" + strSeasonNum + "e" + strEpisodeNum
					+ ")";
			dayOfWeekUpper = nextEpDate.getDayOfWeek().toString();
			dayOfWeek = dayOfWeekUpper.substring(0, 1) + dayOfWeekUpper.substring(1).toLowerCase();
			releaseDate = nextEpDate.format(formatter);
			if (daysTill < 7) {
				hoverDetail = "(" + dayOfWeek + ") " + releaseDate;
			} else {
				hoverDetail = releaseDate + " (" + dayOfWeek + ")";
			}
			show.setDetail(detail);
			show.setHoverDetail(hoverDetail);
			break;

		case SERIES_ENDED:
			int yearBegin = show.getYearBegin();
			int yearEnded = show.getYearLatest();
			show.setDetail(" (" + yearBegin + "-" + yearEnded + ")");
			break;

		case NEW_SEASON_ANNOUNCED:
			show.setDetail(" - Season " + (show.getLatestEpsSeasonNumber() + 1) + " announced! Premiere date TBA.");
			break;

		case SEASON_ENDED:
			show.setDetail(" - " + "awaiting news on next season.");
			break;

		default:
			show.setDetail("");
			show.setHoverDetail("");
			break;

		}
		return show;
	}

	public ArrayList<Show> calculateDetailsForShows(ArrayList<Show> shows) {
		ArrayList<Show> detailedShows = new ArrayList<>();

		for (Show show : shows) {
			Show detailedShow = calculateShowDetailsForToday(show);
			detailedShows.add(detailedShow);
		}

		return detailedShows;
	}

}
