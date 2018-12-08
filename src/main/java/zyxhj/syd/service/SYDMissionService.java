package zyxhj.syd.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.syd.domain.SYDMission;
import zyxhj.syd.repository.SYDMissionRepository;
import zyxhj.syd.repository.SYDProjectRepository;
import zyxhj.syd.repository.SYDRoleRepository;
import zyxhj.syd.repository.SYDStructureRepository;

public class SYDMissionService {

	private static Logger log = LoggerFactory.getLogger(SYDMissionService.class);

	private static SYDMissionService ins;

	public static synchronized SYDMissionService getInstance() {
		if (null == ins) {
			ins = new SYDMissionService();
		}
		return ins;
	}

	private SYDMissionRepository missionRepository;
	private SYDStructureRepository structureRepository;
	private SYDProjectRepository projectRepository;
	private SYDRoleRepository roleRepository;

	private SYDMissionService() {
		try {
			missionRepository = SYDMissionRepository.getInstance();
			structureRepository = SYDStructureRepository.getInstance();
			projectRepository = SYDProjectRepository.getInstance();
			roleRepository = roleRepository.getInstance();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 创建团体
	 */
	public SYDMission createMission(DruidPooledConnection conn) throws Exception {
		return null;
	}

	public SYDMission updateMission(DruidPooledConnection conn) throws Exception {
		return null;
	}

	public SYDMission createProject(DruidPooledConnection conn) throws Exception {
		return null;
	}

	public SYDMission updateProject(DruidPooledConnection conn) throws Exception {
		return null;
	}
}
