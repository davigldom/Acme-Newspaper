
package usecases;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.ActorService;
import services.AdvertisementService;
import services.AgentService;
import services.NewspaperService;
import utilities.AbstractTest;
import domain.Advertisement;
import domain.Agent;
import domain.Newspaper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class AgentTest extends AbstractTest {

	// System under test

	@Autowired
	private NewspaperService		newspaperService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private AgentService			agentService;

	@Autowired
	private AdvertisementService	advertService;


	// ------------------------------------------------------ TESTS
	// ------------------------------------------------------------------

	// Acme-Newspaper 2.0 Extra Functional requirement:
	// AGENT - Edit agent
	// *************************************************************************************************************************************
	// POSITIVE - Editing an agent
	@Test
	public void editUserData() {
		this.authenticate("agent1");
		final Agent agent = (Agent) this.actorService.findOneToEdit(this.getEntityId("agent1"));
		agent.setName("Test name");
		final Agent result = this.agentService.save(agent);

		Assert.isTrue(result.getName().equals("Test name"));

		this.authenticate(null);
	}

	// NEGATIVE - Editing other agent's personal data
	@Test(expected = IllegalArgumentException.class)
	public void editOtherAgentData() {
		this.authenticate("agent1");
		final Agent agent = (Agent) this.actorService.findOneToEdit(this.getEntityId("agent2"));
		agent.setName("Test name");
		this.agentService.save(agent);

		Assert.isTrue(agent.getName().equals("Marcos"));

		this.authenticate(null);
	}

	// NEGATIVE - Editing agent invalid email
	@Test(expected = IllegalArgumentException.class)
	public void editUserInvalidData() {
		this.authenticate("agent1");
		final Agent agent = (Agent) this.actorService.findOneToEdit(this.getEntityId("agent1"));
		agent.setEmail("invalid-email");
		this.agentService.save(agent);

		Assert.isTrue(agent.getEmail().equals("aleruiz@acme.com"));

		this.authenticate(null);
	}

	// Acme-Newspaper 2.0 Extra Functional requirement:
	// AGENT - Delete agent
	// *************************************************************************************************************************************
	// POSITIVE - Deleting an agent
	@Test
	public void deleteAgentData() {
		this.authenticate("agent1");
		final Agent agent = (Agent) this.actorService.findOneToEdit(this.getEntityId("agent1"));

		for (final Advertisement ad : agent.getAdvertisements())
			this.advertService.findNewspaper(ad.getId()).getAdvertisements().remove(ad);

		this.agentService.delete(agent);

		Assert.isTrue(!this.agentService.findAll().contains(agent));

		this.authenticate(null);
	}

	// NEGATIVE - Deleting other user
	@Test(expected = IllegalArgumentException.class)
	public void deleteOtherUser() {
		this.authenticate("agent1");
		final Agent agent = (Agent) this.actorService.findOneToEdit(this.getEntityId("agent2"));
		this.agentService.delete(agent);

		Assert.isTrue(this.agentService.findAll().contains(agent));

		this.authenticate(null);
	}

	// NEGATIVE - Deleting user unauthenticated
	@Test(expected = IllegalArgumentException.class)
	public void deleteNullUser() {
		final Agent agent = (Agent) this.actorService.findOneToEdit(this.getEntityId("agent2"));
		this.agentService.delete(agent);

		Assert.isTrue(this.agentService.findAll().contains(agent));

	}

	//RF4.2
	//Register advertisement and place it in newspaper
	//POSITIVE
	@Test
	public void RegisterAdvertAndPlaceIt() {
		this.authenticate("agent1");
		final Agent agent = (Agent) this.actorService.findOneToEdit(this.getEntityId("agent1"));
		final Newspaper newspaper = this.newspaperService.findOne(this.getEntityId("newspaper1"));
		final Advertisement advert = this.advertService.create();
		advert.setTitle("Test title");
		advert.setBanner("https://www.hellomagazine.com/imagenes/cuisine/2017072640942/kfc-whole-chicken-advert-controversy/0-213-643/kfc-ad-t.jpg");
		advert.setTargetPage("https://www.hellomagazine.com/cuisine/2017072640942/kfc-whole-chicken-advert-controversy/");
		advert.setCreditCardNumber("4267 1148 2813 2174");
		advert.setExpirationMonth(12);
		advert.setExpirationYear(2019);
		advert.setSecurityNumber(123);

		final Advertisement advertSaved = this.advertService.save(advert, newspaper);
		this.advertService.flush();

		Assert.isTrue(this.advertService.findAll().contains(advertSaved));
		Assert.isTrue(agent.getAdvertisements().contains(advertSaved));

		this.authenticate(null);
	}

	//Register advertisement and place it in newspaper which is not published yet
	//NEGATIVE
	@Test(expected = IllegalArgumentException.class)
	public void RegisterAdvertAndPlaceItNotPublished() {
		this.authenticate("agent1");
		final Agent agent = (Agent) this.actorService.findOneToEdit(this.getEntityId("agent1"));
		final Newspaper newspaper = this.newspaperService.findOne(228);
		final Advertisement advert = this.advertService.create();
		advert.setTitle("Test title");
		advert.setBanner("https://www.hellomagazine.com/imagenes/cuisine/2017072640942/kfc-whole-chicken-advert-controversy/0-213-643/kfc-ad-t.jpg");
		advert.setTargetPage("https://www.hellomagazine.com/cuisine/2017072640942/kfc-whole-chicken-advert-controversy/");
		advert.setCreditCardNumber("4267 1148 2813 2174");
		advert.setExpirationMonth(12);
		advert.setExpirationYear(2019);
		advert.setSecurityNumber(123);

		this.advertService.save(advert, newspaper);
		this.advertService.flush();

		Assert.isTrue(!this.advertService.findAll().contains(advert));
		Assert.isTrue(!agent.getAdvertisements().contains(advert));

		this.authenticate(null);
	}

	//Register advertisement and place it in newspaper with an invalid credit card
	//NEGATIVE
	@Test(expected = IllegalArgumentException.class)
	public void RegisterAdvertAndPlaceItInvalidCreditCard() {
		this.authenticate("agent1");
		final Agent agent = (Agent) this.actorService.findOneToEdit(this.getEntityId("agent1"));
		final Newspaper newspaper = this.newspaperService.findOne(226);
		final Advertisement advert = this.advertService.create();
		advert.setTitle("Test title");
		advert.setBanner("https://www.hellomagazine.com/imagenes/cuisine/2017072640942/kfc-whole-chicken-advert-controversy/0-213-643/kfc-ad-t.jpg");
		advert.setTargetPage("https://www.hellomagazine.com/cuisine/2017072640942/kfc-whole-chicken-advert-controversy/");
		advert.setCreditCardNumber("4267 1148 2813 2179");
		advert.setExpirationMonth(12);
		advert.setExpirationYear(2019);
		advert.setSecurityNumber(123);

		this.advertService.save(advert, newspaper);
		this.advertService.flush();

		Assert.isTrue(!this.advertService.findAll().contains(advert));
		Assert.isTrue(!agent.getAdvertisements().contains(advert));

		this.authenticate(null);
	}

	//RF4.3
	//List newspapers with agent's adverts
	//POSITIVE
	@Test
	public void ListNewspapersWithAgentAdverts() {
		this.authenticate("agent1");

		final List<Newspaper> newspapers = new ArrayList<Newspaper>(this.newspaperService.getNewspapersWithAdvertisementByAgent());

		Assert.notNull(newspapers);
		Assert.isTrue(newspapers.size() == 2);

		this.authenticate(null);
	}

	//List newspapers with agent's advert as customer
	//NEGATIVE
	@Test(expected = IllegalArgumentException.class)
	public void ListNewspapersWithAgentAdvertsAsCustomer() {
		this.authenticate("customer1");

		final List<Newspaper> newspapers = new ArrayList<Newspaper>(this.newspaperService.getNewspapersWithAdvertisementByAgent());

		Assert.notNull(newspapers);

		this.authenticate(null);
	}

	//List newspapers with agent's advert as user
	//NEGATIVE
	@Test(expected = IllegalArgumentException.class)
	public void ListNewspapersWithAgentAdvertsAsUser() {
		this.authenticate("user1");

		final List<Newspaper> newspapers = new ArrayList<Newspaper>(this.newspaperService.getNewspapersWithAdvertisementByAgent());

		Assert.notNull(newspapers);

		this.authenticate(null);
	}

	//RF4.4
	//List newspapers with agent's adverts
	//POSITIVE
	@Test
	public void ListNewspapersWithoutAgentAdverts() {
		this.authenticate("agent1");

		final List<Newspaper> newspapers = new ArrayList<Newspaper>(this.newspaperService.getNewspapersWithoutAdvertisementByAgent());

		Assert.notNull(newspapers);
		Assert.isTrue(newspapers.size() == 2);

		this.authenticate(null);
	}

	//List newspapers with agent's advert as customer
	//NEGATIVE
	@Test(expected = IllegalArgumentException.class)
	public void ListNewspapersWithoutAgentAdvertsAsCustomer() {
		this.authenticate("customer1");

		final List<Newspaper> newspapers = new ArrayList<Newspaper>(this.newspaperService.getNewspapersWithoutAdvertisementByAgent());

		Assert.notNull(newspapers);

		this.authenticate(null);
	}

	//List newspapers with agent's advert as user
	//NEGATIVE
	@Test(expected = IllegalArgumentException.class)
	public void ListNewspapersWithoutAgentAdvertsAsUser() {
		this.authenticate("user1");

		final List<Newspaper> newspapers = new ArrayList<Newspaper>(this.newspaperService.getNewspapersWithoutAdvertisementByAgent());

		Assert.notNull(newspapers);

		this.authenticate(null);
	}
}
