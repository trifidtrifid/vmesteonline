package com.vmetsteonline.be.utils;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.apache.log4j.Logger;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoUserGroup;

public class Defaults {

	public static void setGroups() {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		for (VoRubric r : defaultRubrics) {
			javax.jdo.Query q = pm.newQuery(VoRubric.class);
			q.setFilter("visibleName == param");
			q.declareParameters("String param");
			List<VoRubric> rbcs = (List<VoRubric>) q.execute(r.getVisibleName());
			if (rbcs.isEmpty()) {
				pm.makePersistent(r);
			}
		}
	}

	public static List<VoRubric> defaultRubrics;
	private static List<VoGroup> defaultGroups;
	public static List<VoUserGroup> defaultUserGroups;

	static {

		defaultRubrics = new ArrayList<VoRubric>();
		defaultRubrics.add(new VoRubric("rubric1", "rubric first", "rubric about first"));
		defaultRubrics.add(new VoRubric("rubric2", "rubric second", "rubric about second"));
		defaultRubrics.add(new VoRubric("rubric3", "rubric third", "rubric about third"));
		defaultRubrics.add(new VoRubric("rubric4", "rubric fourth", "rubric about fourth"));

		defaultGroups = new ArrayList<VoGroup>();
		defaultGroups.add(new VoGroup("Группа А", "Группа Альфа", "Описание группы Альфа", 10L, 15L, 0));
		defaultGroups.add(new VoGroup("Группа Б", "Группа Бетта", "Описание группы Бетта", 20L, 25L, 0));
		defaultGroups.add(new VoGroup("Группа Д", "Группа Дельта", "Описание группы Дельта", 30L, 35L, 0));
		defaultGroups.add(new VoGroup("Группа О", "Группа Омикрон", "Описание группы Омикрон", 40L, 45L, 0));

		defaultUserGroups = new ArrayList<VoUserGroup>();
		defaultUserGroups.add(new VoUserGroup("Ближайшие", 200));
		defaultUserGroups.add(new VoUserGroup("Далекие", 2000));
		defaultUserGroups.add(new VoUserGroup("Дальнейшие", 5000));

	}

	private static Logger logger = Logger.getLogger(Defaults.class);

}
