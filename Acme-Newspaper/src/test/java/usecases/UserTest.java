
package usecases;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.ActorService;
import services.ArticleService;
import services.ChirpService;
import services.NewspaperService;
import services.UserService;
import services.VolumeService;
import utilities.AbstractTest;
import domain.Article;
import domain.Chirp;
import domain.Followup;
import domain.Newspaper;
import domain.Subscription;
import domain.User;
import domain.Volume;
import domain.VolumeSubscription;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class UserTest extends AbstractTest {

	// System under test

	@Autowired
	private UserService			userService;

	@Autowired
	private ActorService		actorService;

	@Autowired
	private ChirpService		chirpService;

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private ArticleService		articleService;

	@Autowired
	private VolumeService		volumeService;


	// Acme-Newspaper Extra Functional requirement:
	// USER - Edit user
	// *************************************************************************************************************************************
	// POSITIVE - Editing a user
	@Test
	public void editUserData() {
		this.authenticate("user1");
		final User user = (User) this.actorService.findOneToEdit(this.getEntityId("user1"));
		user.setName("Test name");
		final User result = this.userService.save(user);

		Assert.isTrue(result.getName().equals("Test name"));

		this.authenticate(null);
	}

	// NEGATIVE - Editing other user's personal data
	@Test(expected = IllegalArgumentException.class)
	public void editOtherUserData() {
		this.authenticate("user1");
		final User user = (User) this.actorService.findOneToEdit(this.getEntityId("user2"));
		user.setName("Test name");
		final User result = this.userService.save(user);

		Assert.isTrue(result.getName().equals("Test name"));

		this.authenticate(null);
	}

	// NEGATIVE - Editing invalid email
	@Test(expected = IllegalArgumentException.class)
	public void editUserInvalidData() {
		this.authenticate("user5");
		final User user = (User) this.actorService.findOneToEdit(this.getEntityId("user5"));
		user.setEmail("Invalid test");
		final User result = this.userService.save(user);

		Assert.isTrue(result.getName().equals("Test name"));

		this.authenticate(null);
	}

	// ***************************************************************************************************************************************************

	// Acme-Newspaper Extra Functional requirement:
	// USER - Delete user
	// *************************************************************************************************************************************
	// POSITIVE - Deleting a user
	@Test
	public void deleteUserData() {
		this.authenticate("user9");
		final User user = (User) this.actorService.findOneToEdit(this.getEntityId("user9"));

		for (final Chirp c : this.chirpService.findAllByUser(user.getId()))
			this.chirpService.delete(c);
		for (final User u : user.getFollowers())
			u.getFollowing().remove(user);
		for (final User u : user.getFollowing())
			u.getFollowers().remove(user);
		for (final Volume v : this.volumeService.findMyVolumes(user.getId()))
			this.volumeService.delete(v);

		this.userService.delete(user);

		Assert.isTrue(!this.userService.findAll().contains(user));

		this.authenticate(null);
	}

	// NEGATIVE - Deleting other user
	@Test(expected = IllegalArgumentException.class)
	public void deleteOtherUser() {
		this.authenticate("user8");
		final User user = this.userService.findOne(this.getEntityId("user2"));
		user.setName("Test name");
		this.userService.delete(user);

		Assert.isTrue(!this.userService.findAll().contains(user));

		this.authenticate(null);
	}

	// NEGATIVE - Deleting user unauthenticated
	@Test(expected = IllegalArgumentException.class)
	public void deleteNullUser() {
		final User user = this.userService.findOne(this.getEntityId("user2"));
		user.setName("Test name");
		this.userService.delete(user);

		Assert.isTrue(!this.userService.findAll().contains(user));

	}

	// *****************************************************************************************************************************************************

	// Acme-Newspaper FR16.1:
	// Post a chirp.

	// POSITIVE - Posting a chirp
	@Test
	public void postChirp() {
		this.authenticate("user1");
		final Chirp chirp = this.chirpService.create();
		chirp.setUser(this.userService.findByPrincipal());
		chirp.setDescription("Test description");
		chirp.setMoment(Calendar.getInstance());
		chirp.setTitle("Test title");
		final Chirp result = this.chirpService.save(chirp);

		Assert.isTrue(this.chirpService.findAll().contains(result));

		this.authenticate(null);
	}

	// NEGATIVE - Posting a chirp without being authenticated
	@Test(expected = IllegalArgumentException.class)
	public void postChirpWithoutAuthentication() {
		final Chirp chirp = this.chirpService.create();
		chirp.setUser(this.userService.findOne(this.getEntityId("user2")));
		chirp.setDescription("Test description");
		chirp.setMoment(Calendar.getInstance());
		chirp.setTitle("Test title");
		final Chirp result = this.chirpService.save(chirp);

		Assert.isTrue(this.chirpService.findAll().contains(result));

		this.authenticate(null);
	}

	// NEGATIVE - Posting a chirp in name of another user.
	@Test(expected = IllegalArgumentException.class)
	public void postOtherUserChirp() {
		this.authenticate("user1");
		final Chirp chirp = this.chirpService.create();
		chirp.setUser(this.userService.findOne(this.getEntityId("user2")));
		chirp.setDescription("Test description");
		chirp.setMoment(Calendar.getInstance());
		chirp.setTitle("Test title");
		final Chirp result = this.chirpService.save(chirp);

		Assert.isTrue(this.chirpService.findAll().contains(result));

		this.authenticate(null);
	}

	// *****************************************************************************************************************************************************

	// Acme-Newspaper FR16.2, FR16.3, FR16.4:
	// Follow or unfollow another user.
	// List the users who he or she follows.
	// List the users who follow him or her.

	// POSITIVE - Follow a user
	@Test
	public void followUser() {
		this.authenticate("user1");
		final User user1 = (User) this.actorService.findOneToEdit(this.getEntityId("user1"));
		final User user9 = this.userService.findOne(this.getEntityId("user9"));

		user1.getFollowing().add(user9);
		user9.getFollowers().add(user9);

		this.userService.save(user1);
		this.userService.save(user9);

		// Simulating the listing of following.
		final Collection<User> following = this.userService.findFollowingByUser(user1);
		Assert.isTrue(following.contains(user9));

		// Simulating the listing of followers.
		final Collection<User> followers = this.userService.findFollowersByUser(user9);
		Assert.isTrue(followers.contains(user1));

		this.authenticate(null);
	}

	// NEGATIVE - Following a user without being authenticated
	@Test(expected = IllegalArgumentException.class)
	public void followUserUnauthenticated() {
		// this.authenticate("user1");
		final User user1 = (User) this.actorService.findOneToEdit(this.getEntityId("user1"));
		final User user9 = this.userService.findOne(this.getEntityId("user9"));

		user1.getFollowing().add(user9);
		user9.getFollowers().add(user1);

		this.userService.save(user1);
		this.userService.save(user9);

		// Simulating the listing of following.
		final Collection<User> following = this.userService.findFollowingByUser(user1);
		Assert.isTrue(following.contains(user9));

		// Simulating the listing of followers.
		final Collection<User> followers = this.userService.findFollowersByUser(user9);
		Assert.isTrue(followers.contains(user1));

		this.authenticate(null);
	}

	// NEGATIVE - Making other user follow another
	@Test(expected = IllegalArgumentException.class)
	public void followOtherUser() {
		this.authenticate("user4");
		final User user1 = (User) this.actorService.findOneToEdit(this.getEntityId("user1"));
		final User user9 = this.userService.findOne(this.getEntityId("user9"));

		user1.getFollowing().add(user9);
		user9.getFollowers().add(user1);

		this.userService.save(user1);
		this.userService.save(user9);

		// Simulating the listing of following.
		final Collection<User> following = this.userService.findFollowingByUser(user1);
		Assert.isTrue(following.contains(user9));

		// Simulating the listing of followers.
		final Collection<User> followers = this.userService.findFollowersByUser(user9);
		Assert.isTrue(followers.contains(user1));

		this.authenticate(null);
	}

	// POSITIVE - Unfollow a user
	@Test
	public void UnfollowUser() {
		this.authenticate("user1");
		final User user1 = (User) this.actorService.findOneToEdit(this.getEntityId("user1"));
		final User user2 = this.userService.findOne(this.getEntityId("user2"));

		user1.getFollowing().remove(user2);
		user2.getFollowers().remove(user1);

		this.userService.save(user1);
		this.userService.save(user2);

		// Simulating the listing of following.
		final Collection<User> following = this.userService.findFollowingByUser(user1);
		Assert.isTrue(!following.contains(user2));

		// Simulating the listing of followers.
		final Collection<User> followers = this.userService.findFollowersByUser(user2);
		Assert.isTrue(!followers.contains(user1));

		this.authenticate(null);
	}

	// NEGATIVE - Unfollowing a user without being authenticated
	@Test(expected = IllegalArgumentException.class)
	public void unfollowUserUnauthenticated() {
		// this.authenticate("user1");
		final User user1 = (User) this.actorService.findOneToEdit(this.getEntityId("user1"));
		final User user2 = this.userService.findOne(this.getEntityId("user2"));

		user1.getFollowing().remove(user2);
		user2.getFollowers().remove(user1);

		this.userService.save(user1);
		this.userService.save(user2);

		// Simulating the listing of following.
		final Collection<User> following = this.userService.findFollowingByUser(user1);
		Assert.isTrue(!following.contains(user2));

		// Simulating the listing of followers.
		final Collection<User> followers = this.userService.findFollowersByUser(user2);
		Assert.isTrue(!followers.contains(user1));

		this.authenticate(null);
	}

	// NEGATIVE - Making other user unfollow another
	@Test(expected = IllegalArgumentException.class)
	public void unfollowOtherUser() {
		this.authenticate("user4");
		final User user1 = (User) this.actorService.findOneToEdit(this.getEntityId("user1"));
		final User user2 = this.userService.findOne(this.getEntityId("user2"));

		user1.getFollowing().remove(user2);
		user2.getFollowers().remove(user1);

		this.userService.save(user1);
		this.userService.save(user2);

		// Simulating the listing of following.
		final Collection<User> following = this.userService.findFollowingByUser(user1);
		Assert.isTrue(!following.contains(user2));

		// Simulating the listing of followers.
		final Collection<User> followers = this.userService.findFollowersByUser(user2);
		Assert.isTrue(!followers.contains(user1));

		this.authenticate(null);
	}

	// *****************************************************************************************************************************************************

	// Acme-Newspaper FR16.5:
	// Display a stream with the chirps posted by all of the users that he or she follows.

	// POSITIVE - Display stream
	@Test
	public void chirpStream() {
		this.authenticate("user1");
		final Collection<Chirp> chirps = this.chirpService.getChirpsOfFollowingUsers();

		Assert.isTrue(chirps.size() == 2);

		this.authenticate(null);
	}

	// NEGATIVE - Display stream unauthenticated
	@Test(expected = IllegalArgumentException.class)
	public void chirpStreamUnauthenticated() {
		final Collection<Chirp> chirps = this.chirpService.getChirpsOfFollowingUsers();

		Assert.isTrue(chirps.size() == 2);

		this.authenticate(null);
	}

	// NEGATIVE - Display stream incorrect value
	@Test(expected = IllegalArgumentException.class)
	public void chirpStreamIncorrectValue() {
		this.authenticate("user1");
		final ArrayList<Chirp> chirps = (ArrayList<Chirp>) this.chirpService.getChirpsOfFollowingUsers();
		chirps.remove(0);
		Assert.isTrue(chirps.size() == 2);

		this.authenticate(null);
	}

	//	6. An actor who is authenticated as a user must be able to:
	//	1. Create a newspaper. A user who has created a newspaper is commonly referred to
	//	as a publisher.
	@Test
	public void positiveTestCreateNewspaper() {

		this.authenticate("user1");
		final Newspaper newspaper = this.newspaperService.create();
		newspaper.setArticles(new ArrayList<Article>());
		newspaper.setSubscriptions(new ArrayList<Subscription>());
		newspaper.setPublisher(this.userService.findByPrincipal());
		newspaper.setDescription("");
		newspaper.setTitle("");
		this.newspaperService.save(newspaper);

	}

	//A customer cannot create a newspaper
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestCreateNewspaper() {

		this.authenticate("customer1");
		final Newspaper newspaper = this.newspaperService.create();
		newspaper.setArticles(new ArrayList<Article>());
		newspaper.setSubscriptions(new ArrayList<Subscription>());
		newspaper.setPublisher(this.userService.findByPrincipal());
		newspaper.setDescription("");
		newspaper.setTitle("");
		this.newspaperService.save(newspaper);

	}

	//You cannot create a newspaper with empty attributes
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestCreateNewspaper2() {

		this.authenticate("customer1");
		final Newspaper newspaper = this.newspaperService.create();
		this.newspaperService.save(newspaper);

	}

	//	6. An actor who is authenticated as a user must be able to:
	//	2. Publish a newspaper that he or she's created. Note that no newspaper can be published
	//	until each of the articles of which it is composed is saved in final mode.
	@Test
	public void positiveTestPublishNewspaper() {

		this.authenticate("user2");
		final Newspaper newspaper2 = this.newspaperService.findOne(this.getEntityId("newspaper2"));
		for (final Article a : newspaper2.getArticles())
			a.setDraft(false);
		this.newspaperService.save(newspaper2);
		this.newspaperService.publish(newspaper2.getId());

	}

	//If articles are in draft mode the newspaper cannot be published
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestPublishNewspaper() {

		this.authenticate("user2");
		final Newspaper newspaper2 = this.newspaperService.findOne(this.getEntityId("newspaper2"));
		this.newspaperService.publish(newspaper2.getId());

	}

	//Only the creator can publish the newspaper
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestPublishNewspaper2() {

		this.authenticate("user6");
		final Newspaper newspaper2 = this.newspaperService.findOne(this.getEntityId("newspaper2"));
		for (final Article a : newspaper2.getArticles())
			a.setDraft(false);
		this.newspaperService.save(newspaper2);
		this.newspaperService.publish(newspaper2.getId());

	}

	//	6. An actor who is authenticated as a user must be able to:
	//	3. Write an article and attach it to any newspaper that has not been published, yet.
	//	Note that articles may be saved in draft mode, which allows to modify them later, or
	//	final model, which freezes them forever.
	@Test
	public void positiveTestAttachArticle() {

		this.authenticate("user2");
		final Newspaper newspaper2 = this.newspaperService.findOne(this.getEntityId("newspaper2"));
		final Article article = this.articleService.create();
		article.setBody("");
		article.setCreator(this.userService.findByPrincipal());
		article.setFollowups(new ArrayList<Followup>());
		article.setNewspaper(newspaper2);
		final Collection<String> urls = new ArrayList<String>();
		urls.add("https://drive.google.com/drive/folders/1-zudBSHCK-Vy5aQ6pTzfwxDYx2q5Y9Nf");
		article.setPictures(urls);
		this.articleService.save(article);
		final Collection<Article> articles = newspaper2.getArticles();
		articles.add(article);
		newspaper2.setArticles(articles);
		this.newspaperService.save(newspaper2);
		Assert.isTrue(newspaper2.getArticles().contains(article));

	}

	//A customer cannot attach an article
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestAttachArticle() {

		this.authenticate("customer1");
		final Newspaper newspaper2 = this.newspaperService.findOne(this.getEntityId("newspaper2"));
		final Article article = this.articleService.create();
		article.setBody("");
		article.setCreator(this.userService.findByPrincipal());
		article.setFollowups(new ArrayList<Followup>());
		article.setNewspaper(newspaper2);
		final Collection<String> urls = new ArrayList<String>();
		urls.add("https://drive.google.com/drive/folders/1-zudBSHCK-Vy5aQ6pTzfwxDYx2q5Y9Nf");
		article.setPictures(urls);
		this.articleService.save(article);
		final Collection<Article> articles = newspaper2.getArticles();
		articles.add(article);
		newspaper2.setArticles(articles);
		this.newspaperService.save(newspaper2);
		Assert.isTrue(newspaper2.getArticles().contains(article));

	}

	//You cannot attach an article if the newspaper is published
	@Test(expected = IllegalArgumentException.class)
	public void negativeTestAttachArticle2() {

		this.authenticate("user2");
		final Newspaper newspaper2 = this.newspaperService.findOne(this.getEntityId("newspaper3"));
		final Article article = this.articleService.create();
		article.setBody("");
		article.setCreator(this.userService.findByPrincipal());
		article.setFollowups(new ArrayList<Followup>());
		article.setNewspaper(newspaper2);
		final Collection<String> urls = new ArrayList<String>();
		urls.add("https://drive.google.com/drive/folders/1-zudBSHCK-Vy5aQ6pTzfwxDYx2q5Y9Nf");
		article.setPictures(urls);
		this.articleService.save(article);
		final Collection<Article> articles = newspaper2.getArticles();
		articles.add(article);
		newspaper2.setArticles(articles);
		this.newspaperService.save(newspaper2);
		Assert.isTrue(newspaper2.getArticles().contains(article));

	}

	// Tests 2.0 ------------------------------------------------------------------	

	//RF10.1
	//A user can create a volume
	//POSITIVE
	@Test
	public void CreateVolumeAsUser() {
		this.authenticate("user1");
		final User principal = this.userService.findByPrincipal();
		final Volume v = this.volumeService.create();
		v.setTitle("Volume title");
		v.setDescription("Volume desc");
		v.setYear(2018);
		v.setNewspapers(new HashSet<Newspaper>());
		v.setPublisher(principal);
		v.setVolumeSubscriptions(new HashSet<VolumeSubscription>());

		final Volume vSaved = this.volumeService.save(v);
		this.volumeService.flush();

		Assert.isTrue(this.volumeService.findMyVolumes(principal.getId()).contains(vSaved));

		this.authenticate(null);
	}
	//Create a volume as customer
	//NEGATIVE
	@Test(expected = IllegalArgumentException.class)
	public void CreateVolumeAsCustomer() {
		this.authenticate("customer1");
		final User principal = this.userService.findByPrincipal();
		final Volume v = this.volumeService.create();
		v.setTitle("Volume title");
		v.setDescription("Volume desc");
		v.setYear(2018);
		v.setNewspapers(new HashSet<Newspaper>());
		v.setPublisher(principal);
		v.setVolumeSubscriptions(new HashSet<VolumeSubscription>());

		final Volume vSaved = this.volumeService.save(v);
		this.volumeService.flush();

		Assert.isTrue(this.volumeService.findMyVolumes(principal.getId()).contains(vSaved));

		this.authenticate(null);
	}

	//Create a volume with no title
	//NEGATIVE
	@Test(expected = ConstraintViolationException.class)
	public void CreateVolumeWithNoTitle() {
		this.authenticate("user1");
		final User principal = this.userService.findByPrincipal();
		final Volume v = this.volumeService.create();
		v.setDescription("Volume desc");
		v.setYear(2018);
		v.setNewspapers(new HashSet<Newspaper>());
		v.setPublisher(principal);
		v.setVolumeSubscriptions(new HashSet<VolumeSubscription>());

		final Volume vSaved = this.volumeService.save(v);
		this.volumeService.flush();

		Assert.isTrue(this.volumeService.findMyVolumes(principal.getId()).contains(vSaved));

		this.authenticate(null);
	}

	//RF10.1
	//A user can add newspapers to a volume
	//POSITIVE
	@Test
	public void AddNewspapersToVolumeAsUser() {
		this.authenticate("user1");

		final Volume v = this.volumeService.findOne(this.getEntityId("volume1"));
		final Newspaper n = this.newspaperService.findOne(this.getEntityId("newspaper9"));

		Assert.notNull(n);

		v.getNewspapers().add(n);

		final Volume vSaved = this.volumeService.save(v);

		Assert.isTrue(vSaved.getNewspapers().contains(n));

		this.authenticate(null);
	}
	//A customer can't add a newspaper to a volume
	//NEGATIVE
	@Test(expected = IllegalArgumentException.class)
	public void AddNewspaperToVolumeAsCustomer() {
		this.authenticate("customer1");

		final Volume v = this.volumeService.findOne(this.getEntityId("volume1"));
		final Newspaper n = this.newspaperService.findOne(this.getEntityId("newspaper9"));

		Assert.notNull(n);

		v.getNewspapers().add(n);
		final Volume vSaved = this.volumeService.save(v);

		Assert.isTrue(vSaved.getNewspapers().contains(n));

		this.authenticate(null);
	}

	//A user can't add a newspaper that doesn't exist to a volume
	//NEGATIVE
	@Test(expected = AssertionError.class)
	public void AddNewspaperToVolumeThatAlreadyHasIt() {
		this.authenticate("user1");

		final Volume v = this.volumeService.findOne(this.getEntityId("volume1"));
		final Newspaper n = this.newspaperService.findOne(this.getEntityId("newspaper15"));

		Assert.notNull(n);

		v.getNewspapers().add(n);
		final Volume vSaved = this.volumeService.save(v);

		Assert.isTrue(vSaved.getNewspapers().contains(n));

		this.authenticate(null);
	}

	//RF10.1
	//A user can remove newspapers from his or her volumes
	//POSITIVE
	@Test
	public void RemoveNewspapersFromVolumeAsUser() {
		this.authenticate("user1");

		final Volume v = this.volumeService.findOne(this.getEntityId("volume1"));
		final Newspaper n = this.newspaperService.findOne(this.getEntityId("newspaper1"));

		Assert.notNull(n);

		v.getNewspapers().remove(n);
		final Volume vSaved = this.volumeService.save(v);

		Assert.isTrue(!vSaved.getNewspapers().contains(n));

		this.authenticate(null);
	}
	//Only users can edit volumes
	//NEGATIVE
	@Test(expected = IllegalArgumentException.class)
	public void RemoveNewspaperFromVolumeNotAuthenticated() {

		this.authenticate(null);

		final Volume v = this.volumeService.findOne(this.getEntityId("volume1"));
		final Newspaper n = this.newspaperService.findOne(this.getEntityId("newspaper9"));

		Assert.notNull(n);

		v.getNewspapers().remove(n);
		final Volume vSaved = this.volumeService.save(v);

		Assert.isTrue(vSaved.getNewspapers().contains(n));

	}

	//Remove invalid newspaper from volume
	//NEGATIVE
	@Test(expected = AssertionError.class)
	public void RemoveNewspaperFromVolumeThatDoesNotHaveIt() {
		this.authenticate("user1");

		final Volume v = this.volumeService.findOne(this.getEntityId("volume1"));
		final Newspaper n = this.newspaperService.findOne(this.getEntityId("newspaper15"));

		Assert.notNull(n);

		v.getNewspapers().remove(n);
		final Volume vSaved = this.volumeService.save(v);

		Assert.isTrue(vSaved.getNewspapers().contains(n));

		this.authenticate(null);
	}

}
