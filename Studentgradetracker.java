import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

// ─────────────────────────────────────────────
//  Student model
// ─────────────────────────────────────────────
class Student {
    private String name;
    private ArrayList<Double> grades;

    public Student(String name) {
        this.name   = name;
        this.grades = new ArrayList<>();
    }

    public String getName() { return name; }

    public void addGrade(double grade) {
        if (grade < 0 || grade > 100)
            throw new IllegalArgumentException("Grade must be between 0 and 100.");
        grades.add(grade);
    }

    public ArrayList<Double> getGrades() { return grades; }

    public double getAverage() {
        if (grades.isEmpty()) return 0.0;
        double sum = 0;
        for (double g : grades) sum += g;
        return sum / grades.size();
    }

    public double getHighest() {
        if (grades.isEmpty()) return 0.0;
        return Collections.max(grades);
    }

    public double getLowest() {
        if (grades.isEmpty()) return 0.0;
        return Collections.min(grades);
    }

    public String getLetterGrade() {
        double avg = getAverage();
        if (avg >= 90) return "A";
        if (avg >= 80) return "B";
        if (avg >= 70) return "C";
        if (avg >= 60) return "D";
        return "F";
    }
}

// ─────────────────────────────────────────────
//  Grade Manager
// ─────────────────────────────────────────────
class GradeManager {
    private ArrayList<Student> students = new ArrayList<>();

    public void addStudent(String name) {
        for (Student s : students)
            if (s.getName().equalsIgnoreCase(name))
                throw new IllegalArgumentException("Student '" + name + "' already exists.");
        students.add(new Student(name));
        System.out.println("  ✔ Student '" + name + "' added.");
    }

    public Student findStudent(String name) {
        for (Student s : students)
            if (s.getName().equalsIgnoreCase(name)) return s;
        return null;
    }

    public void addGradeToStudent(String name, double grade) {
        Student s = findStudent(name);
        if (s == null) throw new IllegalArgumentException("Student '" + name + "' not found.");
        s.addGrade(grade);
        System.out.printf("  ✔ Grade %.1f added for %s.%n", grade, s.getName());
    }

    public void removeStudent(String name) {
        Student s = findStudent(name);
        if (s == null) throw new IllegalArgumentException("Student '" + name + "' not found.");
        students.remove(s);
        System.out.println("  ✔ Student '" + name + "' removed.");
    }

    public void listStudents() {
        if (students.isEmpty()) {
            System.out.println("  No students recorded yet.");
            return;
        }
        System.out.println();
        System.out.printf("  %-20s  %8s  %7s  %7s  %5s%n",
                "Name", "Average", "Highest", "Lowest", "Grade");
        System.out.println("  " + "─".repeat(55));
        for (Student s : students) {
            System.out.printf("  %-20s  %7.1f%%  %6.1f%%  %6.1f%%  %5s%n",
                    s.getName(),
                    s.getAverage(),
                    s.getHighest(),
                    s.getLowest(),
                    s.getLetterGrade());
        }
    }

    public void viewGrades(String name) {
        Student s = findStudent(name);
        if (s == null) throw new IllegalArgumentException("Student '" + name + "' not found.");
        System.out.println();
        System.out.println("  Grades for: " + s.getName());
        if (s.getGrades().isEmpty()) {
            System.out.println("  No grades yet.");
            return;
        }
        ArrayList<Double> g = s.getGrades();
        System.out.print("  Scores   : ");
        for (int i = 0; i < g.size(); i++) {
            System.out.printf("%.1f", g.get(i));
            if (i < g.size() - 1) System.out.print(", ");
        }
        System.out.println();
        System.out.printf("  Average  : %.2f%% (%s)%n", s.getAverage(), s.getLetterGrade());
        System.out.printf("  Highest  : %.1f%%%n", s.getHighest());
        System.out.printf("  Lowest   : %.1f%%%n", s.getLowest());
    }

