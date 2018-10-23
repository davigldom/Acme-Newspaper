
package usecases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.AgentService;
import services.ArticleService;
import services.CustomerService;
import services.NewspaperService;
import services.UserService;
import services.VolumeService;
import utilities.AbstractTest;
import domain.Advertisement;
import domain.Agent;
import domain.Article;
import domain.Customer;
import domain.Folder;
import domain.Newspaper;
import domain.Subscription;
import domain.User;
import domain.Volume;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class NonAuthenticatedTest extends AbstractTest {

	// System under test

	@Autowired
	private UserService			userService;

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private ArticleService		articleService;

	@Autowired
	private CustomerService		customerService;

	@Autowired
	private AgentService		agentService;

	@Autowired
	private VolumeService		volumeService;


	@Before
	public void setupAuthentication() {
		SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("GUEST", "USERNAME", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
	}

	// Tests ------------------------------------------------------------------

	//RF4.1
	//An actor who is not authenticated must be able to:
	//1. Register to the system as a user.

	// POSITIVE: Creating a user
	@Test
	public void positiveTestRegisterAsUser() {
		final User user = this.userService.create();
		user.getUserAccount().setUsername("testUser");
		user.getUserAccount().setPassword("testUser");
		user.setArticles(new ArrayList<Article>());
		user.setNewspapers(new ArrayList<Newspaper>());
		user.setFolders(new HashSet<Folder>());
		user.setEmail("user@gmail.co");
		user.setName("user");
		user.setPhone("+34664052167");
		user.setPostalAddress("41530");
		user.setSurname("surname");

		this.userService.save(user);

		this.authenticate("testUser");
		this.authenticate(null);
	}

	// NEGATIVE: Creating a user with a username that already exists
	@Test(expected = DataIntegrityViolationException.class)
	public void negativeRegisterAsUserWhoseUsernameExists() {
		final User user = this.userService.create();
		user.getUserAccount().setUsername("user1");
		user.getUserAccount().setPassword("testUser");
		user.setArticles(new ArrayList<Article>());
		user.setNewspapers(new ArrayList<Newspaper>());
		user.setFolders(new HashSet<Folder>());
		user.setEmail("user@gmail.co");
		user.setName("user");
		user.setPhone("+34664052167");
		user.setPostalAddress("41530");
		user.setSurname("surname");

		this.userService.save(user);
		this.userService.flush();
	}

	// NEGATIVE: Creating a user without email
	@Test(expected = ConstraintViolationException.class)
	public void negativeRegisterAsUserWithoutEmail() {
		final User user = this.userService.create();
		user.getUserAccount().setUsername("testUser");
		user.getUserAccount().setPassword("testUser");
		user.setArticles(new ArrayList<Article>());
		user.setNewspapers(new ArrayList<Newspaper>());
		user.setFolders(new HashSet<Folder>());
		user.setName("user");
		user.setPhone("+34664052167");
		user.setPostalAddress("41530");
		user.setSurname("surname");

		this.userService.save(user);
		this.userService.flush();
	}

	//RF4.2
	//An actor who is not authenticated must be able to:
	//2. List the newspapers that are published and browse their articles.

	// POSITIVE: List newspapers as unauthenticated
	@Test
	public void positiveTestListNewspapersAndBrowseArticles() {

		this.authenticate(null);
		final Collection<Newspaper> newspapers = this.newspaperService.findAllPublished();
		Assert.notNull(newspapers);
		final Collection<Article> articles = this.articleService.findAllByNewspaper(this.getEntityId("newspaper1"));
		Assert.isTrue(articles.size() == 6);

	}

	// Negative: List all newspapers and see incorrect number of articles
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestListAllNewspapers() {

		this.authenticate(null);
		final Collection<Newspaper> newspapers = this.newspaperService.findAllPublished();
		Assert.notNull(newspapers);
		final int newspaperId = this.getEntityId("newspaper1");
		Assert.notNull(newspaperId);
		final ArrayList<Article> articles = (ArrayList<Article>) this.articleService.findAllByNewspaper(newspaperId);
		articles.remove(0);
		Assert.isTrue(articles.size() == 1);

	}

	// Negative: List all newspapers and try to see articles from non existent newspaper should not work
	@Test(expected = AssertionError.class)
	public void negativeTestListNewspapersandBrowseNonExistentNewspaper() {

		this.authenticate(null);
		final Collection<Newspaper> newspapers = this.newspaperService.findAllPublished();
		Assert.notNull(newspapers);
		final int newspaperId = this.getEntityId("newspaper99");
		Assert.notNull(newspaperId);
		final Collection<Article> articles = this.articleService.findAllByNewspaper(newspaperId);
		Assert.notNull(articles);

	}

	//RF4.3
	//An actor who is not authenticated must be able to:
	//3. List the users of the system and display their profiles, which must include their personal
	//data and the list of articles that they have written as long as they are published
	//in a newspaper.

	// POSITIVE: List users and navigate to one's profile and show its data.
	@Test
	public void positiveTestListUsersAndTheirArticles() {

		this.authenticate(null);
		final Collection<User> users = this.userService.findAll();
		Assert.notNull(users);
		final User user1 = this.userService.findOne(this.getEntityId("user1"));
		Assert.notNull(user1.getEmail());
		Assert.notNull(user1.getName());
		Assert.notNull(user1.getPostalAddress());
		Assert.notNull(user1.getPhone());
		Assert.notNull(user1.getSurname());
		final Collection<Article> articles = this.articleService.findAllByCreator(user1.getId());
		Assert.notNull(articles);
	}

	//NEGATIVE: You cannot see a customer profile unless you are that customer
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestListUsersAndNavigateToProfile() {

		this.authenticate(null);
		final Collection<User> users = this.userService.findAll();
		Assert.notNull(users);
		final User user1 = this.userService.findOne(this.getEntityId("customer1"));
		Assert.notNull(user1.getEmail());
		Assert.notNull(user1.getName());
		Assert.notNull(user1.getPostalAddress());
		Assert.notNull(user1.getPhone());
		Assert.notNull(user1.getSurname());
		final Collection<Article> articles = this.articleService.findAllByCreator(user1.getId());
		Assert.notNull(articles);
	}

	//NEGATIVE: You cannot navigate to a non existent user
	@Test(expected = AssertionError.class)
	public void negativeTestListUsers() {

		this.authenticate(null);
		final Collection<User> users = this.userService.findAll();
		Assert.notNull(users);
		final User user1 = this.userService.findOne(this.getEntityId("user99"));
		Assert.notNull(user1.getEmail());
		Assert.notNull(user1.getName());
		Assert.notNull(user1.getPostalAddress());
		Assert.notNull(user1.getPhone());
		Assert.notNull(user1.getSurname());
		final Collection<Article> articles = this.articleService.findAllByCreator(user1.getId());
		Assert.notNull(articles);
	}

	//RF4.4
	//An actor who is not authenticated must be able to:
	//4. Search for a published article using a single key word that must appear somewhere
	//in its title, summary, or body.
	@Test
	public void positiveTestSearchArticlesByFinder() {

		this.authenticate(null);
		final Collection<Article> articles = this.articleService.findByKeyword("spiderman", this.getEntityId("newspaper1"));
		Assert.notNull(articles);
		Assert.isTrue(articles.contains(this.articleService.findOne(this.getEntityId("article1"))));
	}
	//NEGATIVE:There is no articles with "99" in its content
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestSearchArticlesByFinder() {

		this.authenticate(null);
		final Collection<Article> articles = this.articleService.findByKeyword("99", this.getEntityId("newspaper1"));
		Assert.notNull(articles);
		Assert.isTrue(articles.contains(this.articleService.findOne(this.getEntityId("article1"))));
	}

	//NEGATIVE:You cannot see a non published article as unauthenticated
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestSearchNonPublishedArticlesByFinder() {

		this.authenticate(null);
		final Collection<Article> articles = this.articleService.findByKeyword("a", this.getEntityId("newspaper2"));
		Assert.notNull(articles);
		Assert.isTrue(articles.contains(this.articleService.findOne(this.getEntityId("article2"))));
	}

	//RF4.5
	//An actor who is not authenticated must be able to:
	//5. Search for a published newspaper using a single keyword that must appear somewhere
	//in its title or its description.
	@Test
	public void positiveTestSearchPublicNewspaper() {

		this.authenticate(null);
		final Collection<Newspaper> newspaper = this.newspaperService.findPublicByKeyword("new york");
		Assert.notNull(newspaper);
		Assert.isTrue(newspaper.contains(this.newspaperService.findOne(this.getEntityId("newspaper1"))));
	}

	//Negative Test: pineapple does not match any newspaper
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestSearchPublicNewspaper() {

		this.authenticate(null);
		final Collection<Newspaper> newspaper = this.newspaperService.findPublicByKeyword("pineapple");
		Assert.isTrue(!newspaper.isEmpty());
	}

	//Negative Test: Does not match any newspaper because newspaper3 is private
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestSearchPublicNewspaper2() {

		this.authenticate(null);
		final Collection<Newspaper> newspaper = this.newspaperService.findAllByKeyword("This newspaper is a school project about sex");
		Assert.notNull(newspaper);
		Assert.isTrue(newspaper.contains(this.newspaperService.findOne(this.getEntityId("newspaper3"))));
	}

	//RF22.1
	//An actor who is not authenticated must be able to:
	//1. Register to the system as a customer.

	// POSITIVE: Creating a customer
	@Test
	public void positiveTestRegisterAsCustomer() {
		final Customer customer = this.customerService.create();
		customer.getUserAccount().setUsername("testUser");
		customer.getUserAccount().setPassword("testUser");
		customer.setSubscriptions(new ArrayList<Subscription>());
		customer.setFolders(new HashSet<Folder>());
		customer.setEmail("user@gmail.co");
		customer.setName("user");
		customer.setPhone("+34664052167");
		customer.setPostalAddress("41530");
		customer.setSurname("surname");

		this.customerService.save(customer);
	}

	// NEGATIVE: Creating a customer with name that already exits should not work
	@Test(expected = DataIntegrityViolationException.class)
	public void negativeTestRegisterAsCustomer() {
		final Customer customer = this.customerService.create();
		customer.getUserAccount().setUsername("customer1");
		customer.getUserAccount().setPassword("testUser");
		customer.setSubscriptions(new ArrayList<Subscription>());
		customer.setFolders(new HashSet<Folder>());
		customer.setEmail("user@gmail.co");
		customer.setName("user");
		customer.setPhone("+34664052167");
		customer.setPostalAddress("41530");
		customer.setSurname("surname");

		this.customerService.save(customer);

		this.authenticate("testUser");
		this.authenticate(null);
	}

	// NEGATIVE: Creating a customer without email should not work
	@Test(expected = DataIntegrityViolationException.class)
	public void negativeTestRegisterAsCustomer2() {
		final Customer customer = this.customerService.create();
		customer.getUserAccount().setUsername("customer1");
		customer.getUserAccount().setPassword("testUser");
		customer.setSubscriptions(new ArrayList<Subscription>());
		customer.setFolders(new HashSet<Folder>());
		customer.setEmail("");
		customer.setName("user");
		customer.setPhone("+34664052167");
		customer.setPostalAddress("41530");
		customer.setSurname("surname");

		this.customerService.save(customer);

		this.authenticate("testUser");
		this.authenticate(null);
	}

	// Tests 2.0 ------------------------------------------------------------------

	//RF3.1
	//An actor who is not authenticated must be able to:
	//1. Register to the system as an agent.

	// POSITIVE: Creating an agent
	@Test
	public void positiveTestRegisterAsAgent() {
		final Agent agent = this.agentService.create();
		agent.getUserAccount().setUsername("testAgent");
		agent.getUserAccount().setPassword("testAgent");
		agent.setEmail("user@gmail.co");
		agent.setName("agent");
		agent.setPhone("+34664052167");
		agent.setPostalAddress("41530");
		agent.setSurname("surname");

		agent.setFolders(new HashSet<Folder>());
		agent.setAdvertisements(new HashSet<Advertisement>());

		this.agentService.save(agent);

		this.authenticate("testAgent");
		this.authenticate(null);
	}

	// NEGATIVE: Creating an agent with a username that already exists
	@Test(expected = DataIntegrityViolationException.class)
	public void negativeRegisterAsAgentWhoseUsernameExists() {
		final Agent agent = this.agentService.create();
		agent.getUserAccount().setUsername("agent1");
		agent.getUserAccount().setPassword("testAgent");
		agent.setEmail("user@gmail.co");
		agent.setName("agent");
		agent.setPhone("+34664052167");
		agent.setPostalAddress("41530");
		agent.setSurname("surname");

		agent.setFolders(new HashSet<Folder>());
		agent.setAdvertisements(new HashSet<Advertisement>());

		this.agentService.save(agent);
		this.agentService.flush();
	}

	// NEGATIVE: Creating an agent without email
	@Test(expected = ConstraintViolationException.class)
	public void negativeRegisterAsAgentWithoutEmail() {
		final Agent agent = this.agentService.create();
		agent.getUserAccount().setUsername("testAgent");
		agent.getUserAccount().setPassword("testAgent");
		agent.setName("agent");
		agent.setPhone("+34664052167");
		agent.setPostalAddress("41530");
		agent.setSurname("surname");

		agent.setFolders(new HashSet<Folder>());
		agent.setAdvertisements(new HashSet<Advertisement>());

		this.agentService.save(agent);
		this.agentService.flush();
	}

	//RF8.2
	//An actor who is not authenticated must be able to:
	//List the volumes and browse their newspapers

	// POSITIVE
	@Test
	public void positiveTestListVolumesAndBrowseNewspapers() {

		final Set<Volume> volumes = new HashSet<Volume>(this.volumeService.findAll());
		Assert.notNull(volumes);
		final Collection<Newspaper> newspapers = this.volumeService.findOne(this.getEntityId("volume1")).getNewspapers();
		Assert.notNull(newspapers);

		Assert.isTrue(volumes.size() == 7);
		Assert.isTrue(newspapers.size() == 2);

	}

	// Negative: List all volumes and see incorrect size of them
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestListAllVolumesAndBrowseNewspapersBadSize() {

		final Set<Volume> volumes = new HashSet<Volume>(this.volumeService.findAll());
		Assert.notNull(volumes);
		final Collection<Newspaper> newspapers = this.volumeService.findOne(this.getEntityId("volume1")).getNewspapers();
		Assert.notNull(newspapers);

		Assert.isTrue(volumes.size() == 4);
		Assert.isTrue(newspapers.size() == 2);

	}

	// Negative: List all volumes and see incorrect size of newspapers from one volume
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestListVolumesAndIncorrectSizeOfNewspapers() {

		final Set<Volume> volumes = new HashSet<Volume>(this.volumeService.findAll());
		Assert.notNull(volumes);
		final Collection<Newspaper> newspapers = this.volumeService.findOne(this.getEntityId("volume1")).getNewspapers();
		Assert.notNull(newspapers);

		Assert.isTrue(volumes.size() == 7);
		Assert.isTrue(newspapers.size() == 5);

	}

	//RF8.2
	//An actor who is not authenticated must be able to:
	//Display a newspaper from volume

	// POSITIVE
	@Test
	public void positiveTestDisplayNewspaperFromVolume() {

		final Set<Volume> volumes = new HashSet<Volume>(this.volumeService.findAll());
		Assert.notNull(volumes);
		final List<Newspaper> newspapers = new ArrayList<Newspaper>(this.volumeService.findOne(this.getEntityId("volume1")).getNewspapers());
		Assert.notNull(newspapers);
		final Newspaper n = this.newspaperService.findOne(newspapers.get(0).getId());

		Assert.notNull(n);

	}

	// Negative: Display a newspaper from volume but newspaper's id is not correct
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestDisplayNewspaperFromVolumeIncorrectId() {

		final Set<Volume> volumes = new HashSet<Volume>(this.volumeService.findAll());
		Assert.notNull(volumes);
		final List<Newspaper> newspapers = new ArrayList<Newspaper>(this.volumeService.findOne(this.getEntityId("volume1")).getNewspapers());
		Assert.notNull(newspapers);
		final Newspaper n = this.newspaperService.findOne(2869);

		Assert.notNull(n);

	}

	// Negative: Display a newspaper from volume but newspaper shown is not from that volume
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestDisplayNewspaperFromVolumeIncorrectOne() {

		final Set<Volume> volumes = new HashSet<Volume>(this.volumeService.findAll());
		Assert.notNull(volumes);
		final List<Newspaper> newspapers = new ArrayList<Newspaper>(this.volumeService.findOne(this.getEntityId("volume2")).getNewspapers());
		Assert.notNull(newspapers);
		final Newspaper n = this.newspaperService.findOne(2877);

		Assert.notNull(n);
		Assert.isTrue(newspapers.contains(n));
	}

}
