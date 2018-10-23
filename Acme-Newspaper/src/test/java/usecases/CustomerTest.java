
package usecases;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.CustomerService;
import services.NewspaperService;
import services.SubscriptionService;
import services.VolumeService;
import services.VolumeSubscriptionService;
import utilities.AbstractTest;
import domain.Customer;
import domain.Newspaper;
import domain.Subscription;
import domain.Volume;
import domain.VolumeSubscription;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class CustomerTest extends AbstractTest {

	// System under test

	@Autowired
	private SubscriptionService			subscriptionService;

	@Autowired
	private CustomerService				customerService;

	@Autowired
	private NewspaperService			newspaperService;

	@Autowired
	private VolumeService				volumeService;

	@Autowired
	private VolumeSubscriptionService	volumeSubscriptionService;

	DecimalFormat						df	= new DecimalFormat("#.##");	// To get only 2 decimals


	// ------------------------------------------------------ TESTS
	// ------------------------------------------------------------------

	//	22. An actor who is authenticated as a customer can:
	//		1. Subscribe to a private newspaper by providing a valid credit card.
	@Test
	public void driverSubscribePrivateNewspaper() {
		final Object testingData[][] = {
			//1 POSITIVE test
			{
				"customer2", "4916 5400 2470 5600", 10, 2019, 123, "newspaper6", null
			},
			//2 Negative Test: invalid credit card number
			{
				"customer2", "4916", 10, 2019, 123, "newspaper3", IllegalArgumentException.class
			},
			//3 Negative Test: A user cannot subscribe to newspapers
			{
				"user1", "4916 5400 2470 5600", 10, 2019, 123, "newspaper3", NullPointerException.class
			},
			//4 Negative Test: An admin cannot subscribe to newspapers
			{
				"admin", "4916 5400 2470 5600", 10, 2019, 123, "newspaper3", NullPointerException.class
			},
			//5 Negative Test: As unauthenticated cannot subscribe to newspapers
			{
				null, "4916 5400 2470 5600", 10, 2019, 123, "newspaper3", IllegalArgumentException.class
			},
			//6 Negative Test: Customer1 is already subscribed
			{
				"customer1", "4916 5400 2470 5600", 10, 2019, 123, "newspaper3", IllegalArgumentException.class
			},
			//7 Negative Test: Cannot create a subscription with an already expired year
			{
				"customer2", "4916 5400 2470 5600", 10, 2017, 123, "newspaper3", IllegalArgumentException.class
			},
			//8 Negative Test: newspaper1 is not private
			{
				"customer2", "4916 5400 2470 5600", 10, 2019, 123, "newspaper1", IllegalArgumentException.class
			},
			//9 Negative Test: Cannot create a subscription with an invalid month
			{
				"customer2", "4916 5400 2470 5600", 13, 2019, 123, "newspaper1", IllegalArgumentException.class
			},
			//10 Negative Test: Cannot subscribe without the securityCode
			{
				"customer2", "4916 5400 2470 5600", 10, 2019, 0, "newspaper1", IllegalArgumentException.class

			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateSubscribePrivateNewspaper((String) testingData[i][0], (String) testingData[i][1], (int) testingData[i][2], (int) testingData[i][3], (int) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);
	}

	protected void templateSubscribePrivateNewspaper(final String username, final String creditCardNumber, final int expirationMonth, final int expirationYear, final int securityCode, final String newspaper, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {

			this.authenticate(username);

			//We simulate the listing of newspapers. The user picks one.
			final Collection<Newspaper> newspapers = this.newspaperService.findAll();
			Assert.isTrue(!newspapers.isEmpty());
			final Newspaper result = this.newspaperService.findOne(this.getEntityId(newspaper));

			//We create the subscriptions
			final Subscription sub = new Subscription();
			sub.setCreditCardNumber(creditCardNumber);
			sub.setExpirationMonth(expirationMonth);
			sub.setExpirationYear(expirationYear);
			sub.setNewspaper(result);
			sub.setSecurityCode(securityCode);
			sub.setSubscriber(this.customerService.findByPrincipal());

			this.subscriptionService.save(sub);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}

	// Tests 2.0 ------------------------------------------------------------------

	// Acme-Newspaper 2.0 FR9.1:
	// CUSTOMER - Subscribe to a volume *****************************************************************************************************************************
	// This use case has 10 different test cases because it involves a listing and edition view.
	@Test
	public void driverSubscribeToVolume() {

		final Object testingData[][] = {
			// Test 1: POSITIVE - Subscribe to a volume correctly.
			{
				"customer1", "volume7", "4577 3723 2009 7999", 11, 2019, 123, null
			},
			// Test 2: NEGATIVE - Try to subscribe to a volume as user
			{
				"user1", "volume1", "4577 3723 2009 7999", 11, 2019, 123, NullPointerException.class
			},
			// Test 3: NEGATIVE - Try to subscribe with an invalid credit card number
			{
				"customer1", "volume7", "4577 3723 2009 7997", 11, 2019, 123, IllegalArgumentException.class
			},
			// Test 4: NEGATIVE - Try to subscribe with an invalid expiration month
			{
				"customer1", "volume7", "4577 3723 2009 7999", 20, 2019, 123, IllegalArgumentException.class
			},
			// Test 5: NEGATIVE - Try to subscribe with an invalid format of expiration year
			{
				"customer1", "volume7", "4577 3723 2009 7999", 11, 19, 123, IllegalArgumentException.class
			},
			// Test 6: NEGATIVE - Try to subscribe with an invalid security code
			{
				"customer1", "volume7", "4577 3723 2009 7999", 11, 2019, 0, IllegalArgumentException.class
			},
			// Test 7: NEGATIVE - Try to subscribe to a volume the customer is already subscribed to
			{
				"customer1", "volume5", "4577 3723 2009 7999", 11, 2019, 123, IllegalArgumentException.class
			},
			// Test 8: NEGATIVE - Try to subscribe with a past expiration year
			{
				"customer1", "volume7", "4577 3723 2009 7999", 11, 2017, 123, IllegalArgumentException.class
			},
			// Test 9: NEGATIVE - Try to subscribe without credit card number
			{
				"customer1", "volume7", null, 11, 2019, 123, IllegalArgumentException.class
			},
			// Test 10: NEGATIVE - Try to subscribe to a volume that doesn't exist
			{
				"customer1", "volume8", "4577 3723 2009 7999", 11, 2019, 123, AssertionError.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateSubscribeToVolume((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Integer) testingData[i][5], (Class<?>) testingData[i][6]);
	}

	protected void templateSubscribeToVolume(final String username, final String rendezvousBean, final String creditCardNumber, final int expirationMonth, final int expirationYear, final int securityCode, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);

			final Customer principal = this.customerService.findByPrincipal();

			//Get all the volumes
			final List<Volume> vSubscribedTo = new ArrayList<Volume>();
			final List<Volume> vNotSubscribedTo = new ArrayList<Volume>(this.volumeService.findNotSubscribed(principal.getId()));
			for (final VolumeSubscription vs : principal.getVolumeSubscriptions())
				vSubscribedTo.add(vs.getVolume());

			// First, obtain the selected volume to subscribe to
			final int volumeStoredId = super.getEntityId(rendezvousBean);
			final Volume volumeStored = this.volumeService.findOne(volumeStoredId);

			// Then subscribe to it.
			Assert.isTrue(vNotSubscribedTo.contains(volumeStored));
			final VolumeSubscription vs = this.volumeSubscriptionService.create(volumeStored.getId());
			vs.setCreditCardNumber(creditCardNumber);
			vs.setExpirationMonth(expirationMonth);
			vs.setExpirationYear(expirationYear);
			vs.setSecurityCode(securityCode);
			vs.setSubscriber(principal);

			final VolumeSubscription vsSaved = this.volumeSubscriptionService.save(vs);
			Assert.notNull(vsSaved);
			this.volumeSubscriptionService.flush();

			//Finally check the subscriptions to volumes
			final List<VolumeSubscription> vsList = new ArrayList<VolumeSubscription>(this.volumeSubscriptionService.findAll());
			Assert.isTrue(vsList.contains(vsSaved));
			Assert.isTrue(!this.volumeService.findNotSubscribed(principal.getId()).contains(vsSaved.getVolume()));

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}

}
