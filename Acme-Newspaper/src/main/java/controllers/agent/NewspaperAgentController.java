
package controllers.agent;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import services.NewspaperService;
import controllers.AbstractController;
import domain.Newspaper;

@Controller
@RequestMapping("/newspaper/agent")
public class NewspaperAgentController extends AbstractController {

	@Autowired
	private NewspaperService	newspaperService;


	public NewspaperAgentController() {
		super();
	}

	//List newspapers in wich agent has one or more advertisements
	@RequestMapping(value = "/list-advertisements")
	public ModelAndView listAdvertisements() {
		ModelAndView result;

		final Collection<Newspaper> newspapers = this.newspaperService.getNewspapersWithAdvertisementByAgent();

		result = new ModelAndView("newspaper/list");
		result.addObject("newspapers", newspapers);
		result.addObject("requestURI", "newspaper/list-advertisements.do");
		return result;
	}

	//List newspapers in wich agent has no advertisements
	@RequestMapping(value = "/list-non-advertisements")
	public ModelAndView listNonAdvertisements() {
		ModelAndView result;

		final Collection<Newspaper> newspapers = this.newspaperService.getNewspapersWithoutAdvertisementByAgent();

		result = new ModelAndView("newspaper/list");
		result.addObject("newspapers", newspapers);
		result.addObject("requestURI", "newspaper/list-advertisements.do");
		return result;
	}

}
