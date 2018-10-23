
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.ArticleRepository;
import domain.Administrator;
import domain.Article;
import domain.Customer;
import domain.Followup;
import domain.Newspaper;
import domain.NewspaperStatus;
import domain.User;

@Service
@Transactional
public class ArticleService {

	@Autowired
	private ArticleRepository		articleRepository;

	@Autowired
	private UserService				userService;

	@Autowired
	private AdministratorService	administratorService;

	@Autowired
	private FollowupService			followupService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private CustomerService			customerService;

	@Autowired
	private NewspaperService		newspaperService;


	public Article create() {
		final Article result;

		result = new Article();
		result.setPictures(new ArrayList<String>());

		return result;
	}

	public Article findOne(final int articleId) {
		Article result;
		Newspaper newspaper;
		Assert.isTrue(articleId != 0);

		result = this.articleRepository.findOne(articleId);
		newspaper = result.getNewspaper();

		if (newspaper.getMakePrivate())
			if (!this.actorService.findByPrincipal().getUserAccount().getAuthorities().toArray()[0].toString().equals("ADMIN"))
				if (this.actorService.findByPrincipal().getUserAccount().getAuthorities().toArray()[0].toString().equals("CUSTOMER")) {
					final Customer customer = this.customerService.findByPrincipal();
					Assert.notNull(customer);
					Assert.isTrue(this.newspaperService.findAlreadySubscribedByCustomer(customer).contains(newspaper));
				} else if (this.actorService.findByPrincipal().getUserAccount().getAuthorities().toArray()[0].toString().equals("USER")) {
					final User user = this.userService.findByPrincipal();
					Assert.notNull(user);
					Assert.isTrue(newspaper.getPublisher().equals(user) || result.getCreator().equals(user));
				} else if (!this.actorService.isAuthenticated())
					Assert.notNull(this.actorService.findByPrincipal());

		Assert.notNull(result);
		return result;
	}

	public Collection<Article> findPublishedByUser(final int userId) {
		Assert.isTrue(userId != 0);
		final Collection<Article> result = this.articleRepository.findAllPublicPublishedByCreator(userId);
		Assert.notNull(result);
		return result;
	}

	public Collection<Article> findAll() {
		return this.articleRepository.findAll();
	}

	public Article save(final Article article) {
		Article result;
		final User principal = this.userService.findByPrincipal();
		Assert.notNull(article);

		Assert.isTrue(article.getCreator().equals(principal));
		Assert.isTrue(article.getNewspaper().getStatus().equals(NewspaperStatus.OPEN));
		Assert.isTrue(!article.getNewspaper().getMakePrivate());
		boolean picturesWithURLS = false;

		//If the article has no URLs:
		if (article.getPictures() == null || article.getPictures().isEmpty())
			picturesWithURLS = true;
		//If the article has URLs:
		else
			for (final String s : article.getPictures())
				picturesWithURLS = this.isUrl(s);
		Assert.isTrue(picturesWithURLS);
		result = this.articleRepository.save(article);
		return result;
	}

	private boolean isUrl(final String s) {
		final String regex = "^(https?://)?(([\\w!~*'().&=+$%-]+: )?[\\w!~*'().&=+$%-]+@)?(([0-9]{1,3}\\.){3}[0-9]{1,3}|([\\w!~*'()-]+\\.)*([\\w^-][\\w-]{0,61})?[\\w]\\.[a-z]{2,6})(:[0-9]{1,4})?((/*)|(/+[\\w!~*'().;?:@&=+$,%#-]+)+/*)$";

		try {
			final Pattern patt = Pattern.compile(regex);
			final Matcher matcher = patt.matcher(s);
			return matcher.matches();

		} catch (final RuntimeException e) {
			return false;
		}
	}

	public void delete(final Article article) {
		Administrator admin;
		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);
		Assert.notNull(article);

		article.getNewspaper().getArticles().remove(article);
		for (final Followup f : article.getFollowups())
			this.followupService.delete(f);

		this.articleRepository.delete(article);
	}

	public Article findOneToEdit(final int articleId) {
		Article result;
		final User principal = this.userService.findByPrincipal();

		Assert.isTrue(articleId != 0);
		result = this.articleRepository.findOne(articleId);
		Assert.notNull(result);
		Assert.isTrue(result.getCreator().equals(principal));
		Assert.isTrue(result.isDraft());

		return result;
	}

	public void flush() {
		this.articleRepository.flush();
	}

	public Collection<Article> findAllByNewspaper(final int newspaperId) {
		return this.articleRepository.findAllByNewspaper(newspaperId);
	}

	public Collection<Article> findAllByCreator(final int userId) {
		return this.articleRepository.findAllByCreator(userId);
	}

	public Double getRatioArticlesCreatedByUser() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.articleRepository.getRatioArticlesCreatedByUser();
	}

	public Double getAverageArticlesPerUser() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.articleRepository.getAverageArticlesPerUser();
	}

	public Double getStandardDeviationArticlesPerUser() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.articleRepository.getStandardDeviationArticlesPerUser();
	}

	public Double getAverageArticlesPerNewspaper() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.articleRepository.getAverageArticlesPerNewspaper();
	}

	public Double getStandardDeviationArticlesPerNewspaper() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.articleRepository.getStandardDeviationArticlesPerNewspaper();
	}

	public Collection<Article> findByKeyword(String keyword, final int newspaperId) {
		if (keyword == null)
			keyword = "%";
		Collection<Article> result = null;
		final Newspaper newspaper = this.newspaperService.findOne(newspaperId);
		if (newspaper.getStatus().equals(NewspaperStatus.OPEN))
			Assert.notNull(this.userService.findByPrincipal());
		if (newspaper.getStatus().equals(NewspaperStatus.CLOSE))
			Assert.isTrue(newspaper.getPublisher().equals(this.actorService.findByPrincipal()));
		result = this.articleRepository.findByKeyword(keyword, newspaperId);
		return result;
	}


	// Reconstruct

	@Autowired
	private Validator	validator;


	public Article reconstruct(final Article article, final Newspaper newspaper, final BindingResult binding) {
		final Article articleStored;

		if (article.getId() == 0) {
			final User principal = this.userService.findByPrincipal();
			article.setCreator(principal);
			article.setNewspaper(newspaper);
			article.setPublicationMoment(null);
			article.setDraft(true);
			article.setPublished(false);
			article.setFollowups(new ArrayList<Followup>());
		} else {
			articleStored = this.articleRepository.findOne(article.getId());

			article.setCreator(articleStored.getCreator());
			article.setNewspaper(articleStored.getNewspaper());
			article.setPublished(articleStored.isPublished());
			article.setId(articleStored.getId());
			article.setVersion(articleStored.getVersion());
			article.setFollowups(articleStored.getFollowups());
		}
		this.validator.validate(article, binding);

		return article;
	}

}
