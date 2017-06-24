package com.isoft.iaas.openstack.ceilometer.v2.api;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.ceilometer.v2.model.Alarm;
import com.isoft.iaas.openstack.ceilometer.v2.model.AlarmCombination;
import com.isoft.iaas.openstack.ceilometer.v2.model.AlarmHistory;
import com.isoft.iaas.openstack.ceilometer.v2.model.Query;

public class AlarmsResource {

	private final OpenStackClient CLIENT;

	public AlarmsResource(OpenStackClient client) {
		CLIENT = client;
	}
	
	public Create create(Alarm alarm) {
		return new Create(alarm);
	}
	
	public class Create extends OpenStackRequest<Alarm> {

		public Create(Alarm alarm) {
			super(CLIENT, HttpMethod.POST, "/alarms", Entity.json(alarm), Alarm.class);
		}

	}
	
	public CreateCombination createCombination(AlarmCombination alarm) {
		return new CreateCombination(alarm);
	}
	
	public class CreateCombination extends OpenStackRequest<AlarmCombination> {

		public CreateCombination(AlarmCombination alarm) {
			super(CLIENT, HttpMethod.POST, "/alarms", Entity.json(alarm), AlarmCombination.class);
		}

	}
	
	public Update update(String id, Alarm alarm) {
		return new Update(id, alarm);
	}
	
	public class Update extends OpenStackRequest<Alarm> {

		public Update(String id, Alarm alarm) {
			super(CLIENT, HttpMethod.PUT, 
					new StringBuilder("/alarms/").append(id), 
					Entity.json(alarm), Alarm.class);
		}

	}
	
	public UpdateCombination updateCombination(String id, AlarmCombination alarm) {
		return new UpdateCombination(id, alarm);
	}
	
	public class UpdateCombination extends OpenStackRequest<AlarmCombination> {

		public UpdateCombination(String id, AlarmCombination alarm) {
			super(CLIENT, HttpMethod.PUT, 
					new StringBuilder("/alarms/").append(id), 
					Entity.json(alarm), AlarmCombination.class);
		}

	}

	public List list(Query... queries) {
		List list = new List();
		if (queries != null && queries.length > 0) {
			for (Query q : queries) {
				q.marshal(list);
			}
		}
		return list;
	}
		
	public class List extends OpenStackRequest<Alarm[]> {

		public List() {
			super(CLIENT, HttpMethod.GET, "/alarms",
					null, Alarm[].class);
		}

	}
	
	public Show show(String id) {
		return new Show(id);
	}
	
	public class Show extends OpenStackRequest<Alarm> {

		public Show(String id) {
			super(CLIENT, HttpMethod.GET, 
					new StringBuilder("/alarms/").append(id),
					null, Alarm.class);
		}

	}
	
	public Delete delete(String id) {
		return new Delete(id);
	}
	
	public class Delete extends OpenStackRequest<Void> {

		public Delete(String id) {
			super(CLIENT, HttpMethod.DELETE, 
					new StringBuilder("/alarms/").append(id),
					null, Void.class);
		}

	}
	
	public GetState getstate(String id) {
		return new GetState(id);
	}
	
	public class GetState extends OpenStackRequest<String> {

		public GetState(String id) {
			super(CLIENT, HttpMethod.GET, 
					new StringBuilder("/alarms/").append(id).append("/state"),
					null, String.class);
		}

	}
	
	public SetState setstate(String id, String state) {
		return new SetState(id, state);
	}
		
	public class SetState extends OpenStackRequest<String> {

		public SetState(String id, String state) {
			super(CLIENT, HttpMethod.PUT, 
					new StringBuilder("/alarms/").append(id).append("/state").toString(),
					Entity.json('"'+state+'"'), String.class);
		}

	}
	
	public History history(String id, Query... queries) {
		History list = new History(id);
		if (queries != null && queries.length > 0) {
			for (Query q : queries) {
				q.marshal(list);
			}
		}
		return list;
	}
	
	public class History extends OpenStackRequest<AlarmHistory[]> {

		public History(String id) {
			super(CLIENT, HttpMethod.GET, 
					new StringBuilder("/alarms/").append(id).append("/history"),
					null, AlarmHistory[].class);
		}

	}

}
