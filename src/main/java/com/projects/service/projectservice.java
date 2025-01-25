package com.projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import com.projects.dao.ProjectsDao;
import com.projects.entity.Project;

public class  ProjectService {
    private  ProjectsDao  projectsDao = new ProjectsDao();
    
    
        public  Project addProject(Project  projects) {
            return projectsDao.insertProjects(projects);
    }
    public List<Project> fetchAllProjects() {
        return projectsDao.fetchAllProjects();
    }

    public Project fetchProjecById(Integer projectId) {
        return projectsDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException("Project with project ID=" + projectId + " does not exist."));
    }


      
}
