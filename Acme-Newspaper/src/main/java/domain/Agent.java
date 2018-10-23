
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
public class Agent extends Actor {

	//Relationships
	

	private Collection<Advertisement>	advertisements;

	@OneToMany(cascade = CascadeType.REMOVE)
	@NotNull
	@Valid
	public Collection<Advertisement> getAdvertisements() {
		return advertisements;
	}

	public void setAdvertisements(Collection<Advertisement> advertisements) {
		this.advertisements = advertisements;
	}


}
