
package controllers.admin;

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
import services.AdvertisementService;
import services.SystemConfigService;
import controllers.AbstractController;
import domain.Advertisement;
import domain.Newspaper;

@Controller
@RequestMapping("/advertisement/admin")
public class AdvertisementAdminController extends AbstractController {

	@Autowired
	private AdvertisementService			advertisementService;

	@Autowired
	private AdministratorService	adminService;

	@Autowired
	private SystemConfigService systemConfigService;

	public AdvertisementAdminController() {
		super();
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam final int advertisementId) {
		ModelAndView result;

		final Advertisement advertisement = this.advertisementService.findOne(advertisementId);
		Newspaper newspaper = this.advertisementService.findNewspaper(advertisementId);
		try {
			Assert.notNull(this.adminService.findByPrincipal());
			this.advertisementService.delete(advertisement, newspaper);
			result = new ModelAndView("redirect:/advertisement/admin/list.do");
		} catch (final Throwable oops) {
			result = new ModelAndView("redirect:/advertisement/admin/list.do");
			result.addObject("message", "advertisement.commit.error");
		}

		return result;
	}

	@RequestMapping(value = "/list-taboo", method = RequestMethod.GET)
	public ModelAndView listTaboo() {
		Assert.isTrue(this.adminService.findByPrincipal() != null);
		ModelAndView result;
		Collection<Advertisement> advertisements;
		Collection<Advertisement> advertisementsTaboo = new ArrayList<Advertisement>();
		Collection<String> tabooWords = this.systemConfigService.findConfig()
				.getTabooWords();

		advertisements = this.advertisementService.findAll();

		for (Advertisement a : advertisements) {
			for (String word : tabooWords) {
				if (!advertisementsTaboo.contains(a)) {
					if (a.getTitle().toLowerCase().contains(word.toLowerCase())) {
						advertisementsTaboo.add(a);
					}
				}
			}
		}

		result = new ModelAndView("advertisement/list");
		result.addObject("advertisements", advertisementsTaboo);
		result.addObject("requestURI", "advertisement/admin/list.do");

		return result;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		Assert.isTrue(this.adminService.findByPrincipal() != null);
		ModelAndView result;
		Collection<Advertisement> advertisements;

		advertisements = this.advertisementService.findAll();

		result = new ModelAndView("advertisement/list");
		result.addObject("advertisements", advertisements);
		result.addObject("requestURI", "advertisement/admin/list.do");

		return result;
	}


}
