package nl.knaw.dans.easy.web.admin;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;

public class Ddm2EmdDoc {

    public static void main(String[] args) throws IOException {
        File[] files = new File("src/main/assembly/dist/res/example/editable/help").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".template");
            }
        });
        PrintStream out = new PrintStream(args[0]);
        out.println("<article id='helpIndex'><h1>Help Index</h1><ul>");
        for (File file : files) {
            String name = file.getName().replaceAll("[.].*$", "");
            out.println(String.format("<li><a href='#%s'>%s</a></li>", name, name));
        }
        out.println("</ul></article>");
        for (File file : files) {
            out.println(String.format("<article><a name='%s'> </a>", file.getName().replaceAll("[.].*$", "")));
            FileUtils.copyFile(file, out);
            out.println(String.format("\n</article>"));
        }
    }
}
