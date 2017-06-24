package com.isoft.iaas.openstack.keystone.api;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.keystone.model.User;
import com.isoft.iaas.openstack.keystone.model.Users;

public class UsersResource {

	private OpenStackClient client;

	public UsersResource(OpenStackClient client) {
		this.client = client;
	}

	public List list() {
		return new List();
	}

	public Create create(User user) {
		return new Create(user);
	}

	public Show show(String id) {
		return new Show(id);
	}

	public Update update(String id, User user) {
		return new Update(id, user);
	}

	public Delete delete(String id) {
		return new Delete(id);
	}

	public class List extends OpenStackRequest<Users> {

		public List() {
			super(client, HttpMethod.GET, "/users", null, Users.class);
		}

	}

	public class Create extends OpenStackRequest<User> {

		public Create(User user) {
			super(client, HttpMethod.POST, "/users", Entity.json(user),
					User.class);
		}

	}

	public class Show extends OpenStackRequest<User> {

		public Show(String id) {
			super(client, HttpMethod.GET, new StringBuilder("/users/").append(
					id).toString(), null, User.class);
		}

	}

	public class Update extends OpenStackRequest<User> {

		public Update(String id, User user) {
			super(client, HttpMethod.PUT, new StringBuilder("/users/").append(
					id).toString(), Entity.json(user), User.class);
		}

	}

	public class Delete extends OpenStackRequest<Void> {

		public Delete(String id) {
			super(client, HttpMethod.DELETE, new StringBuilder("/users/")
					.append(id).toString(), null, Void.class);
		}

	}

}
