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
import services.NewspaperService;
import services.SubscriptionService;
import services.VolumeSubscriptionService;
import controllers.AbstractController;
import domain.Customer;
import domain.Newspaper;
import domain.Subscription;
import domain.VolumeSubscription;

@Controller
@RequestMapping("/volume-subscription/customer")
public class VolumeSubscriptionCustomerController extends AbstractController {

	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private VolumeSubscriptionService volumeSubscriptionService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private NewspaperService newspaperService;

	public VolumeSubscriptionCustomerController() {
		super();
	}

	// List customer's volumeSubscriptions
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		final Collection<VolumeSubscription> volumeSubscriptions;

		final Customer principal = this.customerService.findByPrincipal();

		volumeSubscriptions = principal.getVolumeSubscriptions();
		Assert.notNull(volumeSubscriptions);

		result = new ModelAndView("volume-subscription/list");
		result.addObject("volumeSubscriptions", volumeSubscriptions);
		result.addObject("requestURI", "volume-subscription/customer/list.do");

		return result;
	}

	// Create volumeSubscription
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int volumeId) {
		ModelAndView result;
		final VolumeSubscription volumeSubscription = this.volumeSubscriptionService
				.create(volumeId);

		result = this.createEditModelAndView(volumeSubscription);
		return result;
	}

	// Save volumeSubscription
	@RequestMapping(value = "/subscribe", method = RequestMethod.POST, params = "save")
	public ModelAndView subscribe(VolumeSubscription volumeSubscription,
			final BindingResult binding) {
		ModelAndView result;

		volumeSubscription = this.volumeSubscriptionService.reconstruct(
				volumeSubscription, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(volumeSubscription);
		else
			try {
				this.volumeSubscriptionService.save(volumeSubscription);
				for (Newspaper n : volumeSubscription.getVolume()
						.getNewspapers()) {
					
					//If the newspaper in the volume is private and the customer
					//has not subscribed yet, it will create a subscription
					if (n.getMakePrivate() == true
							&& !this.newspaperService
									.findAlreadySubscribedByCustomer(
											this.customerService
													.findByPrincipal())
									.contains(n)) {

						Subscription subscription = this.subscriptionService
								.create(n.getId());
						subscription.setCreditCardNumber(volumeSubscription
								.getCreditCardNumber());
						subscription.setExpirationMonth(volumeSubscription
								.getExpirationMonth());
						subscription.setExpirationYear(volumeSubscription
								.getExpirationYear());
						subscription.setSecurityCode(volumeSubscription
								.getSecurityCode());
						subscription.setSubscriber(this.customerService
								.findByPrincipal());
						this.subscriptionService.save(subscription);
					}
				}
				result = new ModelAndView(
						"redirect:/volume-subscription/customer/list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(volumeSubscription,
						"subscription.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/unsubscribe", method = RequestMethod.GET)
	public ModelAndView unsubscribe(@RequestParam final int volumeSubscriptionId) {
		ModelAndView result;

		final VolumeSubscription volumeSubscription = this.volumeSubscriptionService
				.findOne(volumeSubscriptionId);

		try {
			for (Newspaper n : volumeSubscription.getVolume().getNewspapers()) {
				for (Subscription s : n.getSubscriptions()) {
					if(s.getSubscriber().equals(this.customerService.findByPrincipal())) this.subscriptionService.delete(s);
				}
			}
			this.volumeSubscriptionService.delete(volumeSubscription);
			result = new ModelAndView(
					"redirect:/volume-subscription/customer/list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(volumeSubscription,
					"subscription.commit.error");
		}

		return result;
	}

	// Ancillary

	protected ModelAndView createEditModelAndView(
			final VolumeSubscription volumeSubscription) {
		ModelAndView result;

		result = this.createEditModelAndView(volumeSubscription, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(
			final VolumeSubscription volumeSubscription, final String message) {
		ModelAndView result = null;

		result = new ModelAndView("volume-subscription/create");

		result.addObject("volumeSubscription", volumeSubscription);

		result.addObject("message", message);

		return result;
	}

}
