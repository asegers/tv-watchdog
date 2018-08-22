package segers.alex.tvwatchdog.controller;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import segers.alex.tvwatchdog.beans.Show;
import segers.alex.tvwatchdog.dao.ShowDao;
import segers.alex.tvwatchdog.service.TraktService;
import segers.alex.tvwatchdog.service.UpdateCheckService;
import segers.alex.tvwatchdog.util.TvWatchdogUtil;

@RestController
public class ShowController {

	@Autowired
	TraktService traktSvc;

	@Autowired
	UpdateCheckService updateCheckSvc;

	@Autowired
	TvWatchdogUtil helper;

	@Autowired
	ShowDao daoShow;

	final static Logger logger = LogManager.getLogger(ShowController.class);

	@RequestMapping("/getShows")
	public String getShows(@RequestParam(value = "sort", defaultValue = "best") String sort,
			@RequestParam(value = "shows", defaultValue = "game-of-thrones,breaking-bad") ArrayList<String> slugs) {

		logger.info("Incoming slugs:" + slugs.toString() + "\nsort: " + sort);

		ArrayList<Show> shows = daoShow.getShowsBySlugNames(slugs);

		ArrayList<Show> detailedShows = helper.calculateDetailsForShows(shows);

		return helper.convertShowsToJsonArrayString(detailedShows);
	}

	@RequestMapping("/dailyDatabaseUpdate")
	public void updateDatabaseViaTrakt() {
		// To run daily, need to schedule somehow...
		updateCheckSvc.dailyDatabaseUpdates();
	}

	@RequestMapping("/updateDatabaseWithTopShows")
	public void updateDatabaseWithTraktTopShows() {
		traktSvc.updateDatabaseWithTopShows();
	}

}
