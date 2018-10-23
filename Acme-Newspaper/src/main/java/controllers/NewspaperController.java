
package controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.CustomerService;
import services.NewspaperService;
import domain.Actor;
import domain.Article;
import domain.Newspaper;
import domain.NewspaperStatus;

@Controller
@RequestMapping("/newspaper")
public class NewspaperController extends AbstractController {

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private ActorService		actorService;

	@Autowired
	private CustomerService		customerService;


	public NewspaperController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Collection<Newspaper> newspapers = null;
		Collection<Newspaper> newspapersOpened = null;

		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (!this.actorService.isAuthenticated() || authentication.getAuthorities().toArray()[0].toString().equals("AGENT"))
			newspapers = this.newspaperService.findAllPublicPublished();
		else if (authentication.getAuthorities().toArray()[0].toString().equals("CUSTOMER"))
			newspapers = this.newspaperService.findAllPublished();
		else if (authentication.getAuthorities().toArray()[0].toString().equals("USER")) {
			newspapers = this.newspaperService.findAllPublicPublished();
			newspapersOpened = this.newspaperService.findAllPublicOpened();
		} else
			newspapers = this.newspaperService.findAll(); // ADMIN

		result = new ModelAndView("newspaper/list");
		result.addObject("newspapers", newspapers);
		result.addObject("newspapersOpened", newspapersOpened);
		result.addObject("requestURI", "newspaper/list.do");
		result.addObject("isListingCreated", false);

		return result;
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int newspaperId) {
		final ModelAndView result;
		Newspaper newspaper;
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Boolean alreadySubscribed = false;
		String momentFormated = null;
		Actor principal = null;

		if (this.actorService.isAuthenticated())
			principal = this.actorService.findByPrincipal();

		newspaper = this.newspaperService.findOne(newspaperId);

		Assert.notNull(newspaper);

		if (!authentication.getAuthorities().toArray()[0].toString().equals("ADMIN"))
			if (newspaper.getStatus().equals(NewspaperStatus.CLOSE))
				Assert.isTrue(newspaper.getPublisher().equals(principal));
			else if (newspaper.getStatus().equals(NewspaperStatus.OPEN))
				Assert.isTrue(authentication.getAuthorities().toArray()[0].toString().equals("USER"));

		result = new ModelAndView("newspaper/display");

		if (newspaper.getPublicationDate() != null) {
			final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			momentFormated = formatter.format(newspaper.getPublicationDate().getTime());
		}

		if (authentication.getAuthorities().toArray()[0].toString().equals("CUSTOMER")) {
			if (this.newspaperService.findAlreadySubscribedByCustomer(this.customerService.findByPrincipal()).contains(newspaper))
				alreadySubscribed = true;
		} else if (authentication.getAuthorities().toArray()[0].toString().equals("ADMIN"))
			alreadySubscribed = true;

		final Collection<Article> articles = newspaper.getArticles();

		result.addObject("principal", principal);
		result.addObject("momentFormated", momentFormated);
		result.addObject("newspaper", newspaper);
		result.addObject("newspaperId", newspaperId);
		result.addObject("alreadySubscribed", alreadySubscribed);
		result.addObject("displayNewspaper", true);
		result.addObject("articles", articles);

		return result;
	}

	@RequestMapping(value = "/search-word", method = RequestMethod.GET)
	public ModelAndView search(@RequestParam final String keyword) {
		ModelAndView result;

		Collection<Newspaper> newspapers = new ArrayList<Newspaper>();
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (!this.actorService.isAuthenticated() || authentication.getAuthorities().toArray()[0].toString().equals("USER"))
			newspapers = this.newspaperService.findPublicByKeyword(keyword);
		else
			newspapers = this.newspaperService.findAllByKeyword(keyword); // ADMIN

		result = new ModelAndView("newspaper/list");
		result.addObject("newspapers", newspapers);
		result.addObject("isListingCreated", false);
		result.addObject("requestURI", "newspaper/search-word.do");

		return result;
	}

	@RequestMapping(value = "/listVolumeNewspapers", method = RequestMethod.GET)
	public ModelAndView listVolumeNewspapers(@RequestParam final int volumeId) {
		final ModelAndView result;

		Collection<Newspaper> newspapers = new ArrayList<Newspaper>();

		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (!this.actorService.isAuthenticated() || authentication.getAuthorities().toArray()[0].toString().equals("AGENT"))
			newspapers = this.newspaperService.getVolumeNewspapersNotAuthenticated(volumeId);
		else if (this.actorService.isAuthenticated()
			&& (authentication.getAuthorities().toArray()[0].toString().equals("CUSTOMER") || authentication.getAuthorities().toArray()[0].toString().equals("USER") || authentication.getAuthorities().toArray()[0].toString().equals("ADMIN")))
			newspapers = this.newspaperService.getVolumeNewspapersAsCustomer(volumeId);

		result = new ModelAndView("newspaper/list");
		result.addObject("newspapers", newspapers);
		result.addObject("requestURI", "newspaper/listVolumeNewspapers.do");
		result.addObject("backButton", true);

		return result;
	}

}
