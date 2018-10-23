
package controllers.user;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ArticleService;
import services.NewspaperService;
import services.UserService;
import controllers.AbstractController;
import domain.Article;
import domain.Newspaper;
import domain.User;

@Controller
@RequestMapping("/article/user")
public class ArticleUserController extends AbstractController {

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private UserService			userService;

	@Autowired
	private ArticleService		articleService;


	public ArticleUserController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Collection<Article> articles;

		final User principal = this.userService.findByPrincipal();

		articles = this.articleService.findAllByCreator(principal.getId());

		result = new ModelAndView("article/listCreated");
		result.addObject("articles", articles);
		result.addObject("requestURI", "article/user/list.do");
		result.addObject("isListingCreated", true);
		result.addObject("principal", principal);

		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int newspaperId) {
		ModelAndView result;
		Article article;
		article = this.articleService.create();
		result = this.createEditModelAndView(article, newspaperId);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int articleId) {
		ModelAndView result;
		Article article;

		article = this.articleService.findOneToEdit(articleId);
		Assert.notNull(article);
		result = this.createEditModelAndView(article, article.getNewspaper().getId());

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@RequestParam final int newspaperId, Article article, final BindingResult binding) {
		ModelAndView result;
		if (article.getId() != 0) {
			final Article storedArticle = this.articleService.findOne(article.getId());
			Assert.isTrue(storedArticle.isDraft());
		}

		final Newspaper newspaper = this.newspaperService.findOne(newspaperId);
		article = this.articleService.reconstruct(article, newspaper, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(article, newspaperId);
		else
			try {
				final Article articleSaved = this.articleService.save(article);
				result = new ModelAndView("redirect:/article/display.do?articleId=" + articleSaved.getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(article, newspaperId, "article.commit.error");
			}
		return result;
	}

	protected ModelAndView createEditModelAndView(final Article article, final int newspaperId) {
		ModelAndView result;

		result = this.createEditModelAndView(article, newspaperId, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Article article, final int newspaperId, final String message) {
		ModelAndView result = null;

		if (article.getId() != 0)
			result = new ModelAndView("article/edit");
		else if (article.getId() == 0)
			result = new ModelAndView("article/create");

		result.addObject("article", article);
		result.addObject("newspaperId", newspaperId);

		result.addObject("message", message);

		return result;
	}

}
