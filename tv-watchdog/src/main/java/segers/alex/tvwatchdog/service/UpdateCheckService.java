package segers.alex.tvwatchdog.service;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import segers.alex.tvwatchdog.beans.Show;
import segers.alex.tvwatchdog.dao.ShowDao;

@Service
public class UpdateCheckService {

	@Autowired
	TraktService traktSvc;

	@Autowired
	ShowDao daoShow;

	private static final String NEW_SEASON_ANNOUNCED = "newSeasonAnnounced";
	private static final String NEW_SEASON_HAS_PREMIERE_DATE = "newSeasonHasPremiereDate";
	private static final String SEASON_CURRENTLY_AIRING = "seasonCurrentlyAiring";
	private static final String SEASON_ENDED = "seasonEnded";
	private static final String SERIES_ENDED = "seriesEnded";

	private static final String ARROW_SYMBOL = " --> ";

	private ArrayList<Show> showsToCallSeasonsApi;
	private ArrayList<Show> showsToCallNextEpApi;
	private ArrayList<Show> showsToCallGetShowApi;

	private ArrayList<Show> mongoUpdateShows;

	final static Logger logger = LogManager.getLogger(UpdateCheckService.class);

	public UpdateCheckService() {
		this.showsToCallSeasonsApi = new ArrayList<Show>();
		this.showsToCallNextEpApi = new ArrayList<Show>();
		this.showsToCallGetShowApi = new ArrayList<Show>();

		this.mongoUpdateShows = new ArrayList<Show>();
	}

