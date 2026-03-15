package com.example.myapp.repositories;

import com.example.myapp.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    Optional<Item> findByName(String name);
    
    Page<Item> findByCategory(String category, Pageable pageable);

    Page<Item> findByQuantityLessThan(Integer quantity, Pageable pageable);
    
    boolean existsByName(String name);
    
    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(i.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Item> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT DISTINCT i.category FROM Item i ORDER BY i.category")
    List<String> findAllCategories();
}