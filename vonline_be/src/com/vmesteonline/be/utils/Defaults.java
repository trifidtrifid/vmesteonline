package com.vmesteonline.be.utils;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.log4j.Logger;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoRubric;

public class Defaults {
	
	public static List<VoGroup> defaultGroups;
	public static List<VoRubric> defaultRubrics;

	static {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		defaultRubrics = new ArrayList<VoRubric>();
		try {
			Query q = pm.newQuery(VoRubric.class);
			q.setFilter(" subscribedByDefault == true");
			List<VoRubric> defRubrics = (List<VoRubric>) q.execute();
			if( defRubrics.isEmpty()) {
				for (VoRubric dr: new VoRubric[] {  
					new VoRubric("rubric1", "rubric first", "rubric about first",true),
					new VoRubric("rubric2", "rubric second", "rubric about second",true),
					new VoRubric("rubric3", "rubric third", "rubric about third",true),
					new VoRubric("rubric4", "rubric fourth", "rubric about fourth",true)}){
					
					pm.makePersistent(dr);
					defaultRubrics.add(dr);
				}
			}
			q.closeAll();
			
			defaultGroups = new ArrayList<VoGroup>();
			q = pm.newQuery(VoGroup.class);
			q.setFilter("subscribedByDefault == true");
			List<VoGroup> defGroups = (List<VoGroup>) q.execute();
			 
			for (VoGroup dg: new VoGroup[] {  
					new VoGroup("Мой дом", 0, true),
					new VoGroup("Соседи", 200, true),
					new VoGroup("Пешая доступность", 2000, true),
					new VoGroup("Быстро Доехать", 5000, true)}){
				
				defaultGroups.add(dg);
				if(defGroups.isEmpty()) pm.makePersistent(dg);
				
			}
		} finally{
			pm.close();
		}	
	}

	private static Logger logger = Logger.getLogger(Defaults.class);

}
