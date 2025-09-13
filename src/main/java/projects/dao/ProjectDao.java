package projects.dao;

import java.math.BigDecimal;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import projects.entity.Category;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;
 

public class ProjectDao extends DaoBase {
	
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";
	
		public Project insertProject (Project project) {
			
		// ? are filled with values from PreparedStatement
		// @formatter:off
		 String sql  = ""
		        + "INSERT INTO " +  PROJECT_TABLE + " "
		        +"(project_name , estimated_hours, actual_hours, difficulty, notes) "
		        +"VALUES "
		        +"(?, ?, ?, ?, ?)";
		// @formatter:on
		 
			// Opens a connection to the database using the JDBC URL from DbConnection.getConnection()
		    //   - This authenticates the Java program to the database (with username/password).
		 try(Connection conn = DbConnection.getConnection()){
			 startTransaction(conn); 
			 
	            // PreparedStatement safely executes the SQL query stored in variable sql
	            //   - Prevents SQL Injection (malicious input altering SQL logic)
	            //   - Always use PreparedStatement instead of concatenating raw user input
	            //   - Inserts values safely with setString(), setInt(), etc.
	            //   - Ensures user input is treated strictly as data, not part of SQL structure
			 try(PreparedStatement stmt = conn.prepareStatement(sql)){
				 setParameter(stmt, 1, project.getProjectName(), String.class);
				 setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				 setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				 setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				 setParameter(stmt, 5, project.getNotes(), String.class);
				 
				 // method part of data manipulation language(DML)
				 //used when modifying database (e.g., Insert, Delete, update)
				 stmt.executeUpdate();
				 
				 Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				 commitTransaction(conn);
				 
				 project.setProjectId(projectId);
				 return project;
				 
			 } //if errors rollback undos changes within the transaction
			 catch(Exception e){
				 rollbackTransaction(conn);
				 throw new DbException(e);
			 }
		 }
		catch(SQLException e) {
			throw new DbException(e);
		}
		 
	}
		

		public List<Project> fetchAllProjects() {
		    String sql = "SELECT * FROM project ORDER BY project_id"; // define SQL

		    List<Project> projects = new LinkedList<>();

		    try (Connection conn = DbConnection.getConnection();
		         PreparedStatement stmt = conn.prepareStatement(sql);
		         ResultSet rs = stmt.executeQuery()) {// ResultSet is a container that holds rows from SQL. It's like a table in memory
		    	                                      //executeQuery() method runs the query

		        startTransaction(conn);  
		        
		         //next() moves the cursor(internal pointer) to the next row of the result set
		        // extract(rs, Project.class) converts the current row of the ResultSet into a Project object.
		        // loops through and projects.add(...) appends that Project object to the 'projects' list  
		        while (rs.next()) {
		            projects.add(extract(rs, Project.class));
		        }
		        
		        return projects;

		    } catch (SQLException e) {
		        throw new DbException(e);
		    }
		}
        
		// Optional is an object that may or may not have a non null value
		public Optional<Project> fetchProjectById(Integer projectId) {
			String sql = "Select * from " +PROJECT_TABLE+ " Where project_id = ?";
			
			try(Connection conn = DbConnection.getConnection()){
				startTransaction(conn);
				
			  try{Project project = null;
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt, 1, projectId, Integer.class);
					
				   try(ResultSet rs = stmt.executeQuery()){
					  if(rs.next()) {
						  project = extract(rs, Project.class);
					  }
				   }
				}
				
				if (Objects.nonNull(project)) {// check to see if project is null
					//if project is not null fetch materials, steps, categories and append to list
				    project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
				    project.getSteps().addAll(fetchStepsForProject(conn, projectId));
				    project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				}
			commitTransaction(conn);
			
			return Optional.ofNullable(project);
				}
			  catch(Exception e) {
				  throw new DbException(e);
			}    
		}
		catch(SQLException e) {
			throw new DbException(e);
		}
	}
		
		  private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) {
			    // @formatter:off
			    String sql = ""
			        + "SELECT c.* FROM " + CATEGORY_TABLE + " c "
			        + "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
			        + "WHERE project_id = ?";
			    // @formatter:on

			    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			      setParameter(stmt, 1, projectId, Integer.class);

			      try(ResultSet rs = stmt.executeQuery()) {
			        List<Category> categories = new LinkedList<>();

			        while(rs.next()) {
			          categories.add(extract(rs, Category.class));
			        }

			        return categories;
			      }
			    }
			    catch(SQLException e) {
			      throw new DbException(e);
			    }
			  }
	
		  private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId)
			      throws SQLException {
			    String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";

			    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			      setParameter(stmt, 1, projectId, Integer.class);

			      try(ResultSet rs = stmt.executeQuery()) {
			        List<Material> materials = new LinkedList<>();

			        while(rs.next()) {
			          materials.add(extract(rs, Material.class));
			        }

			        return materials;
			      }
			    }
			  }
		  
		  /**
		   * This method uses JDBC method calls to retrieve project steps for the given project ID. The
		   * connection is supplied by the caller so that steps can be retrieved on the current transaction.
		   * 
		   * @param conn The caller-supplied connection.
		   * @param projectId The project ID used to retrieve the steps.
		   * @return A list of steps in step order.
		   * @throws SQLException Thrown if the database driver encounters an error.
		   */
		  
		  private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
			    String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";

			    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			      setParameter(stmt, 1, projectId, Integer.class);

			      try(ResultSet rs = stmt.executeQuery()) {
			        List<Step> steps = new LinkedList<>();

			        while(rs.next()) {
			          steps.add(extract(rs, Step.class));
			        }

			        return steps;
			      }
			    }
			  }


