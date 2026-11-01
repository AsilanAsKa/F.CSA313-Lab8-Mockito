package mn.edu.must.sqat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class CourseRegistrationServiceTest {

    @Mock
    StudentRepository repository;

    @Mock
    EmailSender emailSender;

    CourseRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new CourseRegistrationService(repository, emailSender);
    }

    // A1: Идэвхтэй оюутан амжилттай бүртгүүлэх
    @Test
    void activeStudent_registersSuccessfully_returnsOK() {
        Student student = new Student(
                "S001",
                "student@gmail.com",
                true,
                List.of("CS101")
        );

        when(repository.findStudent("S001")).thenReturn(student);
        when(repository.courseExists("CS102")).thenReturn(true);
        when(repository.prerequisites("CS102")).thenReturn(List.of("CS101"));

        String result = service.register("S001", "CS102");

        assertEquals("OK", result);

        verify(repository).saveRegistration("S001", "CS102");
        verify(emailSender).send(
                eq("student@gmail.com"),
                eq("Бүртгэл баталгаажлаа"),
                contains("CS102")
        );
    }
        // A2: Идэвхгүй оюутан бүртгүүлэх боломжгүй
    @Test
    void inactiveStudent_returnsErrorAndDoesNotRegister() {
        Student student = new Student(
                "S002",
                "inactive@gmail.com",
                false,
                List.of()
        );

        when(repository.findStudent("S002")).thenReturn(student);

        String result = service.register("S002", "CS102");

        assertEquals("ERROR_INACTIVE_STUDENT", result);

        verify(repository, never()).saveRegistration(anyString(), anyString());
        verifyNoInteractions(emailSender);
    }
        // A3: Оюутан олдоогүй
    @Test
    void studentNotFound_returnsErrorAndDoesNotRegister() {
        when(repository.findStudent("S003")).thenReturn(null);

        String result = service.register("S003", "CS102");

        assertEquals("ERROR_NO_STUDENT", result);

        verify(repository, never()).saveRegistration(anyString(), anyString());
        verifyNoInteractions(emailSender);
    }
        // A4: Өмнөх холбоо хичээл дутуу
    @Test
    void missingPrerequisites_returnsErrorAndDoesNotRegister() {
        Student student = new Student(
                "S004",
                "student4@gmail.com",
                true,
                List.of("CS100")
        );

        when(repository.findStudent("S004")).thenReturn(student);
        when(repository.courseExists("CS102")).thenReturn(true);
        when(repository.prerequisites("CS102")).thenReturn(List.of("CS101"));

        String result = service.register("S004", "CS102");

        assertEquals("ERROR_PREREQUISITES", result);

        verify(repository, never()).saveRegistration(anyString(), anyString());
        verifyNoInteractions(emailSender);
    }
        // A5: Имэйл илгээхэд алдаа гарсан ч бүртгэл амжилттай байх
    @Test
    void emailFailure_registrationStillSucceeds() {
        Student student = new Student(
                "S005",
                "student5@gmail.com",
                true,
                List.of("CS101")
        );

        when(repository.findStudent("S005")).thenReturn(student);
        when(repository.courseExists("CS102")).thenReturn(true);
        when(repository.prerequisites("CS102")).thenReturn(List.of("CS101"));

        doThrow(new RuntimeException("Email error"))
                .when(emailSender)
                .send(anyString(), anyString(), anyString());

        String result = service.register("S005", "CS102");

        assertEquals("OK", result);
        verify(repository).saveRegistration("S005", "CS102");
        verify(emailSender).send(anyString(), anyString(), anyString());
    }
        // A6: studentId болон courseId null үед алдаа үүсэх
    @Test
    void nullInput_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(null, "CS102")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register("S006", null)
        );

        verifyNoInteractions(repository, emailSender);
    }
        // A7: Имэйлийн body дотор хичээлийн код байгаа эсэх
    @Test
    void emailBody_containsCourseCode() {
        Student student = new Student(
                "S007",
                "student7@gmail.com",
                true,
                List.of("CS101")
        );

        when(repository.findStudent("S007")).thenReturn(student);
        when(repository.courseExists("CS102")).thenReturn(true);
        when(repository.prerequisites("CS102")).thenReturn(List.of("CS101"));

        String result = service.register("S007", "CS102");

        assertEquals("OK", result);

        org.mockito.ArgumentCaptor<String> bodyCaptor =
                org.mockito.ArgumentCaptor.forClass(String.class);

        verify(emailSender).send(
                eq("student7@gmail.com"),
                eq("Бүртгэл баталгаажлаа"),
                bodyCaptor.capture()
        );

        assertTrue(bodyCaptor.getValue().contains("CS102"));
    }
     // A8: Parameterized test — 3 өөр алдааны нөхцөл
    @ParameterizedTest
    @CsvSource({
            "S008, CS102, ERROR_NO_STUDENT",
            "S009, CS102, ERROR_INACTIVE_STUDENT",
            "S010, CS999, ERROR_NO_COURSE"
    })
    void registrationErrors_returnExpectedResult(
            String studentId,
            String courseId,
            String expectedResult
    ) {
        Student activeStudent = new Student(
                studentId, "student@gmail.com", true, List.of("CS101")
        );

        lenient().when(repository.findStudent(studentId))
                .thenReturn(activeStudent);
        lenient().when(repository.courseExists(courseId))
                .thenReturn(true);

        if (expectedResult.equals("ERROR_NO_STUDENT")) {
            when(repository.findStudent(studentId)).thenReturn(null);
        } else if (expectedResult.equals("ERROR_INACTIVE_STUDENT")) {
            when(repository.findStudent(studentId))
                    .thenReturn(new Student(
                            studentId, "student@gmail.com", false, List.of()
                    ));
        } else if (expectedResult.equals("ERROR_NO_COURSE")) {
            when(repository.courseExists(courseId)).thenReturn(false);
        }

        String result = service.register(studentId, courseId);

        assertEquals(expectedResult, result);

        verify(repository, never()).saveRegistration(anyString(), anyString());
        verifyNoInteractions(emailSender);
    }
}