	public void dailyDatabaseUpdates() {
		// There may be a bug triggered when it's been more than x days (or show
		// event has happened) since last update...
		// Perhaps can keep this ordered method of processing, and add a
		// check,etc/default somewhere in there, calling all APIs for show(?)

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
			switch (status) {
			case SEASON_ENDED:
				showsToCallSeasonsApi.add(show);
				break;

			case SERIES_ENDED:
				showsToCallGetShowApi.add(show);
				break;

			default:
				showsToCallNextEpApi.add(show);
				break;
			}
		}
	}

	private void checkShowsForNewSeasonAnnouncement() {
		// iterate apiSeasonsCall (status = seasonEnded), then
		// API-getShowSeasons
		// if current season (max of returned..) > show.latestEpisodesSeasonNum
		// update to seasonAnnounced (& traktUpdatedOn), add to LIST
		// (apiNextEpCall)
		// else do nothing (update traktUpdatedOn date)
		for (Show show : showsToCallSeasonsApi) {
			int currentSeason = traktSvc.getLatestSeasonNumber(show);
			int latestEpisodeSeason = show.getLatestEpsSeasonNumber();
			if (currentSeason > latestEpisodeSeason) {
				logger.info("Updated: " + show.getTitle() + ";\t\t status (tent.) changed: " + show.getStatus()
						+ ARROW_SYMBOL + NEW_SEASON_ANNOUNCED);
				show.setStatus(NEW_SEASON_ANNOUNCED);
				showsToCallNextEpApi.add(show);
				logger.info("Update: New Season Announced (temp) - " + show.getTitle());
			}
			// can't update traktUpdatedOn bc don't get the date from Api
			// call...
		}
	}

	private void checkShowsNextEpisodeOrNone() {
		// iterate apiNextEpCall
		// if nextEp resp != null
		// case status = Announced
		// update to newSeasonHasPremiereDate (& traktUpdatedOn)
		// case status = newSeasonHasPremiereDate || currentlyAiring
		// check if matches current value, update if doesn't (either way, update
		// traktUpdatedOn)
		// else (nextEp == null)
		// case status = Announced
		// nothing
		// case else
		// update to seasonEnded, add to apiGetShowCall
		for (Show show : showsToCallNextEpApi) {
			Show showUpdated = traktSvc.getNextEpisodeProper(show.getIdSlug());
			boolean updatedFlag = false;
			if (null != showUpdated.getNextEpDate()) {
				switch (show.getStatus()) {

				case NEW_SEASON_ANNOUNCED:
					show.setStatus(NEW_SEASON_HAS_PREMIERE_DATE);
					show.setNextEpDate(showUpdated.getNextEpDate());
					show.setNextEpNumber(showUpdated.getNextEpNumber());
					logger.info("Updated: " + show.getTitle() + ";\t\t status changed: " + NEW_SEASON_ANNOUNCED
							+ ARROW_SYMBOL + NEW_SEASON_HAS_PREMIERE_DATE);
					logger.info("Updated: " + show.getTitle() + ";\t\t next episode date changed: " + "null"
							+ ARROW_SYMBOL + showUpdated.getNextEpDate().toString());
					logger.info("Updated: " + show.getTitle() + ";\t\t next episode # changed: " + "null" + ARROW_SYMBOL
							+ showUpdated.getNextEpNumber());

					updatedFlag = true;
					break;

				case NEW_SEASON_HAS_PREMIERE_DATE:
				case SEASON_CURRENTLY_AIRING:
					LocalDateTime dbNextEpDate = show.getNextEpDate();
					LocalDateTime currentNextEpDate = showUpdated.getNextEpDate();
					if (!currentNextEpDate.isEqual(dbNextEpDate)) { // need to
																	// check for
																	// null?
						show.setNextEpDate(currentNextEpDate);
						logger.info("Updated: " + show.getTitle() + ";\t\t next episode date changed: "
								+ dbNextEpDate.toString() + ARROW_SYMBOL + currentNextEpDate.toString());
						updatedFlag = true;
					}

					int dbNextEpNum = show.getNextEpNumber();
					int currentNextEpNum = showUpdated.getNextEpNumber();
					if (currentNextEpNum != dbNextEpNum) {
						show.setNextEpNumber(currentNextEpNum);
						logger.info("Updated: " + show.getTitle() + ";\t\t next episode # changed: " + dbNextEpNum
								+ ARROW_SYMBOL + currentNextEpNum);
						updatedFlag = true;
					}

					if (show.getStatus().equals(NEW_SEASON_HAS_PREMIERE_DATE) && currentNextEpNum != 1) {
						show.determineAndSetMyStatus();
						updatedFlag = true;
					}

					// TO DO: ALSO NEED TO UPDATE LATEST EPISODE! Isn't always
					// accurate when it's been >1 day since last update
					break;

				default:
					break;
				}
			} else { // Next Episode Date response is null/empty
				if (!show.getStatus().equals(NEW_SEASON_ANNOUNCED)) {
					String previousStatus = show.getStatus();
					show.setNextEpDate(null);
					show.setNextEpNumber(-1);
					showsToCallGetShowApi.add(show);
					logger.info("Updated: " + show.getTitle() + ";\t\t status (tent.) changed: " + previousStatus
							+ ARROW_SYMBOL + SEASON_ENDED);
				}
			}
			if (updatedFlag) {
				mongoUpdateShows.add(show);
			}
			// can't update traktUpdatedOn bc don't get the date from Api
			// call...
		}
	}

	private void checkShowsForTraktStatusChange() {
		// iterate apiGetShowCall
		// case traktStatus = canceled/ended
		// check matches, if not then update
		// case traktStatus = returning, else
		// check matches, if not then update
		for (Show show : showsToCallGetShowApi) {
			Show showUpdate = traktSvc.getSingleShow(show.getIdSlug());

			String currentTraktStatus = showUpdate.getStatusTrakt();
			String prevTraktStatus = show.getStatusTrakt();

			boolean updatedFlag = false;
			if (!currentTraktStatus.equals(prevTraktStatus)) {
				show.setStatusTrakt(showUpdate.getStatusTrakt());
				logger.info("Updated: " + show.getTitle() + ";\t\t trakt status changed: " + prevTraktStatus
						+ ARROW_SYMBOL + currentTraktStatus);
				updatedFlag = true;
			}
			String prevStatus = show.getStatus();
			String currentStatus = show.determineAndSetMyStatus();
			if (!currentStatus.equals(prevStatus)) {
				updatedFlag = true;
				logger.info("Updated: " + show.getTitle() + ";\t\t status changed: " + prevStatus + ARROW_SYMBOL
						+ currentStatus);
			}

			if (updatedFlag) {
				mongoUpdateShows.add(show);
			}
		}
	}

}
