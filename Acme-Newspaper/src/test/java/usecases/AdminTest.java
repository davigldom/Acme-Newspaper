
package usecases;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.AdvertisementService;
import services.ArticleService;
import services.ChirpService;
import services.FollowupService;
import services.NewspaperService;
import services.SystemConfigService;
import services.VolumeService;
import utilities.AbstractTest;
import domain.Advertisement;
import domain.Article;
import domain.Chirp;
import domain.Newspaper;
import domain.SystemConfig;
import domain.Volume;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class AdminTest extends AbstractTest {

	// System under test

	@Autowired
	private FollowupService			followupService;

	@Autowired
	private ChirpService			chirpService;

	@Autowired
	private ArticleService			articleService;

	@Autowired
	private NewspaperService		newspaperService;

	@Autowired
	private SystemConfigService		systemConfigService;

	@Autowired
	private AdvertisementService	advertService;

	@Autowired
	private VolumeService			volumeService;

	DecimalFormat					df	= new DecimalFormat("#.##");	// To get only 2 decimals


	// ------------------------------------------------------ TESTS
	// ------------------------------------------------------------------

	// Acme-Newspaper FR7.1: Remove an article that he or she thinks is
	// inappropriate.
	// ***********************************************************************************************************
	// POSITIVE: Removing article from the list
	@Test
	public void removeArticle() {
		this.authenticate("admin");
		final Article c = (Article) this.articleService.findAllByCreator(this.getEntityId("user1")).toArray()[0];
		this.articleService.delete(c);
		Assert.isTrue(!this.articleService.findAll().contains(c));
		this.authenticate(null);
	}

	// NEGATIVE: Removing null article
	@Test(expected = IllegalArgumentException.class)
	public void removeNullArticle() {
		this.authenticate("admin");
		final Article c = null;
		this.articleService.delete(c);
		Assert.isTrue(!this.articleService.findAll().contains(c));
		this.authenticate(null);
	}

	// NEGATIVE: Removing article as a user
	@Test(expected = IllegalArgumentException.class)
	public void removeOtherUserArticle() {
		this.authenticate("user1");
		final Article c = null;
		this.articleService.delete(c);
		Assert.isTrue(!this.articleService.findAll().contains(c));
		this.authenticate(null);
	}

	// *********************************************************************************************************************************

	// Acme-Newspaper FR7.2: Remove a newspaper that he or she thinks is
	// inappropriate.
	// ***********************************************************************************************************
	// POSITIVE: Removing newspaper from the list
	@Test
	public void removeNewspaper() {
		this.authenticate("admin");
		final Newspaper newspaper = (Newspaper) this.newspaperService.findAllByPublisher(this.getEntityId("user1")).toArray()[0];

		final Iterator<Advertisement> iter = newspaper.getAdvertisements().iterator();

		while (iter.hasNext()) {
			final Advertisement ad = iter.next();
			iter.remove();
			this.advertService.delete(ad, newspaper);
		}
		for (final Volume v : this.volumeService.findByNewspaper(newspaper.getId()))
			v.getNewspapers().remove(newspaper);
		this.newspaperService.delete(newspaper);

		Assert.isTrue(!this.newspaperService.findAll().contains(newspaper));
		this.authenticate(null);
	}

	// NEGATIVE: Removing null newspaper
	@Test(expected = AssertionError.class)
	public void removeNullNewspaper() {
		this.authenticate("admin");
		final Newspaper newspaper = this.newspaperService.findOne(this.getEntityId("newspaper123"));

		final Iterator<Advertisement> iter = newspaper.getAdvertisements().iterator();

		while (iter.hasNext()) {
			final Advertisement ad = iter.next();
			iter.remove();
			this.advertService.delete(ad, newspaper);
		}
		for (final Volume v : this.volumeService.findByNewspaper(newspaper.getId()))
			v.getNewspapers().remove(newspaper);
		this.newspaperService.delete(newspaper);

		Assert.isTrue(!this.newspaperService.findAll().contains(newspaper));
		this.authenticate(null);
	}

	// NEGATIVE: Removing newspaper as nonexistent user
	@Test(expected = IllegalArgumentException.class)
	public void removeOtherUserNewspaper() {
		this.authenticate("user123");
		final Newspaper newspaper = (Newspaper) this.newspaperService.findAllByPublisher(this.getEntityId("user1")).toArray()[0];

		final Iterator<Advertisement> iter = newspaper.getAdvertisements().iterator();

		while (iter.hasNext()) {
			final Advertisement ad = iter.next();
			iter.remove();
			this.advertService.delete(ad, newspaper);
		}
		for (final Volume v : this.volumeService.findByNewspaper(newspaper.getId()))
			v.getNewspapers().remove(newspaper);
		this.newspaperService.delete(newspaper);

		Assert.isTrue(!this.newspaperService.findAll().contains(newspaper));
		this.authenticate(null);
	}

	// *********************************************************************************************************************************

	// Acme-Newspaper FR17.1: Manage a list of taboo
	// ***********************************************************************************************************
	// POSITIVE: Edit taboo words
	@Test
	public void editTabooWords() {
		this.authenticate("admin");
		final SystemConfig sys = this.systemConfigService.findConfig();
		final Collection<String> words = sys.getTabooWords();
		words.add("weed");
		final SystemConfig result = this.systemConfigService.save(sys);
		Assert.isTrue(result.getTabooWords().contains("weed"));
		this.authenticate(null);
	}

	// NEGATIVE: Edit taboo words being other user
	@Test(expected = IllegalArgumentException.class)
	public void editTabooWordsBeingUser() {
		this.authenticate("user2");
		final SystemConfig sys = this.systemConfigService.findConfig();
		final Collection<String> words = sys.getTabooWords();
		words.add("weed");
		final SystemConfig result = this.systemConfigService.save(sys);
		Assert.isTrue(result.getTabooWords().contains("weed"));
		this.authenticate(null);
	}

	// NEGATIVE: Save null sysconfig
	@Test(expected = IllegalArgumentException.class)
	public void editTabooWordsNoActor() {
		this.authenticate("admin");
		final SystemConfig sys = null;
		final SystemConfig result = this.systemConfigService.save(sys);
		Assert.isTrue(result.getTabooWords().contains("weed"));
		this.authenticate(null);
	}

	// *********************************************************************************************************************************

	// ***********************************************************************************************************
	// Acme-Newspaper FR17.2: List the articles that contain taboo words.
	// Acme-Newspaper FR17.3: List the newspapers that contain taboo words.
	// Acme-Newspaper FR17.4: List the chirps that contain taboo words.
	// Acme-Newspaper 2.0 FR5.1: List the advertisements that contain taboo words.
	// The four use cases are the same, the only different thing between them
	// is the entity.
	// For that reason, we will use one test case for all of them.
	// POSITIVE: Listing
	@Test
	public void listTabooWordsEntities() {
		this.authenticate("admin");
		final SystemConfig sys = this.systemConfigService.findConfig();
		final Collection<String> words = sys.getTabooWords();
		words.add("weed");
		final Collection<Article> articles = this.articleService.findAll();
		final Collection<Chirp> chirps = this.chirpService.findAll();
		final Collection<Newspaper> newspapers = this.newspaperService.findAll();
		final Collection<Advertisement> adverts = this.advertService.findAll();

		final Collection<Article> articlesTaboo = new ArrayList<Article>();
		final Collection<Chirp> chirpsTaboo = new ArrayList<Chirp>();
		final Collection<Newspaper> newspapersTaboo = new ArrayList<Newspaper>();
		final Collection<Advertisement> advertsTaboo = new ArrayList<Advertisement>();

		final Collection<String> tabooWords = this.systemConfigService.findConfig().getTabooWords();

		for (final String word : tabooWords) {
			for (final Article a : articles)
				if (!articlesTaboo.contains(a))
					if (a.getTitle().contains(word) || a.getBody().contains(word) || a.getSummary().contains(word))
						articlesTaboo.add(a);

			for (final Chirp c : chirps)
				if (!chirpsTaboo.contains(c))
					if (c.getTitle().contains(word) || c.getDescription().contains(word))
						chirpsTaboo.add(c);

			for (final Newspaper n : newspapers)
				if (!chirpsTaboo.contains(n))
					if (n.getTitle().contains(word) || n.getDescription().contains(word))
						newspapersTaboo.add(n);

			for (final Advertisement a : adverts)
				if (!advertsTaboo.contains(a))
					if (a.getTitle().contains(word))
						advertsTaboo.add(a);
		}

		Assert.isTrue(articlesTaboo.size() == 1);
		Assert.isTrue(newspapersTaboo.size() == 1);
		Assert.isTrue(chirpsTaboo.size() == 1);
		Assert.isTrue(advertsTaboo.size() == 1);

		this.authenticate(null);
	}

	// NEGATIVE: Listing bad fixture
	@Test(expected = IllegalArgumentException.class)
	public void listingBadNumberOfEntities() {
		this.authenticate("admin");
		final SystemConfig sys = this.systemConfigService.findConfig();
		final Collection<String> words = sys.getTabooWords();
		words.add("weed");
		final Collection<Article> articles = this.articleService.findAll();
		final Collection<Chirp> chirps = this.chirpService.findAll();
		final Collection<Newspaper> newspapers = this.newspaperService.findAll();
		final Collection<Advertisement> adverts = this.advertService.findAll();

		final Collection<Article> articlesTaboo = new ArrayList<Article>();
		final Collection<Chirp> chirpsTaboo = new ArrayList<Chirp>();
		final Collection<Newspaper> newspapersTaboo = new ArrayList<Newspaper>();
		final Collection<Advertisement> advertsTaboo = new ArrayList<Advertisement>();

		final Collection<String> tabooWords = this.systemConfigService.findConfig().getTabooWords();

		for (final String word : tabooWords) {
			for (final Article a : articles)
				if (!articlesTaboo.contains(a))
					if (a.getTitle().contains(word) || a.getBody().contains(word) || a.getSummary().contains(word))
						articlesTaboo.add(a);

			for (final Chirp c : chirps)
				if (!chirpsTaboo.contains(c))
					if (c.getTitle().contains(word) || c.getDescription().contains(word))
						chirpsTaboo.add(c);

			for (final Newspaper n : newspapers)
				if (!chirpsTaboo.contains(n))
					if (n.getTitle().contains(word) || n.getDescription().contains(word))
						newspapersTaboo.add(n);

			for (final Advertisement a : adverts)
				if (!advertsTaboo.contains(a))
					if (a.getTitle().contains(word))
						advertsTaboo.add(a);
		}

		Assert.isTrue(articlesTaboo.size() == 0);
		Assert.isTrue(newspapersTaboo.size() == 1);
		Assert.isTrue(chirpsTaboo.size() == 1);
		Assert.isTrue(advertsTaboo.size() == 0);

		this.authenticate(null);
	}

	// NEGATIVE: Incorrect number of taboo words
	@Test(expected = IllegalArgumentException.class)
	public void listTabooWordsIncorrectNumberOfTabooWords() {
		this.authenticate("customer2");
		final SystemConfig sys = this.systemConfigService.findConfig();
		final Collection<String> words = sys.getTabooWords();

		Assert.isTrue(words.size() == 5);

		this.authenticate(null);
	}

	// Acme-Newspaper FR17.5: Remove a chirp that he or she thinks is
	// inappropriate.
	// ***********************************************************************************************************
	// POSITIVE: Removing chirp from the list
	@Test
	public void removeChirp() {
		this.authenticate("admin");
		final Chirp c = (Chirp) this.chirpService.findAllByUser(this.getEntityId("user1")).toArray()[0];
		this.chirpService.delete(c);
		Assert.isTrue(!this.chirpService.findAll().contains(c));
		this.authenticate(null);
	}

	// NEGATIVE: Removing null chirp
	@Test(expected = IllegalArgumentException.class)
	public void removeNullChirp() {
		this.authenticate("admin");
		final Chirp c = null;
		this.chirpService.delete(c);
		Assert.isTrue(!this.chirpService.findAll().contains(c));
		this.authenticate(null);
	}

	// NEGATIVE: Removing chirp as nonexistent user
	@Test(expected = IllegalArgumentException.class)
	public void removeOtherUserChirp() {
		this.authenticate("user123");
		final Chirp c = null;
		this.chirpService.delete(c);
		Assert.isTrue(!this.chirpService.findAll().contains(c));
		this.authenticate(null);
	}

	// *********************************************************************************************************************************

	// Acme-Newspaper FR7.3, FR17.6, FR24.1: Display a dashboard with the
	// following information:
	// ****************************************************************************************************************************************
	//
	// /*
	// * Display a dashboard with the following information:
	// - The average and the standard deviation of newspapers created per user.
	// - The average and the standard deviation of articles written by writer.
	// - The average and the standard deviation of articles per newspaper.
	// - The newspapers that have at least 10% more articles than the average.
	// - The newspapers that have at least 10% fewer articles than the average.
	// - The ratio of users who have ever created a newspaper.
	// - The ratio of users who have ever written an article.
	// - The average number of follow-ups per article.
	// - The average number of follow-ups per article up to one week after the
	// corresponding
	// newspaper’s been published.
	// - The average number of follow-ups per article up to two weeks after the
	// corresponding
	// newspaper’s been published.
	// - The average and the standard deviation of the number of chirps per
	// user.
	// - The ratio of users who have posted above 75% the average number of
	// chirps
	// per user.
	// - The ratio of public versus private newspapers.
	// - The average number of articles per private newspapers.
	// - The average number of articles per public newspapers.
	// - The ratio of subscribers per private newspaper versus the total number
	// of
	// customers.
	// - The average ratio of private versus public newspapers per publisher.
	// * This test shouldn't fail as an admin is authenticated POSITIVE
	// */

	@Test
	public void displayDashboardPositiveTest() {
		super.authenticate("admin");

		Assert.isTrue(0.2 == (Double.valueOf(this.df.format(this.followupService.getAverageFollowpsPerArticle()))));
		Assert.isTrue(0.2 == Double.valueOf(this.df.format(this.followupService.getFollowupsPerArticleUpToWeek())));
		Assert.isTrue(0.2 == Double.valueOf(this.df.format(this.followupService.getFollowupsPerArticleUpToTwoWeeks())));
		Assert.isTrue(1.11 == Double.valueOf(this.df.format(this.chirpService.getAverageChirpsPerUser())));
		Assert.isTrue(0.32 == Double.valueOf(this.df.format(this.chirpService.getStandardDeviationChirpsPerUser())));
		Assert.isTrue(0.0 == Double.valueOf(this.df.format(this.chirpService.getRatioUsersMoreChirpsThan75Percent())));
		Assert.isTrue(0.56 == Double.valueOf(this.df.format(this.articleService.getRatioArticlesCreatedByUser())));
		Assert.isTrue(1.11 == Double.valueOf(this.df.format(this.articleService.getAverageArticlesPerUser())));
		Assert.isTrue(1.37 == Double.valueOf(this.df.format(this.articleService.getStandardDeviationArticlesPerUser())));
		Assert.isTrue(1.11 == Double.valueOf(this.df.format(this.articleService.getAverageArticlesPerNewspaper())));
		Assert.isTrue(1.85 == Double.valueOf(this.df.format(this.articleService.getStandardDeviationArticlesPerNewspaper())));
		Assert.isTrue(0.56 == Double.valueOf(this.df.format(this.newspaperService.getRatioNewspaperCreatedByUser())));
		Assert.isTrue(1.0 == Double.valueOf(this.df.format(this.newspaperService.getAverageNewspaperPerUser())));
		Assert.isTrue(1.05 == Double.valueOf(this.df.format(this.newspaperService.getStandardDeviationNewspaperPerUser())));
		Assert.isTrue(2.00 == Double.valueOf(this.df.format(this.newspaperService.getNewspapersTenPercentMoreArticles().size())));
		Assert.isTrue(7.00 == Double.valueOf(this.df.format(this.newspaperService.getNewspapersTenPercentFewerArticles().size())));
		Assert.isTrue(2.00 == Double.valueOf(this.df.format(this.newspaperService.getRatioPublicVsPrivate())));
		Assert.isTrue(0.33 == Double.valueOf(this.df.format(this.newspaperService.getAverageArticlesPerPrivateNewspaper())));
		Assert.isTrue(1.5 == Double.valueOf(this.df.format(this.newspaperService.getAverageArticlesPerPublicNewspaper())));
		Assert.isTrue(0.33 == Double.valueOf(this.df.format(this.newspaperService.getRatioSubscribersPrivateVsTotal())));
		Assert.isTrue(0.0 == Double.valueOf(this.df.format(this.newspaperService.getAvgRatioPrivateVsPublicPerPublisher())));
		Assert.isTrue(0.29 == Double.valueOf(this.df.format(this.advertService.findRatioNewspapersAdvertisementsVSNoAdvertisements())));
		Assert.isTrue(0.0 == Double.valueOf(this.df.format(this.advertService.findRatioAdvertisementsTaboo())));
		Assert.isTrue(1.43 == Double.valueOf(this.df.format(this.volumeService.getAverageNewspapersPerVolume())));
		Assert.isTrue(0.33 == Double.valueOf(this.df.format(this.volumeService.getRatioSubscriptionsNewspaperVsVolume())));

		super.unauthenticate();
	}
	// * Negative test - An unauthenticated user is logged
	// */

	@Test(expected = IllegalArgumentException.class)
	public void displayDashboardUnauthenticatedTest() {

		Assert.isTrue(0.2 == (Double.valueOf(this.df.format(this.followupService.getAverageFollowpsPerArticle()))));
		Assert.isTrue(0.2 == Double.valueOf(this.df.format(this.followupService.getFollowupsPerArticleUpToWeek())));
		Assert.isTrue(0.2 == Double.valueOf(this.df.format(this.followupService.getFollowupsPerArticleUpToTwoWeeks())));
		Assert.isTrue(1.11 == Double.valueOf(this.df.format(this.chirpService.getAverageChirpsPerUser())));
		Assert.isTrue(0.32 == Double.valueOf(this.df.format(this.chirpService.getStandardDeviationChirpsPerUser())));
		Assert.isTrue(0.0 == Double.valueOf(this.df.format(this.chirpService.getRatioUsersMoreChirpsThan75Percent())));
		Assert.isTrue(0.33 == Double.valueOf(this.df.format(this.articleService.getRatioArticlesCreatedByUser())));
		Assert.isTrue(0.56 == Double.valueOf(this.df.format(this.articleService.getAverageArticlesPerUser())));
		Assert.isTrue(0.96 == Double.valueOf(this.df.format(this.articleService.getStandardDeviationArticlesPerUser())));
		Assert.isTrue(1.25 == Double.valueOf(this.df.format(this.articleService.getAverageArticlesPerNewspaper())));
		Assert.isTrue(0.43 == Double.valueOf(this.df.format(this.articleService.getStandardDeviationArticlesPerNewspaper())));
		Assert.isTrue(0.33 == Double.valueOf(this.df.format(this.newspaperService.getRatioNewspaperCreatedByUser())));
		Assert.isTrue(0.44 == Double.valueOf(this.df.format(this.newspaperService.getAverageNewspaperPerUser())));
		Assert.isTrue(0.68 == Double.valueOf(this.df.format(this.newspaperService.getStandardDeviationNewspaperPerUser())));
		Assert.isTrue(1.00 == Double.valueOf(this.df.format(this.newspaperService.getNewspapersTenPercentMoreArticles().size())));
		Assert.isTrue(3.00 == Double.valueOf(this.df.format(this.newspaperService.getNewspapersTenPercentFewerArticles().size())));
		Assert.isTrue(3.00 == Double.valueOf(this.df.format(this.newspaperService.getRatioPublicVsPrivate())));
		Assert.isTrue(1.00 == Double.valueOf(this.df.format(this.newspaperService.getAverageArticlesPerPrivateNewspaper())));
		Assert.isTrue(1.33 == Double.valueOf(this.df.format(this.newspaperService.getAverageArticlesPerPublicNewspaper())));
		Assert.isTrue(0.5 == Double.valueOf(this.df.format(this.newspaperService.getRatioSubscribersPrivateVsTotal())));
		Assert.isTrue(0.0 == Double.valueOf(this.df.format(this.newspaperService.getAvgRatioPrivateVsPublicPerPublisher())));
		Assert.isTrue(0.29 == Double.valueOf(this.df.format(this.advertService.findRatioNewspapersAdvertisementsVSNoAdvertisements())));
		Assert.isTrue(0.0 == Double.valueOf(this.df.format(this.advertService.findRatioAdvertisementsTaboo())));
		Assert.isTrue(1.43 == Double.valueOf(this.df.format(this.volumeService.getAverageNewspapersPerVolume())));
		Assert.isTrue(0.33 == Double.valueOf(this.df.format(this.volumeService.getRatioSubscriptionsNewspaperVsVolume())));

		super.unauthenticate();
	}

	// * Negative test - We display incorrect data
	// */

	@Test(expected = IllegalArgumentException.class)
	public void displayDashboardIncorrectDataTest() {
		this.authenticate("admin");
		//Deleting an article:
		this.articleService.delete(this.articleService.findOne(this.getEntityId("article1")));
		Assert.isTrue(0.2 == (Double.valueOf(this.df.format(this.followupService.getAverageFollowpsPerArticle()))));
		Assert.isTrue(0.2 == Double.valueOf(this.df.format(this.followupService.getFollowupsPerArticleUpToWeek())));
		Assert.isTrue(0.2 == Double.valueOf(this.df.format(this.followupService.getFollowupsPerArticleUpToTwoWeeks())));
		Assert.isTrue(1.11 == Double.valueOf(this.df.format(this.chirpService.getAverageChirpsPerUser())));
		Assert.isTrue(0.32 == Double.valueOf(this.df.format(this.chirpService.getStandardDeviationChirpsPerUser())));
		Assert.isTrue(0.0 == Double.valueOf(this.df.format(this.chirpService.getRatioUsersMoreChirpsThan75Percent())));
		Assert.isTrue(0.33 == Double.valueOf(this.df.format(this.articleService.getRatioArticlesCreatedByUser())));
		Assert.isTrue(0.56 == Double.valueOf(this.df.format(this.articleService.getAverageArticlesPerUser())));
		Assert.isTrue(0.96 == Double.valueOf(this.df.format(this.articleService.getStandardDeviationArticlesPerUser())));
		Assert.isTrue(1.25 == Double.valueOf(this.df.format(this.articleService.getAverageArticlesPerNewspaper())));
		Assert.isTrue(0.43 == Double.valueOf(this.df.format(this.articleService.getStandardDeviationArticlesPerNewspaper())));
		Assert.isTrue(0.33 == Double.valueOf(this.df.format(this.newspaperService.getRatioNewspaperCreatedByUser())));
		Assert.isTrue(0.44 == Double.valueOf(this.df.format(this.newspaperService.getAverageNewspaperPerUser())));
		Assert.isTrue(0.68 == Double.valueOf(this.df.format(this.newspaperService.getStandardDeviationNewspaperPerUser())));
		Assert.isTrue(1.00 == Double.valueOf(this.df.format(this.newspaperService.getNewspapersTenPercentMoreArticles().size())));
		Assert.isTrue(3.00 == Double.valueOf(this.df.format(this.newspaperService.getNewspapersTenPercentFewerArticles().size())));
		Assert.isTrue(3.00 == Double.valueOf(this.df.format(this.newspaperService.getRatioPublicVsPrivate())));
		Assert.isTrue(1.00 == Double.valueOf(this.df.format(this.newspaperService.getAverageArticlesPerPrivateNewspaper())));
		Assert.isTrue(1.33 == Double.valueOf(this.df.format(this.newspaperService.getAverageArticlesPerPublicNewspaper())));
		Assert.isTrue(0.5 == Double.valueOf(this.df.format(this.newspaperService.getRatioSubscribersPrivateVsTotal())));
		Assert.isTrue(0.0 == Double.valueOf(this.df.format(this.newspaperService.getAvgRatioPrivateVsPublicPerPublisher())));
		Assert.isTrue(0.29 == Double.valueOf(this.df.format(this.advertService.findRatioNewspapersAdvertisementsVSNoAdvertisements())));
		Assert.isTrue(0.1 == Double.valueOf(this.df.format(this.advertService.findRatioAdvertisementsTaboo())));
		Assert.isTrue(1.9 == Double.valueOf(this.df.format(this.volumeService.getAverageNewspapersPerVolume())));
		Assert.isTrue(0.45 == Double.valueOf(this.df.format(this.volumeService.getRatioSubscriptionsNewspaperVsVolume())));

		super.unauthenticate();
	}

	// ------------------------------------------------------ TESTS        2.0
	// ------------------------------------------------------------------

	// Acme-Newspaper 2.0 FR5.2: Remove an advert that he or she thinks is
	// inappropriate.
	// ***********************************************************************************************************
	// POSITIVE: Removing advert from the list
	@Test
	public void removeAdvert() {
		this.authenticate("admin");
		final Advertisement a = this.advertService.findOne(this.getEntityId("advertisement1"));
		final Newspaper n = this.newspaperService.findOne(this.getEntityId("newspaper1"));
		this.advertService.delete(a, n);

		Assert.isTrue(!this.advertService.findAll().contains(a));
		this.authenticate(null);
	}
	// NEGATIVE: Removing null advert
	@Test(expected = AssertionError.class)
	public void removeAdvertChirp() {
		this.authenticate("admin");
		final Advertisement a = this.advertService.findOne(this.getEntityId("advertisement10"));
		final Newspaper n = this.newspaperService.findOne(this.getEntityId("newspaper1"));
		this.advertService.delete(a, n);

		Assert.notNull(a);
		Assert.isTrue(!this.advertService.findAll().contains(a));
		this.authenticate(null);
	}

	// NEGATIVE: Removing advert as nonexistent user
	@Test(expected = IllegalArgumentException.class)
	public void removeOtherAdvertChirp() {
		this.authenticate("user123");
		final Advertisement a = this.advertService.findOne(this.getEntityId("advertisement1"));
		final Newspaper n = this.newspaperService.findOne(this.getEntityId("newspaper1"));
		this.advertService.delete(a, n);

		Assert.isTrue(!this.advertService.findAll().contains(a));
		this.authenticate(null);
	}
}
