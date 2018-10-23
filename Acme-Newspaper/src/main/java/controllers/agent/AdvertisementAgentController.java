
package controllers.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AdvertisementService;
import services.NewspaperService;
import controllers.AbstractController;
import domain.Advertisement;
import domain.Newspaper;

@Controller
@RequestMapping("/advertisement/agent")
public class AdvertisementAgentController extends AbstractController {

	@Autowired
	private AdvertisementService		advertisementService;

	@Autowired
	private NewspaperService 			newspaperService;


	public AdvertisementAgentController() {
		super();
	}


	//Create an advertisement
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(int newspaperId) {
		ModelAndView result;

		final Advertisement advertisement= advertisementService.create();

		result = createEditModelAndView(newspaperId,advertisement);
		result.addObject("newspaperId", newspaperId);
		result.addObject("advertisement", advertisement);
//		result.addObject("requestURI", "newspaper/customer/subscription/list.do");

		return result;
	}

	//Save advertisement
	@RequestMapping(value = "/save", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Advertisement advertisement, final BindingResult binding, @RequestParam int newspaperId) {
		ModelAndView result;

		advertisement = this.advertisementService.reconstruct(advertisement, binding);
		Newspaper newspaper= newspaperService.findOne(newspaperId);

		if (binding.hasErrors())
			result = this.createEditModelAndView(newspaperId,advertisement);
		else
			try {
				this.advertisementService.save(advertisement,newspaper);
				result = new ModelAndView("redirect:/newspaper/list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(newspaperId,advertisement, "advertisement.commit.error");
			}

		return result;
	}

	//Ancillary

	protected ModelAndView createEditModelAndView(final int newspaperId,Advertisement advertisement ) {
		ModelAndView result;

		result = this.createEditModelAndView(newspaperId,advertisement, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final int newspaperId,Advertisement advertisement, final String message) {
		ModelAndView result = null;

		result = new ModelAndView("advertisement/edit");

		result.addObject("newspaperId", newspaperId);
		result.addObject("advertisement", advertisement);

		result.addObject("message", message);

		return result;
	}

}
