
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

@Entity
@Access(AccessType.PROPERTY)
public class Volume extends DomainEntity {

	private String	title;
	private String	description;
	private int		year;


	@NotBlank
	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@NotBlank
	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@Range(min = 2000, max = 3000)
	public int getYear() {
		return this.year;
	}

	public void setYear(final int year) {
		this.year = year;
	}


	//Relationship

	private Collection<Newspaper>			newspapers;
	private User							publisher;
	private Collection<VolumeSubscription>	volumeSubscriptions;

	@NotNull
	@Valid
	@ManyToMany
	public Collection<Newspaper> getNewspapers() {
		return this.newspapers;
	}

	public void setNewspapers(final Collection<Newspaper> newspapers) {
		this.newspapers = newspapers;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = true)
	public User getPublisher() {
		return this.publisher;
	}

	public void setPublisher(final User publisher) {
		this.publisher = publisher;
	}

	@NotNull
	@Valid
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "volume")
	public Collection<VolumeSubscription> getVolumeSubscriptions() {
		return this.volumeSubscriptions;
	}

	public void setVolumeSubscriptions(final Collection<VolumeSubscription> volumeSubscriptions) {
		this.volumeSubscriptions = volumeSubscriptions;
	}

}
