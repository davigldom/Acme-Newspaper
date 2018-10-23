
package domain;

import java.util.Calendar;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
public class Message extends DomainEntity {

	//Attributes

	private Calendar		moment;
	private String			subject;
	private String			body;
	private PriorityLevel	priorityLevel;


	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	public Calendar getMoment() {
		return this.moment;
	}

	public void setMoment(final Calendar moment) {
		this.moment = moment;
	}

	@NotBlank
	public String getSubject() {
		return this.subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
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
	@Valid
	public PriorityLevel getPriorityLevel() {
		return this.priorityLevel;
	}

	public void setPriorityLevel(final PriorityLevel priorityLevel) {
		this.priorityLevel = priorityLevel;
	}


	//Relationships

	private Actor	sender;
	private Actor	recipient;


	@NotNull
	@Valid
	@ManyToOne(optional = false)
	@OrderColumn(name = "POSITION")
	public Actor getSender() {
		return this.sender;
	}

	public void setSender(final Actor sender) {
		this.sender = sender;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	@OrderColumn(name = "POSITION")
	public Actor getRecipient() {
		return this.recipient;
	}

	public void setRecipient(final Actor recipient) {
		this.recipient = recipient;
	}

}
