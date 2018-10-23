
package controllers.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.CustomerService;
import services.UserService;
import controllers.AbstractController;
import domain.Actor;
import domain.Customer;

@Controller
@RequestMapping("/actor")
public class ActorCustomerController extends AbstractController {

	@Autowired
	ActorService	actorService;

	@Autowired
	UserService		userService;

	@Autowired
	CustomerService	customerService;


	//Edit Customer
	@RequestMapping(value = "/customer/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Customer customer, final BindingResult binding) {
		ModelAndView result;
		Assert.isTrue(customer.equals(this.actorService.findByPrincipal()));

		customer = this.customerService.reconstructEdit(customer, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(customer);
		else
			try {
				this.customerService.save(customer);
				result = new ModelAndView("redirect:/actor/display-principal.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(customer, "actor.commit.error");
			}
		return result;
	}

	//Delete a customer
	@RequestMapping(value = "/customer/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final int actorId) {
		ModelAndView result;
		final Customer customer = this.customerService.findOne(actorId);
		Assert.isTrue(customer.equals(this.actorService.findByPrincipal()));
		try {
			this.customerService.delete(customer);
			result = new ModelAndView("redirect:../../j_spring_security_logout");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(customer, "actor.commit.error");
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
