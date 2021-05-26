package com.TapasCodes.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "STUDENT")
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(unique = true)
	@NotBlank(message = "Your Identity is required!")
	private String Regd;
	@NotBlank(message = "Your First Name is required!")
	private String FirstName;
	@NotBlank(message = "Your Last Name is required!")
	private String LastName;
	@NotBlank(message = "Your Semester is required!")
	private String Semester;
	@NotBlank(message = "Your Email is required!")
	private String Email;
	@NotBlank(message = "Set a Password!")
	private String password;
	@Range(max = 100 , min = 0)
	@NotNull(message = "How much did you scored?")
	@Digits(integer=3, fraction=2 , message = "fraction part must be of 2 digits")
	private BigDecimal s;
	@Range(max = 100 , min = 0)
	@NotNull(message = "How much did you scored?")
	@Digits(integer=3, fraction=2, message = "fraction part must be of 2 digits")
	private BigDecimal Hs;
	@Range(max = 10 , min = 0)
	@NotNull(message = "How much did you scored?")
	@Digits(integer=2, fraction=2, message = "fraction part must be of 2 digits")
	private BigDecimal CGPA;
	@NotBlank(message = "Your Contact is required!")
	private String Contact;
	@NotBlank(message = "Graduation year is required!")
	private String year;
	@NotBlank(message = "Branch is required!")
	private String branch;
	private String role;
	private boolean placed;
	
	@Override
	public String toString() {
		return "Student [id=" + id + ", Regd=" + Regd + ", FirstName=" + FirstName + ", LastName=" + LastName
				+ ", Semester=" + Semester + ", Email=" + Email + ", password=" + password + ", s=" + s + ", Hs=" + Hs
				+ ", CGPA=" + CGPA + ", Contact=" + Contact + ", year=" + year + ", branch=" + branch + ", role=" + role
				+ ", placed=" + placed + "]";
	}
	
	
	public String getBranch() {
		return branch;
	}


	public void setBranch(String branch) {
		this.branch = branch;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public boolean isPlaced() {
		return placed;
	}

	public void setPlaced(boolean placed) {
		this.placed = placed;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRegd() {
		return Regd;
	}
	public void setRegd(String regd) {
		Regd = regd;
	}	
	public String getFirstName() {
		return FirstName;
	}
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	public String getLastName() {
		return LastName;
	}
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	public String getSemester() {
		return Semester;
	}
	public void setSemester(String semester) {
		Semester = semester;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public BigDecimal getS() {
		return s;
	}
	public void setS(BigDecimal s) {
		this.s = s;
	}
	public BigDecimal getHs() {
		return Hs;
	}
	public void setHs(BigDecimal hs) {
		Hs = hs;
	}
	public BigDecimal getCGPA() {
		return CGPA;
	}
	public void setCGPA(BigDecimal cGPA) {
		CGPA = cGPA;
	}
	public String getContact() {
		return Contact;
	}
	public void setContact(String contact) {
		Contact = contact;
	}
	
}
