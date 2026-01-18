package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "teacher")
public class Teacher {
	@Id
	@SequenceGenerator(
			name = "teacher_seq",
			sequenceName = "teacher_seq",
			allocationSize = 1
	)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teacher_seq")
	@Column(name = "teacher_id")
	private Long teacherId;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	//@OneToMany(cascade = CascadeType.ALL)
	//@JoinColumn(
	//		name = "teacher_id",    				//name which will be in Course table with teacher id.
	//		referencedColumnName = "teacher_id"  	//name of reference column in Teacher table
	//)
	//private List<Course> courseList;
}
