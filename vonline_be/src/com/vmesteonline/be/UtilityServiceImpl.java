package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.jdo.PersistenceManager;

import org.apache.thrift.TException;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.utility.VoCounter;
import com.vmesteonline.be.userservice.Counter;
import com.vmesteonline.be.userservice.UtilityService.Iface;

public class UtilityServiceImpl extends ServiceImpl implements Iface {

	@Override
	public long registerCounter(Counter newCounter) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		VoUser currentUser = getCurrentUser(pm);
		VoCounter cntr = new VoCounter(newCounter.getType(), newCounter.getLocation(), 
				newCounter.getNumber(), currentUser.getAddress());
		pm.makePersistent(cntr);
		return cntr.getId();
	}
	
	

	@Override
	public void updateCounter(Counter updatedCounter) throws InvalidOperation, TException {
		if(0!=updatedCounter.getId()){
			PersistenceManager pm = PMF.getPm();
			try {
				VoCounter cntr = pm.getObjectById(VoCounter.class, updatedCounter.getId());
				cntr.setNumber( updatedCounter.getNumber());
				cntr.setType( updatedCounter.getType());
				cntr.setLocation(updatedCounter.getLocation());
				pm.makePersistent(cntr);
			} catch (Exception e) {
			}
		}
		throw new InvalidOperation(VoError.IncorrectParametrs, "No counter found by id: "+updatedCounter.getId());
	}



	@Override
	public void removeCounter(int counterId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			pm.deletePersistent( pm.getObjectById(VoCounter.class, counterId));
		} catch (Exception e) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "No counter found by id: "+counterId);
		}
	}



	@Override
	public List<Counter> getCounters() throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		List<VoCounter> counters = (List<VoCounter>) pm.newQuery(VoCounter.class, "postalAddressId=="+getCurrentUser(pm).getAddress()).execute();
		List<Counter> outList = new ArrayList<>();
		for (VoCounter voCounter : counters) {
			outList.add( voCounter.getCounter());
		}
		return outList;
	}

	@Override
	public Map<Integer, Double> getCounterHistory(int counterId, int fromDate, int toDate) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoCounter cntr = pm.getObjectById(VoCounter.class, counterId);
			if( fromDate > toDate && toDate != 0 )
				return new HashMap<Integer, Double>();
			return cntr.getValues().subMap(fromDate, 0==toDate ? Integer.MAX_VALUE : toDate);
		} catch (Exception e) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "No counter found by id: "+counterId);
		}
	}

	@Override
	public double setCurrentCounterValue(long counterId, double counterValue, int date) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoCounter cntr = pm.getObjectById(VoCounter.class, counterId);
			SortedMap<Integer, Double> values = cntr.getValues();
			double delta = 0.0;
			
			if( null!=values && values.size() > 0 && values.lastKey() < date )
				delta = counterValue - values.get(values.lastKey());
			
			values.put(date, counterValue);
			pm.makePersistent(cntr);
			return delta;
		} catch (Exception e) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "No counter found by id: "+counterId);
		}
		
	}
	
	public UtilityServiceImpl() {
	}

	public UtilityServiceImpl(String sessId) {
		super(sessId);
	}

}
