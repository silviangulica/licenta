import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public static void generateHTMLDocumentForLectures(List<Lecture> lectures) {

        lectures.sort((l1, l2) -> {
            if (l1.allocatedGroup == null && l2.allocatedGroup == null) {
                return 0;
            }
            if (l1.allocatedGroup == null) {
                return -1;
            }
            if (l2.allocatedGroup == null) {
                return 1;
            }
            return l1.allocatedGroup.getName().compareTo(l2.allocatedGroup.getName());
        });


        var table = table(
                thead(
                        tr(
                                th("Title"),
                                th("Teacher"),
                                th("Classroom"),
                                th("Period"),
                                th("Type"),
                                th("Group")
                        )
                )
        );

        for (Lecture lecture : lectures) {
            var tr = tr(
                    td(lecture.title),
                    td(lecture.teacher.name),
                    td(lecture.classroom == null ? "" : lecture.classroom.name),
                    td(lecture.allocatedPeriod == null ? "" : lecture.allocatedPeriod.time.toString() + " " + lecture.allocatedPeriod.weekDay),
                    td(lecture.type),
                    td(lecture.getGroupName())
            );

            if (lectures.stream().filter(l -> l.allocatedPeriod != null && l.allocatedPeriod.equals(lecture.allocatedPeriod) && l.allocatedGroup.equals(lecture.allocatedGroup)).count() > 1) {
                tr.withClasses("table-danger");
            }

            table.with(tr);
        }

        var redRows = lectures.stream().filter(l -> l.allocatedPeriod != null && lectures.stream().filter(l2 -> l2.allocatedPeriod != null && l2.allocatedPeriod.equals(l.allocatedPeriod) && l2.allocatedGroup.equals(l.allocatedGroup)).count() > 1).count();
        table.with(
                caption("There are " + redRows + " rows in red")
        );

        var html = html(
                head(
                        title("Lectures"),
                        link().withRel("stylesheet").withHref("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
                ),
                body(
                        div(
                                h1("Lectures"),
                                table.withClasses("table table-striped")
                        ).withClasses("container")
                )
        );


        try {
            var file = new File("output.html");
            Files.writeString(file.toPath(), html.render());
        } catch (Exception e) {
            System.out.println("Error writing to file");
            System.out.println(e.getMessage());
        }
    }
}
