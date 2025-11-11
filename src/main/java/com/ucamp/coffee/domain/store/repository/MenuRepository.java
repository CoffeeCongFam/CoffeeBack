package com.ucamp.coffee.domain.store.repository;

import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long>{
    @Query("""
        SELECT m
        FROM Menu m
        JOIN FETCH m.store s
        WHERE s = :store
    """)
    List<Menu> findMenuListWithStore(@Param("store") Store store);
}
