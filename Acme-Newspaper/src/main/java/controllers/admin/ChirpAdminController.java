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
import services.ChirpService;
import services.SystemConfigService;
import controllers.AbstractController;
import domain.Chirp;

@Controller
@RequestMapping("/chirp/admin")
public class ChirpAdminController extends AbstractController {

	@Autowired
	private ChirpService chirpService;
	@Autowired
	private SystemConfigService systemConfigService;
	@Autowired
	private AdministratorService adminService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView display() {
		Assert.isTrue(this.adminService.findByPrincipal() != null);
		ModelAndView result = null;
		result = new ModelAndView("chirp/list");
		Collection<Chirp> chirps = chirpService.findAll();
		Collection<Chirp> chirpsTaboo = new ArrayList<Chirp>();
		Collection<String> tabooWords = this.systemConfigService.findConfig()
				.getTabooWords();

		for (Chirp c : chirps) {
			for (String word : tabooWords) {
				if (!chirpsTaboo.contains(c)) {
					if (c.getTitle().toLowerCase().contains(word.toLowerCase())
							|| c.getDescription().toLowerCase().contains(word.toLowerCase())) {
						chirpsTaboo.add(c);
					}
				}
			}
		}

		result.addObject("chirps", chirpsTaboo);

		return result;
	}
	
	@RequestMapping(value = "/delete")
	public ModelAndView delete(@RequestParam int chirpId) {
		ModelAndView result;
		Chirp chirp = this.chirpService.findOne(chirpId);
		this.chirpService.delete(chirp);
		result = new ModelAndView("redirect:/chirp/admin/list.do");
		return result;
	}

}
