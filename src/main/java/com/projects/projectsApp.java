package com.projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;



import com.projects.entity.Project;

import com.projects.exception.DbException;
import com.projects.service.ProjectService;




public class ProjectsApp { 
    private Scanner scanner = new Scanner(System.in);
    private ProjectService projectService = new ProjectService();
    private Project cuProject;
    private List<String> operations = List.of( 
        "1) Add a project",
        "2) List projects",
        "3) Select a project",
        "4) Update project details",
        "5) Delete a project"
        /* 
        "6) Add step to current project",
        "7) Add category to current project",
        "8) Modify step in current project",
        "9) Add Materials to current project"
*/
        //add materials and steps, just watch "delete a project or whatever in week 11 its more complicated then these simple columns from above
        );
    public static void main(String[] args) {
        new ProjectsApp().processUserSelections();
    }
    private void processUserSelections(){
        boolean done = false; 

        while(!done) {
            try {
                int selection = getUserSelection();
                
                switch(selection) {
                    case -1:
                        done = exitMenu();
                        break;

                    case 1: 
                    createProject();
                    break;

                    case 2: 
                    listProjects();
                    break;

                    case 3:
                    selectProject();
                    break;

                    case 4: 
                    updateProjectDetails();
                    break;

                    case 5:
                    deleteProject();
                    break;
                    /* 
                    case 6:
                    addStepToProject();
                    break;

                    case 7:
                    addCategoryToCurrentProject();
                    break;

                    case 8:
                    modifyStepInCurrentProject();
                    break;
                    
                    case 9:
                    addMaterialToCurrentProject();
                    break;
                        */






                    default:
                        System.out.println("\n" + selection + " is not a valid selection. Try again.");
                        break;
                }
            }
            catch(Exception e) {
                System.out.println("\nError:" + e + "Try again.");

    

            }
        }
    }

    private void selectProject() {
        listProjects();
        Integer projectId = getIntInput("Enter a project ID to select a project");
        cuProject = null;

        cuProject = projectService.fetchProjecById(projectId);
        if (Objects.isNull(cuProject)) {
                System.out.println("That is not a valid project");
        }
    }
    private void listProjects() {
            List<Project> projects = projectService.fetchAllProjects();

            System.out.println("\nProjects:");

            projects.forEach(project -> System.out.println(" " + project.getProjectId() + ": " + project.getProjectName()));
    }
    private void createProject() {
        String projectName = getStringInput("Enter the project name");
        BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
        BigDecimal actualHours = getDecimalInput("Enter the actual hours");
        Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
        String notes = getStringInput("Enter the project notes");
        
        Project project = new Project();
        
        project.setProjectName(projectName);
        project.setEstimatedHours(estimatedHours);
        project.setActualHours(actualHours);
        project.setDifficulty(difficulty);
        project.setNotes(notes);
        
        Project dbProject =  projectService.addProject(project);
        System.out.println("You have successfully created project" + dbProject);
        
            }
        
         private Integer getIntInput(String  prompt) {
                String input = getStringInput(prompt);

                if (Objects.isNull(input))
                    return null;
            
        
            try {
                return Integer.valueOf(input);
            }
            catch(NumberFormatException e) {
                throw new DbException(input + "is not a valid number.");
            }
        }
    private BigDecimal getDecimalInput(String prompt) {
        String input = getStringInput(prompt);

        if (Objects.isNull(input))
            return null;
    

    try {
        return new BigDecimal(input).setScale(2);
    }
    catch(NumberFormatException e) {
        throw new DbException(input + "is not a valid decimal number.");
    }
}
    private boolean exitMenu() {
        System.out.println("Exiting the menu.");
        return true;
    }

    private int getUserSelection() {
        printOperations();
        Integer input = getIntInput("Enter a menu selection");

        return Objects.isNull(input) ? -1 : input;
    }


    private String getStringInput(String prompt) {
        System.out.print(prompt + ": ");
        String input = scanner.nextLine();

        return input.isBlank() ? null : input.trim();
    }

