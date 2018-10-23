
package controllers.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.AdvertisementService;
import services.AgentService;
import services.ChirpService;
import services.NewspaperService;
import controllers.AbstractController;
import domain.Actor;
import domain.Advertisement;
import domain.Agent;

@Controller
@RequestMapping("/actor")
public class ActorAgentController extends AbstractController {

	@Autowired
	ActorService		actorService;

	@Autowired
	AgentService		agentService;

	@Autowired
	NewspaperService	newspaperService;

	@Autowired
	ChirpService		chirpService;

	@Autowired
	AdvertisementService		advertisementService;


	//Edit Agent
	@RequestMapping(value = "/agent/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Agent agent, final BindingResult binding) {
		ModelAndView result;
		Assert.isTrue(agent.equals(this.actorService.findByPrincipal()));
		agent = this.agentService.reconstructEdit(agent, binding);
		if (binding.hasErrors())
			result = this.createEditModelAndView(agent);
		else
			try {
				this.agentService.save(agent);
				result = new ModelAndView("redirect:/actor/display-principal.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(agent, "actor.commit.error");
			}
		return result;
	}

	//Delete an agent
	@RequestMapping(value = "/agent/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final int actorId) {
		ModelAndView result;
		final Agent agent = this.agentService.findOne(actorId);
		Assert.isTrue(agent.equals(this.actorService.findByPrincipal()));
		try {
			for (Advertisement ad : agent.getAdvertisements()) {
				this.advertisementService.findNewspaper(ad.getId()).getAdvertisements().remove(ad);
			}
			this.agentService.delete(agent);
			result = new ModelAndView("redirect:../../j_spring_security_logout");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(agent, "actor.commit.error");
		}
		return result;
	}

	protected ModelAndView createEditModelAndView(final Actor actor) {
		ModelAndView result;
		result = this.createEditModelAndView(actor, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final Actor actor, final String message) {
		ModelAndView result;
		result = new ModelAndView("actor/edit");
		result.addObject(actor.getUserAccount().getAuthorities().toArray()[0].toString().toLowerCase(), actor);
		result.addObject("authority", actor.getUserAccount().getAuthorities().toArray()[0].toString().toLowerCase());
		result.addObject("message", message);
		result.addObject("actor", actor);
		return result;
	}
}
