
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@Access(AccessType.PROPERTY)
public class SystemConfig extends DomainEntity {

	private Collection<String> tabooWords;

	@NotNull
	@ElementCollection
	public Collection<String> getTabooWords() {
		return tabooWords;
	}

	public void setTabooWords(Collection<String> tabooWords) {
		this.tabooWords = tabooWords;
	}


}
