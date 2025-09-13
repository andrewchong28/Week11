package projects.service;

import java.util.List;

import java.util.NoSuchElementException;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {

    // Declare projectDao as an instance variable
    private ProjectDao projectDao = new ProjectDao();

    public Project addProject(Project project) {
        return projectDao.insertProject(project);
    }

    //fetch all projects
    public List<Project> fetchAllProjects() {
        return projectDao.fetchAllProjects();
    }

    // fetch project by ID
    public Project fetchProjectById(Integer projectId) {
        return projectDao.fetchProjectById(projectId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Project with project ID = " + projectId + " does not exist."));
    }

    
 //Changes to Project Service 1.a   
    //if returns !projectDao.modifyProjectDetails(project) == false that means update failed
    //then ! flips it to a true condition to trigger the throw exception
	public void modifyProjectDetails(Project project) {
		if(!projectDao.modifyProjectDetails(project)) {
			throw new DbException("Project with ID="
					+ project.getProjectId() + "does not exist.");
		}
		
	}

	public void deleteProject(Integer projectId) {
		 if(!projectDao.deleteProject(projectId)) {
			 throw new DbException("Project with ID= " + projectId + "does not exist.");
		 }
		
	}


 
}