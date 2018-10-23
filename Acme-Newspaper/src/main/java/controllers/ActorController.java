package controllers;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.AgentService;
import services.ArticleService;
import services.ChirpService;
import services.CustomerService;
import services.UserService;
import domain.Actor;
import domain.Agent;
import domain.Article;
import domain.Chirp;
import domain.Customer;
import domain.User;
import forms.RegisterAgent;
import forms.RegisterCustomer;
import forms.RegisterUser;

@Controller
@RequestMapping("/actor")
public class ActorController extends AbstractController {

	@Autowired
	ActorService actorService;

	@Autowired
	UserService userService;

	@Autowired
	CustomerService customerService;
	
	@Autowired
	AgentService agentService;

	@Autowired
	ChirpService chirpService;

	@Autowired
	ArticleService articleService;

	// Create User
	@RequestMapping(value = "/create-user", method = RequestMethod.GET)
	public ModelAndView createUser() {
		ModelAndView result;
		RegisterUser registerUser;

		registerUser = new RegisterUser();
		result = this.createEditModelAndViewRegisterUser(registerUser, null);
		return result;
	}

	@RequestMapping(value = "/create-user-ok", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final RegisterUser registerUser,
			final BindingResult binding) {
		ModelAndView result;
		final User user;

		if (binding.hasErrors()) {
			registerUser.setAcceptedTerms(false);
			result = this
					.createEditModelAndViewRegisterUser(registerUser, null);
		} else
			try {
				user = this.userService.reconstruct(registerUser, binding);
				if (binding.hasErrors())
					result = this.createEditModelAndViewRegisterUser(
							registerUser, null);
				else {
					this.userService.save(user);
					result = new ModelAndView("redirect:/welcome/index.do");
				}
			} catch (final Throwable oops) {
				registerUser.setAcceptedTerms(false);
				result = this.createEditModelAndViewRegisterUser(registerUser,
						"actor.commit.error");
			}

		return result;
	}

	// Create Customer
	@RequestMapping(value = "/create-customer", method = RequestMethod.GET)
	public ModelAndView createCustomer() {
		ModelAndView result;
		RegisterCustomer registerCustomer;

		registerCustomer = new RegisterCustomer();
		result = this.createEditModelAndViewRegisterCustomer(registerCustomer,
				null);
		return result;
	}

	@RequestMapping(value = "/create-customer-ok", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final RegisterCustomer registerCustomer,
			final BindingResult binding) {
		ModelAndView result;
		final Customer customer;

		if (binding.hasErrors()) {
			registerCustomer.setAcceptedTerms(false);
			result = this.createEditModelAndViewRegisterCustomer(
					registerCustomer, null);
		} else
			try {
				customer = this.customerService.reconstructRegister(
						registerCustomer, binding);
				if (binding.hasErrors())
					result = this.createEditModelAndViewRegisterCustomer(
							registerCustomer, null);
				else {
					this.customerService.save(customer);
					result = new ModelAndView("redirect:/welcome/index.do");
				}
			} catch (final Throwable oops) {
				registerCustomer.setAcceptedTerms(false);
				result = this.createEditModelAndViewRegisterCustomer(
						registerCustomer, "actor.commit.error");
			}

		return result;
	}
	
	// Create Agent
		@RequestMapping(value = "/create-agent", method = RequestMethod.GET)
		public ModelAndView createAgent() {
			ModelAndView result;
			RegisterAgent registerAgent= new RegisterAgent();
			result = this.createEditModelAndViewRegisterAgent(registerAgent,
					null);
			return result;
		}

