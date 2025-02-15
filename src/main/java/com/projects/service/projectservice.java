package com.projects.service;

import java.util.List;


import java.util.NoSuchElementException;


import com.projects.dao.ProjectsDao;
import com.projects.entity.Material;
import com.projects.entity.Project;
import com.projects.entity.Step;
import com.projects.exception.DbException;

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

    public void modifyProjectDetails(Project project) {
        if(!projectsDao.modifyProjectDetails(project)) {
            throw new DbException("Project with ID=" + project.getProjectId() + " does not exist.");
        }
    }


            public void deleteProject(Integer projectId) {
                if(!projectsDao.deleteProject(projectId)) {
                    throw new DbException("Project with ID=" + projectId + " does not exist.");
                }
            }
            public void addStep(Step step) {
               projectsDao.addStepToProject(step);
            }
            /* 
            public List<Unit> fetchUnits() {
               return projectsDao.fetchAllUnits();
            }
            */
            public void addMaterial(Material material) {
                projectsDao.addMaterialToProject(material);
            }
            /* 
            public List<Category> fetchCategories() {
             return projectsDao.fetchAllCategories();
            } */
            public void addCategoryToProject(Integer projectId, String category) {
               projectsDao.addCategoryToProject(projectId, category);
            }
}
      

