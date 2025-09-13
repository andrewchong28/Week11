package projects;

import java.math.BigDecimal;



import java.util.List;
import java.util.Objects;
import java.util.Scanner;

 
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;


public class ProjectsApp {
	
	//scanner variable reads from the standard input stream
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService =  new ProjectService();
	private Project curProject;
	
	
	//create list of strings called operations
	//List.of() creates and immutable list
	
	//@formatter:off 
	private List<String> operations = List.of(
			 
			 "1) Add a project"
			,"2) List projects"
			,"3) Select a project"
//Add the line "4) Update project details" to the list of operations			
			,"4) Update project details"
//Add a new option: "5) Delete a project" to the list of operations.
			,"5) Delete a project"
 
			);
	//@formatter:on
	
	public static void main(String[] args) {
		 // creates a new instance of ProjectApps class
		// call method processUserSelections()
		 new ProjectsApp().processUserSelections();

	}

 
	
	private void processUserSelections() {
		
		//while boolean = false loop runs
		boolean done = false;
		
		//loop keeps going until done = true
		while (!done) {
			// no error if selection  = getUserSelection();
			try {
				int selection  = getUserSelection();
				
				switch(selection) {
				case -1 :
					  exitMenu(); //returns true
					break;
					
				case 1:
					  createProject();// returns false, menu continues
					break;
					
				case 2:
					  listProjects(); // listProjects() should also return boolean (false)
					break;
				case 3:
					  selectProject();
					break;
/*
* Add case 4 to the switch statement and call method updateProjectDetails().
* Let Eclipse create the method for you.
*/				
				case 4:
					 updateProjectDetails();
					 break;

//Add a new option: "5) Delete a project" to the list of operations.
			     
				case 5:
					deleteProject();
					break;
					
				default: 
					System.out.println("\n" + selection + " is not a valid selection. Try again");
					
				}
				
			}
			// errors on invalid user selection other than int
		    catch(Exception e) {
		    	System.out.println("\nError: " + e + " Try again.");
			}
		}

	}

private void deleteProject() {
 
		listProjects();	 //retrieves list of projects
		
		Integer projectId = getIntInput("\nPlease enter project to delete");
 
		Project deleteProjectName = projectService.fetchProjectById(projectId);
		projectService.deleteProject(projectId);
		System.out.println("\nProject: " + deleteProjectName.getProjectId()+ " - " + deleteProjectName.getProjectName() + " deleted successfully!5");	
		
		//Clear curProject after delete
	     // Check to see if variable projectId equals projectId
		 // set curProject to null
		   if (curProject != null && projectId.equals(curProject.getProjectId())) {
		        curProject = null;
		    }
	     }

/*
* 3a. Check to see if curProject is null. If so, print a message "\nPlease select a project." and return from the method.
*/	  private void updateProjectDetails() {
		   //if statement to check to see if current project is null and print message
		   if(Objects.isNull(curProject)) {// Can also write: if(curProject == null){}
			   System.out.println("\n***Error*** Please select a project before updating.");
			   return;// return stops the method when null
		   }
		   

//3b. For each field in the Project object, print a message along with the
//current setting in curProject. Here is an example
 
		   // for variable projectName prompt user for String input. displays current name of project from curProject.getProjectName()
		   String projectName =
				   getStringInput("Enter updated project name. Current project name: [" + curProject.getProjectName() + "]");
		   BigDecimal estimatedHours = 
				   getDecimalInput("Enter updated estimated hours. Current estimated hours: [" + curProject.getEstimatedHours() + "]");
		   BigDecimal actualHours = 
				   getDecimalInput("Enter updated actual hours. Current actual hours: [" + curProject.getActualHours() + "]");
		   Integer difficulty = 
				   getIntInput("Enter updated project difficulty (1 - 5). Current difficulty: [" + curProject.getDifficulty() + "]");
		   String notes = 
				   getStringInput("Enter updated project notes. Current notes: [" + curProject.getNotes() + "]");
		   
//3c part 1. Create a new Project object.
//new instance of Class Project from projects.entity.Project
//variable project is where updated values or currProjet values are stored
//for example: in projects.entity.Project  private String projectName will store the old or new value through getters and setters methods
		   Project project = new Project();
		   

//3c part2. If the user input for a value is not null,
//add the value to the Project object. If the value is null, add the value from
//curProject. Repeat for all Project variables
 
		  	//project.setProjectName - is calling the method setProjectName from projects.entity.Project 
		   	//                         being done through the new instance of Project referenced by variable 
		   	//                         project from Project project = new Project()
		   	//Objects.isNull(projectName) - is checking user input from variable projectName is null(user did not enter value) or 
		   	//                              if value was entered by user
		   	//Ternary statement = ? Null(curProject.getProjectName()): not null(variable projectName)
		   	//                    like an if/else statement. If input is null then no change to project name
		   	//                    If input not null then use the new project name
		
		   project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		   project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
		   project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		   project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		   project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
		   
		   
//3d. Set the project ID field in the Project object to the value in the curProject object.
		   //call setProjectId from projects.entity.Project from new instance of project
		   //Set with curProject.getProjectId()
		   project.setProjectId(curProject.getProjectId());

		   
/*
* 3e. Call projectService.modifyProjectDetails(). Pass the Project object as a
* parameter. Let Eclipse create the method for you in ProjectService.java.
* 
* 3f. Reread the current project to pick up the changes by calling
* projectService.fetchProjectById(). Pass the project ID obtained from
* curProject.
*/
		     //runs update method modifyProjectDetails from projectDao
      /*3e*/  projectService.modifyProjectDetails(project);
      
              //fetches current projectid so curProject has the the newest value
     /*3f*/   curProject = projectService.fetchProjectById(curProject.getProjectId());
      
   	
	}



