package controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ArticleService;
import services.FollowupService;
import controllers.AbstractController;
import domain.Article;
import domain.Followup;

@Controller
@RequestMapping("/followup/user")
public class FollowupUserController extends AbstractController {

	@Autowired
	private ArticleService articleService;

	@Autowired
	private FollowupService followupService;

	public FollowupUserController() {
		super();
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int articleId) {
		ModelAndView result;
		Followup followup;
		followup = this.followupService.create();
		result = this.createEditModelAndView(followup, articleId);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@RequestParam final int articleId,
			Followup followup, final BindingResult binding) {
		ModelAndView result;
		final Article article = this.articleService.findOne(articleId);
		followup = this.followupService.reconstruct(followup, article, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(followup, articleId);
		else
			try {
				this.followupService.save(followup);
				result = new ModelAndView(
						"redirect:/article/display.do?articleId=" + articleId);
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(followup, articleId,
						"followup.commit.error");
			}
		return result;
	}

	protected ModelAndView createEditModelAndView(final Followup followup,
			final int articleId) {
		ModelAndView result;

		result = this.createEditModelAndView(followup, articleId, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Followup followup,
			final int articleId, final String message) {
		ModelAndView result = null;

		result = new ModelAndView("followup/edit");

		result.addObject("followup", followup);
		result.addObject("articleId", articleId);

		result.addObject("message", message);

		return result;
	}

}
