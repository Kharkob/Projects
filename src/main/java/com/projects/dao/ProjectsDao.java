package com.projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.projects.entity.Project;
import com.projects.exception.DbException;
import com.projects.provided.util.DaoBase;


@SuppressWarnings("unused")
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
}


