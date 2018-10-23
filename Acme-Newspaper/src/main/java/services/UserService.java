
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.UserRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Article;
import domain.Folder;
import domain.Message;
import domain.Newspaper;
import domain.User;
import forms.RegisterUser;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository		userRepository;

	@Autowired
	private UserAccountService	userAccountService;

	@Autowired
	private ActorService		actorService;

	@Autowired
	private FolderService		folderService;

	@Autowired
	private Validator			validator;


	public User create() {

		final UserAccount userAccount = this.userAccountService.create();
		final Authority authority = new Authority();
		authority.setAuthority(Authority.USER);
		userAccount.addAuthority(authority);

		final User result;
		result = new User();
		result.setUserAccount(userAccount);
		return result;
	}

	public Collection<User> findAll() {
		Collection<User> result;
		result = this.userRepository.findAll();
		return result;
	}

	public User findOne(final int userId) {
		User result;
		Assert.isTrue(userId != 0);
		result = this.userRepository.findOne(userId);
		Assert.notNull(result);
		return result;
	}

	public User save(final User user) {
		User result;
		Assert.notNull(user);
		Assert.notNull(user.getUserAccount().getUsername());
		Assert.notNull(user.getUserAccount().getPassword());
		if (user.getId() == 0) {
			String passwordHashed = null;
			final Md5PasswordEncoder encoder = new Md5PasswordEncoder();
			passwordHashed = encoder.encodePassword(user.getUserAccount().getPassword(), null);
			user.getUserAccount().setPassword(passwordHashed);

			//System folders
			final Collection<Folder> sysFolders = new HashSet<>();

			//In box
			final Folder inbox_created = this.folderService.create();
			inbox_created.setName("in box");
			inbox_created.setSysFolder(true);
			inbox_created.setMessages(new ArrayList<Message>());
			inbox_created.setChildren(new ArrayList<Folder>());
			final Folder inbox = this.folderService.save(inbox_created);
			sysFolders.add(inbox);

			//Out box
			final Folder outbox_created = this.folderService.create();
			outbox_created.setName("out box");
			outbox_created.setSysFolder(true);
			outbox_created.setMessages(new ArrayList<Message>());
			outbox_created.setChildren(new ArrayList<Folder>());
			final Folder outbox = this.folderService.save(outbox_created);
			sysFolders.add(outbox);

			//Notification box
			final Folder notificationbox_created = this.folderService.create();
			notificationbox_created.setName("notification box");
			notificationbox_created.setSysFolder(true);
			notificationbox_created.setMessages(new ArrayList<Message>());
			notificationbox_created.setChildren(new ArrayList<Folder>());
			final Folder notificationbox = this.folderService.save(notificationbox_created);
			sysFolders.add(notificationbox);

			//Trash box
			final Folder trashbox_created = this.folderService.create();
			trashbox_created.setName("trash box");
			trashbox_created.setSysFolder(true);
			trashbox_created.setMessages(new ArrayList<Message>());
			trashbox_created.setChildren(new ArrayList<Folder>());
			final Folder trashbox = this.folderService.save(trashbox_created);
			sysFolders.add(trashbox);

			//Spam box
			final Folder spambox_created = this.folderService.create();
			spambox_created.setName("spam box");
			spambox_created.setSysFolder(true);
			spambox_created.setMessages(new ArrayList<Message>());
			spambox_created.setChildren(new ArrayList<Folder>());
			final Folder spambox = this.folderService.save(spambox_created);
			sysFolders.add(spambox);
		}

		result = this.userRepository.save(user);
		return result;
	}

	public void delete(final User user) {
		Assert.notNull(user);
		Assert.isTrue(user.getId() != 0);
		Assert.isTrue(this.actorService.findByPrincipal().equals(user));
		this.userRepository.delete(user);
		//		Assert.isTrue(!this.userRepository.findAll().contains(user));
	}

	public User findByPrincipal() {
		User result;
		final UserAccount userAccount = LoginService.getPrincipal();
		Assert.notNull(userAccount);
		result = this.findByUserAccount(userAccount);
		Assert.notNull(result);

		return result;
	}

	public User findByUserAccount(final UserAccount userAccount) {
		Assert.notNull(userAccount);
		User result;
		result = this.userRepository.findByUserAccountId(userAccount.getId());
		Assert.notNull(result);

		return result;
	}

	public Collection<User> findFollowersByUser(final User principal) {
		Collection<User> result;

		Assert.notNull(principal.getId());
		Assert.isTrue(principal.getId() != 0);

		result = this.userRepository.findFollowersByUser(principal.getId());
		return result;
	}

	public Collection<User> findFollowingByUser(final User principal) {
		Collection<User> result;

		Assert.notNull(principal.getId());
		Assert.isTrue(principal.getId() != 0);

		result = this.userRepository.findFollowingByUser(principal.getId());
		return result;
	}

	public User reconstructEdit(final User user, final BindingResult binding) {
		User userStored;

		if (user.getId() != 0) {
			userStored = this.userRepository.findOne(user.getId());
			//			result.setEmail(user.getEmail());
			//			result.setName(user.getName());
			//			result.setPhone(user.getPhone());
			//			result.setSurname(user.getSurname());
			//			result.setPostalAddress(user.getPostalAddress());

			user.setArticles(userStored.getArticles());
			user.setId(userStored.getId());
			user.setFollowers(userStored.getFollowers());
			user.setFollowing(userStored.getFollowing());
			user.setNewspapers(userStored.getNewspapers());
			user.setUserAccount(userStored.getUserAccount());
			user.setVersion(userStored.getVersion());
			user.setFolders(userStored.getFolders());
		}
		this.validator.validate(user, binding);
		return user;

	}

	public User reconstruct(final RegisterUser user, final BindingResult binding) {
		User result;
		Assert.isTrue(user.isAcceptedTerms());
		Assert.isTrue(user.getPassword().equals(user.getRepeatedPassword()));
		result = this.create();

		result.setEmail(user.getEmail());
		result.setName(user.getName());
		result.setPhone(user.getPhone());
		result.setPostalAddress(user.getPostalAddress());
		result.setSurname(user.getSurname());
		result.setArticles(new ArrayList<Article>());
		result.setNewspapers(new ArrayList<Newspaper>());
		result.setFolders(new ArrayList<Folder>());

		result.getUserAccount().setUsername(user.getUsername());
		result.getUserAccount().setPassword(user.getPassword());

		this.validator.validate(result, binding);

		return result;
	}

	public void flush() {
		this.userRepository.flush();
	}

}
