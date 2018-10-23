
package domain;

import java.util.Calendar;
import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
public class Newspaper extends DomainEntity {

	private String			title;
	private Calendar		publicationDate;
	private String			description;
	private String			picture;
	private Double			price;
	private NewspaperStatus	status;
	private boolean			makePrivate;


	@NotBlank
	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	public Calendar getPublicationDate() {
		return this.publicationDate;
	}

	public void setPublicationDate(final Calendar publicationDate) {
		this.publicationDate = publicationDate;
	}

	@NotBlank
	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@URL
	public String getPicture() {
		return this.picture;
	}

	public void setPicture(final String picture) {
		this.picture = picture;
	}

	@Range(min = 0)
	public Double getPrice() {
		return this.price;
	}

	public void setPrice(final Double price) {
		this.price = price;
	}

	public NewspaperStatus getStatus() {
		return this.status;
	}

	public void setStatus(final NewspaperStatus status) {
		this.status = status;
	}

	public boolean getMakePrivate() {
		return this.makePrivate;
	}

	public void setMakePrivate(final boolean makePrivate) {
		this.makePrivate = makePrivate;
	}


	//Relationships

	private User						publisher;
	private Collection<Article>			articles;
	private Collection<Subscription>	subscriptions;
	private Collection<Advertisement>	advertisements;


	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public User getPublisher() {
		return this.publisher;
	}

	public void setPublisher(final User publisher) {
		this.publisher = publisher;
	}

	@NotNull
	@Valid
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "newspaper")
	public Collection<Article> getArticles() {
		return this.articles;
	}

	public void setArticles(final Collection<Article> articles) {
		this.articles = articles;
	}

	@NotNull
	@Valid
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "newspaper")
	public Collection<Subscription> getSubscriptions() {
		return this.subscriptions;
	}

	public void setSubscriptions(final Collection<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	@NotNull
	@OneToMany(cascade = CascadeType.REMOVE)
	@Valid
	public Collection<Advertisement> getAdvertisements() {
		return this.advertisements;
	}

	public void setAdvertisements(final Collection<Advertisement> advertisements) {
		this.advertisements = advertisements;
	}

}
