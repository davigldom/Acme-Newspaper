
package services;

import java.util.Calendar;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.FollowupRepository;
import domain.Article;
import domain.Followup;
import domain.NewspaperStatus;
import domain.User;

@Service
@Transactional
public class FollowupService {

	@Autowired
	private FollowupRepository		followupRepository;

	@Autowired
	private UserService				userService;

	@Autowired
	private AdministratorService	administratorService;


	public Followup create() {
		final Followup result;

		result = new Followup();

		return result;
	}

	public Followup findOne(final int followupId) {
		Followup result;
		Assert.isTrue(followupId != 0);
		result = this.followupRepository.findOne(followupId);
		Assert.notNull(result);
		return result;
	}

	public Collection<Followup> findAll() {
		return this.followupRepository.findAll();
	}

	public Followup save(final Followup followup) {
		Followup result;
		final User principal = this.userService.findByPrincipal();
		Assert.notNull(followup);
		Assert.isTrue(followup.getArticle().getCreator().equals(principal));
		Assert.isTrue(!followup.getArticle().isDraft());
		Assert.isTrue(followup.getArticle().getNewspaper().getStatus().equals(NewspaperStatus.PUBLISHED));

		result = this.followupRepository.save(followup);
		return result;
	}

	public void delete(final Followup followup) {
		//		Administrator admin;
		//		admin = this.administratorService.findByPrincipal();
		//		Assert.notNull(admin);
		Assert.notNull(followup);

		this.followupRepository.delete(followup);
	}

	public void flush() {
		this.followupRepository.flush();
	}

	public Double getAverageFollowpsPerArticle() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.followupRepository.getAverageFollowpsPerArticle();
	}

	public Double getFollowupsPerArticleUpToWeek() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.followupRepository.getFollowupsPerArticleUpToWeek();
	}

	public Double getFollowupsPerArticleUpToTwoWeeks() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.followupRepository.getFollowupsPerArticleUpToTwoWeeks();
	}


	// Reconstruct

	@Autowired
	private Validator	validator;


	public Followup reconstruct(final Followup followup, final Article article, final BindingResult binding) {
		final Followup followupStored;

		if (followup.getId() == 0) {
			followup.setArticle(article);
			followup.setPublicationMoment(Calendar.getInstance());
		} else {
			followupStored = this.followupRepository.findOne(followup.getId());

			followup.setArticle(followupStored.getArticle());
			followup.setPublicationMoment(followupStored.getPublicationMoment());
			followup.setId(followupStored.getId());
			followup.setVersion(followupStored.getVersion());
		}
		this.validator.validate(followup, binding);

		return followup;
	}

}
