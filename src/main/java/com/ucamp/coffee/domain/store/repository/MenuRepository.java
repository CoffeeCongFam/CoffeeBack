package com.ucamp.coffee.domain.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucamp.coffee.domain.store.entity.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long>{

}
