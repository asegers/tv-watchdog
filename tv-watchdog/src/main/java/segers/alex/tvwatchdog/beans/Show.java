package segers.alex.tvwatchdog.beans;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.json.JSONObject;

public class Show {

	private static final String NEXT_EPISODE_DATE = "nextEpisodeDate";
	private static final String NEXT_EPISODE_NUMBER = "nextEpisodeNum";
	private static final String LATEST_EPISODE_DATE = "latestEpisodeDate";
	private static final String LATEST_EPISODE_SEASON_NUMBER = "latestEpisodeSeasonNum";
	private static final String TRAKT_UPDATED_ON = "traktUpdatedOn";

	final static Logger logger = LogManager.getLogger(Show.class);

	// from trakt via REST API
	private String title;
	private int yearBegin;
	private int idTrakt;
	private String idSlug;
	private String statusTrakt;
	private LocalDateTime updatedAtTrakt;

	private LocalDateTime nextEpDate;
	private int nextEpNumber;
	private LocalDateTime latestEpDate;
	private int latestEpsSeasonNumber;

	// my generated vars
	private int currentSeason;
	private int yearLatest;
	private String status;
	private String detail;
	private String hoverDetail;

	public String toString() {
		return "" + "Id:\t " + getIdSlug() + "\n" + "Title:\t " + getTitle() + "\n" + "Year Began:\t " + getYearBegin()
				+ "\n" + "StatusTrakt:\t " + getStatusTrakt() + "\n" + "MY STATUS:\t " + getStatus() + "\n"
				+ "Last Season's Year: " + getYearLatest() + "\n" + "Current Season:\t " + getCurrentSeason() + "\n"
				+ "Last Episode:\t season #" + getLatestEpsSeasonNumber() + ", on " + getLatestEpDate() + "\n"
				+ "Next Episode:\t #" + getNextEpNumber() + " on " + getNextEpDate();
	}

	public JSONObject toJson() {
		JSONObject showJson = null;

		showJson = new JSONObject().put("title", this.getTitle()).put("slug", this.getIdSlug())
				.put("status", this.getStatus()).put("yearBegin", this.getYearBegin())
				.put("yearLatest", this.getYearLatest()).put("currentSeason", this.getCurrentSeason());
		if (null != this.getNextEpDate()) {
			showJson.put(NEXT_EPISODE_DATE, this.getNextEpDate().toString()).put(NEXT_EPISODE_NUMBER,
					this.getNextEpNumber());
		} else {
			showJson.put(NEXT_EPISODE_DATE, "").put(NEXT_EPISODE_NUMBER, "");
		}

		if (null != this.getLatestEpDate()) {
			showJson.put(LATEST_EPISODE_DATE, this.getLatestEpDate().toString()).put(LATEST_EPISODE_SEASON_NUMBER,
					this.getLatestEpsSeasonNumber());
		} else {
			showJson.put(LATEST_EPISODE_DATE, "").put(LATEST_EPISODE_SEASON_NUMBER, "");
		}

		showJson.put("idTrakt", this.getIdTrakt()).put("statusTrakt", this.getStatusTrakt());

		if (null != this.getUpdatedAtTrakt()) {
			showJson.put(TRAKT_UPDATED_ON, this.getUpdatedAtTrakt().toString());
		} else {
			showJson.put(TRAKT_UPDATED_ON, "");
		}

		if (null != this.getDetail()) {
			showJson.put("detail", this.getDetail());
		} else {
			showJson.put("detail", "");
		}

		if (null != this.getHoverDetail()) {
			showJson.put("hoverDate", this.getHoverDetail());
		} else {
			showJson.put("hoverDate", "");
		}
		return showJson;
	}

