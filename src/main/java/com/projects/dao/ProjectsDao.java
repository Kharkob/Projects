package com.projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.projects.entity.Category;
import com.projects.entity.Material;
import com.projects.entity.Project;
import com.projects.entity.Step;
import com.projects.exception.DbException;
import com.projects.provided.util.DaoBase;



public class ProjectsDao extends DaoBase {
private static final String CATEGORY_TABLE = "category";
private static final String MATERIAL_TABLE = "material";
private static final String PROJECT_TABLE = "project";
private static final String PROJECT_CATEGORY_TABLE = "project_category";
private static final String STEP_TABLE = "step";

public Project insertProjects(Project projects) {
String sql = ""
+ "INSERT INTO " +  PROJECT_TABLE + " "
+ " ( project_name, estimated_hours, actual_hours, difficulty, notes) "
+ "VALUES "
+"(?, ?, ?, ?, ?) ";

try (Connection conn = DbConnection.getConnection()) {
startTransaction(conn);

try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    setParameter(stmt, 1, projects.getProjectName(), String.class);
    setParameter(stmt, 2, projects.getEstimatedHours(), BigDecimal.class);
    setParameter(stmt, 3, projects.getActualHours(), BigDecimal.class);
    setParameter(stmt, 4, projects.getDifficulty(), Integer.class);
    setParameter(stmt, 5, projects.getNotes(), String.class);

    stmt.executeUpdate();

    Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
    commitTransaction(conn);

    projects.setProjectId(projectId);
    return projects;
}
catch(Exception e) {
    rollbackTransaction(conn);
    throw new DbException(e);
}
} catch (SQLException e) {
    throw new DbException(e);
} 
}

public List<Project> fetchAllProjects() {
    String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
     
    try(Connection conn = DbConnection.getConnection()) {
        startTransaction(conn);

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            try(ResultSet rs = stmt.executeQuery()) {
                List<Project> projects = new LinkedList<>();

                while(rs.next()) {
                    projects.add(extract(rs, Project.class));
                }
                
                return projects;
            }
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
 public Optional<Project> fetchProjectById(Integer projectId) {
    String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE  project_id = ?";

    try(Connection conn = DbConnection.getConnection()) {
        startTransaction(conn);
        try {
            Project project = null;

            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, projectId, Integer.class);

                try(ResultSet rs = stmt.executeQuery()) {
                    if(rs.next()) {
                        project =extract(rs, Project.class);

                    }
                }
            }
            if (Objects.nonNull(project)) {
                project.getMaterials().addAll(fetchMaterialsForProject(conn,projectId));
                project.getSteps().addAll(fetchStepsForProject(conn, projectId));
                project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
            }

            commitTransaction(conn);
            return Optional.ofNullable(project);
        }
            catch(Exception e) {
                System.out.println("rollbackTransaction");
                rollbackTransaction(conn);
                throw new DbException(e);
            }
         }
     catch(SQLException e) {
        throw new DbException(e);
     }
    }








private List<Category> fetchCategoriesForProject(Connection conn, 
Integer projectId) throws SQLException {
String sql = " "
    + "SELECT c.* FROM " + CATEGORY_TABLE + " c "
    + " JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
    + "WHERE project_id = ?";

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

}
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


private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
        String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
    
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameter(stmt, 1,projectId, Integer.class);
        
        try(ResultSet rs = stmt.executeQuery()) {
            List<Material> materials = new LinkedList<>();
            
            while(rs.next()) {
                materials.add(extract(rs, Material.class));
            }
            return materials;
        }
    }
}

public boolean modifyProjectDetails(Project projects) {
    String sql = ""
        + "UPDATE " + PROJECT_TABLE + " SET "
        +"project_name = ?, "
        +"estimated_hours = ?, "
        +"actual_hours = ?, "
        +"difficulty = ?, "
        +"notes = ? "
        +"Where project_id = ? ";
            
        try(Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, projects.getProjectName(), String.class);
                setParameter(stmt, 2, projects.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, projects.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, projects.getDifficulty(), Integer.class);
                setParameter(stmt, 5, projects.getNotes(), String.class);
                setParameter(stmt, 6, projects.getProjectId(),Integer.class); 
                boolean modified = stmt.executeUpdate() == 1;
                commitTransaction(conn);

                return modified;
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

public boolean deleteProject(Integer projectId) {
    String sql = "DELETE FROM "  + PROJECT_TABLE + " WHERE project_id = ?";

   try (Connection conn = DbConnection.getConnection()) {
    startTransaction(conn);

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, projectId, Integer.class);
    boolean deleted = stmt.executeUpdate() == 1;

    commitTransaction(conn);
    return deleted;
   }
   catch(Exception e) {
    rollbackTransaction(conn);
    throw new DbException(e);
    }
} catch (SQLException e) {
    throw new DbException();
}
}

public void addStepToProject(Step step) {
  String sql = "INSERT INTO " + STEP_TABLE + " (project_id, step_order, step_text)" 
  + " VALUES (?, ?, ?)";

  try(Connection conn = DbConnection.getConnection()) {
  startTransaction(conn);
    
    Integer order = getNextSequenceNumber(conn, step.getProjectId(), STEP_TABLE, "project_id");

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, step.getProjectId(), Integer.class);
        setParameter(stmt, 2, order, Integer.class);
        setParameter(stmt, 3, step.getStepText(), String.class);

        stmt.executeUpdate();
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



public void addMaterialToProject(Material material) {
          String sql = " INSERT INTO " + MATERIAL_TABLE +
          " (project_id,  material_name, num_required, material_order, cost)" +
          "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = DbConnection.getConnection()) {
    startTransaction(conn);

            try {
             

                try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                    setParameter(stmt, 1, material.getProjectId(), Integer.class);
                    setParameter(stmt, 3, material.getMaterialName(), String.class);
                     setParameter(stmt, 5, material.getNumRequired(),  Integer.class);
                    setParameter(stmt, 6, material.getCost(), BigDecimal.class);
                }
            }
            catch(Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }

        }   catch (SQLException e) { 
throw new DbException(e);
     } 

    }

public List<Category> fetchAllCategories() {
  String sql =  "SELECT * FROM " + CATEGORY_TABLE + " ORDER BY category_name";

  try(Connection conn = DbConnection.getConnection()) {
    startTransaction(conn);

    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
        try(ResultSet rs = stmt.executeQuery()) {
            List<Category> categories = new LinkedList<>();

            while(rs.next()) {
                categories.add(extract(rs, Category.class));
            }
            return categories;
        }
    }
  catch(Exception e) {
    rollbackTransaction(conn);
    throw new DbException(e);
}  
}catch (SQLException e) { 
    throw new DbException(e);
         } 
  }


public void addCategoryToProject(Integer projectId, String category) {
   String subQuery = "(SELECT category_id FROM " + CATEGORY_TABLE
   + " WHERE category_name = ?)";

   String sql = "INSERT INTO " + PROJECT_CATEGORY_TABLE + " (project_id, category_id) VALUES (?,  " + subQuery + ")";

   try(Connection conn = DbConnection.getConnection()) {
    startTransaction(conn);

    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, projectId, Integer.class);
        setParameter(stmt, 2, category, String.class);

        stmt.executeUpdate();
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




