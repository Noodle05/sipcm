package com.mycallstation.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mycallstation.common.business.UserService;
import com.mycallstation.common.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/appContext-core.xml", "/appContext-datasource.xml",
		"/appContext-hibernate.xml", "/appContext-mailer.xml" })
public class UserServiceTest {
	@Resource(name = "userService")
	private UserService userService;

	@Test
	// @Ignore
	public void testCreateNew() {
		Collection<User> users = new ArrayList<User>(2);
		User user = userService.createNewEntity();
		user.setFirstName("Wei");
		user.setLastName("Gao");
		user.setUsername("wgao");
		user.setEmail("wei.j.gao@gmail.com");
		userService.setPassword(user, "P@ssw0rd");
		users.add(user);
		user = userService.createNewEntity();
		user.setFirstName("Lindsey");
		user.setLastName("George");
		user.setUsername("lgeorge");
		user.setEmail("lindsey.george@gmail.com");
		userService.setPassword(user, "P@ssw0rd");
		users.add(user);
		userService.saveEntities(users);
	}

	@Test
	@Ignore
	public void testSearchUsername() {
		User user = userService.getUserByUsername("wgao");
		assertNotNull(user);
		assertTrue(userService.matchPassword(user, "P@ssw0rd"));
	}

	@Test
	@Ignore
	public void testDelete() {
		User user = userService.getUserByUsername("wgao");
		assertNotNull(user);
		Long id = user.getId();
		userService.removeEntityById(id);
		user = null;
		Collection<User> users = userService.getEntities();
		assertEquals(users.size(), 1);
		user = userService.getEntityById(id);
		assertNotNull(user);
//		assertNotNull(user.getDeleteDate());
	}
}