    public void printSummaryReport() {
        if (students.isEmpty()) {
            System.out.println("  No data to report.");
            return;
        }

        double classTotal = 0, classHigh = Double.MIN_VALUE, classLow = Double.MAX_VALUE;
        Student topStudent = null, bottomStudent = null;

        for (Student s : students) {
            double avg = s.getAverage();
            classTotal += avg;
            if (avg > classHigh) { classHigh = avg; topStudent = s; }
            if (avg < classLow)  { classLow  = avg; bottomStudent = s; }
        }
        double classAvg = classTotal / students.size();

        // Grade distribution
        int[] dist = new int[5]; // A B C D F
        for (Student s : students) {
            switch (s.getLetterGrade()) {
                case "A": dist[0]++; break;
                case "B": dist[1]++; break;
                case "C": dist[2]++; break;
                case "D": dist[3]++; break;
                case "F": dist[4]++; break;
            }
        }

        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════╗");
        System.out.println("  ║          STUDENT GRADE SUMMARY REPORT        ");
        System.out.println("");
        System.out.println();
        System.out.printf("  Total students  : %d%n", students.size());
        System.out.printf("  Class average   : %.2f%% (%s)%n", classAvg, letterFrom(classAvg));
        System.out.printf("  Highest average : %.2f%%  — %s%n", classHigh,
                topStudent != null ? topStudent.getName() : "-");
        System.out.printf("  Lowest average  : %.2f%%  — %s%n", classLow,
                bottomStudent != null ? bottomStudent.getName() : "-");
        System.out.println();
        System.out.println("  Grade distribution:");
        String[] labels = {"A (90–100)", "B (80–89)", "C (70–79)", "D (60–69)", "F (< 60)"};
        for (int i = 0; i < 5; i++) {
            int bars = (students.size() > 0) ? (dist[i] * 20 / students.size()) : 0;
            System.out.printf("  %-12s | %-20s %d student(s)%n",
                    labels[i], "█".repeat(bars), dist[i]);
        }
        System.out.println();
        System.out.println("  Detailed roster:");
        listStudents();
    }

    private String letterFrom(double avg) {
        if (avg >= 90) return "A";
        if (avg >= 80) return "B";
        if (avg >= 70) return "C";
        if (avg >= 60) return "D";
        return "F";
    }

    public boolean isEmpty() { return students.isEmpty(); }
}

// ─────────────────────────────────────────────
//  Main application
// ─────────────────────────────────────────────
class StudentGradeTracker {

    private static final Scanner sc = new Scanner(System.in);
    private static final GradeManager manager = new GradeManager();

    public static void main(String[] args) {
        printBanner();
        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            System.out.println();
            try {
                switch (choice) {
                    case "1": handleAddStudent();       break;
                    case "2": handleAddGrade();         break;
                    case "3": handleViewGrades();       break;
                    case "4": manager.listStudents();   break;
                    case "5": handleRemoveStudent();    break;
                    case "6": manager.printSummaryReport(); break;
                    case "7": running = false; System.out.println("  Goodbye!"); break;
                    default:  System.out.println("  Invalid option. Please choose 1–7.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  ✖ Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("  ✖ Unexpected error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    // ── Menu helpers ─────────────────────────

    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║       STUDENT GRADE TRACKER              ║");
        System.out.println("  ║     Manage grades. See results instantly. ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printMenu() {
        System.out.println("  ┌──────────────────────────────┐");
        System.out.println("  │           MAIN MENU           │");
        System.out.println("  ├──────────────────────────────┤");
        System.out.println("  │  1. Add student               │");
        System.out.println("  │  2. Add grade to student      │");
        System.out.println("  │  3. View student grades       │");
        System.out.println("  │  4. List all students         │");
        System.out.println("  │  5. Remove student            │");
        System.out.println("  │  6. Print summary report      │");
        System.out.println("  │  7. Exit                      │");
        System.out.println("  └──────────────────────────────┘");
        System.out.print("  Choice: ");
    }

    private static void handleAddStudent() {
        System.out.print("  Enter student name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) { System.out.println("  Name cannot be empty."); return; }
        manager.addStudent(name);
    }

    private static void handleAddGrade() {
        System.out.print("  Student name: ");
        String name = sc.nextLine().trim();
        System.out.print("  Grade (0–100): ");
        String input = sc.nextLine().trim();
        double grade = parseDouble(input);
        manager.addGradeToStudent(name, grade);
    }

    private static void handleViewGrades() {
        System.out.print("  Student name: ");
        String name = sc.nextLine().trim();
        manager.viewGrades(name);
    }

    private static void handleRemoveStudent() {
        System.out.print("  Student name to remove: ");
        String name = sc.nextLine().trim();
        manager.removeStudent(name);
    }

    private static double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + s + "' is not a valid number.");
        }
    }
}