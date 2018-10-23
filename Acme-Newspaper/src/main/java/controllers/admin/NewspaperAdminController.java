
package controllers.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AdministratorService;
import services.AdvertisementService;
import services.NewspaperService;
import services.SystemConfigService;
import services.VolumeService;
import controllers.AbstractController;
import domain.Actor;
import domain.Advertisement;
import domain.Article;
import domain.Newspaper;
import domain.Volume;

@Controller
@RequestMapping("/newspaper/admin")
public class NewspaperAdminController extends AbstractController {

	@Autowired
	private NewspaperService		newspaperService;

	@Autowired
	private AdministratorService	adminService;

	@Autowired
	private SystemConfigService	systemConfigService;

	@Autowired
	private AdvertisementService	advertisementService;

	@Autowired
	private VolumeService	volumeService;


	public NewspaperAdminController() {
		super();
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam final int newspaperId) {
		ModelAndView result;

		final Newspaper newspaper = this.newspaperService.findOne(newspaperId);
		try {
			Assert.notNull(this.adminService.findByPrincipal());
			//			for (Article a : newspaper.getArticles()) {
			//				this.articleService.delete(a);
			//			}
			Iterator<Advertisement> iter = newspaper.getAdvertisements().iterator();

			while (iter.hasNext()) {
			    Advertisement ad = iter.next();
			    iter.remove();
			    this.advertisementService.delete(ad, newspaper);
			}
			for (Volume v : this.volumeService.findByNewspaper(newspaper.getId())) {
				v.getNewspapers().remove(newspaper);
			}
			this.newspaperService.delete(newspaper);
			result = new ModelAndView("redirect:/newspaper/list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(newspaper, "newspaper.commit.error");
		}

		return result;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		Assert.isTrue(this.adminService.findByPrincipal() != null);
		ModelAndView result;
		Collection<Newspaper> newspapers;
		final Collection<Newspaper> newspapersTaboo = new ArrayList<Newspaper>();
		final Collection<String> tabooWords = this.systemConfigService.findConfig().getTabooWords();

		newspapers = this.newspaperService.findAll();

		for (Newspaper a: newspapers) {
			for (String word : tabooWords) {
				if (!newspapersTaboo.contains(a)) {
					if(a.getTitle().toLowerCase().contains(word.toLowerCase()) || a.getDescription().toLowerCase().contains(word.toLowerCase())){

						newspapersTaboo.add(a);
					}
				}
			}
		}
		result = new ModelAndView("newspaper/list");
		result.addObject("newspapers", newspapersTaboo);
		result.addObject("requestURI", "newspaper/admin/list.do");
		result.addObject("isListingCreated", false);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Newspaper newspaper) {
		ModelAndView result;

		result = this.createEditModelAndView(newspaper, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Newspaper newspaper, final String message) {
		ModelAndView result;
		String momentFormated = null;
		Actor principal = null;

		principal = this.adminService.findByPrincipal();

		result = new ModelAndView("newspaper/display");

		if (newspaper.getPublicationDate() != null) {
			final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			momentFormated = formatter.format(newspaper.getPublicationDate().getTime());
		}

		final Collection<Article> articles = newspaper.getArticles();

		result.addObject("principal", principal);
		result.addObject("momentFormated", momentFormated);
		result.addObject("newspaper", newspaper);
		result.addObject("newspaperId", newspaper.getId());
		result.addObject("displayNewspaper", true);
		result.addObject("articles", articles);

		return result;
	}

}
