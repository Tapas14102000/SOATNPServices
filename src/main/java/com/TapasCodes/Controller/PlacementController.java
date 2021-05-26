package com.TapasCodes.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.TapasCodes.dao.PlacementRepository;
import com.TapasCodes.dao.StudentRepository;
import com.TapasCodes.entities.Placement;
import com.TapasCodes.entities.Student;
import com.TapasCodes.helper.Message;

@Controller
@RequestMapping("/admin")
public class PlacementController {
	
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private PlacementRepository placementRepository;
	
	
	@GetMapping("/profile")
	public String adminProfile(Model m, HttpSession session, Principal p) {
		System.out.println("accessing");
		String regd = p.getName();
		Student st = this.studentRepository.getUserByUserName(regd);
		StrongTextEncryptor ste = new StrongTextEncryptor();
		ste.setPassword((String) session.getAttribute("key"));
		st.setContact(ste.decrypt(st.getContact()));
		st.setFirstName(ste.decrypt(st.getFirstName()));
		st.setLastName(ste.decrypt(st.getLastName()));
		st.setYear(ste.decrypt(st.getYear()));
		m.addAttribute("student", st);
		session.setAttribute("stud", st);
		m.addAttribute("title",st.getFirstName());
		return "TNP/profiles";
	}
	
