
package services;

import java.util.Calendar;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.ChirpRepository;
import domain.Chirp;
import domain.User;

@Service
@Transactional
public class ChirpService {

	@Autowired
	private ChirpRepository			chirpRepository;

	@Autowired
	private UserService				userService;

	@Autowired
	private AdministratorService	administratorService;


	public Chirp create() {
		Chirp result;
		result = new Chirp();
		return result;
	}

	public Chirp findOne(final int chirpId) {
		Chirp result;
		Assert.isTrue(chirpId != 0);
		result = this.chirpRepository.findOne(chirpId);
		Assert.notNull(result);
		return result;
	}

	public Collection<Chirp> findAll() {
		return this.chirpRepository.findAll();
	}

	public Collection<Chirp> findAllByUser(final int userId) {
		Collection<Chirp> result;

		Assert.isTrue(userId != 0);
		result = this.chirpRepository.findAllByUser(userId);

		return result;
	}

	public Collection<Chirp> getChirpsOfFollowingUsers() {
		Collection<Chirp> result;
		final User user = this.userService.findByPrincipal();
		Assert.notNull(user);
		final int userId = user.getId();
		Assert.isTrue(userId != 0);
		result = this.chirpRepository.getChirpsOfFollowingUsers(userId);

		return result;
	}

	public Chirp save(final Chirp chirp) {
		Chirp result;

		Assert.notNull(chirp);
		final User principal = this.userService.findByPrincipal();
		Assert.isTrue(chirp.getUser().equals(principal));

		result = this.chirpRepository.save(chirp);
		return result;
	}

	public void delete(final Chirp chirp) {

		//		Assert.notNull(chirp);
		//		final Administrator principal = this.administratorService.findByPrincipal();
		//		Assert.notNull(principal);
		Assert.notNull(chirp);

		this.chirpRepository.delete(chirp);
	}

	public Double getAverageChirpsPerUser() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.chirpRepository.getAverageChirpsPerUser();
	}

	public Double getStandardDeviationChirpsPerUser() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.chirpRepository.getStandardDeviationChirpsPerUser();
	}

	public Double getRatioUsersMoreChirpsThan75Percent() {
		Assert.notNull(this.administratorService.findByPrincipal());
		return this.chirpRepository.getRatioUsersMoreChirpsThan75Percent();
	}


	// Reconstruct

	@Autowired
	private Validator	validator;


	public Chirp reconstruct(final Chirp chirp, final BindingResult binding) {
		final Chirp oldChirp;

		if (chirp.getId() == 0) {
			final User principal = this.userService.findByPrincipal();
			chirp.setUser(principal);
			chirp.setMoment(Calendar.getInstance());
		} else {
			oldChirp = this.chirpRepository.findOne(chirp.getId());

			chirp.setMoment(Calendar.getInstance());
			chirp.setId(oldChirp.getId());
			chirp.setVersion(oldChirp.getVersion());
		}

		this.validator.validate(chirp, binding);

		return chirp;
	}

}
