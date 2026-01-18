package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "course")
public class Course {
	@Id
	@SequenceGenerator(
			name = "course_seq",
			sequenceName = "course_seq",
			allocationSize = 1
	)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_seq")
	private Long courseId;

	private String title;

	private Integer credit;

	@ToString.Exclude
	@OneToOne(mappedBy = "course")
	private CourseMaterial courseMaterial;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(
			name = "teacher_id",
			referencedColumnName = "teacher_id"
	)
	private Teacher teacher;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "student_course_map",
			joinColumns = @JoinColumn(
					name = "course_id",     			//for this entity
					referencedColumnName = "courseId"
			),
			inverseJoinColumns = @JoinColumn(
					name = "student_id",				//for whatever is in variable (here Student)
					referencedColumnName = "studentId"
			)
	)
	private List<Student> studentList;

	public void addStudent(Student student){
		if(studentList == null){
			studentList = new ArrayList<Student>();
		}
		studentList.add(student);
	}
}
