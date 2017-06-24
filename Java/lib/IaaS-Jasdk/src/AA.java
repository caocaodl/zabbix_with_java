import static com.isoft.iaas.openstack.OpsUtils.getOpenStackClientForAdmin;

import java.util.List;

import com.isoft.iaas.openstack.Configuration;
import com.isoft.iaas.openstack.IaaSClient;
import com.isoft.iaas.openstack.OpsUtils;
import com.isoft.iaas.openstack.base.client.OpenStackResponseException;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.Service;
import com.isoft.iaas.openstack.keystone.model.Services;
import com.isoft.iaas.openstack.keystone.model.Tenant;
import com.isoft.iaas.openstack.keystone.model.Tenants;
import com.isoft.iaas.openstack.keystone.model.User;
import com.isoft.iaas.openstack.keystone.model.Users;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;
import com.isoft.iaas.openstack.keystone.utils.KeystoneUtils;
import com.isoft.iaas.openstack.nova.Nova;
import com.isoft.iaas.openstack.nova.model.Server;
import com.isoft.iaas.openstack.nova.model.Servers;
import com.isoft.iaas.openstack.quantum.Quantum;
import com.isoft.iaas.openstack.quantum.model.Port;
import com.isoft.iaas.openstack.quantum.model.Ports;
import com.isoft.iaas.openstack.quantum.model.Router;
import com.isoft.iaas.openstack.quantum.model.Routers;
import com.isoft.iaas.openstack.quantum.model.Subnet;
import com.isoft.iaas.openstack.quantum.model.Subnets;


public class AA {

	public static void main(String[] args) {
//		bak(args);
		
		IaaSClient osClient = getOpenStackClientForAdmin();
		Keystone keystone = osClient.getIdentityClient();
		Users echoUsers = keystone.users().list().execute();
		List<User> osUsers = echoUsers.getList();
		for (User u : osUsers) {
			User osUser = keystone.users().show(u.getId()).execute();
		}
	}
	
	/**
	 * @param args
	 */
	public static void bak(String[] args) {
		
		

//		Keystone keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
//		Access access = keystone.tokens()
//				.authenticate(
//						new UsernamePassword(Configuration.KEYSTONE_USERNAME,
//								Configuration.KEYSTONE_PASSWORD))
//				.withTenantName(Configuration.KEYSTONE_ADMIN_TENANT_NAME).execute();
//		Access.User user = access.getUser();
////		System.out.println(user);
//		keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
//		keystone.token(access.getToken().getId());
//		Services echoServices = keystone.services().list().execute();
//		List<Service> services = echoServices.getList();
//		for(Service s:services){
////			System.out.println(s);
//		}
//		User user1 = keystone.users().show(user.getId()).execute();
////		System.out.println(user1);
//		
//		Tenants tenants = keystone.tenants().list().execute();
//		for(Tenant t: tenants.getList()) {
//			System.out.println(t);
//		}

		IaaSClient admClient = OpsUtils.getOpenStackClientForAdmin();
		
//		Nova novaClient = admClient.getComputeClient();
//		Servers servers = novaClient.servers().list(false).execute();
//		for (Server server : servers) {
//			server = novaClient.servers().show(server.getId()).execute();
//			System.out.println(server.getAddresses().getAddresses().get("test").get(0).getAddr());
//			System.out.println(server);
//		}
		
		Quantum networkClient = admClient.getNetworkClient();
		
		Routers routerEcho = networkClient.routers().list().execute();
		List<Router> routers = routerEcho.getList();
		for (Router router : routers) {
			System.out.println(router);
		}
		
		Subnets subnetEcho = networkClient.subnets().list().execute();
		List<Subnet> subnets = subnetEcho.getList();
		for(Subnet subnet : subnets){
			System.out.println(subnet);
		}
		
		Ports portEcho = networkClient.ports().list().execute();
		List<Port> ports = portEcho.getList();
		for(Port port : ports){
			System.out.println(port);
		}
	}

}
