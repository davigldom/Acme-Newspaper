package controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.AdministratorService;
import services.AdvertisementService;
import services.ArticleService;
import services.ChirpService;
import services.FollowupService;
import services.NewspaperService;
import services.VolumeService;
import controllers.AbstractController;
import domain.Actor;
import domain.Administrator;

@Controller
@RequestMapping("/actor")
public class ActorAdministratorController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private AdministratorService administratorService;

	@Autowired
	private NewspaperService newspaperService;

	@Autowired
	private ChirpService chirpService;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private FollowupService followupService;

	@Autowired
	private AdvertisementService advertisementService;

	@Autowired
	private VolumeService volumeService;

	// Constructors -----------------------------------------------------------

	public ActorAdministratorController() {
		super();
	}

	// Edit Administrator
	@RequestMapping(value = "/admin/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Administrator administrator,
			final BindingResult binding) {
		ModelAndView result;
		administrator = this.administratorService.reconstruct(administrator,
				binding);
		if (binding.hasErrors())
			result = this.createEditModelAndView(administrator);
		else
			try {
				this.administratorService.save(administrator);
				result = new ModelAndView(
						"redirect:/actor/display-principal.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(administrator,
						"actor.commit.error");
			}
		return result;
	}

	// Show dashboard -----------------------------------------------

	@RequestMapping("/admin/dashboard")
	public ModelAndView dashboard() {
		final ModelAndView result;

		result = new ModelAndView("administrator/dashboard");

		result.addObject("getAverageFollowpsPerArticle",
				this.followupService.getAverageFollowpsPerArticle());
		result.addObject("getFollowupsPerArticleUpToWeek",
				this.followupService.getFollowupsPerArticleUpToWeek());
		result.addObject("getFollowupsPerArticleUpToTwoWeeks",
				this.followupService.getFollowupsPerArticleUpToTwoWeeks());
		result.addObject("getAverageChirpsPerUser",
				this.chirpService.getAverageChirpsPerUser());
		result.addObject("getStandardDeviationChirpsPerUser",
				this.chirpService.getStandardDeviationChirpsPerUser());
		result.addObject("getRatioUsersMoreChirpsThan75Percent",
				this.chirpService.getRatioUsersMoreChirpsThan75Percent());
		result.addObject("getRatioArticlesCreatedByUser",
				this.articleService.getRatioArticlesCreatedByUser());
		result.addObject("getAverageArticlesPerUser",
				this.articleService.getAverageArticlesPerUser());
		result.addObject("getStandardDeviationArticlesPerUser",
				this.articleService.getStandardDeviationArticlesPerUser());
		result.addObject("getAverageArticlesPerNewspaper",
				this.articleService.getAverageArticlesPerNewspaper());
		result.addObject("getStandardDeviationArticlesPerNewspaper",
				this.articleService.getStandardDeviationArticlesPerNewspaper());
		result.addObject("getRatioNewspaperCreatedByUser",
				this.newspaperService.getRatioNewspaperCreatedByUser());
		result.addObject("getAverageNewspaperPerUser",
				this.newspaperService.getAverageNewspaperPerUser());
		result.addObject("getStandardDeviationNewspaperPerUser",
				this.newspaperService.getStandardDeviationNewspaperPerUser());
		result.addObject("getNewspapersTenPercentMoreArticles",
				this.newspaperService.getNewspapersTenPercentMoreArticles());
		result.addObject("getNewspapersTenPercentFewerArticles",
				this.newspaperService.getNewspapersTenPercentFewerArticles());
		result.addObject("getRatioPublicVsPrivate",
				this.newspaperService.getRatioPublicVsPrivate());
		result.addObject("getAverageArticlesPerPrivateNewspaper",
				this.newspaperService.getAverageArticlesPerPrivateNewspaper());
		result.addObject("getAverageArticlesPerPublicNewspaper",
				this.newspaperService.getAverageArticlesPerPublicNewspaper());
		result.addObject("getRatioSubscribersPrivateVsTotal",
				this.newspaperService.getRatioSubscribersPrivateVsTotal());
		result.addObject("getAvgRatioPrivateVsPublicPerPublisher",
				this.newspaperService.getAvgRatioPrivateVsPublicPerPublisher());
		result.addObject("findRatioNewspapersAdvertisementsVSNoAdvertisements",
				this.advertisementService.findRatioNewspapersAdvertisementsVSNoAdvertisements());
		result.addObject("findRatioAdvertisementsTaboo",
				this.advertisementService.findRatioAdvertisementsTaboo());
		result.addObject("getAverageNewspapersPerVolume",
				this.volumeService.getAverageNewspapersPerVolume());
		result.addObject("getRatioSubscriptionsNewspaperVsVolume",
				this.volumeService.getRatioSubscriptionsNewspaperVsVolume());

		return result;

	}

	protected ModelAndView createEditModelAndView(final Actor actor) {
		ModelAndView result;
		result = this.createEditModelAndView(actor, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final Actor actor,
			final String message) {
		ModelAndView result;
		result = new ModelAndView("actor/edit");
		result.addObject(actor.getUserAccount().getAuthorities().toArray()[0]
				.toString().toLowerCase(), actor);
		result.addObject("authority", actor.getUserAccount().getAuthorities()
				.toArray()[0].toString().toLowerCase());
		result.addObject("message", message);
		result.addObject("actor", actor);
		return result;
	}
}
