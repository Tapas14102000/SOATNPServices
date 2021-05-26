package com.TapasCodes.Controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TapasCodes.EmailService;
import com.TapasCodes.dao.StudentRepository;
import com.TapasCodes.entities.Student;
import com.TapasCodes.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private EmailService emailService;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@GetMapping("/")
	public String home(Model m) {
		m.addAttribute("title", "Home-TNP");
		return "Home/home";
	}

	@GetMapping("/signin")
	public String customLogin(Model m, HttpSession session) {
		m.addAttribute("title", "Login Page");
		return "Home/login";
	}

	@GetMapping("/logout")
	public String logout() {
		System.out.println("logout");
		return "redirect:/Home/signin?logout";
	}

	@GetMapping("/signup")
	public String register(Model m,HttpSession s) {
		m.addAttribute("title", "Register | Student");
		s.removeAttribute("message");
		List<String> semester=Arrays.asList(null,"5","6","7","8");
		m.addAttribute("semesterlist",semester);
		List<String> branch=Arrays.asList(null,"CSE","CSIT","ECE","EE","EEE","MECH");
		m.addAttribute("branchlist",branch);
		m.addAttribute("student", new Student());
		return "Home/signup";
	}

	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("student") Student student, BindingResult result1,
			@RequestParam("key") String key, Model m, HttpSession s) {
		if (result1.hasErrors()) {
			s.removeAttribute("message");
			System.out.println("Error : " + result1.toString());
			m.addAttribute("user", student);
			List<String> semester=Arrays.asList(null,"5","6","7","8");
			m.addAttribute("semesterlist",semester);
			List<String> branch=Arrays.asList(null,"CSE","CSIT","ECE","EE","EEE","MECH");
			m.addAttribute("branchlist",branch);
			return "Home/signup";
		}
		s.setAttribute("student", student);
		int otp = (int) (Math.random() * 9000) + 1000;
		System.out.println("Otp = " + otp);
		String fn = student.getFirstName(), ln = student.getLastName();
		String subject = "Training and Placement - OTP";
		String message = com.TapasCodes.message.before + "OTP" + com.TapasCodes.message.middle + fn + " " + ln
				+ com.TapasCodes.message.mid2 + "YOUR OTP IS <h3 style=\"color: rgb(45, 154, 187);\">" + otp + "</h3>"
				+ com.TapasCodes.message.end;
		String to = student.getEmail();
		boolean flag = this.emailService.sendEmail(subject, message, to);
		System.out.println("flag = " + flag);
		if (flag) {
			StrongTextEncryptor ste = new StrongTextEncryptor();
			ste.setPassword(key);
			student.setRole("ROLE_USER");
			student.setPlaced(false);
			student.setContact(ste.encrypt(student.getContact()));
			student.setFirstName(ste.encrypt(fn));
			student.setLastName(ste.encrypt(ln));
			student.setSemester(ste.encrypt(student.getSemester()));
			student.setYear(ste.encrypt(student.getYear()));
			student.setPassword(bcrypt.encode(student.getPassword()));
			s.setAttribute("student", student);

			s.setAttribute("myotp", otp);
			s.setAttribute("email", student.getEmail());
			s.removeAttribute("message");
			int i=3;
			s.setAttribute("attempt", i);
			return "Home/verify_email";
		} else {
			s.setAttribute("message", new Message("Internet not connected!!", "alert-danger"));
			List<String> semester=Arrays.asList(null,"5","6","7","8");
			m.addAttribute("semesterlist",semester);
			List<String> branch=Arrays.asList(null,"CSE","CSIT","ECE","EE","EEE","MECH");
			m.addAttribute("branchlist",branch);
			m.addAttribute("title", "Register | Student");
			return "Home/signup";
		}
	}

	@PostMapping("/verify-email")
	public String verifyEMAIL(@RequestParam("otp") int otp, HttpSession session, Model m) {
		int myotp = (int) session.getAttribute("myotp");
		System.out.println("OTP = "+myotp);
		if (myotp == otp) {
			Student student = (Student) session.getAttribute("student");
			try {
				this.studentRepository.save(student);
				System.out.println("no Error saving!");
				m.addAttribute("student", new Student());
				session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));
				session.removeAttribute("email");
				return "Home/signup";
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Error saving!");
				m.addAttribute("student", (Student) session.getAttribute("student"));
				if (e.getMessage().equalsIgnoreCase(
						"could not execute statement; SQL [n/a]; constraint [student.UK_fx3hw8ehpdqdcq1kcubg2arq3]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement")) {

					m.addAttribute("student", new Student());
					session.setAttribute("message", new Message("You are Already Registered !!", "alert-danger"));
				} else {
					session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
				}
				session.removeAttribute("email");
				return "Home/signup";
			}

		} else {
			int attempt=(int)session.getAttribute("attempt");
			session.setAttribute("message", "You Have entered Wrong OTP ! <"+attempt+" attempts left >");
			if(attempt>=1) {
				attempt--;
				session.setAttribute("attempt", attempt);
			return "Home/verify_email";
			}else {
				return "redirect:/";
			}
		}
	}

	@GetMapping("/check_key")
	public String checkkeyadmin(Model m, HttpSession session,Principal p) {
		session.setAttribute("regd", p.getName());
		m.addAttribute("title", "Secret Key");
		return "Home/check_key";
	}

	@PostMapping("/check-key")
	public String secretkey(@RequestParam("key") String key, Model m, HttpSession session,Principal p) {
		StrongTextEncryptor ste = new StrongTextEncryptor();
		String regd = session.getAttribute("regd").toString();
		ste.setPassword(key);
		System.out.println("key = "+key);
		Student st = null;
		try {
			st = this.studentRepository.getUserByUserName(regd);
			ste.decrypt(st.getFirstName());
			System.out.println(st);
			System.out.println("valid key");
			session.setAttribute("key", key);

		} catch (Exception e) {
			System.out.println(st);
			System.out.println("invalid key");
			session.setAttribute("message", new Message("Invalid Key", "danger"));
			return "redirect:/check_key";
		}
		Student student=this.studentRepository.getUserByUserName(p.getName());
		if(student.getRole().equals("ROLE_ADMIN"))
			return "redirect:/admin/profile";
		return "redirect:/student/profile";
	}
	
	@GetMapping("/deleteverification")
	private String verifyotp(Principal p,HttpSession s) {
		Student st=this.studentRepository.getUserByUserName(p.getName());
		int otp = (int) (Math.random() * 9000) + 1000;
		System.out.println("Otp = " + otp);

		String subject = "Training and Placement - OTP";
		String message = com.TapasCodes.message.before + "OTP" + com.TapasCodes.message.middle + p.getName()
				+ com.TapasCodes.message.mid2 + "YOUR OTP IS <h3 style=\"color: rgb(45, 154, 187);\">" + otp + "</h3>"
				+ com.TapasCodes.message.end;
		String to = st.getEmail();
		boolean flag = this.emailService.sendEmail(subject, message, to);
		System.out.println("flag = " + flag);
		if (flag) {
			s.setAttribute("myotp", otp);
			s.setAttribute("email", st.getEmail());
			s.removeAttribute("message");
			int i=3;
			s.setAttribute("attempt", i);
			return "Home/delete_verify";
		} else {
			s.setAttribute("message", new Message("Internet not connected!!", "danger"));
			return "redirect:/check_key";
		}
	}
	
	@PostMapping("/delete-verify")
	public String verifyEMAILdelete(@RequestParam("otp") int otp, HttpSession session, Model m,Principal p) {
		int myotp = (int) session.getAttribute("myotp");
		System.out.println("OTP = "+myotp);
		if (myotp == otp) {
			String regd=p.getName();
			Student student = this.studentRepository.getUserByUserName(regd);
			try {
				this.studentRepository.delete(student);
				m.addAttribute("student", new Student());
				session.setAttribute("message", new Message("Your old Account Deleted !!", "alert-success"));
				session.removeAttribute("email");
				return "Home/signup";
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Error in deletion!");
				return "redirect:/signin?logout";
			}

		} else {
			int attempt=(int)session.getAttribute("attempt");
			session.setAttribute("message", "You Have entered Wrong OTP ! <"+attempt+" attempts left >");
			if(attempt>=1) {
				attempt--;
				session.setAttribute("attempt", attempt);
				return "Home/delete_verify";
			}else {
				return "redirect:/";
			}			
		}
	}

	@PostMapping("/download/{filename}")
	public void download(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("filename") String filename) throws IOException {
		
		String dir = new ClassPathResource("static/placement").getFile().toString();
		Path file = Paths.get(dir, filename);
		if (Files.exists(file)) {
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition", "attachment;filename-" + filename);
			try {
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	@GetMapping("/admin_signup")
	public String registerAdmin(Model m,HttpSession s) {
		s.removeAttribute("message");
		m.addAttribute("title", "Register | Admin");
		m.addAttribute("student", new Student());
		return "Home/Adminsignup";
	}
	
	@PostMapping("/do_registerAdmin")
	public String registerAdmin(@ModelAttribute("student") Student student, BindingResult result1,
			@RequestParam("key") String key, Model m, HttpSession s) {
		if (this.studentRepository.getUserByUserName(student.getEmail())!=null) {
			s.removeAttribute("message");
			s.setAttribute("message", new Message("You are already Registered !!", "alert-danger"));
			System.out.println("Error : " + result1.toString());
			return "Home/Adminsignup";
		}
		s.setAttribute("student", student);
		int otp = (int) (Math.random() * 9000) + 1000;
		System.out.println("Otp = " + otp);
		String fn = student.getFirstName(), ln = student.getLastName();
		String subject = "Training and Placement - OTP";
		String message = com.TapasCodes.message.before + "OTP" + com.TapasCodes.message.middle + fn + " " + ln
				+ com.TapasCodes.message.mid2 + "YOUR OTP IS <h3 style=\"color: rgb(45, 154, 187);\">" + otp + "</h3>"
				+ com.TapasCodes.message.end;
		String to = "tapassahu076@gmail.com";								//TNP-OFFICIAL MAIL
		boolean flag = this.emailService.sendEmail(subject, message, to);
		System.out.println("flag = " + flag);
		if (flag) {
			StrongTextEncryptor ste = new StrongTextEncryptor();
			ste.setPassword(key);
			student.setRole("ROLE_ADMIN");
			student.setBranch("ADMIN");
			student.setCGPA(new BigDecimal(0.0));
			student.setHs(new BigDecimal(0.0));
			student.setS(new BigDecimal(0.0));
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy hh.mm.ss aa");
			String formattedDate = dateFormat.format(new Date()).toString();
			student.setYear(ste.encrypt(formattedDate));
			student.setSemester(ste.encrypt(formattedDate));
			student.setRegd(student.getEmail());
			student.setPlaced(false);
			student.setContact(ste.encrypt(student.getContact()));
			student.setFirstName(ste.encrypt(fn));
			student.setLastName(ste.encrypt(ln));
			student.setPassword(bcrypt.encode(student.getPassword()));
			s.setAttribute("student", student);

			s.setAttribute("myotp", otp);
			s.setAttribute("email", student.getEmail());
			s.removeAttribute("message");
			int i=3;
			s.setAttribute("attempt", i);
			return "Home/verify_email_admin";
		} else {
			s.setAttribute("message", new Message("Internet not connected !!", "alert-danger"));
			m.addAttribute("title", "Register | Admin");
			return "Home/Adminsignup";
		}
	}
	
	@PostMapping("/verify-email-admin")
	public String verifyEMAILAdmin(@RequestParam("otp") int otp, HttpSession session, Model m) {
		int myotp = (int) session.getAttribute("myotp");
		System.out.println("OTP = "+myotp);
		if (myotp == otp) {
			Student student = (Student) session.getAttribute("student");
			try {
				this.studentRepository.save(student);
				System.out.println("no Error saving!");
				m.addAttribute("student", new Student());
				session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));
				session.removeAttribute("email");
				return "Home/Adminsignup";
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Error saving!");
				m.addAttribute("student", (Student) session.getAttribute("student"));
				if (e.getMessage().equalsIgnoreCase(
						"could not execute statement; SQL [n/a]; constraint [student.UK_fx3hw8ehpdqdcq1kcubg2arq3]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement")) {

					m.addAttribute("student", new Student());
					session.setAttribute("message", new Message("You are Already Registered !!", "alert-danger"));
				} else {
					session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
				}
				session.removeAttribute("email");
				return "Home/Adminsignup";
			}

		} else {
			int attempt=(int)session.getAttribute("attempt");
			session.setAttribute("message", "You Have entered Wrong OTP ! <"+attempt+" attempts left >");
			if(attempt>=1) {
				attempt--;
				session.setAttribute("attempt", attempt);
			return "Home/verify_email_admin";
			}else {
				return "redirect:/";
			}
		}
	}

	
}
