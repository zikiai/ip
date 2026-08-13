import java.util.Scanner;

public class Zikiai {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String banner = " _____ _ _    _       _\n"
                + "|__  /(_) | _(_) __ _(_)\n"
                + "  / / | | |/ / |/ _` | |\n"
                + " / /_ | |   <| | (_| | |\n"
                + "/____||_|_|\\_\\_|\\__,_|_|";
        String line = "_".repeat(60);
        System.out.println(line);
        System.out.println(banner);
        System.out.println("Hello! I'm Zikiai.");
        System.out.println("What can I do for you?");
        System.out.println(line);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                break;
            }
            System.out.println(line);
            System.out.println("    " + input);
            System.out.println(line);
        }
        System.out.println(line);
        System.out.println("okay, bai bai");
        System.out.println(line);
        scanner.close();
    }
}
