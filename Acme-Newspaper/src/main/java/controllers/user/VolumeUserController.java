
package controllers.user;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.NewspaperService;
import services.SubscriptionService;
import services.UserService;
import services.VolumeService;
import controllers.AbstractController;
import domain.Actor;
import domain.Newspaper;
import domain.NewspaperStatus;
import domain.Subscription;
import domain.Volume;
import domain.VolumeSubscription;

@Controller
@RequestMapping("/volume/user")
public class VolumeUserController extends AbstractController {

	@Autowired
	private VolumeService		volumeService;

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private ActorService		actorService;

	@Autowired
	private UserService			userService;

	@Autowired
	private SubscriptionService	subscriptionService;


	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result = null;
		final Actor principal = this.actorService.findByPrincipal();

		result = new ModelAndView("volume/list");
		final Collection<Volume> volumes = this.volumeService.findMyVolumes(principal.getId());
		result.addObject("volumes", volumes);
		result.addObject("requestURI", "volume/user/list.do");

		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		final Volume volume;
		volume = this.volumeService.create();
		result = this.createEditModelAndView(volume);

		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Volume volume, final BindingResult binding) {
		ModelAndView result;

		volume = this.volumeService.reconstruct(volume, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(volume);
		else
			try {
				this.volumeService.save(volume);
				result = new ModelAndView("redirect:/volume/user/list.do?");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(volume, "volume.commit.error");
			}
		return result;
	}

	@RequestMapping(value = "/add-newspaper", method = RequestMethod.GET)
	public ModelAndView addNewspaper(final int newspaperId, final int volumeId) {
		ModelAndView result;

		final Volume volume;
		Newspaper newspaper;

		volume = this.volumeService.findOne(volumeId);
		newspaper = this.newspaperService.findOne(newspaperId);

		Assert.isTrue(newspaper.getPublisher().equals(this.userService.findByPrincipal()));
		Assert.isTrue(volume.getPublisher().equals(this.userService.findByPrincipal()));
		Assert.isTrue(newspaper.getStatus().equals(NewspaperStatus.PUBLISHED));
		Assert.isTrue(!volume.getNewspapers().contains(newspaper));

		final Collection<Newspaper> newspapers = volume.getNewspapers();

		try {
			newspapers.add(newspaper);
			result = new ModelAndView("redirect:/volume/user/list.do?");
		} catch (final Throwable oops) {
			result = this.addRemoveModelAndView(volume, "volume.commit.error");
		}

		volume.setNewspapers(newspapers);
		this.volumeService.save(volume);
		if (newspaper.getMakePrivate())
			for (final VolumeSubscription vs : volume.getVolumeSubscriptions()) {

				final Collection<Newspaper> newspapersSubscribed = this.newspaperService.findAlreadySubscribedByCustomer(vs.getSubscriber());

				if (!newspapersSubscribed.contains(newspaper)) {
					final Subscription s = this.subscriptionService.create(newspaper.getId());
					s.setCreditCardNumber(vs.getCreditCardNumber());
					s.setExpirationMonth(vs.getExpirationMonth());
					s.setExpirationYear(vs.getExpirationYear());
					s.setNewspaper(newspaper);
					s.setSecurityCode(vs.getSecurityCode());
					s.setSubscriber(vs.getSubscriber());
					this.subscriptionService.saveFromVolume(s, vs.getSubscriber());
				}
			}

		return result;
	}

	@RequestMapping(value = "/remove-newspaper", method = RequestMethod.GET)
	public ModelAndView removeNewspaper(final int newspaperId, final int volumeId) {
		final Volume volume;
		Newspaper newspaper;

		volume = this.volumeService.findOne(volumeId);
		newspaper = this.newspaperService.findOne(newspaperId);

		Assert.isTrue(newspaper.getPublisher().equals(this.userService.findByPrincipal()));
		Assert.isTrue(volume.getPublisher().equals(this.userService.findByPrincipal()));
		Assert.isTrue(volume.getNewspapers().contains(newspaper));

		final Collection<Newspaper> newspapers = volume.getNewspapers();
		newspapers.remove(newspaper);

		volume.setNewspapers(newspapers);
		this.volumeService.save(volume);

		return this.display();
	}

	protected ModelAndView createEditModelAndView(final Volume volume) {
		ModelAndView result;

		result = this.createEditModelAndView(volume, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Volume volume, final String message) {
		ModelAndView result = null;

		result = new ModelAndView("volume/edit");
		result.addObject("volume", volume);
		result.addObject("message", message);

		return result;
	}

	protected ModelAndView addRemoveModelAndView(final Volume volume, final String message) {
		ModelAndView result = null;

		result = new ModelAndView("newspaper/list");
		result.addObject("volume", volume);
		result.addObject("message", message);

		return result;
	}

}
