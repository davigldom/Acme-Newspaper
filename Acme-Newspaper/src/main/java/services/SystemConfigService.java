
package services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.SystemConfigRepository;
import domain.Administrator;
import domain.SystemConfig;

@Service
@Transactional
public class SystemConfigService {

	@Autowired
	private SystemConfigRepository	systemConfigRepository;

	@Autowired
	private AdministratorService	administratorService;


	// CRUD METHODS

	public SystemConfig save(final SystemConfig sys) {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Assert.notNull(sys);

		return this.systemConfigRepository.save(sys);
	}

	// OTHER METHODS

	public SystemConfig findConfig() {
		SystemConfig sys;

		sys = this.systemConfigRepository.findAll().get(0);

		Assert.notNull(sys);

		return sys;
	}
}
