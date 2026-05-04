package com.yinyang.project.entity;

import com.yinyang.project.DBContext;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FRACategory {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Status status;
    private LocalDateTime createdAt;

    public enum Status {
        ACTIVE, SUSPENDED
    }

    public FRACategory getFRACategoryById(Integer id) {
        String sql = "SELECT * FROM fra_category where id = ?";
        try {
            return DBContext.getJdbcTemplate().queryForObject(
                    sql,
                    new Object[]{id},
                    (rs, rowNum) -> {
                        FRACategory fraCategory = new FRACategory();
                        fraCategory.setId(rs.getInt("id"));
                        fraCategory.setName(rs.getString("name"));
                        fraCategory.setDescription(rs.getString("description"));
                        fraCategory.setStatus(Status.valueOf(rs.getString("status")));
                        fraCategory.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        return fraCategory;
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public FRACategory getFRACategoryByName(String name) {
        String sql = "SELECT * FROM fra_category where name = ?";
        try {
            return DBContext.getJdbcTemplate().queryForObject(
                    sql,
                    new Object[]{name},
                    (rs, rowNum) -> {
                        FRACategory fraCategory = new FRACategory();
                        fraCategory.setId(rs.getInt("id"));
                        fraCategory.setName(rs.getString("name"));
                        fraCategory.setDescription(rs.getString("description"));
                        fraCategory.setStatus(Status.valueOf(rs.getString("status")));
                        fraCategory.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        return fraCategory;
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean isNameTakenByOtherCategories(String name, Integer id) {
        String sql = "SELECT COUNT(*) FROM fra_category WHERE name = ? AND id != ?";
        Integer count = DBContext.getJdbcTemplate().queryForObject(
                sql,
                Integer.class,
                name,
                id
        );
        return count != null && count > 0;
    }

    public boolean createFRACategory(FRACategory fraCategoryData) {
        if (this.getFRACategoryByName(fraCategoryData.getName()) == null) {
            String sql = "INSERT INTO fra_category (name, description, status, created_at) values (?, ?, ?, ?)";
            DBContext.getJdbcTemplate().update(
                    sql,
                    fraCategoryData.getName(),
                    fraCategoryData.getDescription(),
                    fraCategoryData.getStatus().name(),
                    LocalDateTime.now()
            );
            return true;
        }
        return false;
    }

    public List<FRACategory> getAllFRACategories() {
        String sql = "SELECT * FROM fra_category ORDER BY created_at DESC";
        return DBContext.getJdbcTemplate().query(
                sql,
                (rs, rowNum) -> {
                    FRACategory fraCategory = new FRACategory();
                    fraCategory.setId(rs.getInt("id"));
                    fraCategory.setName(rs.getString("name"));
                    fraCategory.setDescription(rs.getString("description"));
                    fraCategory.setStatus(Status.valueOf(rs.getString("status")));
                    fraCategory.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return fraCategory;
                }
        );
    }

    public boolean updateFRACategory(FRACategory newFRACategoryData) {
        if (!this.isNameTakenByOtherCategories(newFRACategoryData.getName(), newFRACategoryData.getId())) {
            String sql = "UPDATE fra_category SET name = ?, description = ? WHERE id = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    newFRACategoryData.getName(),
                    newFRACategoryData.getDescription(),
                    newFRACategoryData.getId()
            );
            return row == 1;
        }
        return false;
    }

    public List<FRACategory> searchFRACategories(String name, String description, Status status, @NotNull String order) {
        StringBuilder sql = new StringBuilder("SELECT * FROM fra_category WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + name + "%");
        }

        if (description != null && !description.isEmpty()) {
            sql.append(" AND description LIKE ?");
            params.add("%" + description + "%");
        }

        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status.name());
        }

        String orderDirection = "DESC";
        if ("asc".equalsIgnoreCase(order)) {
            orderDirection = "ASC";
        }
        sql.append(" ORDER BY created_at ").append(orderDirection);

        return DBContext.getJdbcTemplate().query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> {
                    FRACategory fraCategory = new FRACategory();
                    fraCategory.setId(rs.getInt("id"));
                    fraCategory.setName(rs.getString("name"));
                    fraCategory.setDescription(rs.getString("description"));
                    fraCategory.setStatus(Status.valueOf(rs.getString("status")));
                    fraCategory.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return fraCategory;
                }
        );
    }

    public boolean suspendFRACategory(Integer fraCategoryId) {
        FRACategory fraCategory = this.getFRACategoryById(fraCategoryId);
        if (fraCategory != null && fraCategory.getStatus() != Status.SUSPENDED) {
            String sql = "UPDATE fra_category SET status = 'SUSPENDED' WHERE id = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    fraCategoryId
            );
            return row == 1;
        }
        return false;
    }

    public boolean activateFRACategory(Integer fraCategoryId) {
        FRACategory fraCategory = this.getFRACategoryById(fraCategoryId);
        if (fraCategory != null && fraCategory.getStatus() != Status.ACTIVE) {
            String sql = "UPDATE fra_category SET status = 'ACTIVE' WHERE id = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    fraCategoryId
            );
            return row == 1;
        }
        return false;
    }
}
