package com.example.model;

import com.example.model.embeddable.Guardian;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(
		name = "student",
		uniqueConstraints = @UniqueConstraint(columnNames = {"email_address"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Student {
	@Id
	@SequenceGenerator(
			name = "student_seq",
			sequenceName = "student_seq",
			allocationSize = 1
	)
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "student_seq"
	)
	private Long studentId;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@NotBlank
	@Column(name = "email_address", nullable = false)
	private String emailId;

	@Embedded
	private Guardian guardian;
}
