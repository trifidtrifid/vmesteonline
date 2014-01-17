package com.vmetsteonline.be.utils;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.apache.log4j.Logger;

import com.vmesteonline.be.Rubric;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoRubric;

public class Defaults {

	public static void setRubrics() {

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

	private static List<VoRubric> defaultRubrics;
	private static List<VoGroup> defaultGroups;

	static {
		defaultRubrics.add(new VoRubric("rubric1", "rubric first", "rubric about first"));
		defaultRubrics.add(new VoRubric("rubric2", "rubric second", "rubric about second"));
		defaultRubrics.add(new VoRubric("rubric3", "rubric third", "rubric about third"));
		defaultRubrics.add(new VoRubric("rubric4", "rubric fourth", "rubric about fourth"));

		defaultGroups.add(new VoGroup("Группа А", "Группа Альфа", "Описание группы Альфа"));
		defaultGroups.add(new VoGroup("Группа Б", "Группа Бетта", "Описание группы Бетта"));
		defaultGroups.add(new VoGroup("Группа Д", "Группа Дельта", "Описание группы Дельта"));
		defaultGroups.add(new VoGroup("Группа О", "Группа Омикрон", "Описание группы Омикрон"));

	}

	private static Logger logger = Logger.getLogger(Defaults.class);

}
