package segers.alex.tvwatchdog.service;

import java.time.LocalDateTime;
import java.util.ArrayList;

import segers.alex.tvwatchdog.beans.Show;
import segers.alex.tvwatchdog.dao.ShowDao;

public class UpdateCheckService {

	public TraktService traktSvc = new TraktService();
	public ShowDao daoShow = new ShowDao();
	
	private ArrayList<Show> showsToCallSeasonsApi; // = new ArrayList<>();
	private ArrayList<Show> showsToCallNextEpApi; // = new ArrayList<>();
	private ArrayList<Show> showsToCallGetShowApi; // = new ArrayList<>();
	
	private ArrayList<Show> mongoUpdateShows; // = new ArrayList<>();


	public void dailyDatabaseUpdates() {
		// There may be a bug triggered when it's been more than x days (or show event has happened) since last update...
		// Perhaps can keep this ordered method of processing, and add a check,etc/default somewhere in there, calling all APIs for show(?)
		
		ArrayList<Show> allShows = daoShow.getAll();
		separateShowsByStatus(allShows);

		checkShowsForNewSeasonAnnouncement();
		checkShowsNextEpisodeOrNone();
		checkShowsForTraktStatusChange();
		
		daoShow.updateShows(mongoUpdateShows);
	}

	private void separateShowsByStatus(ArrayList<Show> shows) {
		
		for (Show show : shows) {
			String status = show.getStatus();
			switch(status) {
			case "seasonEnded":
				showsToCallSeasonsApi.add(show);
				break;
				
			case "seriesEnded":
				showsToCallGetShowApi.add(show);
				break;
				
			default:
				showsToCallNextEpApi.add(show);
				break;
			}
		}
	}
	
	private void checkShowsForNewSeasonAnnouncement() {
		// iterate apiSeasonsCall (status = seasonEnded), then API-getShowSeasons
			// if current season (max of returned..) > show.latestEpisodesSeasonNum
				// update to seasonAnnounced (& traktUpdatedOn), add to LIST (apiNextEpCall)
			// else do nothing (update traktUpdatedOn date)
    	for (Show show : showsToCallSeasonsApi) {
    		int currentSeason = traktSvc.getLatestSeasonNumber(show);
    		int latestEpisodeSeason = show.getLatestEpsSeasonNumber();
    		if (currentSeason > latestEpisodeSeason) {
    			System.out.println("Updated: " + show.getTitle() + ";\t\t status (tent.) changed: " + show.getStatus() + " --> " + "newSeasonAnnounced");
    			show.setStatus("newSeasonAnnounced");
    			showsToCallNextEpApi.add(show);
    			System.out.println("Update: New Season Announced (temp) - " + show.getTitle());
    		}
    		// can't update traktUpdatedOn bc don't get the date from Api call...
    	}
	}	
	
	private void checkShowsNextEpisodeOrNone() {
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
    	for (Show show : showsToCallNextEpApi) {
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
    				String previousStatus = show.getStatus();
    				show.setStatus("seasonEnded");
    				showsToCallGetShowApi.add(show);
        			System.out.println("Updated: " + show.getTitle() + ";\t\t status (tent.) changed: " + previousStatus + " --> " + "seasonEnded");
    			}
    		}
    		// can't update traktUpdatedOn bc don't get the date from Api call...
    	}		
	}	
	
	private void checkShowsForTraktStatusChange() {
		// iterate apiGetShowCall
			// case traktStatus = canceled/ended
				// check matches, if not then update
			// case traktStatus = returning, else
				// check matches, if not then update
		for (Show show : showsToCallGetShowApi) {
//			if(!show.getTitle().equals("12 Monkeys")){
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
//		}
	}		
	}





	


	
	
	
	
}
