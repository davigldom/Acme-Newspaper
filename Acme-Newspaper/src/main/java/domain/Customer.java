
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Entity
@Access(AccessType.PROPERTY)
public class Customer extends Actor {

	//Relationships

	private Collection<Subscription>	subscriptions;
	private Collection<VolumeSubscription>	volumeSubscriptions;


	@NotNull
	@Valid
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "subscriber")
	public Collection<Subscription> getSubscriptions() {
		return this.subscriptions;
	}

	public void setSubscriptions(final Collection<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	@NotNull
	@Valid
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "subscriber")
	public Collection<VolumeSubscription> getVolumeSubscriptions() {
		return volumeSubscriptions;
	}

	public void setVolumeSubscriptions(
			Collection<VolumeSubscription> volumeSubscriptions) {
		this.volumeSubscriptions = volumeSubscriptions;
	}
	
	

}
