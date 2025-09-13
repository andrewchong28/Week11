package recipes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.exception.DbException;
import provided.util.DaoBase;
import recipes.entity.Category;
import recipes.entity.Ingredient;
import recipes.entity.Recipe;
import recipes.entity.Step;
import recipes.entity.Unit;

public class RecipeDao extends DaoBase {
	
	private static final String CATEGORY_TABLE = "category";
	private static final String INGREDIENT_TABLE = "ingredient";
	private static final String RECIPE_CATEGORY_TABLE = "recipe_category";
	
	private static final String RECIPE_TABLE = "recipe";
	private static final String STEP_TABLE = "step";
	private static final String UNIT_TABLE = "unit";
	
	//only returning a single recipe using return type Optional
    public Optional <Recipe> fetchRecipeById (Integer recipeId){
    	String sql = "Select * from " + RECIPE_TABLE + " where recipe_id = ?";
    	
    	try(Connection conn = DbConnection.getConnection()){
    		startTransaction(conn);
    		
    		try {
    			Recipe recipe = null;
    			
    			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
    				setParameter(stmt, 1, recipeId, Integer.class);
    				
    				try(ResultSet rs = stmt.executeQuery()) {
    					if(rs.next()) {
    						recipe = extract(rs, Recipe.class);
    					}
    				}
    			}
    			if(Objects.nonNull(recipe)) {
    				recipe.getIngredients()
    					   .addAll(fetchRecipeIngredients(conn,recipeId));
    				
    				recipe.getSteps().addAll(fetchRecipeSteps(conn, recipeId));
    				recipe.getCategories().addAll(fetchRecipesCategories(conn,recipeId));
    			}
    			return Optional.ofNullable(recipe);
    		}
    		catch(Exception e) {
    			rollbackTransaction(conn);
    			throw new DbException(e);
    		}	
    	}
    	catch(SQLException e) {
    		throw new DbException(e);
    	}
    }
    
    
	private List<Category> fetchRecipesCategories(Connection conn, Integer recipeId) throws SQLException {
		// @formatter:off
		String sql = ""
				+ "Select c.* "
				+ "from " + RECIPE_CATEGORY_TABLE + " rc "
				+ "join " + CATEGORY_TABLE + " c USING (category_id) "
				+ "where recipe_id = ? ";
		// @formatter:on
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, recipeId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<Category>();
				
				while(rs.next()) {
					categories.add(extract(rs, Category.class));
				}
				return categories;
			  }
			}
	      } 
	    

	private List<Step> fetchRecipeSteps(Connection conn, Integer recipeId) throws SQLException {
		String sql = "Select * from " +  STEP_TABLE + " s where s.recipe_id = ?";
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, recipeId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<Step>();
				
				while(rs.next()) {
					steps.add(extract(rs, Step.class));
			   }
			
				return steps;
			}	
		}
	}


	private List<Ingredient> fetchRecipeIngredients(Connection conn, Integer recipeId) throws SQLException {
	    
	    // @formatter:off
	    String sql = "" 
	        + "SELECT i.* , u.unit_name_singular, u.unit_name_plural " 
	        + "FROM " + INGREDIENT_TABLE + " i "
	        + "LEFT JOIN " + UNIT_TABLE + " u USING (unit_id) "
	        + "WHERE i.recipe_id = ? "
	        + "ORDER BY i.ingredient_order";
	    // @formatter:on
	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        setParameter(stmt, 1, recipeId, Integer.class);
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            List<Ingredient> ingredients = new LinkedList<>();
	            
	            while (rs.next()) {
	                Ingredient ingredient = extract(rs, Ingredient.class);
	                Unit unit = extract(rs, Unit.class);
	                
	                ingredient.setUnit(unit); 
	                ingredients.add(ingredient);
	            }
	            
	            return ingredients;
	        }
	    }
	}


	public List<Recipe> fetchAllRecipes() {
		
		
        String sql = "SELECT * FROM " + RECIPE_TABLE + " ORDER BY RECIPE_NAME";
		
		// Opens a connection to the database using the JDBC URL from DbConnection.getConnection()
	    //   - This authenticates the Java program to the database (with username/password).
        //opens connections to database using JDBC URL from DbConnections
        try(Connection conn = DbConnection.getConnection()){
        	startTransaction(conn);
           
            // PreparedStatement safely executes the SQL query stored in variable sql
            //   - Prevents SQL Injection (malicious input altering SQL logic)
            //   - Always use PreparedStatement instead of concatenating raw user input
            //   - Inserts values safely with setString(), setInt(), etc.
            //   - Ensures user input is treated strictly as data, not part of SQL structure
           try(PreparedStatement stmt = conn.prepareStatement(sql)){
        	   try(ResultSet rs = stmt.executeQuery()){
        	   List<Recipe> recipes = new LinkedList<>();// create list of recipes
        	   
        	   while(rs.next()) {
        		   recipes.add(extract(rs, Recipe.class));
        	   }
        	   
        	   return recipes;
            }
           //Exception e: stores the error in variable e to passed in DbException class  
           } catch(Exception e) {
        	   rollbackTransaction(conn);
        	   throw new DbException(e);
           }
        } catch(SQLException e) {
        	throw new DbException(e);
        }
	}

	
	public Recipe insertRecipe(Recipe recipe) {
		// @formatter:off
		 String sql  = ""
		        + "INSERT INTO " +  RECIPE_TABLE + " "
		        +"(recipe_name , notes, num_servings, prep_time, cook_time) "
		        +"VALUES "
		        +"(?, ?, ?, ?, ?)";
		// @formatter:on
		 
		 try(Connection conn = DbConnection.getConnection()) {
			 startTransaction(conn);
			 
			 try(PreparedStatement stmt = conn.prepareStatement(sql)){
				 setParameter(stmt, 1, recipe.getRecipeName(), String.class);
				 setParameter(stmt, 2, recipe.getNotes(), String.class);
				 setParameter(stmt, 3, recipe.getNumServings(), Integer.class);
				 setParameter(stmt, 4, recipe.getPrepTime(), LocalTime.class);
				 setParameter(stmt, 5, recipe.getCookTime(), LocalTime.class);
				 
				 stmt.executeUpdate();
				 Integer recipeId = getLastInsertId(conn, RECIPE_TABLE);
				 
				 commitTransaction(conn);
				 
				 recipe.setRecipeId(recipeId);
				 return recipe;
				 
				 
			 }
			 catch(Exception e) {
				 rollbackTransaction(conn);
				 throw new DbException(e);
			 }
		 } catch (SQLException e) {
			 throw new DbException(e);
		 }
		 
	}
	
	
	
	public void executeBatch(List<String>sqlBatch) {
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(Statement stmt = conn.createStatement()){
				for(String sql : sqlBatch) {
					stmt.addBatch(sql);
				}
				stmt.executeBatch();
				commitTransaction(conn);
			}
			catch(Exception e) {
			 rollbackTransaction(conn);	
			 throw new DbException(e);
			}
		  } catch (SQLException e) {
			throw new DbException(e);
		  }
	}


 

}
