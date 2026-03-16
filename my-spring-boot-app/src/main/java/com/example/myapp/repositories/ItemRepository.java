package com.example.myapp.repositories;

import com.example.myapp.models.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.name = :name")
    Optional<Item> findByNameForUpdate(@Param("name") String name);

    List<Item> findByCategory(String category);

    Page<Item> findByCategory(String category, Pageable pageable);

    List<Item> findByQuantityLessThan(Integer quantity);

    Page<Item> findByQuantityLessThan(Integer quantity, Pageable pageable);

    List<Item> findByUserId(Long userId);

    boolean existsByName(String name);

    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(i.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Item> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT i FROM Item i WHERE i.userId = :userId AND (" +
           "LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(i.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Item> searchByKeywordAndUserId(@Param("keyword") String keyword, @Param("userId") Long userId);

    @Query("SELECT DISTINCT i.category FROM Item i ORDER BY i.category")
    List<String> findAllCategories();
}