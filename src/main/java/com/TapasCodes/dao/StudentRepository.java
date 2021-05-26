package com.TapasCodes.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.TapasCodes.entities.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
	
	  @Query("select u from Student u where u.Regd = :Regd") 
	  public Student getUserByUserName(@Param("Regd") String Regd);
	  
	  @Query("select u from Student u where u.role = 'ROLE_USER' order by u.CGPA desc")
	  public Page<Student> getAllUser(Pageable pageable);

	  @Query("select u from Student u where u.role = 'ROLE_USER' and u.placed=1 order by u.CGPA desc")
	  public Page<Student> getAllUserPlaced(Pageable pageable);
	  
	  @Query("select u from Student u where u.role = 'ROLE_USER' and u.placed=0 order by u.CGPA desc")
	  public Page<Student> getAllUserNPlaced(Pageable pageable);

	  @Query("select u from Student u where u.Regd like %:Regd%")
	  public List<Student> findByRegdContaining(@Param("Regd") String query);
	  
	  @Query("select count(*) from Student u where u.role = 'ROLE_USER'")
	  public int countStudent();
	  
	  @Query("select count(*) from Student u where u.role = 'ROLE_USER' and u.placed=1")
	  public int countPStudent();
	  
}
