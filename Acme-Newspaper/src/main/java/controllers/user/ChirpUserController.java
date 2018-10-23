
package controllers.user;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ChirpService;
import controllers.AbstractController;
import domain.Chirp;

@Controller
@RequestMapping("/chirp/user")
public class ChirpUserController extends AbstractController {

	@Autowired
	private ChirpService	chirpService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result = null;
		result = new ModelAndView("chirp/list");
		Collection<Chirp> chirps= chirpService.getChirpsOfFollowingUsers();
		result.addObject("chirps",chirps);

		return result;
	}
	

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Chirp chirp;
		chirp = this.chirpService.create();
		result = this.createEditModelAndView(chirp);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Chirp chirp, final BindingResult binding) {
		ModelAndView result;

		chirp = this.chirpService.reconstruct(chirp, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(chirp);
		else
			try {
				this.chirpService.save(chirp);
				result = new ModelAndView("redirect:/actor/display-principal.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(chirp, "chirp.commit.error");
			}
		return result;
	}

	protected ModelAndView createEditModelAndView(final Chirp chirp) {
		ModelAndView result;

		result = this.createEditModelAndView(chirp, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Chirp chirp, final String message) {
		ModelAndView result = null;

		result = new ModelAndView("chirp/create");
		result.addObject("chirp", chirp);
		result.addObject("message", message);

		return result;
	}

}
