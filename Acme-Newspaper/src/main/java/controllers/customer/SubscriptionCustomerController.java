
package controllers.customer;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CustomerService;
import services.SubscriptionService;
import controllers.AbstractController;
import domain.Customer;
import domain.Subscription;

@Controller
@RequestMapping("/newspaper/customer/subscription")
public class SubscriptionCustomerController extends AbstractController {

	@Autowired
	private SubscriptionService	subscriptionService;
	@Autowired
	private CustomerService		customerService;


	public SubscriptionCustomerController() {
		super();
	}

	//We are not going to make an edit controller as it's not required, if someone wants to "edit" one of his/her subscriptions, he/she would have to unsubscribe and subscribe again

	//List customer's subscriptions
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		final Collection<Subscription> subscriptions;

		final Customer principal = this.customerService.findByPrincipal();

		subscriptions = principal.getSubscriptions();
		Assert.notNull(subscriptions);

		result = new ModelAndView("subscription/list");
		result.addObject("subscriptions", subscriptions);
		result.addObject("requestURI", "newspaper/customer/subscription/list.do");

		return result;
	}

	//Create subscription
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int newspaperId) {
		ModelAndView result;
		final Subscription subscription = this.subscriptionService.create(newspaperId);

		result = this.createEditModelAndView(subscription);
		return result;
	}

	//Save subscription
	@RequestMapping(value = "/subscribe", method = RequestMethod.POST, params = "save")
	public ModelAndView subscribe(Subscription subscription, final BindingResult binding) {
		ModelAndView result;

		subscription = this.subscriptionService.reconstruct(subscription, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(subscription);
		else
			try {
				this.subscriptionService.save(subscription);
				result = new ModelAndView("redirect:/newspaper/customer/subscription/list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(subscription, "subscription.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/unsubscribe", method = RequestMethod.GET)
	public ModelAndView unsubscribe(@RequestParam final int subscriptionId) {
		ModelAndView result;

		final Subscription subscription = this.subscriptionService.findOne(subscriptionId);

		try {
			this.subscriptionService.delete(subscription);
			result = new ModelAndView("redirect:/newspaper/customer/subscription/list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(subscription, "subscription.commit.error");
		}

		return result;
	}

	//Ancillary 

	protected ModelAndView createEditModelAndView(final Subscription subscription) {
		ModelAndView result;

		result = this.createEditModelAndView(subscription, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Subscription subscription, final String message) {
		ModelAndView result = null;

		result = new ModelAndView("subscription/create");

		result.addObject("subscription", subscription);

		result.addObject("message", message);

		return result;
	}

}
