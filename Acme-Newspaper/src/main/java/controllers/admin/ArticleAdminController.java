
package controllers.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AdministratorService;
import services.ArticleService;
import services.SystemConfigService;
import controllers.AbstractController;
import domain.Actor;
import domain.Article;

@Controller
@RequestMapping("/article/admin")
public class ArticleAdminController extends AbstractController {

	@Autowired
	private ArticleService			articleService;

	@Autowired
	private AdministratorService	adminService;

	@Autowired
	private SystemConfigService systemConfigService;

	public ArticleAdminController() {
		super();
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam final int articleId) {
		ModelAndView result;

		final Article article = this.articleService.findOne(articleId);
		final int newspaperId = article.getNewspaper().getId();
		try {
			Assert.notNull(this.adminService.findByPrincipal());
			this.articleService.delete(article);
			result = new ModelAndView("redirect:/newspaper/display.do?newspaperId=" + newspaperId);
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(article, "article.commit.error");
		}

		return result;
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		Assert.isTrue(this.adminService.findByPrincipal() != null);
		ModelAndView result;
		Collection<Article> articles;
		Collection<Article> articlesTaboo = new ArrayList<Article>();
		Collection<String> tabooWords = this.systemConfigService.findConfig()
				.getTabooWords();

		articles = this.articleService.findAll();

		for (Article a : articles) {
			for (String word : tabooWords) {
				if (!articlesTaboo.contains(a)) {
					if (a.getTitle().toLowerCase().contains(word.toLowerCase())
							|| a.getBody().toLowerCase().contains(word.toLowerCase())
							|| a.getSummary().toLowerCase().contains(word.toLowerCase())) {
						articlesTaboo.add(a);
					}
				}
			}
		}

		result = new ModelAndView("article/list");
		result.addObject("articles", articlesTaboo);
		result.addObject("isListingCreated", false);
		result.addObject("requestURI", "article/admin/list.do");

		return result;
	}

	protected ModelAndView createEditModelAndView(final Article article) {
		ModelAndView result;

		result = this.createEditModelAndView(article, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Article article, final String message) {
		ModelAndView result;
		String momentFormated = null;
		Actor principal = null;

		principal = this.adminService.findByPrincipal();

		Assert.notNull(article);
		result = new ModelAndView("article/display");

		if (article.getPublicationMoment() != null) {
			final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			momentFormated = formatter.format(article.getPublicationMoment().getTime());
		}

		result.addObject("principal", principal);
		result.addObject("momentFormated", momentFormated);
		result.addObject("article", article);
		result.addObject("articleId", article.getId());
		result.addObject("displayArticle", true);

		return result;
	}

}
