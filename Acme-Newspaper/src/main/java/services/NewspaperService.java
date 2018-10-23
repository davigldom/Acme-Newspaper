package services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.NewspaperRepository;
import domain.Advertisement;
import domain.Article;
import domain.Customer;
import domain.Newspaper;
import domain.NewspaperStatus;
import domain.Subscription;
import domain.User;

@Service
@Transactional
public class NewspaperService {

	@Autowired
	private NewspaperRepository newspaperRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private AdministratorService administratorService;

	@Autowired
	private AgentService agentService;

	public Newspaper create() {
		final Newspaper result;

		result = new Newspaper();

		return result;
	}

	public Newspaper findOne(final int newspaperId) {
		Newspaper result;
		Assert.isTrue(newspaperId != 0);
		result = this.newspaperRepository.findOne(newspaperId);
		Assert.notNull(result);
		return result;
	}

	public Collection<Newspaper> findAll() {
		return this.newspaperRepository.findAll();
	}

	public Newspaper save(final Newspaper newspaper) {
		Newspaper result;
		final User principal = this.userService.findByPrincipal();
		Assert.notNull(newspaper);

		Assert.isTrue(newspaper.getPublisher().equals(principal));

		if (newspaper.getPrice() != null)
			Assert.isTrue(newspaper.getMakePrivate() == true);

		if (newspaper.getMakePrivate() == true)
			Assert.isTrue(newspaper.getPrice() != null);

		result = this.newspaperRepository.save(newspaper);
		return result;
	}

	public void delete(final Newspaper newspaper) {
		// Administrator admin;
		// admin = this.administratorService.findByPrincipal();
		// Assert.notNull(admin);
		Assert.notNull(newspaper);

		this.newspaperRepository.delete(newspaper);
	}

	public Newspaper findOneToEdit(final int newspaperId) {
		Newspaper result;
		final User principal = this.userService.findByPrincipal();

		Assert.isTrue(newspaperId != 0);
		result = this.newspaperRepository.findOne(newspaperId);
		Assert.notNull(result);
		Assert.isTrue(result.getPublisher().equals(principal));
		Assert.isTrue(!result.getStatus().equals(NewspaperStatus.PUBLISHED));

		return result;
	}

	public Newspaper findOneToSubscribe(final int newspaperId) {
		final Newspaper result;
		final Customer principal = this.customerService.findByPrincipal();
		final Collection<Newspaper> alreadySubscribedTo = this
				.findAlreadySubscribedByCustomer(principal);

		Assert.isTrue(newspaperId != 0);
		result = this.newspaperRepository.findOne(newspaperId);
		Assert.notNull(result);
		Assert.isTrue(!alreadySubscribedTo.contains(result));
		Assert.isTrue(result.getMakePrivate());
		Assert.isTrue(result.getStatus().equals(NewspaperStatus.PUBLISHED));

		return result;
	}

	public void flush() {
		this.newspaperRepository.flush();
	}

	// TODO: No necesario si se mantiene la bidireccionalidad
	public Collection<Newspaper> findAllByPublisher(final int userId) {
		return this.newspaperRepository.findAllByPublisher(userId);
	}

	public Collection<Newspaper> findAllPublished() {
		return this.newspaperRepository.findAllPublished();
	}

	public Collection<Newspaper> findAllPublishedOrOpened() {
		return this.newspaperRepository.findAllPublishedOrOpened();
	}

	public Collection<Newspaper> findAllPublicOpened() {
		return this.newspaperRepository.findAllPublicOpened();
	}

	public Collection<Newspaper> findAllPublicPublished() {
		return this.newspaperRepository.findAllPublicPublished();
	}

	// With this you see public,private and those the customer id subscribed to,
	// so you can subscribe to other ones later
	public Collection<Newspaper> findAllPublishedAndSubscribedTo(
			final int customerId) {
		return this.newspaperRepository
				.findAllPublishedAndSubscribedTo(customerId);
	}

	public Collection<Newspaper> findAlreadySubscribedByCustomer(
			final Customer customer) {
		return this.newspaperRepository
				.findAlreadySubscribedByCustomer(customer);
	}

	public void publish(final int newspaperId) {
		Assert.notNull(newspaperId);
		final Calendar moment = Calendar.getInstance();

		final User principal = this.userService.findByPrincipal();
		Assert.notNull(principal);

		final Newspaper newspaper = this.findOne(newspaperId);
		Assert.notNull(newspaper);
		Assert.isTrue(newspaper.getPublisher().equals(principal));
		Assert.isTrue(!newspaper.getStatus().equals(NewspaperStatus.PUBLISHED));

		final Collection<Article> articles = this.articleService
				.findAllByNewspaper(newspaperId);
		for (final Article a : articles)
			Assert.isTrue(!a.isDraft());

		newspaper.setStatus(NewspaperStatus.PUBLISHED);

		for (final Article a : articles) {
			a.setPublished(true);
			a.setPublicationMoment(moment);
		}

		newspaper.setPublicationDate(moment);

		this.userService.save(principal);
	}

