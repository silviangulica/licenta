import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        var groupedLectures = lectures.stream()
                .collect(Collectors.groupingBy(Lecture::getGroupName));



        var html = html(
                head(
                        title("Lectures"),
                        link().withRel("stylesheet").withHref("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
                ),
                body(
                        div(
                                h1("Lectures")
                        ).withClasses("container")
                )
        );

        for (Map.Entry<String, List<Lecture>> entry : groupedLectures.entrySet()) {
            var group = entry.getKey();
            var groupLectures = entry.getValue();

            var groupTable = table(
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

            for (Lecture lecture : groupLectures) {
                var tr = tr(
                        td(lecture.title),
                        td(lecture.teacher.name),
                        td(lecture.classroom == null ? "" : lecture.classroom.name),
                        td(lecture.allocatedPeriod == null ? "" : lecture.allocatedPeriod.time.toString() + " " + lecture.allocatedPeriod.weekDay),
                        td(lecture.type),
                        td(lecture.getGroupName())
                );

                // check if two groups have the same period
                if (groupLectures.stream().filter(l -> l.allocatedPeriod != null && l.allocatedPeriod.equals(lecture.allocatedPeriod) && l.allocatedGroup.equals(lecture.allocatedGroup)).count() > 1) {
                    tr.withClasses("table-danger");
                }

                groupTable.with(tr);
            }

            var redRows = groupLectures.stream().filter(l -> l.allocatedPeriod != null && groupLectures.stream().filter(l2 -> l2.allocatedPeriod != null && l2.allocatedPeriod.equals(l.allocatedPeriod) && l2.allocatedGroup.equals(l.allocatedGroup)).count() > 1).count();
            groupTable.with(
                    caption("There are " + redRows + " rows in red")
            );

            html.with(
                    div(
                            h2("Group " + group),
                            groupTable.withClasses("table table-striped")
                    ).withClasses("container")
            );
        }

        try {
            var file = new File("output.html");
            Files.writeString(file.toPath(), html.render());
        } catch (Exception e) {
            System.out.println("Error writing to file");
            System.out.println(e.getMessage());
        }
    }
}
