package com.TapasCodes.Controller;

import java.security.Principal;
import javax.servlet.http.HttpSession;

import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.TapasCodes.dao.PlacementRepository;
import com.TapasCodes.dao.StudentRepository;
import com.TapasCodes.entities.Placement;
import com.TapasCodes.entities.Student;

@Controller
@RequestMapping("/student")
public class StudentController {

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private PlacementRepository placementRepository;

	@GetMapping("/profile")
	public String yourProfile(Model m, HttpSession session, Principal p) {
		String regd = p.getName();
		Student st = this.studentRepository.getUserByUserName(regd);
		StrongTextEncryptor ste = new StrongTextEncryptor();
		ste.setPassword((String) session.getAttribute("key"));
		st.setContact(ste.decrypt(st.getContact()));
		st.setFirstName(ste.decrypt(st.getFirstName()));
		st.setLastName(ste.decrypt(st.getLastName()));
		st.setSemester(ste.decrypt(st.getSemester()));
		st.setYear(ste.decrypt(st.getYear()));
		m.addAttribute("title", st.getFirstName());
		m.addAttribute("student", st);
		System.out.println(st);
		return "Student/profile";
	}

	@GetMapping("/placementlist/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, HttpSession session, Principal p) {
		String regd = p.getName();
		StrongTextEncryptor ste = new StrongTextEncryptor();
		ste.setPassword((String) session.getAttribute("key"));
		Student st = this.studentRepository.getUserByUserName(regd);
		st.setFirstName(ste.decrypt(st.getFirstName()));
		st.setLastName(ste.decrypt(st.getLastName()));
		st.setSemester(ste.decrypt(st.getSemester()));
		st.setYear(ste.decrypt(st.getYear()));
		m.addAttribute("student", st);
		m.addAttribute("title", "Placements");
		Pageable pageable = PageRequest.of(page, 9);
		String type="";
		if(st.isPlaced()) {
			type+="yes";
		}else {
			type+="no";
		}
		Page<Placement> placements = this.placementRepository.findPlacement(st.getHs(), st.getS(), st.getCGPA(),
				st.getYear(), st.getSemester(),st.getBranch(),type,regd, pageable);

		m.addAttribute("placements", placements);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", placements.getTotalPages());
		System.out.println("placements.getTotalPages() = " + placements.getTotalPages() + "\npage = " + page);
		return "Student/placement_list";
	}

}