    private void printOperations() {
        System.out.println("\nThese are the available selections. Press the Enter key to quit:");
    
    operations.forEach(line -> System.out.println(" " + line));

 if (Objects.isNull(cuProject)) {
        System.out.println("\nYou are not working with a project.");
    }
    else {
        System.out.println("\nYou are working with project: " + cuProject);
    }
}
    private void deleteProject() {
        listProjects();
        
        Integer projectId = getIntInput("Enter the ID of the project to delete");

        projectService.deleteProject(projectId);
        System.out.println("Project " + projectId + " was deleted successfully.");

        if(Objects.nonNull(cuProject) && cuProject.getProjectId().equals(projectId)) {
            cuProject = null;
    }
}

private void updateProjectDetails() {
    if(Objects.isNull(cuProject)) {
        System.out.println("\nPlease Select a project.");
        return;
    }
    String projectName = 
        getStringInput("Enter the project name  [" + cuProject.getProjectName() + "]");
    BigDecimal estimatedHours = 
        getDecimalInput("Enter the estimated hours ["  + cuProject.getEstimatedHours() + "]");
    BigDecimal actualHours =
        getDecimalInput("Enter the  actual hours  ["  + cuProject.getActualHours() + "]");
    Integer difficulty =
    getIntInput("Enter the project difficulty (1-5) [" + cuProject.getActualHours() + "]");

    String notes = getStringInput("Enter the project notes [" + cuProject.getNotes() + "]");

    Project project = new Project();

    project.setProjectId(cuProject.getProjectId());
    project.setProjectName(Objects.isNull(projectName) ? cuProject.getProjectName() : projectName);

    project.setEstimatedHours(
        Objects.isNull(estimatedHours) ? cuProject.getEstimatedHours() : estimatedHours);
    
    project.setActualHours(Objects.isNull(actualHours) ? cuProject.getActualHours() : actualHours);
    project.setDifficulty(Objects.isNull(difficulty) ? cuProject.getDifficulty() : difficulty);
    project.setNotes(Objects.isNull(notes) ? cuProject.getNotes() : notes);

    projectService.modifyProjectDetails(project);

    cuProject = projectService.fetchProjecById(cuProject.getProjectId());

}
}
/* 
private void addStepToProject() {
if (Objects.isNull(cuProject)) {
    System.out.println("\nPlease select a project first.");
    return;
}

String stepText = getStringInput("Enter the step text");

if(Objects.nonNull(stepText)) {
    Step step = new Step();

    step.setProjectId(cuProject.getProjectId());
    step.setStepText(stepText);

    projectService.addStep(step);
    cuProject = projectService.fetchProjecById(step.getProjectId());
}
}
}
 //I think this should be addMaterialToCurrentProject maybe 
/* 
 private void addMaterialToCurrentProject() {
    if (Objects.isNull(cuProject)) {
        System.out.println("\nPlease select a project first.");
        return;
    }

    String name = getStringInput("Enter the Material name");
    String description = getStringInput("Enter an description");
    
  

    BigDecimal amount = Objects.isNull(inputAmount) ?  null : new BigDecimal(inputAmount).setScale(2);

  

    Integer unitId = getIntInput("Enter a unit ID (press Enter for none)");

  

    Material material= new Material();

    material.setProjectId(cuProject.getProjectId());
    material.setMaterialName(name);
    material.setDescription(description);
    material.setAmount(amount);

    projectService.addMaterial(material);
    cuProject = projectService.fetchProjecById(material.getProjectId());
}

private void addCategoryToCurrentProject() {
    if (Objects.isNull(cuProject)) {
        System.out.println("\nPlease select a project");
        return;
    }

    List<Category> categories = projectService.fetchCategories();

    categories.forEach(
        category -> System.out.println("  " + category.getCategoryName()));

    String category = getStringInput("Enter the category to add");

    if(Objects.nonNull(category)) {
        projectService.addCategoryToProject(cuProject.getProjectId(), category);
        cuProject = projectService.fetchProjecById(cuProject.getProjectId());
    }
}
}
*/