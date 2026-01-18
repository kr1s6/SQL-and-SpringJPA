package com.example.loader;

import com.example.model.Course;
import com.example.model.Teacher;
import com.example.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeacherDataLoader implements CommandLineRunner {

	private final TeacherRepository teacherRepository;

	@Override
	public void run(String... args) throws Exception {
		//
		//Course course1 = Course.builder()
		//		.title("DDD")
		//		.credit(4)
		//		.build();
		//
		//Course course2 = Course.builder()
		//		.title("XXX")
		//		.credit(15)
		//		.build();

		Teacher teacher1 = Teacher.builder()
				.firstName("teacher1")
				.lastName("surname1")
				//.courseList(List.of(course1, course2))
				.build();

		teacherRepository.save(teacher1);

		System.out.println(teacherRepository.findAll());
		//System.out.println(courseRepository.findAll());
		//System.out.println(teacherRepository.findAllWithCourses());  //for OneToMany

	}
}
