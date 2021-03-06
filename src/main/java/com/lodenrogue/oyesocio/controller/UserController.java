package com.lodenrogue.oyesocio.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lodenrogue.oyesocio.model.Friendship;
import com.lodenrogue.oyesocio.model.Post;
import com.lodenrogue.oyesocio.model.User;
import com.lodenrogue.oyesocio.service.FriendshipFacade;
import com.lodenrogue.oyesocio.service.PostFacade;
import com.lodenrogue.oyesocio.service.UserFacade;

@RestController
public class UserController {

	/**
	 * Get user by id
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(path = "/api/users/{emailOrId:.*}", method = RequestMethod.GET)
	public User getUser(@PathVariable String emailOrId) {
		User user = null;
		try {
			long id = Long.valueOf(emailOrId);
			user = new UserFacade().find(id);
			if (user != null) {
				user.setFriends(getFriendIds(id));
				user.setPosts(getPosts(id));
				return user;
			}
		}
		catch (NumberFormatException e) {
			user = new UserFacade().findByEmail(emailOrId);
			if (user != null) {
				user.setFriends(getFriendIds(user.getId()));
				user.setPosts(getPosts(user.getId()));
				return user;
			}
		}

		return new User();
	}

	/**
	 * Create a user
	 * 
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param password
	 * @return
	 */
	@RequestMapping(path = "/api/users", method = RequestMethod.POST)
	//@formatter:off
	public User createUser(
			@RequestParam String firstName, 
			@RequestParam String lastName, 
			@RequestParam String email) {
	//@formatter:on

		// Find the user by email. If they don't exist then create a new
		// user
		User user = new UserFacade().findByEmail(email);
		if (user == null) {
			user = new User();
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmail(email);
			user = new UserFacade().create(user);
			return user;
		}
		else {
			return new User();
		}
	}

	private List<Long> getFriendIds(long userId) {
		List<Friendship> friends = new FriendshipFacade().findAllByUser(userId);
		List<Long> friendIds = new ArrayList<Long>();

		for (Friendship f : friends) {
			friendIds.add(f.getFriendId());
		}
		return friendIds;
	}

	private List<Long> getPosts(long userId) {
		List<Post> posts = new PostFacade().findAllByUser(userId);
		List<Long> postIds = new ArrayList<Long>();

		for (Post p : posts) {
			postIds.add(p.getId());
		}
		return postIds;
	}

}
