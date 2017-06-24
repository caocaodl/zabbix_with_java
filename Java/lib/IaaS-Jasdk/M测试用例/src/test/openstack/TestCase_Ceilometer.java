package test.openstack;

import java.util.List;

import com.isoft.iaas.openstack.ceilometer.Ceilometer;
import com.isoft.iaas.openstack.ceilometer.v2.model.Alarm;
import com.isoft.iaas.openstack.ceilometer.v2.model.AlarmCombination;
import com.isoft.iaas.openstack.ceilometer.v2.model.AlarmCombinationRule;
import com.isoft.iaas.openstack.ceilometer.v2.model.AlarmHistory;
import com.isoft.iaas.openstack.ceilometer.v2.model.AlarmThresholdRule;
import com.isoft.iaas.openstack.ceilometer.v2.model.Meter;
import com.isoft.iaas.openstack.ceilometer.v2.model.Query;
import com.isoft.iaas.openstack.ceilometer.v2.model.Resource;
import com.isoft.iaas.openstack.ceilometer.v2.model.Sample;
import com.isoft.iaas.openstack.ceilometer.v2.model.Statistic;
import com.isoft.iaas.openstack.keystone.model.Access.Facing;
import com.isoft.iaas.openstack.keystone.model.Access.Service;
import com.isoft.iaas.openstack.keystone.utils.KeystoneUtils;
import com.isoft.test.Mapper;

public class TestCase_Ceilometer extends BaseTestCase {

	private Ceilometer client;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		List<Service> serviceCatalogs = access.getServiceCatalog();
		String endpoint = KeystoneUtils.findEndpointURL(serviceCatalogs,
				"metering", "RegionOne", Facing.PUBLIC);
		System.out.println("publicURL => " + endpoint);

