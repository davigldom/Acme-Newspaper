
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

@Entity
@Access(AccessType.PROPERTY)
public class VolumeSubscription extends DomainEntity {

	//Attributes

	private String	creditCardNumber;
	private int		expirationMonth;
	private int		expirationYear;
	private int		securityCode;


	@CreditCardNumber
	@NotBlank
	@Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$")
	public String getCreditCardNumber() {
		return this.creditCardNumber;
	}

	public void setCreditCardNumber(final String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	@Range(min = 1, max = 12)
	public int getExpirationMonth() {
		return this.expirationMonth;
	}

	public void setExpirationMonth(final int expirationMonth) {
		this.expirationMonth = expirationMonth;
	}

	@Range(min = 2018, max = 2100)
	public int getExpirationYear() {
		return this.expirationYear;
	}

	public void setExpirationYear(final int expirationYear) {
		this.expirationYear = expirationYear;
	}

	@Range(min = 1, max = 999)
	public int getSecurityCode() {
		return this.securityCode;
	}

	public void setSecurityCode(final int securityCode) {
		this.securityCode = securityCode;
	}


	//Relationships

	private Customer	subscriber;
	private Volume		volume;


	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Customer getSubscriber() {
		return this.subscriber;
	}

	public void setSubscriber(final Customer subscriber) {
		this.subscriber = subscriber;
	}


	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

}
