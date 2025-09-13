/**

 * 
 */
package recipes;

import java.time.LocalTime;


import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.exception.DbException;
import recipes.dao.DbConnection;
import recipes.entity.Recipe;
import recipes.service.RecipeService;

/**
 * 
 */
public class Recipes {
	private Scanner scanner = new Scanner(System.in); 
	private RecipeService recipeService = new RecipeService();
	
	//@formatter: off
	private List<String> operations = List.of( 
		"1) Create and populate all tables",
		"2) Add a recipe",	
		"3) List Recipes",
		"4) Select working recipe"
	);
	private Object curRecipe;
	//@formatter:on


	public static void main(String[] args) {
		 new Recipes().displayMenu();

	}
	
	
	private void displayMenu() {
		
		//exit condition for while loop
			// - exit loop when done = true
		       // - happens with user exits program
		boolean done = false; // false means display menu is not finished
		
		while(!done) {
			
		try { // handles errors like if user enters wrong data input(abc instead of 1)
		  int operation = getOperation();// calls getOpertions() method to read user input
  
		  switch(operation) {//switch operations in menu
		  case -1 :// exiting menu 
			  done = exitMenu();
			  break;  
		  case 1 :
			   createTables(); // Selection creates DB table
		       break;
		  case 2 :
			   addRecipe();   // Selection adds new recipe
			   break;
		  case 3:
			  listRecipes(); // Selection list all recipes
			  break;
		  case 4:
			  setCurrentRecipe();
			  break;
			  
		       
			  //default is like an else statement - in this case when wrong selection made
		       default:
		    	   System.out.println("\n" +  operation + " is not valid. try again.");
		    	   break;
		  }
		} catch(Exception e) {
			System.out.println("\nError: " + e.toString() + " Try again. ");
		}
	}
}

	
	private void setCurrentRecipe() {
		List<Recipe> recipes = listRecipes();
		
		Integer recipeId = getIntInput("Select a recipe ID");
		
		curRecipe = null;
		
		for(Recipe recipe : recipes) {
			if(recipe.getRecipeId().equals(recipeId)) {
				curRecipe = recipeService.fetchRecipeById(recipeId);
				break;
			}
		}	
		
		if(Objects.isNull(curRecipe)) {
			System.out.println("\nInvalid recipe selected.");
		}
		
	}


	private List<Recipe> listRecipes() {
		//Will fetch all recipes because fetchRecipes() has no properties
		List<Recipe> recipes = recipeService.fetchRecipes();
		
		System.out.println("\nRecipes:");
		
		//Lamda operator works like enhanced for loop
		//works the same as: for(Recipe recipe:recipes)
		recipes.forEach(recipe -> 
		           System.out.println("      " + recipe.getRecipeId() + ": " +  recipe.getRecipeName()));
		           
		  return recipes;
	}


	private void addRecipe() {
		String name = getStringInput("Enter the recipe name");
		String notes = getStringInput("Enter the recipe notes");
		Integer numServings = getIntInput("Enter number of servings");
		Integer prepMinutes = getIntInput("Enter prep time in minutes");
		Integer cookMinutes = getIntInput("Enter cook time in minutes");
		
		LocalTime prepTime = minutesToLocalTime(prepMinutes);
		LocalTime cookTime = minutesToLocalTime(cookMinutes);
		
		//creates new instance of recipe Class from recipes.entity package
		Recipe recipe = new Recipe();
		
		//sets fields from user provided values
		recipe.setRecipeName(name);
		recipe.setNotes(notes);
		recipe.setNumServings(numServings);
		recipe.setPrepTime(prepTime);
		recipe.setCookTime(cookTime);
		
		Recipe dbRecipe = recipeService.addRecipe(recipe);
		System.out.println("You added this recipee: \n" +  dbRecipe);
		
		curRecipe = recipeService.fetchRecipeById(dbRecipe.getRecipeId());
		
		
	}


	private LocalTime minutesToLocalTime(Integer numMinutes) {
	 int min = Objects.isNull(numMinutes) ? 0 : numMinutes;
	 int hours = min/60;
	 int minutes = min % 60;
	 
	 return LocalTime.of(hours, minutes);
	}


	private void createTables() {
		recipeService.createAndPopulateTables();
		 System.out.println("\nTables created and populated!");
		
	}


	private boolean exitMenu() {
	   System.out.println("\nExiting the menu. TTFN!");
	   return true;
	}


	private int getOperation() {
		printOperations();
		System.out.println();
		Integer op = getIntInput("Enter and operation number (press Enter to quit)");
				
		return Objects.isNull(op) ? -1 : op;
	
	}

	private void printOperations() {
		System.out.println();
		System.out.println("Here's what you can do:");
		
		operations.forEach(op -> System.out.println("   " + op));
	}
	
	private Integer getIntInput(String prompt) {
		 String input = getStringInput(prompt);
		 
		 if(Objects.isNull(input)) {
			 return null;
		 }
		 try {
			 return Integer.parseInt(input);
		 }
		 catch(NumberFormatException e) {
			 throw new DbException(input + " is not a valid number.");
		 }
	}
	
	
	private Double getDoubleInput(String prompt) {
		 String input = getStringInput(prompt);
		 
		 if(Objects.isNull(input)) {
			 return null;
		 }
		 try {
			 return Double.parseDouble(input);
		 }
		 catch(NumberFormatException e) {
			 throw new DbException(input + " is not a valid number.");
		 }
	}


	private String getStringInput(String prompt) {
	    System.out.println(prompt + ": ");
	    String line = scanner.nextLine();
	    
	    return line.isBlank() ? null : line.trim();
	}

	
	
}
