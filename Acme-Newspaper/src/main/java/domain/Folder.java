
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
public class Folder extends DomainEntity {

	//Attributes.

	private String	name;
	private boolean	sysFolder;


	@NotBlank
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public boolean isSysFolder() {
		return this.sysFolder;
	}

	public void setSysFolder(final boolean isSysFolder) {
		this.sysFolder = isSysFolder;
	}


	//Relationships

	private Folder				root;
	private Collection<Folder>	children;
	private Collection<Message>	messages;


	@Valid
	@ManyToOne(optional = true)
	public Folder getRoot() {
		return this.root;
	}

	public void setRoot(final Folder root) {
		this.root = root;
	}

	@Valid
	@NotNull
	@OneToMany(mappedBy = "root")
	public Collection<Folder> getChildren() {
		return this.children;
	}

	public void setChildren(final Collection<Folder> children) {
		this.children = children;
	}

	@Valid
	@NotNull
	@OneToMany
	public Collection<Message> getMessages() {
		return this.messages;
	}

	public void setMessages(final Collection<Message> messages) {
		this.messages = messages;
	}

}
