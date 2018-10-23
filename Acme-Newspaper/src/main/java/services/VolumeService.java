
package services;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.VolumeRepository;
import domain.Newspaper;
import domain.User;
import domain.Volume;
import domain.VolumeSubscription;

@Service
@Transactional
public class VolumeService {

	@Autowired
	private VolumeRepository	volumeRepository;

	@Autowired
	private UserService			userService;


	public Volume create() {
		final Volume result;
		result = new Volume();

		return result;
	}

	public Volume findOne(final int volumeId) {
		Volume result;
		Assert.isTrue(volumeId != 0);
		result = this.volumeRepository.findOne(volumeId);
		Assert.notNull(result);
		return result;
	}

	public Collection<Volume> findAll() {
		return this.volumeRepository.findAll();
	}

	public Collection<Volume> findAllPublic() {
		return this.volumeRepository.findAllPublic();
	}

	public Collection<Volume> findMyVolumes(final int userId) {
		return this.volumeRepository.findMyVolumes(userId);
	}

	public Volume save(final Volume volume) {
		Volume result;
		final User principal = this.userService.findByPrincipal();

		Assert.notNull(volume);
		Assert.isTrue(volume.getPublisher().equals(principal));

		result = this.volumeRepository.save(volume);
		return result;
	}

	public void delete(final Volume volume) {

		Assert.notNull(volume);

		this.volumeRepository.delete(volume);
	}

	public void flush() {
		this.volumeRepository.flush();
	}

	public Collection<Volume> findNotSubscribed(final int customerId) {
		return this.volumeRepository.findNotSubscribed(customerId);
	}

	public Collection<Volume> findByNewspaper(final int newspaperId) {
		return this.volumeRepository.findByNewspaper(newspaperId);
	}

	public Double getAverageNewspapersPerVolume() {
		return this.volumeRepository.getAverageNewspapersPerVolume();
	}

	public Double getRatioSubscriptionsNewspaperVsVolume() {
		return this.volumeRepository.getRatioSubscriptionsNewspaperVsVolume();
	}


	// Reconstruct

	@Autowired
	private Validator	validator;


	public Volume reconstruct(final Volume volume, final BindingResult binding) {
		final Volume volumeStored;

		if (volume.getId() == 0) {
			final User principal = this.userService.findByPrincipal();
			volume.setPublisher(principal);
			volume.setNewspapers(new ArrayList<Newspaper>());
			volume.setVolumeSubscriptions(new ArrayList<VolumeSubscription>());
		} else {
			volumeStored = this.volumeRepository.findOne(volume.getId());

			volume.setId(volumeStored.getId());
			volume.setVersion(volumeStored.getVersion());

			volume.setTitle(volumeStored.getTitle());
			volume.setDescription(volumeStored.getDescription());
			volume.setYear(volumeStored.getYear());
			volume.setPublisher(volumeStored.getPublisher());
			volume.setNewspapers(volumeStored.getNewspapers());
			volume.setVolumeSubscriptions(volumeStored.getVolumeSubscriptions());
		}
		this.validator.validate(volume, binding);

		return volume;
	}

}
