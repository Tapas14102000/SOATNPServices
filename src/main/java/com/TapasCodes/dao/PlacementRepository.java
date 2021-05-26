package com.TapasCodes.dao;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.TapasCodes.entities.Placement;

public interface PlacementRepository extends JpaRepository<Placement, Integer> {

		@Query("from Placement as c where ( c.Ss <=:S and c.Es >=:S and c.SHs <=:Hs and c.EHs >=:Hs and "
				+ "c.SCGPA <=:CGPA and c.ECGPA >=:CGPA and c.year =:year and c.type like %:type% and c.Semester like %:semester% and "
				+ "c.branch like %:branch% ) or ( c.personal like %:regd% and c.allow ='Acknowledge' ) or ( c.allow = 'Ignore' and "
				+ " c.personal not like %:regd% ) order by c.id desc")
		public Page<Placement> findPlacement(
				@Param("Hs") BigDecimal Hs,
				@Param("S") BigDecimal S,
				@Param("CGPA") BigDecimal CGPA,
				@Param("year") String year,
				@Param("semester") String semester,
				@Param("branch") String branch,
				@Param("type") String type,
				@Param("regd") String regd,
				Pageable pageable);
		
		@Query("from Placement as c order by c.id desc")
		public Page<Placement> findPlacements(Pageable pageable);
		
		public Placement findByTime(String Time);
}