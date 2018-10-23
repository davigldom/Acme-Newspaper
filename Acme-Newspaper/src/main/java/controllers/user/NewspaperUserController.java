
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

import services.ActorService;
import services.NewspaperService;
import services.VolumeService;
import controllers.AbstractController;
import domain.Actor;
import domain.Article;
import domain.Newspaper;
import domain.NewspaperStatus;
import domain.Volume;

@Controller
@RequestMapping("/newspaper/user")
public class NewspaperUserController extends AbstractController {

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private VolumeService		volumeService;

	@Autowired
	private ActorService		actorService;


	public NewspaperUserController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Collection<Newspaper> newspapers;

		final Actor principal = this.actorService.findByPrincipal();

		newspapers = this.newspaperService.findAllByPublisher(principal.getId());

		result = new ModelAndView("newspaper/listCreated");
		result.addObject("newspapers", newspapers);
		result.addObject("requestURI", "newspaper/user/list.do");
		result.addObject("isListingCreated", true);
		result.addObject("principal", principal);

		return result;
	}

	@RequestMapping(value = "/list-to-add", method = RequestMethod.GET)
	public ModelAndView listToAdd(final int volumeId) {
		ModelAndView result;
		Collection<Newspaper> newspapers;
		Collection<Newspaper> volumeNewspapers;
		Volume volume;

		final Actor principal = this.actorService.findByPrincipal();

		newspapers = this.newspaperService.findAllByPublisher(principal.getId());
		volume = this.volumeService.findOne(volumeId);
		volumeNewspapers = volume.getNewspapers();

		result = new ModelAndView("newspaper/listCreated");
		result.addObject("newspapers", newspapers);
		result.addObject("requestURI", "newspaper/user/list-to-add.do");
		result.addObject("volumeNewspapers", volumeNewspapers);
		result.addObject("volume", volume);
		result.addObject("isListingCreated", true);
		result.addObject("addButton", true);
		result.addObject("principal", principal);

		return result;
	}
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Newspaper newspaper;
		newspaper = this.newspaperService.create();
		result = this.createEditModelAndView(newspaper);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int newspaperId) {
		ModelAndView result;
		Newspaper newspaper;

		newspaper = this.newspaperService.findOneToEdit(newspaperId);
		Assert.notNull(newspaper);
		result = this.createEditModelAndView(newspaper);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Newspaper newspaper, final BindingResult binding) {
		ModelAndView result;
		if (newspaper.getId() != 0) {
			final Newspaper storedNewspaper = this.newspaperService.findOne(newspaper.getId());
			Assert.isTrue(!storedNewspaper.getStatus().equals(NewspaperStatus.PUBLISHED));
		}
		newspaper = this.newspaperService.reconstruct(newspaper, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(newspaper);
		else
			try {
				if (newspaper.getMakePrivate() == true)
					for (final Article a : newspaper.getArticles())
						Assert.isTrue(!a.isDraft());
				final Newspaper newspaperSaved = this.newspaperService.save(newspaper);
				result = new ModelAndView("redirect:/newspaper/display.do?newspaperId=" + newspaperSaved.getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(newspaper, "newspaper.commit.error");
			}
		return result;
	}

	@RequestMapping(value = "/publish", method = RequestMethod.GET)
	public ModelAndView publish(@RequestParam final int newspaperId) {
		ModelAndView result;

		try {
			this.newspaperService.publish(newspaperId);
			result = new ModelAndView("redirect:/newspaper/list.do");
			result.addObject("message", "newspaper.publish.ok");
		} catch (final Throwable oops) {
			result = this.list();
			result.addObject("message", "newspaper.publish.error");
		}

		return result;
	}

	protected ModelAndView createEditModelAndView(final Newspaper newspaper) {
		ModelAndView result;

		result = this.createEditModelAndView(newspaper, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Newspaper newspaper, final String message) {
		ModelAndView result = null;

		if (newspaper.getId() != 0)
			result = new ModelAndView("newspaper/edit");
		else if (newspaper.getId() == 0)
			result = new ModelAndView("newspaper/create");

		result.addObject("newspaper", newspaper);

		result.addObject("message", message);

		return result;
	}

}
