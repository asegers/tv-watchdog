package segers.alex.tvwatchdog.beans;

import java.time.LocalDateTime;

public class Show {
// from trakt via REST API
	private String title;
	private int yearBegin;
	private int idTrakt;
	private String idSlug;
	private String statusTrakt;
	private LocalDateTime updatedAtTrakt;
//	private String urlPicture;
	
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
		return "" +
//		"UpdatedAt:\t " + getUpdatedAt() + "\n" +
//		"UrlPicture:\t " + getUrlPicture() + "\n" +
//		"IdTrakt:\t " + getIdTrakt() + "\n" +

		"Id:\t " + getIdSlug() + "\n" +
		"Title:\t " + getTitle() + "\n" +
		"Year Began:\t " + getYearBegin() + "\n" +
		"StatusTrakt:\t " + getStatusTrakt() + "\n" +
		"MY STATUS:\t " + getStatus() + "\n" +
		"Last Season's Year: " + getYearLatest() + "\n" +
		"Current Season:\t " + getCurrentSeason() + "\n" +
		"Last Episode:\t season #" + getLatestEpsSeasonNumber() + ", on " + getLatestEpDate() + "\n" +
		"Next Episode:\t #" + getNextEpNumber() + " on " + getNextEpDate();
	}

	public String determineAndSetMyStatus() {
		String myStatus = "";
		String traktStatus = this.getStatusTrakt();
		if (traktStatus.equals("ended") || traktStatus.equals("canceled")) {
	    	myStatus = "seriesEnded";
	    }
		else if (traktStatus.equals("returning series")) {
			if (null != this.getNextEpDate()) {
				if (this.getNextEpNumber() > 1) {
					myStatus = "seasonCurrentlyAiring";
				}
				else {
					myStatus = "newSeasonHasPremiereDate";
				}
			}
			else {
				if (this.getLatestEpsSeasonNumber() < this.getCurrentSeason()) {
					myStatus = "newSeasonAnnounced";
				}
				else {
					//if (LocalDateTime.now() - show.getLastEpDate() < LocalDateTime.)
					// if less than a year since last episode, then "seasonEnded"
					// don't need this logic...? bc never will assume new season has been announced?
					// see Bachelor in Paradise 2018.. know it is starting in next 1-2 months, but no non-gossip info on it even "being announced"
					myStatus = "seasonEnded";
				}
			}
		}
		else {
			//"planned" or "in-production"
			if (null != this.getNextEpDate()) {
				myStatus = "newSeasonHasPremiereDate";
			}
			else {
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

/*	public String getUrlPicture() {
		return urlPicture;
	}

	public void setUrlPicture(String urlPicture) {
		this.urlPicture = urlPicture;
	}*/

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
	
	
	
}
