
package services;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.AdvertisementRepository;
import domain.Advertisement;
import domain.Agent;
import domain.Newspaper;
import domain.NewspaperStatus;

@Service
@Transactional
public class AdvertisementService {

	@Autowired
	private AdvertisementRepository	advertisementRepository;

	@Autowired
	private AgentService			agentService;

	public Advertisement create() {
		final Advertisement result;

		result = new Advertisement();

		return result;
	}

	public Advertisement findOne(final int advertisementId) {
		Advertisement result;
		Assert.isTrue(advertisementId != 0);

		result = this.advertisementRepository.findOne(advertisementId);

		Assert.notNull(result);
		return result;
	}

	public Collection<Advertisement> findAll() {
		return this.advertisementRepository.findAll();
	}

	public Advertisement save(final Advertisement advertisement, Newspaper newspaper) {

		Advertisement result;
		final Agent principal = this.agentService.findByPrincipal();
		Assert.notNull(advertisement);
		Assert.notNull(newspaper);
		Assert.isTrue(advertisement.getId()==0);
		Assert.isTrue(newspaper.getStatus().equals(NewspaperStatus.PUBLISHED));
		Assert.isTrue(newspaper.getMakePrivate()==false);
		//Checking if the credit card's expiration month is not the current month
        Calendar fecha = new GregorianCalendar();
        int year = fecha.get(Calendar.YEAR);
        int month = fecha.get(Calendar.MONTH);
        if(advertisement.getExpirationYear()==year){
        	Assert.isTrue(advertisement.getExpirationMonth()!=month+1);
        }

		result = this.advertisementRepository.save(advertisement);

        //Add the advertisement to the agent's advertisements
		principal.getAdvertisements().add(result);
//		this.agentService.save(principal);



		//Add the advertisement to the newspaper's advertisements
		newspaper.getAdvertisements().add(result);
//		this.newspaperService.save(newspaper);

		return result;
	}

	public void delete(final Advertisement advertisement, Newspaper newspaper) {
		final Agent principal = this.advertisementRepository.findAgentCreator(advertisement.getId());
		Assert.notNull(advertisement);
		Assert.notNull(newspaper);

		principal.getAdvertisements().remove(advertisement);
		newspaper.getAdvertisements().remove(advertisement);

		this.advertisementRepository.delete(advertisement);
	}

	public Agent findAgentCreator(int advertisementId){
		return this.advertisementRepository.findAgentCreator(advertisementId);
	}

	public Newspaper findNewspaper(int advertisementId){
		return this.advertisementRepository.findNewspaper(advertisementId);
	}
	
	public Double findRatioNewspapersAdvertisementsVSNoAdvertisements(){
		return this.advertisementRepository.findRatioNewspapersAdvertisementsVSNoAdvertisements();
	}
	
	public Double findRatioAdvertisementsTaboo(){
		return this.advertisementRepository.findRatioAdvertisementsTaboo();
	}


//	private boolean isUrl(String s) {
//	    String regex = "^(https?://)?(([\\w!~*'().&=+$%-]+: )?[\\w!~*'().&=+$%-]+@)?(([0-9]{1,3}\\.){3}[0-9]{1,3}|([\\w!~*'()-]+\\.)*([\\w^-][\\w-]{0,61})?[\\w]\\.[a-z]{2,6})(:[0-9]{1,4})?((/*)|(/+[\\w!~*'().;?:@&=+$,%#-]+)+/*)$";
//
//	    try {
//	        Pattern patt = Pattern.compile(regex);
//	        Matcher matcher = patt.matcher(s);
//	        return matcher.matches();
//
//	    } catch (RuntimeException e) {
//	        return false;
//	    }
//	}



	public Advertisement findOneToEdit(final int advertisementId) {
		Advertisement result;
		final Agent principal = this.agentService.findByPrincipal();

		Assert.isTrue(advertisementId != 0);
		result = this.advertisementRepository.findOne(advertisementId);
		Assert.notNull(result);
		Assert.isTrue(principal.getAdvertisements().contains(result));

		return result;
	}

	public void flush() {
		this.advertisementRepository.flush();
	}


	// Reconstruct

	@Autowired
	private Validator	validator;


	public Advertisement reconstruct(final Advertisement advertisement,final BindingResult binding) {

		this.validator.validate(advertisement, binding);

		return advertisement;
	}

}