	@GetMapping("/addplacement")
	public String AddPlacement(Model m,HttpSession s) {
		Student st=(Student) s.getAttribute("stud");
		m.addAttribute("student", st);
		List<String> semester=Arrays.asList("5","6","7","8");
		m.addAttribute("semesterlist",semester);
		List<String> branch=Arrays.asList("CSE","CSIT","ECE","EE","EEE","MECH");
		m.addAttribute("branchlist",branch);
		List<String> type=Arrays.asList("yes","no");
		m.addAttribute("typelist",type);
		m.addAttribute("title", "Add Placement");
		m.addAttribute("placement",new Placement());
		return "TNP/add_placement";
	}
	@GetMapping("/privateplacement")
	public String privatePlacement(Model m,HttpSession s) {
		Student st=(Student) s.getAttribute("stud");
		m.addAttribute("student", st);
		List<String> type=Arrays.asList("Ignore","Acknowledge");
		m.addAttribute("typelist",type);
		m.addAttribute("title", "Add Placement");
		m.addAttribute("placement",new Placement());
		return "TNP/privateplacement";
	}
	@PostMapping("/process-placement-private")
	public String processplacementp(@Valid @ModelAttribute Placement placement,@RequestParam("document") MultipartFile file,
			@RequestParam(value="types",required=false) String[] types,
			Model m, HttpSession session, Principal p) {
		System.out.println("no error");
		Student st=(Student) session.getAttribute("stud");
		List<String> type=Arrays.asList("Ignore","Acknowledge");
		m.addAttribute("typelist",type);
		m.addAttribute("student", st);
		m.addAttribute("placement", placement);
		placement.setBranch("-PRIVATE-");
		placement.setSemester("-PRIVATE-");
		placement.setType("-PRIVATE-");
		placement.setYear("-PRIVATE-");
		placement.setDocfile("contact.png");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy hh.mm.ss aa");
		String formattedDate = dateFormat.format(new Date()).toString();
		System.out.println(formattedDate);
		placement.setTime(formattedDate);
		this.placementRepository.save(placement);
		Placement placement1=null;
		try {
			placement1=this.placementRepository.findByTime(formattedDate);
			placement1.setDocfile(placement1.getId()+file.getOriginalFilename());
			File savefFile = new ClassPathResource("static/placement").getFile();
			Path path1 = Paths.get(savefFile.getAbsolutePath() + File.separator + placement1.getId() +file.getOriginalFilename());
			Files.copy(file.getInputStream(), path1, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			session.setAttribute("message", new Message("Placement not added", "danger"));
			e.printStackTrace();
		}
		System.out.println(placement1);
		this.placementRepository.save(placement1);
		session.setAttribute("message", new Message("Placement added", "success"));
		return "TNP/privateplacement";
	}
	
	@PostMapping("/process-placement")
	public String processContact(@Valid @ModelAttribute Placement placement,@RequestParam("document") MultipartFile file,
			@RequestParam(value="branchs",required=false) String[] branchs,
			@RequestParam(value="semesters",required=false) String[] semesters,
			@RequestParam(value="types",required=false) String[] types,
			Model m, HttpSession session, Principal p) {
		System.out.println("no error");
		List<String> semester=Arrays.asList("5","6","7","8");
		m.addAttribute("semesterlist",semester);
		Student st=(Student) session.getAttribute("stud");
		List<String> branch=Arrays.asList("CSE","CSIT","ECE","EE","EEE","MECH");
		m.addAttribute("branchlist",branch);
		List<String> type=Arrays.asList("yes","no");
		m.addAttribute("typelist",type);
		m.addAttribute("student", st);
		m.addAttribute("placement", placement);
		String b="";
		if(branchs==null||semesters==null||types==null) {
			session.setAttribute("message", new Message("Choose atleast one branch or one semester or one category!", "danger"));
			return "TNP/add_placement";}
		for(int i=0;i<branchs.length;i++) {
		System.out.println(branchs[i]);
		if(i==branchs.length-1)
			{b+=branchs[i];
			break;
			}
		b+=branchs[i]+"/";
		}
		placement.setBranch(b);
		b="";
		for(int i=0;i<semesters.length;i++) {
			if(i==semesters.length-1)
			{
				b+=semesters[i];
				break;
			}
			b+=semesters[i]+"/";
		}
		placement.setSemester(b);
		b="";
		for(int i=0;i<types.length;i++) {
			if(i==types.length-1) {
				b+=types[i];
				break;
			}
			b+=types[i]+"/";
		}
		placement.setAllow("-GLOBAL-");
		placement.setPersonal("-GLOBAL-");
		placement.setType(b);
		placement.setDocfile("contact.png");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy hh.mm.ss aa");
		String formattedDate = dateFormat.format(new Date()).toString();
		System.out.println(formattedDate);
		placement.setTime(formattedDate);
		this.placementRepository.save(placement);
		Placement placement1=null;
		try {
			placement1=this.placementRepository.findByTime(formattedDate);
			placement1.setDocfile(placement1.getId()+file.getOriginalFilename());
			File savefFile = new ClassPathResource("static/placement").getFile();
			Path path1 = Paths.get(savefFile.getAbsolutePath() + File.separator + placement1.getId() +file.getOriginalFilename());
			Files.copy(file.getInputStream(), path1, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			session.setAttribute("message", new Message("Placement not added", "danger"));
			e.printStackTrace();
		}
		System.out.println(placement1);
		this.placementRepository.save(placement1);
		session.setAttribute("message", new Message("Placement added", "success"));
		return "TNP/add_placement";
	}
	
	@GetMapping("/placementlist/{page}")
	public String showplacements(@PathVariable("page") Integer page, Model m, HttpSession session) {
		Student st=(Student) session.getAttribute("stud");
		m.addAttribute("student", st);
		m.addAttribute("title", "Placements");
		Pageable pageable = PageRequest.of(page, 9);
		Page<Placement> placements = this.placementRepository.findPlacements(pageable);
		m.addAttribute("placements", placements);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", placements.getTotalPages());
		System.out.println("placements.getTotalPages() = " + placements.getTotalPages() + "\npage = " + page);
		return "TNP/all_placement";
	}

	@PostMapping("/deleteplacement/{cId}")
	public String DeleteContact(@PathVariable("cId") Integer cId, Model m, HttpSession session, Principal p) {
		try {
			Optional<Placement> placement=this.placementRepository.findById(cId);
			File savefFile = new ClassPathResource("static/placement").getFile();
			Path path = Paths.get(savefFile.getAbsolutePath() + File.separator + placement.get().getDocfile());
			Files.deleteIfExists(path);
			this.placementRepository.deleteById(cId);
			System.out.println("Deleted successfully!");
			
		} catch (Exception e) {
			System.out.println("Something went wrong!");
		}
		return "redirect:/admin/placementlist/0";
	}
	
	@GetMapping("/studentlist/{page}")
	public String showStudentlist(@PathVariable("page") Integer page, Model m, HttpSession session) {
		session.removeAttribute("message");
		Student st=(Student) session.getAttribute("stud");
		m.addAttribute("student", st);
		int total=this.studentRepository.countStudent();
		int placed=this.studentRepository.countPStudent();
		m.addAttribute("total",total);
		m.addAttribute("placedc", placed);
		m.addAttribute("title", "StudentList | TNP");
		Pageable pageable = PageRequest.of(page, 9);
		Page<Student> student=this.studentRepository.getAllUser(pageable);
		m.addAttribute("students", student);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", student.getTotalPages());
		System.out.println("placements.getTotalPages() = " + student.getTotalPages() + "\npage = " + page);
		return "TNP/all_student";
	}
	
	@GetMapping("/Placedstudentlist/{page}")
	public String showStudentlistPlaced(@PathVariable("page") Integer page, Model m, HttpSession session) {
		session.removeAttribute("message");
		Student st=(Student) session.getAttribute("stud");
		m.addAttribute("student", st);
		int total=this.studentRepository.countStudent();
		int placed=this.studentRepository.countPStudent();
		m.addAttribute("total",total);
		m.addAttribute("placedc", placed);
		m.addAttribute("title", "StudentList | TNP");
		Pageable pageable = PageRequest.of(page, 9);
		Page<Student> student=this.studentRepository.getAllUserPlaced(pageable);
		m.addAttribute("students", student);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", student.getTotalPages());
		System.out.println("placements.getTotalPages() = " + student.getTotalPages() + "\npage = " + page);
		return "TNP/all_student";
	}
	

	@GetMapping("/NotPlacedstudentlist/{page}")
	public String showStudentlistNotPlaced(@PathVariable("page") Integer page, Model m, HttpSession session) {
		session.removeAttribute("message");
		Student st=(Student) session.getAttribute("stud");
		m.addAttribute("student", st);
		int total=this.studentRepository.countStudent();
		int placed=this.studentRepository.countPStudent();
		m.addAttribute("total",total);
		m.addAttribute("placedc", placed);
		m.addAttribute("title", "StudentList | TNP");
		Pageable pageable = PageRequest.of(page, 9);
		Page<Student> student=this.studentRepository.getAllUserNPlaced(pageable);
		m.addAttribute("students", student);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", student.getTotalPages());
		System.out.println("placements.getTotalPages() = " + student.getTotalPages() + "\npage = " + page);
		return "TNP/all_student";
	}
	
	@GetMapping("/deletestudent/{regd}")
	public String deleteplacement(@PathVariable("regd") String regd, Model m, HttpSession session, Principal p) {
		Student st=this.studentRepository.getUserByUserName(regd);
		this.studentRepository.delete(st);
		return "redirect:/admin/studentlist/0";
	}
	
	@GetMapping("/toggleplaced/{regd}")
	public String tooglestudent(@PathVariable("regd") String regd, Model m, HttpSession session, Principal p) {
		Student st=this.studentRepository.getUserByUserName(regd);
		if(st.isPlaced())
			st.setPlaced(false);
		else
			st.setPlaced(true);
		this.studentRepository.save(st);
		return "redirect:/admin/studentlist/0";
	}
	
	@GetMapping("/search/{query}")
	@ResponseBody
	public ResponseEntity<List<Student>> search(Principal principal,@PathVariable("query") String query){
		List<Student> list=this.studentRepository.findByRegdContaining(query);
		return ResponseEntity.ok(list);
	}
	
	@GetMapping("/placementlist/{regd}/{page}")
	public String showContacts(@PathVariable("page") Integer page,@PathVariable("regd") String regd, Model m, HttpSession session, Principal p) {
		StrongTextEncryptor ste = new StrongTextEncryptor();
		ste.setPassword((String) session.getAttribute("key"));
		Student st = this.studentRepository.getUserByUserName(regd);
		st.setFirstName(ste.decrypt(st.getFirstName()));
		st.setLastName(ste.decrypt(st.getLastName()));
		st.setSemester(ste.decrypt(st.getSemester()));
		st.setYear(ste.decrypt(st.getYear()));
		Student st1 = this.studentRepository.getUserByUserName(p.getName());
		st1.setFirstName(ste.decrypt(st1.getFirstName()));
		st1.setLastName(ste.decrypt(st1.getLastName()));
		m.addAttribute("student",st1 );
		m.addAttribute("title", "Placements");
		m.addAttribute("regd", regd);
		Pageable pageable = PageRequest.of(page, 20);
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
		return "TNP/placement_list";
	}


}
