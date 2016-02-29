package ixa.time;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.razorvine.pyro.*;

public class crfClient {

	
	public void train (List<List<Hashtable<String, String>>> dataset_X, List<List<String>> dataset_Y, String modelFileBase)
	{
		NameServerProxy ns;
		try {			
			ns = NameServerProxy.locateNS("localhost");
			PyroProxy remoteobject = new PyroProxy(ns.lookup("mlServer"));
			remoteobject.call("crfTrain", dataset_X, dataset_Y, modelFileBase);
			remoteobject.close();
			ns.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<List<String>> tag (List<List<Hashtable<String, String>>> dataset_X, String modelFileBase)
	{
		List<List<String>> prediction = new ArrayList<List<String>>();
		NameServerProxy ns;
		try {			
			ns = NameServerProxy.locateNS("localhost");
			PyroProxy remoteobject = new PyroProxy(ns.lookup("mlServer"));
			Object result = remoteobject.call("crfTag", dataset_X, modelFileBase);
			prediction = (List<List<String>>) result;
			remoteobject.close();
			ns.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return prediction;
	}
}
