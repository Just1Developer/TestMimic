package net.justonedev;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public final class Convert {
    public static void main(String[] args) {
        try {
            concatAndPrint(new File("test"));
        } catch (Throwable e) { e.printStackTrace(); }
    }

    private Convert() { }

    private static void concatAndPrint(File folder) throws IOException {
        StringJoiner joiner = new StringJoiner("%T");
        for (File file : Objects.requireNonNull(
                folder.listFiles((dir, name) -> name.endsWith(".txt") || name.endsWith(".protocol")))) {
            List<String> lines = Files.readAllLines(file.toPath());
            StringJoiner joiner2 = new StringJoiner("%n");
            lines.forEach(line -> joiner2.add(line.replace("\\", "\\\\").replace(" ", "§")));
            joiner.add(joiner2.toString());
        }
        StringBuilder builder = new StringBuilder();
        builder.append("    public static final String TESTS = \"\"\"").append(System.lineSeparator());
        String allTestcases = joiner.toString();
        while (allTestcases.length() > 140) {
            builder.append(allTestcases, 0, 140).append(System.lineSeparator());
            allTestcases = allTestcases.substring(140);
        }
        if (!allTestcases.isEmpty()) {
            builder.append(allTestcases).append(System.lineSeparator());
        }
        builder.append("            \"\"\";");
        System.out.println(builder);
    }
}