	public String determineAndSetMyStatus() {
		String myStatus = "";
		String traktStatus = this.getStatusTrakt();
		if (traktStatus.equals("ended") || traktStatus.equals("canceled")) {
			myStatus = "seriesEnded";
		} else if (traktStatus.equals("returning series")) {
			if (null != this.getNextEpDate()) {
				if (this.getNextEpNumber() > 1) {
					myStatus = "seasonCurrentlyAiring";
				} else {
					myStatus = "newSeasonHasPremiereDate";
				}
			} else {
				if (this.getLatestEpsSeasonNumber() < this.getCurrentSeason()) {
					myStatus = "newSeasonAnnounced";
				} else {
					// if less than a year since last episode, then
					// "seasonEnded"
					// don't need this logic...? bc never will assume new season
					// has been announced?
					// see Bachelor in Paradise 2018.. know it is starting in
					// next 1-2 months, but no non-gossip info on it even "being
					// announced"
					myStatus = "seasonEnded";
				}
			}
		} else {
			// "planned" or "in-production"
			if (null != this.getNextEpDate()) {
				myStatus = "newSeasonHasPremiereDate";
			} else {
				myStatus = "newSeasonAnnounced";
			}
		}

		this.setStatus(myStatus);
		return myStatus;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getYearBegin() {
		return yearBegin;
	}

	public void setYearBegin(int yearBegin) {
		this.yearBegin = yearBegin;
	}

	public int getIdTrakt() {
		return idTrakt;
	}

	public void setIdTrakt(int idTrakt) {
		this.idTrakt = idTrakt;
	}

	public String getIdSlug() {
		return idSlug;
	}

	public void setIdSlug(String idSlug) {
		this.idSlug = idSlug;
	}

	public String getStatusTrakt() {
		return statusTrakt;
	}

	public void setStatusTrakt(String statusTrakt) {
		this.statusTrakt = statusTrakt;
	}

	public LocalDateTime getUpdatedAtTrakt() {
		return updatedAtTrakt;
	}

	public void setUpdatedAtTrakt(LocalDateTime updatedAt) {
		this.updatedAtTrakt = updatedAt;
	}

	public LocalDateTime getNextEpDate() {
		return nextEpDate;
	}

	public void setNextEpDate(LocalDateTime nextEpDate) {
		this.nextEpDate = nextEpDate;
	}

	public int getNextEpNumber() {
		return nextEpNumber;
	}

	public void setNextEpNumber(int nextEpNumber) {
		this.nextEpNumber = nextEpNumber;
	}

	public int getCurrentSeason() {
		return currentSeason;
	}

	public void setCurrentSeason(int currentSeason) {
		this.currentSeason = currentSeason;
	}

	public int getYearLatest() {
		return yearLatest;
	}

	public void setYearLatest(int yearLatest) {
		this.yearLatest = yearLatest;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getLatestEpDate() {
		return latestEpDate;
	}

	public void setLatestEpDate(LocalDateTime latestEpDate) {
		this.latestEpDate = latestEpDate;
	}

	public int getLatestEpsSeasonNumber() {
		return latestEpsSeasonNumber;
	}

	public void setLatestEpsSeasonNumber(int latestEpsSeasonNumber) {
		this.latestEpsSeasonNumber = latestEpsSeasonNumber;
	}

	public String getHoverDetail() {
		return hoverDetail;
	}

	public void setHoverDetail(String hoverDetail) {
		this.hoverDetail = hoverDetail;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Document toDocument() {
		Document doc = new Document("title", this.getTitle()).append("slug", this.getIdSlug())
				.append("status", this.getStatus()).append("yearBegin", this.getYearBegin())
				.append("yearLatest", this.getYearLatest()).append("currentSeason", this.getCurrentSeason());

		if (null != this.getNextEpDate()) {
			doc.append(NEXT_EPISODE_DATE, this.getNextEpDate().toString()).append(NEXT_EPISODE_NUMBER,
					this.getNextEpNumber());
		} else {
			doc.append(NEXT_EPISODE_DATE, "").append(NEXT_EPISODE_NUMBER, "");
		}
		if (null != this.getLatestEpDate()) {
			doc.append(LATEST_EPISODE_DATE, this.getLatestEpDate().toString()).append(LATEST_EPISODE_SEASON_NUMBER,
					this.getLatestEpsSeasonNumber());
		} else {
			doc.append(LATEST_EPISODE_DATE, "").append(LATEST_EPISODE_SEASON_NUMBER, "");
		}
		doc.append("idTrakt", this.getIdTrakt()).append("statusTrakt", this.getStatusTrakt());

		if (null != this.getUpdatedAtTrakt()) {
			doc.append(TRAKT_UPDATED_ON, this.getUpdatedAtTrakt().toString());
		} else {
			doc.append(TRAKT_UPDATED_ON, "");
		}
		return doc;
	}

}
