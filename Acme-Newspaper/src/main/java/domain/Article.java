
package domain;

import java.util.Calendar;
import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
public class Article extends DomainEntity {

	private String				title;
	private Calendar			publicationMoment;
	private String				summary;
	private String				body;
	private Collection<String>	pictures;
	private boolean				published;
	private boolean				draft;


	@NotBlank
	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	public Calendar getPublicationMoment() {
		return this.publicationMoment;
	}

	public void setPublicationMoment(final Calendar publicationMoment) {
		this.publicationMoment = publicationMoment;
	}

	@NotBlank
	@Column(length = 1000000)
	public String getSummary() {
		return this.summary;
	}

	public void setSummary(final String summary) {
		this.summary = summary;
	}

	@NotBlank
	@Column(length = 1000000)
	public String getBody() {
		return this.body;
	}

	public void setBody(final String body) {
		this.body = body;
	}

	@NotNull
	@ElementCollection
	public Collection<String> getPictures() {
		return this.pictures;
	}

	public void setPictures(final Collection<String> pictures) {
		this.pictures = pictures;
	}

	public boolean isPublished() {
		return this.published;
	}

	public void setPublished(final boolean published) {
		this.published = published;
	}

	public boolean isDraft() {
		return this.draft;
	}

	public void setDraft(final boolean draft) {
		this.draft = draft;
	}


	//Relationships

	private User					creator;
	private Newspaper				newspaper;
	private Collection<Followup>	followups;


	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public User getCreator() {
		return this.creator;
	}

	public void setCreator(final User creator) {
		this.creator = creator;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Newspaper getNewspaper() {
		return this.newspaper;
	}

	public void setNewspaper(final Newspaper newspaper) {
		this.newspaper = newspaper;
	}

	@NotNull
	@Valid
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "article")
	public Collection<Followup> getFollowups() {
		return this.followups;
	}

	public void setFollowups(final Collection<Followup> followups) {
		this.followups = followups;
	}

}
