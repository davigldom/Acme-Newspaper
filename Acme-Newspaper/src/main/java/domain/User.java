
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Entity
@Access(AccessType.PROPERTY)
public class User extends Actor {

	//Relationships

	private Collection<Newspaper>	newspapers;
	private Collection<Article>		articles;

	private Collection<User>		following;
	private Collection<User>		followers;


	@NotNull
	@Valid
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "publisher")
	public Collection<Newspaper> getNewspapers() {
		return this.newspapers;
	}

	public void setNewspapers(final Collection<Newspaper> newspapers) {
		this.newspapers = newspapers;
	}

	@NotNull
	@Valid
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "creator")
	public Collection<Article> getArticles() {
		return this.articles;
	}

	public void setArticles(final Collection<Article> articles) {
		this.articles = articles;
	}

	@ManyToMany
	public Collection<User> getFollowing() {
		return this.following;
	}

	public void setFollowing(final Collection<User> following) {
		this.following = following;
	}

	@ManyToMany(mappedBy = "following")
	public Collection<User> getFollowers() {
		return this.followers;
	}

	public void setFollowers(final Collection<User> followers) {
		this.followers = followers;
	}

}
