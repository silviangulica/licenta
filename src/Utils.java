import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Utils {
    public static Optional<List<String>> readFromCsv(String path) {
        try {
            Scanner scanner = new Scanner(new File(path));
            List<String> lines = new ArrayList<>();

            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }

            return Optional.of(lines);
        } catch (Exception e) {
            System.out.println("File not found");
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }
}