	public Collection<Newspaper> findAllByKeyword(String keyword) {
		Assert.isTrue(this.administratorService.findByPrincipal() != null
				|| this.customerService.findByPrincipal() != null);
		if (keyword == null)
			keyword = "%";
		Collection<Newspaper> result = null;
		result = this.newspaperRepository.findAllByKeyword(keyword);
		return result;
	}

	public Collection<Newspaper> findPublicByKeyword(String keyword) {
		if (keyword == null)
			keyword = "%";
		Collection<Newspaper> result = null;
		result = this.newspaperRepository.findPublicByKeyword(keyword);
		return result;
	}

	// Dashboard

	public Double getRatioPublicVsPrivate() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.newspaperRepository.getRatioPublicVsPrivate();
	}

	public Double getAverageArticlesPerPrivateNewspaper() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.newspaperRepository.getAverageArticlesPerPrivateNewspaper();
	}

	public Double getAverageArticlesPerPublicNewspaper() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.newspaperRepository.getAverageArticlesPerPublicNewspaper();
	}

	public Double getRatioSubscribersPrivateVsTotal() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.newspaperRepository.getRatioSubscribersPrivateVsTotal();
	}

	public Double getRatioNewspaperCreatedByUser() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.newspaperRepository.getRatioNewspaperCreatedByUser();
	}

	public Double getAverageNewspaperPerUser() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.newspaperRepository.getAverageNewspaperPerUser();
	}

	public Double getStandardDeviationNewspaperPerUser() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.newspaperRepository.getStandardDeviationNewspaperPerUser();
	}

	public Collection<Newspaper> getNewspapersTenPercentMoreArticles() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.newspaperRepository.getNewspapersTenPercentMoreArticles();
	}

	public Collection<Newspaper> getNewspapersTenPercentFewerArticles() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.newspaperRepository.getNewspapersTenPercentFewerArticles();
	}

	public Double getAvgRatioPrivateVsPublicPerPublisher() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.newspaperRepository
				.getAvgRatioPrivateVsPublicPerPublisher();
	}

	public Collection<Newspaper> getNewspapersWithAdvertisementByAgent() {
		int agentId = agentService.findByPrincipal().getId();
		Assert.notNull(agentId);
		return this.newspaperRepository
				.getNewspapersWithAdvertisementByAgent(agentId);
	}

	public Collection<Newspaper> getNewspapersWithoutAdvertisementByAgent() {
		int agentId = agentService.findByPrincipal().getId();
		Assert.notNull(agentId);
		return this.newspaperRepository
				.getNewspapersWithoutAdvertisementByAgent(agentId);
	}

	public Collection<Newspaper> getVolumeNewspapersNotAuthenticated(
			final int volumeId) {
		return this.newspaperRepository
				.getVolumeNewspapersNotAuthenticated(volumeId);
	}

	public Collection<Newspaper> getVolumeNewspapersAsCustomer(
			final int volumeId) {

		return this.newspaperRepository.getVolumeNewspapersAsCustomer(volumeId);

	}

	// Reconstruct

	@Autowired
	private Validator validator;

	public Newspaper reconstruct(final Newspaper newspaper,
			final BindingResult binding) {
		Newspaper newspaperStored;

		if (newspaper.getId() == 0) {
			final User principal = this.userService.findByPrincipal();
			final Collection<Article> articles = new ArrayList<>();
			newspaper.setPublisher(principal);
			newspaper.setArticles(articles);
			newspaper.setStatus(NewspaperStatus.CLOSE);
			newspaper.setPublicationDate(null);
			newspaper.setSubscriptions(new HashSet<Subscription>());
			newspaper.setAdvertisements(new HashSet<Advertisement>());

		} else {
			newspaperStored = this.newspaperRepository.findOne(newspaper
					.getId());

			newspaper.setPublicationDate(newspaperStored.getPublicationDate());
			newspaper.setArticles(newspaperStored.getArticles());
			newspaper.setPublisher(newspaperStored.getPublisher());
			newspaper.setSubscriptions(newspaperStored.getSubscriptions());
			newspaper.setId(newspaperStored.getId());
			newspaper.setVersion(newspaperStored.getVersion());
			newspaper.setAdvertisements(newspaperStored.getAdvertisements());
		}
		this.validator.validate(newspaper, binding);

		return newspaper;
	}

}
