
package services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import security.UserAccount;
import security.UserAccountRepository;

@Service
@Transactional
public class UserAccountService {

	@Autowired
	private UserAccountRepository	userAccountRepository;


	public UserAccount create() {
		final UserAccount userAccount = new UserAccount();
		return userAccount;
	}

	public UserAccount save(final UserAccount userAccount) {
		Assert.notNull(userAccount);
		Assert.notNull(userAccount.getUsername());
		Assert.notNull(userAccount.getPassword());
		final UserAccount saved = this.userAccountRepository.save(userAccount);
		Assert.isTrue(this.userAccountRepository.findAll().contains(saved));
		return saved;

	}

}