		this.client = new Ceilometer(endpoint);
		this.client.token(access.getToken().getId());
	}
	
	public void test_alarms_create() throws Exception {
		Alarm alarm = new Alarm();
		alarm.setName("T"+System.currentTimeMillis());
		alarm.setDescription("test"+System.currentTimeMillis());
		alarm.setState("ok");
		alarm.setEnabled(true);
		alarm.setAlarmActions(new String[]{"http://a.com/alarm"});
		alarm.setOkActions(new String[]{"http://a.com/ok"});
		alarm.setInsufficientDataActions(new String[]{"http://a.com/ida"});
		AlarmThresholdRule thresholdRule = new AlarmThresholdRule();
		thresholdRule.setMeterName("memory");
		thresholdRule.setPeriod(20);
		thresholdRule.setEvaluationPeriods(2);
		thresholdRule.setStatistic("max");
		thresholdRule.setComparisonOperator("gt");
		thresholdRule.setThreshold(60);
		thresholdRule.setQuery(new Query[]{Query.eq("source", "openstack")});
		alarm.setThresholdRule(thresholdRule );
		alarm.setRepeatActions(true);
		alarm.setType("threshold");

		alarm = client.alarms().create(alarm).execute();
		Mapper.f(alarm);
	}

	public void test_meter_list() throws Exception {
		Meter[] meters = client.meters().list().execute();
		for (Meter e : meters) {
			Mapper.f(e);
		}
	}
	
	public void test_resource_list() throws Exception {
		Resource[] resources = client.resources().list().execute();
		for (Resource e : resources) {
			Mapper.f(e);
		}
	}
	
	public void test_resource_show() throws Exception {
		Resource[] resources = client.resources().list().execute();
		for (Resource e : resources) {
			Resource r = client.resources().show(e.getResourceid()).execute();
			Mapper.f(r);
		}
	}
	
	public void test_meter_statistics() throws Exception {
		Meter[] meters = client.meters().list().execute();
		for (Meter e : meters) {
			Statistic[] stats = client.meters().statistics(e.getName()).execute();
			for (Statistic s : stats) {
				Mapper.f(s);
			}			
		}
	}
	
	public void test_meter_statistics_with_queries() throws Exception {
		Meter[] meters = client.meters().list().execute();
		for (Meter e : meters) {
			Statistic[] stats = client.meters().statistics(e.getName(), Query.eq("start", "2014-06-26T17:39:59")).execute();
			for (Statistic s : stats) {
				Mapper.f(s);
			}			
		}
	}
	
	public void test_meter_samples_list() throws Exception {
		Meter[] meters = client.meters().list().execute();
		for (Meter e : meters) {
			Sample[] samples = client.meters().samples(e.getName()).execute();
			for (Sample s : samples) {
				Mapper.f(s);
			}			
		}
	}
	
	public void test_meter_samples_with_queries() throws Exception {
		Meter[] meters = client.meters().list().execute();
		for (Meter e : meters) {
			Sample[] samples = client.meters().samples(e.getName(), Query.eq("start", "2014-06-26T17:39:59")).limit(2).execute();
			for (Sample s : samples) {
				Mapper.f(s);
			}			
		}
	}
	
	public void test_alarms_create_combination() throws Exception {
		Alarm[] alarms = client.alarms().list().execute();
		String[] alarmids = new String[alarms.length];
		for (int i=0;i<alarms.length;i++) {
			alarmids[i] = alarms[i].getId();
		}
		AlarmCombination ac = new AlarmCombination();
		
		ac.setName("C_"+System.currentTimeMillis());
		ac.setDescription("hello");
		ac.setState("ok");
		ac.setEnabled(true);
		ac.setAlarmActions(new String[]{"http://a.com/alarm"});
		ac.setOkActions(new String[]{"http://a.com/ok"});
		ac.setInsufficientDataActions(new String[]{"http://a.com/ida"});
		ac.setRepeatActions(true);
		
		
		AlarmCombinationRule cr = new AlarmCombinationRule();
		cr.setOperator("and");
		cr.setAlarmids(alarmids);
		ac.setCombinationRule(cr);
		ac.setType("combination");
		
		ac = client.alarms().createCombination(ac).execute();
		Mapper.f(ac);
	}
	
	public void test_alarms_update() throws Exception {
		Alarm[] alarms = client.alarms().list().execute();
		int i =0;
		for (Alarm e : alarms) {
			Alarm alarm = new Alarm();
			alarm.setName("T_"+System.currentTimeMillis()+"_"+(++i));
			alarm.setDescription("test"+System.currentTimeMillis());
			alarm.setState("ok");
			alarm.setEnabled(true);
			alarm.setAlarmActions(new String[]{"http://a.com/alarm"});
			alarm.setOkActions(new String[]{"http://a.com/ok"});
			alarm.setInsufficientDataActions(new String[]{"http://a.com/ida"});
			AlarmThresholdRule thresholdRule = new AlarmThresholdRule();
			thresholdRule.setMeterName("memory");
			thresholdRule.setPeriod(20);
			thresholdRule.setEvaluationPeriods(2);
			thresholdRule.setStatistic("max");
			thresholdRule.setComparisonOperator("gt");
			thresholdRule.setThreshold(60);
			thresholdRule.setQuery(new Query[]{Query.eq("source", "openstack")});
			alarm.setThresholdRule(thresholdRule );
			alarm.setRepeatActions(true);
			alarm.setType("threshold");

			alarm = client.alarms().update(e.getId(), alarm).execute();
			Mapper.f(alarm);
		}
	}
	
	public void test_alarms_update_combination() throws Exception {
		Alarm[] alarms = client.alarms().list().execute();
		String[] alarmids = new String[alarms.length];
		for (int i=0;i<alarms.length;i++) {
			alarmids[i] = alarms[i].getId();
		}
		AlarmCombination ac = new AlarmCombination();
		
		ac.setName("C_"+System.currentTimeMillis());
		ac.setDescription("hello");
		ac.setState("ok");
		ac.setEnabled(true);
		ac.setAlarmActions(new String[]{"http://a.com/alarm"});
		ac.setOkActions(new String[]{"http://a.com/ok"});
		ac.setInsufficientDataActions(new String[]{"http://a.com/ida"});
		ac.setRepeatActions(true);
		
		
		AlarmCombinationRule cr = new AlarmCombinationRule();
		cr.setOperator("and");
		cr.setAlarmids(alarmids);
		ac.setCombinationRule(cr);
		ac.setType("combination");
		
		ac = client.alarms().createCombination(ac).execute();
		ac = client.alarms().updateCombination(ac.getId(),ac).execute();
		Mapper.f(ac);
	}
	
	public void test_alarms_list() throws Exception {
		Alarm[] alarms = client.alarms().list().execute();
		for (Alarm e : alarms) {
			Mapper.f(e);
		}
	}
	
	public void test_alarms_show() throws Exception {
		Alarm[] alarms = client.alarms().list().execute();
		for (Alarm e : alarms) {
			Alarm r = client.alarms().show(e.getId()).execute();
			Mapper.f(r);
		}
	}
	
	public void test_alarms_getstate() throws Exception {
		Alarm[] alarms = client.alarms().list().execute();
		for (Alarm e : alarms) {
			String r = client.alarms().getstate(e.getId()).execute();
			Mapper.f(r);
		}
	}
	
	public void test_alarms_setstate() throws Exception {
		Alarm[] alarms = client.alarms().list().execute();
		for (Alarm e : alarms) {
			String r = client.alarms().setstate(e.getId(),"ok").execute();
			Mapper.f(r);
		}
	}
	
	public void test_alarms_history() throws Exception {
		Alarm[] alarms = client.alarms().list().execute();
		for (Alarm e : alarms) {
			AlarmHistory[] hises = client.alarms().history(e.getId()).execute();
			for (AlarmHistory s : hises) {
				Mapper.f(s);
			}
		}
	}
	
	public void test_alarms_delete() throws Exception {
		Alarm[] alarms = client.alarms().list().execute();
		for (Alarm e : alarms) {
			client.alarms().delete(e.getId()).execute();
		}
	}
}
