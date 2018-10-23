
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

import repositories.CustomerRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Customer;
import domain.Folder;
import domain.Message;
import domain.Subscription;
import domain.VolumeSubscription;
import forms.RegisterCustomer;

@Service
@Transactional
public class CustomerService {

	@Autowired
	private CustomerRepository	customerRepository;

	@Autowired
	private UserAccountService	userAccountService;

	@Autowired
	private ActorService		actorService;

	@Autowired
	private FolderService		folderService;

	@Autowired
	private Validator			validator;


	public Customer create() {

		final UserAccount userAccount = this.userAccountService.create();
		final Authority authority = new Authority();
		authority.setAuthority(Authority.CUSTOMER);
		userAccount.addAuthority(authority);

		final Customer result;
		result = new Customer();
		result.setUserAccount(userAccount);
		return result;
	}

	public Customer findOne(final int customerId) {
		Customer result;
		Assert.isTrue(customerId != 0);
		result = this.customerRepository.findOne(customerId);
		Assert.notNull(result);
		return result;
	}

	public Customer save(final Customer customer) {
		Customer result;
		Assert.notNull(customer);
		Assert.notNull(customer.getUserAccount().getUsername());
		Assert.notNull(customer.getUserAccount().getPassword());
		if (customer.getId() == 0) {
			String passwordHashed = null;
			final Md5PasswordEncoder encoder = new Md5PasswordEncoder();
			passwordHashed = encoder.encodePassword(customer.getUserAccount().getPassword(), null);
			customer.getUserAccount().setPassword(passwordHashed);

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

			customer.getFolders().addAll(sysFolders);
		}

		result = this.customerRepository.save(customer);
		return result;
	}

	public void delete(final Customer customer) {
		Assert.isTrue(customer.getId() != 0);
		Assert.isTrue(this.actorService.findByPrincipal().equals(customer));
		this.customerRepository.delete(customer);
		Assert.isTrue(!this.customerRepository.findAll().contains(customer));
	}

	public Customer findByPrincipal() {
		Customer result;
		final UserAccount userAccount = LoginService.getPrincipal();
		Assert.notNull(userAccount);
		result = this.findByUserAccount(userAccount);
		//		Assert.notNull(result);

		return result;
	}

	public Customer findByUserAccount(final UserAccount userAccount) {
		Assert.notNull(userAccount);
		Customer result;
		result = this.customerRepository.findByUserAccountId(userAccount.getId());
		//		Assert.notNull(result);

		return result;
	}

	//Reconstruct

	public Customer reconstructRegister(final RegisterCustomer customer, final BindingResult binding) {
		Customer result;
		Assert.isTrue(customer.isAcceptedTerms());
		Assert.isTrue(customer.getPassword().equals(customer.getRepeatedPassword()));
		result = this.create();

		result.setEmail(customer.getEmail());
		result.setName(customer.getName());
		result.setPhone(customer.getPhone());
		result.setPostalAddress(customer.getPostalAddress());
		result.setSurname(customer.getSurname());
		result.setSubscriptions(new HashSet<Subscription>());
		result.setVolumeSubscriptions(new HashSet<VolumeSubscription>());
		result.setFolders(new ArrayList<Folder>());

		result.getUserAccount().setUsername(customer.getUsername());
		result.getUserAccount().setPassword(customer.getPassword());

		this.validator.validate(result, binding);

		return result;
	}

	public Customer reconstructEdit(final Customer customer, final BindingResult binding) {
		Customer storedCustomer;

		if (customer.getId() != 0) {
			storedCustomer = this.customerRepository.findOne(customer.getId());

			customer.setId(storedCustomer.getId());
			customer.setUserAccount(storedCustomer.getUserAccount());
			customer.setVersion(storedCustomer.getVersion());
			customer.setFolders(storedCustomer.getFolders());
			customer.setSubscriptions(storedCustomer.getSubscriptions());
			customer.setVolumeSubscriptions(storedCustomer.getVolumeSubscriptions());
		}

		this.validator.validate(customer, binding);

		return customer;
	}

}
