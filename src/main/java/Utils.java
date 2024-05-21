import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static j2html.TagCreator.*;

public class Utils {
    public static Optional<List<Teacher>> readTeachersFromJson(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Teacher> teachers = objectMapper.readValue(new File(path), objectMapper.getTypeFactory().constructCollectionType(List.class, Teacher.class));
            return Optional.of(teachers);
        } catch (Exception e) {
            System.out.println("Error reading teachers from json");
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<List<Subject>> readSubjectsFromJson(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Subject> subjects = objectMapper.readValue(new File(path), objectMapper.getTypeFactory().constructCollectionType(List.class, Subject.class));
            return Optional.of(subjects);
        } catch (Exception e) {
            System.out.println("Error reading subjects from json");
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    public static void generateHTMLDocumentForSubject(List<Subject> subjects) {

        // sort the subjects by weekDay and time
        subjects.sort((subject1, subject2) -> {
            var lecture1 = subject1.getLectureList().getFirst();
            var lecture2 = subject2.getLectureList().getFirst();
            return lecture1.allocatedPeriod.weekDay - lecture2.allocatedPeriod.weekDay;
        });

        // Create the html document with the style of an actual timetable
        var html = html(
                head(
                        title("Timetable"),
                        style(".timetable { border-collapse: collapse; width: 100%; }" +
                                ".timetable td, .timetable th { border: 1px solid #ddd; padding: 8px; }" +
                                ".timetable tr:nth-child(even){background-color: #f2f2f2;}" +
                                ".timetable tr:hover {background-color: #ddd;}" +
                                ".timetable th { padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #4CAF50; color: white; }")
                ),
                body(
                        h1("Timetable"),
                        table(attrs(".timetable"),
                                thead(
                                        tr(
                                                th("Subject"),
                                                th("Lecture"),
                                                th("Teacher"),
                                                th("Classroom"),
                                                th("Period"),
                                                th("Type"),
                                                th("Group")
                                        )
                                ),
                                tbody(
                                        each(subjects, subject -> each(subject.getLectureList(), lecture -> tr(
                                                td(subject.getName()),
                                                td(lecture.title),
                                                td(lecture.teacher != null ? lecture.teacher.name : ""),
                                                td(lecture.classroom != null ? lecture.classroom.name : ""),
                                                td(lecture.allocatedPeriod != null ? lecture.allocatedPeriod.time.toString() + " " + lecture.allocatedPeriod.weekDay : ""),
                                                td(lecture.type),
                                                td(lecture.allocatedGroup != null ? lecture.allocatedGroup.getName() : "")
                                        )))
                                )
                        )
                )
        );

        // Write the html to a file
        try {
            var file = new File("output.html");
            Files.writeString(file.toPath(), html.render());
        } catch (Exception e) {
            System.out.println("Error writing to file");
            System.out.println(e.getMessage());
        }
    }
}
