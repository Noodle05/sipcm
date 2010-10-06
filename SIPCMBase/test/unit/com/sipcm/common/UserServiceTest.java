package com.sipcm.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sipcm.common.business.UserService;
import com.sipcm.common.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/appContext-core.xml", "/appContext-datasource.xml",
		"/appContext-hibernate.xml" })
public class UserServiceTest {
	@Resource(name = "userService")
	private UserService userService;

	@Test
	public void testCreateNew() {
		Collection<User> users = new ArrayList<User>(2);
		User user = userService.createNewEntity();
		user.setFirstName("Wei");
		user.setLastName("Gao");
		user.setUsername("wgao");
		user.setEmail("wei.j.gao@gmail.com");
		user.setSipId("SCM00001");
		user.setSipPassword("DSY3NB");
		user.setPassword("P@ssw0rd");
		users.add(user);
		user = userService.createNewEntity();
		user.setFirstName("Lindsey");
		user.setLastName("George");
		user.setUsername("lgeorge");
		user.setEmail("lindsey.george@gmail.com");
		user.setSipId("SCM00002");
		user.setSipPassword("38NSFP");
		user.setPassword("P@ssw0rd");
		users.add(user);
		userService.saveEntities(users);
	}

	@Test
	public void testSearchUsername() {
		User user = userService.getUserByUsername("wgao");
		assertNotNull(user);
		assertEquals(user.getPassword(), "P@ssw0rd");
	}

	@Test
	public void testDelete() {
		User user = userService.getUserBySipId("SCM00001");
		assertNotNull(user);
		Long id = user.getId();
		userService.removeEntityById(id);
		user = null;
		Collection<User> users = userService.getEntities();
		assertEquals(users.size(), 1);
		user = userService.getEntityById(id);
		assertNotNull(user);
		assertNotNull(user.getDeleteDate());
	}
}
