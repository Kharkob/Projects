package com.projects.service;

import com.projects.dao.ProjectsDao;
import com.projects.entity.Project;

public class projectservice {
    private static ProjectsDao  projectsDao = new ProjectsDao();
    
    
        public static Project addProject(Project  projects) {
            return projectsDao.insertProjects(projects);
    }
}
