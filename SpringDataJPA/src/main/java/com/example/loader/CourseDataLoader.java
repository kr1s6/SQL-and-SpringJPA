package com.example.loader;

import com.example.model.Course;
import com.example.model.CourseMaterial;
import com.example.model.Student;
import com.example.model.Teacher;
import com.example.model.embeddable.Guardian;
import com.example.repository.CourseMaterialRepository;
import com.example.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseDataLoader implements CommandLineRunner {

	private final CourseRepository courseRepository;
	private final CourseMaterialRepository courseMaterialRepository;

	@Override
	public void run(String... args) throws Exception {
		Course course1 = Course.builder()
				.title("kurs1")
				.credit(1000)
				.teacher(Teacher.builder()
						.firstName("FFFFF")
						.lastName("FFFFFSDD")
						.build())
				.build();

		Course course2 = Course.builder()
				.title("kurs2")
				.credit(2000)
				.teacher(Teacher.builder()
						.firstName("22222222")
						.lastName("22222222")
						.build())
				.build();

		CourseMaterial courseMaterial1 = CourseMaterial.builder()
				.url("DDDDDD")
				.course(course1)
				.build();

		CourseMaterial courseMaterial2 = CourseMaterial.builder()
				.url("XXXXXX")
				.course(course2)
				.build();

		courseMaterialRepository.save(courseMaterial1);
		courseMaterialRepository.save(courseMaterial2);
		//System.out.println(courseRepository.findAll());
		//System.out.println(courseMaterialRepository.findAll());

		Course course3 = Course.builder()
				.title("3333333")
				.credit(4)
				.build();

		Course course4 = Course.builder()
				.title("4444444")
				.credit(15)
				.build();

		Course course5 = Course.builder()
				.title("5555555")
				.credit(15)
				.build();
		courseRepository.saveAll(List.of(course3, course4, course5));

		Pageable firstPageWithThreeRecords = PageRequest.of(0, 3);

		List<Course> courseList = courseRepository.findAll(firstPageWithThreeRecords).getContent();
		long totalElements = courseRepository.findAll(firstPageWithThreeRecords).getTotalElements();
		long totalEPages = courseRepository.findAll(firstPageWithThreeRecords).getTotalPages();

		System.out.println("Courses = " + courseList);
		System.out.println("totalElements = " + totalElements);
		System.out.println("totalEPages = " + totalEPages);

		//sorting

		Pageable sortByTitle = PageRequest.of(0, 2, Sort.by("title"));
		Pageable sortByCredit = PageRequest.of(0, 2, Sort.by("credit").descending());
		Pageable sortByTitleAndCreditDesc = PageRequest.of(0, 2, Sort.by("title").descending().and(Sort.by("credit")));

		List<Course> courses = courseRepository.findAll(sortByTitleAndCreditDesc).getContent();
		System.out.println(courses);

		System.out.println(courseRepository.findByTitleContaining("kurs", sortByTitle).getContent());


		//save ManyToMany

		Student student1 = Student.builder()
				.firstName("AdamGrant")
				.lastName("GrantHHAHAH")
				.emailId("Email")
				.guardian(new Guardian("SSSS", "SSSS", "SSSS"))
				.build();

		Student student2 = Student.builder()
				.firstName("Edwart")
				.lastName(null)
				.emailId("EmailDDDDD")
				.guardian(new Guardian("XXX", "XXXX", "XXXXXXXXXXXXXX"))
				.build();

		Course course6 = Course.builder()
				.title("kurs1")
				.credit(1000)
				.teacher(Teacher.builder()
						.firstName("FFFFF")
						.lastName("FFFFFSDD")
						.build())
				.build();

		course6.addStudent(student1);
		course6.addStudent(student2);

		courseRepository.save(course6);
	}
}
