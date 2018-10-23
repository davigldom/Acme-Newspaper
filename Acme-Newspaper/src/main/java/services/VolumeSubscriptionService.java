
package services;

import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.VolumeSubscriptionRepository;
import domain.Customer;
import domain.Volume;
import domain.VolumeSubscription;

@Service
@Transactional
public class VolumeSubscriptionService {

	@Autowired
	private VolumeSubscriptionRepository	volumeSubscriptionRepository;

	@Autowired
	private CustomerService					customerService;

	@Autowired
	private VolumeService					volumeService;


	public VolumeSubscription create(final int volumeId) {
		final VolumeSubscription result;
		final Volume volume = this.volumeService.findOne(volumeId);

		result = new VolumeSubscription();

		result.setVolume(volume);

		return result;
	}

	public VolumeSubscription findOne(final int volumeSubscriptionId) {
		VolumeSubscription result;
		Assert.isTrue(volumeSubscriptionId != 0);
		result = this.volumeSubscriptionRepository.findOne(volumeSubscriptionId);
		Assert.notNull(result);
		return result;
	}

	public Collection<VolumeSubscription> findAll() {
		return this.volumeSubscriptionRepository.findAll();
	}

	public VolumeSubscription save(final VolumeSubscription volumeSubscription) {
		VolumeSubscription result;
		final Customer principal = this.customerService.findByPrincipal();
		Assert.notNull(volumeSubscription);

		Assert.isTrue(volumeSubscription.getSubscriber().equals(principal));
		Assert.notNull(this.volumeService.findOne(volumeSubscription.getVolume().getId()));

		for (VolumeSubscription vs : principal.getVolumeSubscriptions()) {
			Assert.isTrue(!vs.getVolume().equals(volumeSubscription.getVolume()));
		}
		result = this.volumeSubscriptionRepository.save(volumeSubscription);

		result.getVolume().getVolumeSubscriptions().add(result);
		principal.getVolumeSubscriptions().add(result);

		return result;
	}

	public void delete(final VolumeSubscription volumeSubscription) {
		final Customer principal = this.customerService.findByPrincipal();

		Assert.notNull(principal);
		Assert.notNull(volumeSubscription);

		Assert.isTrue(volumeSubscription.getSubscriber().equals(principal));

		this.volumeSubscriptionRepository.delete(volumeSubscription);
	}

	public void flush() {
		this.volumeSubscriptionRepository.flush();
	}


	//Reconstruct 

	@Autowired
	private Validator	validator;


	public VolumeSubscription reconstruct(final VolumeSubscription volumeSubscription, final BindingResult binding) {

		Assert.isTrue(volumeSubscription.getId() == 0);

		final Customer principal = this.customerService.findByPrincipal();
		final Volume volume = volumeSubscription.getVolume();
		Assert.notNull(volume);

		volumeSubscription.setSubscriber(principal);
		volumeSubscription.setVolume(volume);

		this.validator.validate(volumeSubscription, binding);

		return volumeSubscription;
	}

}