		@RequestMapping(value = "/create-agent-ok", method = RequestMethod.POST, params = "save")
		public ModelAndView save(@Valid final RegisterAgent registerAgent,
				final BindingResult binding) {
			ModelAndView result;
			final Agent agent;

			if (binding.hasErrors()) {
				registerAgent.setAcceptedTerms(false);
				result = this.createEditModelAndViewRegisterAgent(
						registerAgent, null);
			} else
				try {
					agent = this.agentService.reconstruct(registerAgent, binding);
					if (binding.hasErrors())
						result = this.createEditModelAndViewRegisterAgent(
								registerAgent, null);
					else {
						this.agentService.save(agent);
						result = new ModelAndView("redirect:/welcome/index.do");
					}
				} catch (final Throwable oops) {
					registerAgent.setAcceptedTerms(false);
					result = this.createEditModelAndViewRegisterAgent(
							registerAgent, "actor.commit.error");
				}

			return result;
		}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int actorId) {
		ModelAndView result;
		final Actor actor = this.actorService.findOneToEdit(actorId);

		result = this.createEditModelAndView2(actor, null);
		result.addObject("actor", actor);

		return result;
	}

	// Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int actorId) {
		ModelAndView result;
		Actor actor;

		actor = this.actorService.findOne(actorId);

		Assert.notNull(actor);
		result = new ModelAndView("actor/display");
		result.addObject("actor", actor);

		if (actor.getUserAccount().getAuthorities().toArray()[0].toString()
				.equals("USER")) {
			Collection<Chirp> chirps;
			chirps = this.chirpService.findAllByUser(actorId);
			Collection<Article> articles = this.articleService
					.findPublishedByUser(actor.getId());
			result.addObject("articles", articles);
			result.addObject("chirps", chirps);
			result.addObject("requestURI", "actor/display.do");
		}

		return result;
	}

	// Display own data
	@RequestMapping(value = "/display-principal", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result;
		Actor actor;

		actor = this.actorService.findOne(this.actorService.findByPrincipal()
				.getId());
		Assert.notNull(actor);
		result = new ModelAndView("actor/display");
		result.addObject("actor", actor);
		result.addObject("authority", actor.getUserAccount().getAuthorities()
				.toArray()[0].toString().toLowerCase());

		if (actor.getUserAccount().getAuthorities().toArray()[0].toString()
				.equals("USER")) {
			Collection<Chirp> chirps;
			Collection<Article> articles = this.articleService
					.findPublishedByUser(actor.getId());
			result.addObject("articles", articles);

			chirps = this.chirpService.findAllByUser(actor.getId());
			result.addObject("chirps", chirps);
			result.addObject("requestURI", "actor/display-principal.do");
		}

		return result;
	}

	// List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;

		final Collection<User> users = this.userService.findAll();
		result = new ModelAndView("actor/list");
		result.addObject("users", users);

		final String requestURI = "/actor/list.do";
		result.addObject("requestURI", requestURI);

		if (this.actorService.isAuthenticated()
				&& this.actorService.findByPrincipal().getUserAccount()
						.getAuthorities().toArray()[0].toString()
						.equals("USER")) {
			final User principal = this.userService.findByPrincipal();
			result.addObject("principal", principal);
		}

		return result;
	}

	protected ModelAndView createEditModelAndView(final Actor actor) {
		ModelAndView result;
		result = this.createEditModelAndView(actor, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final Actor actor,
			final String message) {
		ModelAndView result;
		result = new ModelAndView("actor/signup");
		result.addObject(actor.getUserAccount().getAuthorities().toArray()[0]
				.toString().toLowerCase(), actor);
		result.addObject("authority", actor.getUserAccount().getAuthorities()
				.toArray()[0].toString().toLowerCase());
		result.addObject("message", message);
		return result;
	}

	protected ModelAndView createEditModelAndView2(final Actor actor,
			final String message) {
		ModelAndView result;
		result = new ModelAndView("actor/edit");
		result.addObject(actor.getUserAccount().getAuthorities().toArray()[0]
				.toString().toLowerCase(), actor);
		result.addObject("authority", actor.getUserAccount().getAuthorities()
				.toArray()[0].toString().toLowerCase());
		result.addObject("message", message);
		return result;
	}

	protected ModelAndView createEditModelAndViewRegisterUser(
			final RegisterUser registerUser, final String message) {
		ModelAndView result;

		final String requestURI = "actor/create-user-ok.do";

		result = new ModelAndView("actor/signup");
		result.addObject("registerActor", "registerUser");
		result.addObject("registerUser", registerUser);
		result.addObject("message", message);
		result.addObject("requestURI", requestURI);

		return result;
	}

	protected ModelAndView createEditModelAndViewRegisterCustomer(
			final RegisterCustomer registerCustomer, final String message) {
		ModelAndView result;

		final String requestURI = "actor/create-customer-ok.do";

		result = new ModelAndView("actor/signup");
		result.addObject("registerActor", "registerCustomer");
		result.addObject("registerCustomer", registerCustomer);
		result.addObject("message", message);
		result.addObject("requestURI", requestURI);

		return result;
	}
	protected ModelAndView createEditModelAndViewRegisterAgent(
			final RegisterAgent registerAgent, final String message) {
		ModelAndView result;

		final String requestURI = "actor/create-agent-ok.do";

		result = new ModelAndView("actor/signup");
		result.addObject("registerActor", "registerAgent");
		result.addObject("registerAgent", registerAgent);
		result.addObject("message", message);
		result.addObject("requestURI", requestURI);

		return result;
	}

}
