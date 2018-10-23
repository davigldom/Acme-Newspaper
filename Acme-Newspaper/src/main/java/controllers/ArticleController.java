
package controllers;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.ArticleService;
import services.NewspaperService;
import services.UserService;
import domain.Actor;
import domain.Advertisement;
import domain.Article;
import domain.Newspaper;
import domain.NewspaperStatus;

@Controller
@RequestMapping("/article")
public class ArticleController extends AbstractController {

	@Autowired
	private ArticleService		articleService;

	@Autowired
	private ActorService		actorService;

	@Autowired
	private UserService			userService;

	@Autowired
	private NewspaperService	newspaperService;


	public ArticleController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int newspaperId) {
		ModelAndView result;
		Collection<Article> articles;

		articles = this.articleService.findAllByNewspaper(newspaperId);
		final Newspaper newspaper = this.newspaperService.findOne(newspaperId);
		if (newspaper.getStatus().equals(NewspaperStatus.OPEN))
			Assert.notNull(this.userService.findByPrincipal());
		if (newspaper.getStatus().equals(NewspaperStatus.CLOSE))
			Assert.isTrue(newspaper.getPublisher().equals(this.actorService.findByPrincipal()));

		result = new ModelAndView("article/list");
		result.addObject("articles", articles);
		result.addObject("isListingCreated", false);
		result.addObject("listArticles", true);
		result.addObject("requestURI", "article/list.do");
		result.addObject("newspaper", newspaper);
		result.addObject("newspaperId", newspaperId);

		return result;
	}
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int articleId) {
		ModelAndView result;
		Article article;
		//		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String momentFormatted = null;
		Actor principal = null;

		if (this.actorService.isAuthenticated())
			principal = this.actorService.findByPrincipal();

		article = this.articleService.findOne(articleId);
		if (article.getNewspaper().getStatus().equals(NewspaperStatus.OPEN))
			Assert.notNull(this.userService.findByPrincipal());

		Assert.notNull(article);
		result = new ModelAndView("article/display");

		if (article.getPublicationMoment() != null) {
			final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			momentFormatted = formatter.format(article.getPublicationMoment().getTime());
		}

		if (!article.getNewspaper().getAdvertisements().isEmpty()) {
			final Random rand = new Random();
			final Advertisement ad = (Advertisement) article.getNewspaper().getAdvertisements().toArray()[rand.nextInt(article.getNewspaper().getAdvertisements().size())];
			result.addObject("ad", ad);
		}

		result.addObject("principal", principal);
		result.addObject("momentFormated", momentFormatted);
		result.addObject("article", article);
		result.addObject("articleId", articleId);
		result.addObject("displayArticle", true);
		return result;
	}

	@RequestMapping(value = "/search-word", method = RequestMethod.GET)
	public ModelAndView search(@RequestParam final String keyword, @RequestParam final int newspaperId) {
		ModelAndView result;

		final Collection<Article> articles = this.articleService.findByKeyword(keyword, newspaperId);

		result = new ModelAndView("article/list");
		result.addObject("articles", articles);
		result.addObject("requestURI", "article/search-word.do");
		result.addObject("isListingCreated", false);
		result.addObject("newspaperId", newspaperId);

		return result;
	}

}
