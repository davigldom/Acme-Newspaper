
package services;

import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.SubscriptionRepository;
import domain.Customer;
import domain.Newspaper;
import domain.Subscription;

@Service
@Transactional
public class SubscriptionService {

	@Autowired
	private SubscriptionRepository	subscriptionRepository;

	@Autowired
	private CustomerService			customerService;

	@Autowired
	private NewspaperService		newspaperService;


	public Subscription create(final int newspaperId) {
		final Subscription result;
		final Newspaper newspaper = this.newspaperService.findOneToSubscribe(newspaperId);

		result = new Subscription();

		result.setNewspaper(newspaper);

		return result;
	}

	public Subscription findOne(final int subscriptionId) {
		Subscription result;
		Assert.isTrue(subscriptionId != 0);
		result = this.subscriptionRepository.findOne(subscriptionId);
		Assert.notNull(result);
		return result;
	}

	public Collection<Subscription> findAll() {
		return this.subscriptionRepository.findAll();
	}

	public Subscription save(final Subscription subscription) {
		Subscription result;
		final Customer principal = this.customerService.findByPrincipal();
		Assert.notNull(subscription);

		Assert.isTrue(subscription.getSubscriber().equals(principal));
		Assert.notNull(this.newspaperService.findOneToSubscribe(subscription.getNewspaper().getId()));

		result = this.subscriptionRepository.save(subscription);

		result.getNewspaper().getSubscriptions().add(result);
		principal.getSubscriptions().add(result);

		return result;
	}

	public Subscription saveFromVolume(final Subscription subscription, Customer customer) {
		Subscription result;
		Assert.notNull(subscription);
		Assert.notNull(customer);

		result = this.subscriptionRepository.save(subscription);

		result.getNewspaper().getSubscriptions().add(result);
		customer.getSubscriptions().add(result);

		return result;
	}
	
	public void delete(final Subscription subscription) {
		final Customer principal = this.customerService.findByPrincipal();

		Assert.notNull(principal);
		Assert.notNull(subscription);

		this.subscriptionRepository.delete(subscription);
	}

	public void flush() {
		this.subscriptionRepository.flush();
	}


	//Reconstruct 

	@Autowired
	private Validator	validator;


	public Subscription reconstruct(final Subscription subscription, final BindingResult binding) {

		Assert.isTrue(subscription.getId() == 0);

		final Customer principal = this.customerService.findByPrincipal();
		final Newspaper newspaper = subscription.getNewspaper();
		Assert.notNull(newspaper);

		subscription.setSubscriber(principal);
		subscription.setNewspaper(newspaper);

		this.validator.validate(subscription, binding);

		return subscription;
	}

}
