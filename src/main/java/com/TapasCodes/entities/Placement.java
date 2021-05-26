package com.TapasCodes.entities;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Placement {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String time;
	private String docfile;
	private BigDecimal Ss;
	private BigDecimal Es;
	private BigDecimal SHs;
	private BigDecimal EHs;
	private BigDecimal SCGPA;
	private BigDecimal ECGPA;
	private String Semester;
	private String company;
	private String branch;
	private String year;
	private String message;
	private String type;
	private String allow;
	private String personal;
	@Override
	public String toString() {
		return "Placement [id=" + id + ", time=" + time + ", docfile=" + docfile + ", Ss=" + Ss + ", Es=" + Es
				+ ", SHs=" + SHs + ", EHs=" + EHs + ", SCGPA=" + SCGPA + ", ECGPA=" + ECGPA + ", Semester=" + Semester
				+ ", company=" + company + ", branch=" + branch + ", year=" + year + ", message=" + message + ", type="
				+ type + ", allow=" + allow + ", personal=" + personal + "]";
	}
	
	public String getAllow() {
		return allow;
	}

	public void setAllow(String allow) {
		this.allow = allow;
	}

	public String getPersonal() {
		return personal;
	}

	public void setPersonal(String personal) {
		this.personal = personal;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSemester() {
		return Semester;
	}

	public void setSemester(String semester) {
		Semester = semester;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getDocfile() {
		return docfile;
	}

	public void setDocfile(String docfile) {
		this.docfile = docfile;
	}

	public BigDecimal getSs() {
		return Ss;
	}
	public void setSs(BigDecimal ss) {
		Ss = ss;
	}
	public BigDecimal getEs() {
		return Es;
	}
	public void setEs(BigDecimal es) {
		Es = es;
	}
	public BigDecimal getSHs() {
		return SHs;
	}
	public void setSHs(BigDecimal sHs) {
		SHs = sHs;
	}
	public BigDecimal getEHs() {
		return EHs;
	}
	public void setEHs(BigDecimal eHs) {
		EHs = eHs;
	}
	public BigDecimal getSCGPA() {
		return SCGPA;
	}
	public void setSCGPA(BigDecimal sCGPA) {
		SCGPA = sCGPA;
	}
	public BigDecimal getECGPA() {
		return ECGPA;
	}
	public void setECGPA(BigDecimal eCGPA) {
		ECGPA = eCGPA;
	}
	
	
	
}
