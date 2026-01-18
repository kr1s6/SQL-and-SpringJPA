package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "course_material")
public class CourseMaterial {
	@Id
	@SequenceGenerator(
			name = "course_material_seq",
			sequenceName = "course_material_seq",
			allocationSize = 1
	)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_material_seq")
	@Column(name = "course_material_id")
	private Long courseMaterialId;
	private String url;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(
			name = "course_id",						//name which will be in CourseMaterial table with course id.
			referencedColumnName = "courseId")   	//name of reference column in Course table
	private Course course;

}
