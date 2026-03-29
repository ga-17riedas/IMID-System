package com.example.medicalsystem.repository;

import com.example.medicalsystem.model.AIModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AIModelRepository extends JpaRepository<AIModel, Long> {
    Page<AIModel> findByType(String type, Pageable pageable);
    
    @Modifying
    @Transactional
    @Query("UPDATE AIModel m SET m.active = false WHERE m.type = :type")
    void deactivateAllByType(@Param("type") String type);
    
    boolean existsByTypeAndActive(String type, boolean active);

    /**
     * 根据类型查找模型
     */
    List<AIModel> findByType(String type);
    
    /**
     * 查找指定类型的激活模型
     */
    Optional<AIModel> findByTypeAndActiveTrue(String type);
    
    /**
     * 根据名称查找模型
     */
    List<AIModel> findByName(String name);
    
    /**
     * 根据名称和版本查找模型
     */
    Optional<AIModel> findByNameAndVersion(String name, String version);
} 