	private void selectProject() {
		  
		    listProjects();
		    Integer projectId = getIntInput("Enter a project ID to select a project");

            //unselect current project
		    curProject = null;

		    /* invalid project id then throw exception. */
		    curProject = projectService.fetchProjectById(projectId);
		    
		    System.out.println("\nYou have selected this project:\n" + curProject);
	}



	/**
	   * This method calls the project service to retrieve a list of projects from the projects table.
	   * It then uses a Lambda expression to print the project IDs and names on the console. 
	   */
	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		System.out.println("\nProjects:");
		
		

	    projects.forEach(project -> System.out
	        .println("   " + project.getProjectId() + ": " + project.getProjectName()));
	 
	  }


	private void createProject() {
      String projectName = getStringInput("Enter the project name");
      BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
      BigDecimal actualHours = getDecimalInput("Enter actual hours");
      Integer difficulty = getIntInput("Enter the project difficulty (1 - 5)");
      String notes = getStringInput("Enter the project notes");
       
      Project project = new Project();

      project.setProjectName(projectName);
      project.setEstimatedHours(estimatedHours);
      project.setActualHours(actualHours);
      project.setDifficulty(difficulty);
      project.setNotes(notes);

      
      Project dbProject = projectService.addProject(project);
      System.out.println("You have successfully created project: " + dbProject);
          
	}


	private BigDecimal getDecimalInput(String prompt) {
        String input = getStringInput(prompt);
        
   	 if(Objects.isNull(input)) {
			 return null;
		 }
   	 
   	 try {
   		 return new BigDecimal(input). setScale(2);
   	 }
	     catch (NumberFormatException e) {
	    	throw new DbException(input + " is not a valid number. ");
	    }
		
	 }
	

	private  void exitMenu() {
	    System.out.println("Exiting the menu.");
 
	}

	private int getUserSelection() {
		
		//call method  
		printOperations();
		Integer input = getIntInput("Enter menu selection");
		return Objects.isNull(input) ? -1 : input;
	}

	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");
		
		//iterates through and prints each option
		operations.forEach(line -> System.out.println("  " + line));
		
	    // show current project status
	    if (curProject == null) {
	        System.out.println("\nYou are not working with a project.");
	    } else {
	        System.out.println("\nYou are working with project: " + curProject.getProjectName());
	    }
	
	}
	

	private Integer getIntInput(String prompt) {
         String input = getStringInput(prompt);
         Objects.isNull(prompt);
         
    	 if(Objects.isNull(input)) {
			 return null;
		 }
    	 
    	 try {
    		 return Integer.valueOf(input);
    	 }
	     catch (NumberFormatException e) {
	    	throw new DbException(input + " is not a valid number. ");
	    }
		
	 }


	private String getStringInput(String prompt) {
	  System.out.print(prompt + ": ");
	  String input = scanner.nextLine();
	  
	  // ? is if/else  
	  // if input is blank then null else input.trim()
	  // this checks if user hit enter  
	  return input.isBlank() ? null : input.trim();
	}
	
	

}

