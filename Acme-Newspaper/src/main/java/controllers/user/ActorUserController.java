
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
import services.ChirpService;
import services.NewspaperService;
import services.UserService;
import services.VolumeService;
import controllers.AbstractController;
import domain.Actor;
import domain.Chirp;
import domain.User;
import domain.Volume;

@Controller
@RequestMapping("/actor")
public class ActorUserController extends AbstractController {

	@Autowired
	ActorService		actorService;

	@Autowired
	UserService			userService;

	@Autowired
	NewspaperService	newspaperService;

	@Autowired
	ChirpService	chirpService;

	@Autowired
	VolumeService	volumeService;


	//Edit User
	@RequestMapping(value = "/user/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(User user, final BindingResult binding) {
		ModelAndView result;
		Assert.isTrue(user.equals(this.actorService.findByPrincipal()));
		user = this.userService.reconstructEdit(user, binding);
		if (binding.hasErrors())
			result = this.createEditModelAndView(user);
		else
			try {
				this.userService.save(user);
				result = new ModelAndView("redirect:/actor/display-principal.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(user, "actor.commit.error");
			}
		return result;
	}

	//Delete an user
	@RequestMapping(value = "/user/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final int actorId) {
		ModelAndView result;
		final User user = this.userService.findOne(actorId);
		Assert.isTrue(user.equals(this.actorService.findByPrincipal()));
		try {
			for (Chirp c : this.chirpService.findAllByUser(user.getId())) {
				this.chirpService.delete(c);
			}
			for (User u : user.getFollowers()) {
				u.getFollowing().remove(user);
			}
			for (User u : user.getFollowing()) {
				u.getFollowers().remove(user);
			}
			for (Volume v : this.volumeService.findMyVolumes(user.getId())) {
				this.volumeService.delete(v);
			}
			this.userService.delete(user);
			result = new ModelAndView("redirect:../../j_spring_security_logout");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(user, "actor.commit.error");
		}
		return result;
	}

	//List following
	@RequestMapping(value = "/user/list-following", method = RequestMethod.GET)
	public ModelAndView listFollowing() {
		ModelAndView result;

		final User principal = this.userService.findByPrincipal();

		final Collection<User> users = this.userService.findFollowingByUser(principal);
		result = new ModelAndView("actor/list");
		result.addObject("users", users);
		result.addObject("principal", principal);

		final String requestURI = "/actor/user/list-following.do";
		result.addObject("requestURI", requestURI);

		return result;
	}

	//List followers
	@RequestMapping(value = "/user/list-followers", method = RequestMethod.GET)
	public ModelAndView listFollowers() {
		ModelAndView result;

		final User principal = this.userService.findByPrincipal();

		final Collection<User> users = this.userService.findFollowersByUser(principal);
		result = new ModelAndView("actor/list");
		result.addObject("users", users);
		result.addObject("principal", principal);

		final String requestURI = "/actor/user/list-followers.do";
		result.addObject("requestURI", requestURI);

		return result;
	}

	//Follow
	@RequestMapping(value = "/user/follow", method = RequestMethod.GET)
	public ModelAndView follow(final int userId) {
		ModelAndView result;

		final User principal = this.userService.findByPrincipal();
		final User user = this.userService.findOne(userId);

		final Collection<User> following = this.userService.findFollowingByUser(principal);
		following.add(user);
		principal.setFollowing(following);

		final Collection<User> followers = this.userService.findFollowersByUser(user);
		followers.add(principal);
		user.setFollowers(followers);

		this.userService.save(principal);
		this.userService.save(user);


		result = new ModelAndView("redirect:/actor/user/list-following.do");

		return result;
	}

	//Unfollow
	@RequestMapping(value = "/user/unfollow", method = RequestMethod.GET)
	public ModelAndView unfollow(final int userId) {
		ModelAndView result;

		final User principal = this.userService.findByPrincipal();
		final User user = this.userService.findOne(userId);

		final Collection<User> following = this.userService.findFollowingByUser(principal);
		following.remove(user);
		principal.setFollowing(following);

		final Collection<User> followers = this.userService.findFollowersByUser(user);
		followers.remove(principal);
		user.setFollowers(followers);

		this.userService.save(principal);
		this.userService.save(user);

		result = new ModelAndView("redirect:/actor/user/list-following.do");

		return result;
	}

	protected ModelAndView createEditModelAndView(final Actor actor) {
		ModelAndView result;
		result = this.createEditModelAndView(actor, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final Actor actor, final String message) {
		ModelAndView result;
		result = new ModelAndView("actor/edit");
		result.addObject(actor.getUserAccount().getAuthorities().toArray()[0].toString().toLowerCase(), actor);
		result.addObject("authority", actor.getUserAccount().getAuthorities().toArray()[0].toString().toLowerCase());
		result.addObject("message", message);
		result.addObject("actor", actor);
		return result;
	}
}
