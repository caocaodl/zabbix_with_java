package com.isoft.iaas.openstack.cinder.v2;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.cinder.v2.model.EncryptionType;
import com.isoft.iaas.openstack.cinder.v2.model.EncryptionTypeForCreate;
import com.isoft.iaas.openstack.cinder.v2.model.ExtraSpecs;
import com.isoft.iaas.openstack.cinder.v2.model.Type;
import com.isoft.iaas.openstack.cinder.v2.model.Types;
import com.isoft.iaas.openstack.cinder.v2.model.Volume;

public class TypesResource {

	private final OpenStackClient CLIENT;

	public TypesResource(OpenStackClient client) {
		CLIENT = client;
	}

	public List list() {
		return new List();
	}

	public Create create(Type type) {
		return new Create(type);
	}

	public Update update(String id, Volume image) {
		return new Update(id, image);
	}
	
	public ExtraSpecsResource extraspecs(String id){
		return new ExtraSpecsResource(id);
	}
	
	public EncryptionTypesResource encryptiontypes(String id){
		return new EncryptionTypesResource(id);
	}

	public Delete delete(String id) {
		return new Delete(id);
	}

	public class List extends OpenStackRequest<Types> {

		public List() {
			super(CLIENT, HttpMethod.GET, "/types", null, Types.class);
		}

	}

	public class Create extends OpenStackRequest<Type> {

		public Create(Type type) {
			super(CLIENT, HttpMethod.POST, "/types", Entity.json(type),
					Type.class);
		}

	}

	public class Update extends OpenStackRequest<Volume> {

		public Update(String id, Volume volume) {
			super(CLIENT, HttpMethod.PUT, new StringBuilder("/types/").append(
					id).toString(), Entity.json(volume), Volume.class);
		}

	}

	public class Delete extends OpenStackRequest<Void> {

		public Delete(String id) {
			super(CLIENT, HttpMethod.DELETE, new StringBuilder("/types/")
					.append(id).toString(), null, Void.class);
		}

	}

	public class ExtraSpecsResource {

		private final String id;

		private ExtraSpecsResource(String id) {
			this.id = id;
		}

		public Set set(ExtraSpecs specs) {
			return new Set(id, specs);
		}

		public Unset unset(String key) {
			return new Unset(id, key);
		}

		public List list() {
			return new List(id);
		}

		public class Set extends OpenStackRequest<ExtraSpecs> {

			public Set(String id, ExtraSpecs volume) {
				super(CLIENT, HttpMethod.POST, new StringBuilder("/types/")
						.append(id).append("/extra_specs"),
						Entity.json(volume), ExtraSpecs.class);
			}

		}

		public class Unset extends OpenStackRequest<Void> {

			public Unset(String id, String key) {
				super(CLIENT, HttpMethod.DELETE, new StringBuilder("/types/")
						.append(id).append("/extra_specs/").append(key), null,
						Void.class);
			}

		}

		public class List extends OpenStackRequest<ExtraSpecs> {

			public List(String id) {
				super(CLIENT, HttpMethod.GET, new StringBuilder("/types/")
						.append(id).append("/extra_specs"), null,
						ExtraSpecs.class);
			}

		}
	}

	public class EncryptionTypesResource {

		private final String id;

		private EncryptionTypesResource(String id) {
			this.id = id;
		}

		public Create set(EncryptionTypeForCreate encryType) {
			return new Create(id, encryType);
		}

		public Delete delete() {
			return new Delete(id);
		}

		public Show show() {
			return new Show(id);
		}

		public class Create extends OpenStackRequest<EncryptionTypeForCreate> {

			public Create(String id, EncryptionTypeForCreate encryType) {
				super(CLIENT, HttpMethod.POST, new StringBuilder("/types/")
						.append(id).append("/encryption"), Entity
						.json(encryType), EncryptionTypeForCreate.class);
			}

		}

		public class Delete extends OpenStackRequest<Void> {

			public Delete(String id) {
				super(CLIENT, HttpMethod.DELETE, new StringBuilder("/types/")
						.append(id).append("/encryption/provider"), null,
						Void.class);
			}

		}

		public class Show extends OpenStackRequest<EncryptionType> {

			public Show(String id) {
				super(CLIENT, HttpMethod.GET, new StringBuilder("/types/")
						.append(id).append("/encryption"), null,
						EncryptionType.class);
			}

		}
	}
}