//Changes to Project Dao
		//1a In modifyProjectDetails(), write the SQL statement to modify the project details. Do not
		// update the project ID – it should be part of the WHERE clause. Remember to use question marks
		// as parameter placeholders.
		  
		public boolean modifyProjectDetails(Project project) {

			// @formatter: off
			 String sql = ""
			 + "UPDATE " + PROJECT_TABLE + " SET "
			 + "project_name = ?, "
			 + "estimated_hours = ?, "
			 + "actual_hours = ?, "
			 + "difficulty = ?, "
			 + "notes = ?  "
			 + "Where project_id = ?";
			// @formatter:on
			 
			 
			 //2a. Obtain the Connection and PreparedStatement using the appropriate try-withresource and catch blocks. 
			 //Start and rollback a transaction as usual. Throw a DbException from each catch block.
			 try(Connection conn = DbConnection.getConnection()){
                startTransaction(conn);
                
                //3. Set all parameters on the PreparedStatement. Call executeUpdate() and check if the
               // return value is 1. Save the result in a variable.
                try(PreparedStatement stmt = conn.prepareStatement(sql)){

                     setParameter(stmt,1, project.getProjectName(), String.class);
                	 setParameter(stmt,2, project.getEstimatedHours(), BigDecimal.class);
                	 setParameter(stmt,3, project.getActualHours(), BigDecimal.class);
                	 setParameter(stmt,4, project.getDifficulty(), Integer.class);
                	 setParameter(stmt,5, project.getNotes(), String.class);
                	 setParameter(stmt,6, project.getProjectId(), Integer.class);
                	 
                	 
                	 //variable to store update statement rows
                	 int updatedstmtscnt = stmt.executeUpdate();
                	 
                	// 4. Commit the transaction and return the result from executeUpdate() as a boolean. At this
                	// point there should be no compilation errors.
                	 
                	 //without commitTransaction() updates made by executeUpdate() are not complete
                	 commitTransaction(conn);
                	 
                	 //returns rows affected
                	 // if rows are greater than 0 
                	 return updatedstmtscnt > 0 ;

                    }
           	         catch(Exception e){
				     rollbackTransaction(conn);
				     throw new DbException(e);
			         }
                	
                }
		 
			 	catch(SQLException e) {
			 	throw new DbException(e);
		        }
 
		
		    }


		public boolean deleteProject(Integer projectId) {

			// @formatter: off
			String sql = ""
			+ "Delete From " + PROJECT_TABLE + " "
			+ "WHERE Project_Id = ?";
			// formatter:on
			 
		   try(Connection conn = DbConnection.getConnection()){
			   startTransaction(conn);
			   
			   try(PreparedStatement stmt = conn.prepareStatement(sql)){
				   setParameter(stmt, 1, projectId , Integer.class);
				   
				   //variable to store update statement rows
              	 int deletestmtscnt = stmt.executeUpdate();
              	 
				   commitTransaction(conn);
				  	 
				  	 return deletestmtscnt > 0;
			   }
			    catch(Exception e){
				     rollbackTransaction(conn);
				     throw new DbException(e);
			         }
		   }
			catch(SQLException e) {
			 	throw new DbException(e);
		        }
	 
		}
		
	
		   
		   
}

