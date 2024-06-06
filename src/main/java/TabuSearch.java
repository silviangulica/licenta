import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TabuSearch {
    private final List<Classroom> classrooms;
    private final List<Period> periods;
    private final ConstraintChecker hardConstraintChecker;
    private final ConstraintChecker softConstraintChecker;
    private final List<List<Lecture>> tabuList;
    private final int tabuTenure;

    public TabuSearch(List<Period> periods, List<Classroom> classrooms) {
        this.classrooms = classrooms;
        this.periods = periods;

        // The HARD constraints
        this.hardConstraintChecker = new ConstraintChecker();
        this.hardConstraintChecker.addConstraint(new OneLecturePerGroupConstraint());
        this.hardConstraintChecker.addConstraint(new UniqueTimetableSlotConstraint());
        this.hardConstraintChecker.addConstraint(new IsTeacherTeachingConstraint());

        // The SOFT constraints
        this.softConstraintChecker = new ConstraintChecker();
        this.softConstraintChecker.addConstraint(new FirstTwoWeekDayCourseConstraint());
        this.softConstraintChecker.addConstraint(new FitClassroomConstraint());

        this.tabuList = new LinkedList<>();
        this.tabuTenure = 10; // or any other appropriate value
    }

//    public void runTabuSearch(List<Lecture> rawSolution) {
//        // implemeting the tabusearch
//        var solution = rawSolution;
//        var optimalSolution = rawSolution;
//
//        double k = 0;
//        double ratio = 0.1;
//        var max_itter = 20000;
//        double ratio_max = 1.0;
//        double ratio_min = 0.05;
//
//        while (k <= max_itter) {
//            List<List<Lecture>> listOfNeighbours = new ArrayList<>();
//
//            // generate neighbours
//            listOfNeighbours.add(relocate(solution, ratio));
//            listOfNeighbours.add(swap(solution, ratio));
//            listOfNeighbours.add(swap(solution));
//            listOfNeighbours.add(relocate(solution));
//
//
//            // cautam acel vecin care are energia cea mai mica pentru hard + soft
//            var bestNeighbour = listOfNeighbours.stream()
//                    .min((l1, l2) -> {
//                        var score1 = hardConstraintChecker.checkConstraints(l1) + softConstraintChecker.checkConstraints(l1);
//                        var score2 = hardConstraintChecker.checkConstraints(l2) + softConstraintChecker.checkConstraints(l2);
//                        return score1 - score2;
//                    }).get();
//
//            // daca vecinul gasit este mai bun decat solutia curenta, atunci il setam ca solutie
//            var bestNeighbourScore = hardConstraintChecker.checkConstraints(bestNeighbour) + softConstraintChecker.checkConstraints(bestNeighbour);
//            var bestSolutionScore = hardConstraintChecker.checkConstraints(optimalSolution) + softConstraintChecker.checkConstraints(optimalSolution);
//
//            if (bestNeighbourScore < bestSolutionScore) {
//                optimalSolution = bestNeighbour;
//                System.out.println("\nIteration: " + k);
//                System.out.println("Ratio: " + ratio);
//                System.out.println("bestSolutionScore: " + bestSolutionScore);
//            }
//            solution = optimalSolution;
//
//            // update the ratio
//            // first using the linear update
//            //ratio = k / max_itter * (ratio_max - ratio_min) + ratio_min;
//
//            ratio = ratio_min * Math.pow(ratio_max / ratio_min, k / max_itter);
//            k += ratio;
//        }
//
//        // generate the html document
//        Utils.generateHTMLDocumentForLectures(optimalSolution);
//    }

    public void runTabuSearch(List<Lecture> rawSolution) {
        // implementing the tabu search
        var solution = rawSolution;
        var optimalSolution = rawSolution;

        double k = 0;
        double ratio = 0.1;
        var max_iter = 100000;
        double ratio_max = 1.0;
        double ratio_min = 0.05;

        while (k <= max_iter) {
            List<List<Lecture>> listOfNeighbours = new ArrayList<>();

            // generate neighbours
            listOfNeighbours.add(relocate(solution, ratio));
            listOfNeighbours.add(swap(solution, ratio));
//            listOfNeighbours.add(swap(solution));
//            listOfNeighbours.add(relocate(solution));

            var bestNeighbour = listOfNeighbours.stream()
                    .min((l1, l2) -> {
                        var score1 = hardConstraintChecker.checkConstraints(l1) + softConstraintChecker.checkConstraints(l1);
                        var score2 = hardConstraintChecker.checkConstraints(l2) + softConstraintChecker.checkConstraints(l2);
                        return score1 - score2;
                    }).get();

            var bestNeighbourScore = hardConstraintChecker.checkConstraints(bestNeighbour) + softConstraintChecker.checkConstraints(bestNeighbour);
            var bestSolutionScore = hardConstraintChecker.checkConstraints(optimalSolution) + softConstraintChecker.checkConstraints(optimalSolution);

            boolean isTabu = tabuList.contains(bestNeighbour);
            boolean isAspirationCriteriaMet = bestNeighbourScore < bestSolutionScore;

            if (!isTabu || isAspirationCriteriaMet) {
                if (bestNeighbourScore < bestSolutionScore) {
                    optimalSolution = bestNeighbour;
                    System.out.println("\nIteration: " + k);
                    System.out.println("Ratio: " + ratio);
                    System.out.println("bestSolutionScore: " + bestSolutionScore);
                }
                solution = optimalSolution;

                tabuList.add(new ArrayList<>(bestNeighbour));
                if (tabuList.size() > tabuTenure) {
                    tabuList.removeFirst();
                }
            }

            // update the ratio
            // first using the linear update
            // ratio = k / max_iter * (ratio_max - ratio_min) + ratio_min;

            ratio = ratio_min * Math.pow(ratio_max / ratio_min, k / max_iter);
            k += ratio;
        }

        // generate the html document
        Utils.generateHTMLDocumentForLectures(optimalSolution);
    }


    public List<Lecture> relocate(List<Lecture> lectures, double ratio) {
        var possibleSolution = Utils.deepCopy(lectures);
        var selectedLectures = new ArrayList<Lecture>();

        var randomRange = Math.ceil(ratio / lectures.size());

        for (int i = 0; i < randomRange; i++) {
            var randomIndex = (int) (Math.random() * lectures.size());
            if (selectedLectures.stream().noneMatch(lec -> lec.equals(possibleSolution.get(randomIndex)))) {
                selectedLectures.add(possibleSolution.get(randomIndex));
            }
        }

        for (var lecture : selectedLectures) {
            for (var period : periods) {
                for (var classroom : classrooms) {
                    if (possibleSolution.stream().noneMatch(lec -> lec.allocatedPeriod.equals(period) && lec.classroom.equals(classroom))) {
                        lecture.allocatedPeriod = period;
                        lecture.classroom = classroom;
                    }
                }
            }
        }

        return possibleSolution;
    }

    public List<Lecture> relocate(List<Lecture> lectures) {
        var possibleSolution = Utils.deepCopy(lectures);

        for (var lecture : possibleSolution) {
            for (var period : periods) {
                for (var classroom : classrooms) {
                    if (possibleSolution.stream().noneMatch(lec -> lec.allocatedPeriod.equals(period) && lec.classroom.equals(classroom))) {
                        lecture.allocatedPeriod = period;
                        lecture.classroom = classroom;
                    }
                }
            }
        }

        return possibleSolution;
    }

    public List<Lecture> swap(List<Lecture> lectures) {
        var possibleSolution = Utils.deepCopy(lectures);

        // Schimba doua lecturi cu locu pentru toate lecturile
        for (var lec1 : possibleSolution) {
            for (var lec2 : possibleSolution) {
                if (lec1 != lec2) {
                    var tempPeriod = lec1.allocatedPeriod;
                    var tempClassroom = lec1.classroom;

                    lec1.allocatedPeriod = lec2.allocatedPeriod;
                    lec1.classroom = lec2.classroom;

                    lec2.allocatedPeriod = tempPeriod;
                    lec2.classroom = tempClassroom;
                }
            }
        }

        return possibleSolution;
    }

    public List<Lecture> swap(List<Lecture> lectures, double ratio) {
        var possibleSolution = Utils.deepCopy(lectures);

        var n = lectures.size();
        var randomRange = Math.ceil((double) 1 /2 * (2*n - 1 - Math.sqrt(4*n*(n-1) * (1 - ratio) + 1)));

        var selectedLectures = new ArrayList<Lecture>();
        for (int i = 0; i < randomRange; i++) {
            var randomIndex = (int) (Math.random() * lectures.size());
            if (selectedLectures.stream().noneMatch(lec -> lec.equals(possibleSolution.get(randomIndex)))) {
                selectedLectures.add(possibleSolution.get(randomIndex));
            }
        }

        var selectedLectures2 = new ArrayList<Lecture>();
        for (int i = 0; i < randomRange; i++) {
            var randomIndex = (int) (Math.random() * lectures.size());
            // verifica sa nu fie deja selectata si sa nu fie aceeasi cu prima
            if (selectedLectures.stream().noneMatch(lec -> lec.equals(possibleSolution.get(randomIndex))) &&
                    selectedLectures2.stream().noneMatch(lec -> lec.equals(possibleSolution.get(randomIndex)))) {
                selectedLectures2.add(possibleSolution.get(randomIndex));
            }
        }

        for (var lec1 : selectedLectures) {
            for (var lec2 : selectedLectures2) {
                if (lec1 != lec2) {
                    var tempPeriod = lec1.allocatedPeriod;
                    var tempClassroom = lec1.classroom;

                    lec1.allocatedPeriod = lec2.allocatedPeriod;
                    lec1.classroom = lec2.classroom;

                    lec2.allocatedPeriod = tempPeriod;
                    lec2.classroom = tempClassroom;
                }
            }
        }


        return possibleSolution;
    }

//    public List<Lecture> ejectionChain(List<Lecture> lectures, double ratio) {
//        var possibleResults = Utils.deepCopy(lectures);
//
//        // selecteaza random ratio * n lectures
//        var selectedLectures = new ArrayList<Lecture>();
//        for (int i = 0; i < Math.ceil(ratio * lectures.size()); i++) {
//            var randomIndex = (int) (Math.random() * lectures.size());
//            selectedLectures.add(possibleResults.get(randomIndex));
//        }
//
//        // pentru fiecare selected lecture, se incearca sa se mute in alta perioada si sala,
//        // dar daca energia configuratiei hard este == 0 sau daca exista mai mult de 2 conflicte ne oprim
//        // in schimb daca exista un conflict, se incearca sa se mute in alta perioada si sala
//        for (var lecture : selectedLectures) {
//            for (var newPeriod : periods) {
//                for (var newClassroom : classrooms) {
//                    if (possibleResults.stream().noneMatch(lec -> lec.allocatedPeriod.equals(newPeriod) && lec.classroom.equals(newClassroom))) {
//                        lecture.allocatedPeriod = newPeriod;
//                        lecture.classroom = newClassroom;
//
//                        if (hardConstraintChecker.checkConstraints(possibleResults) == 0) {
//                            return possibleResults;
//                        }
//                    }
//                }
//            }
//        }
//
//        return possibleResults;
//    }
//
//    public List<Lecture> ejectionChain(List<Lecture> lectures) {
//        var possibleResults = Utils.deepCopy(lectures);
//
//        // incearca sa mute fiecare lecture catre alta perioada si sala.
//        // daca primeste hardconstraint == 0 sau daca are mai mult de 2 conflicte, se opreste si returneaza
//        // daca are doar 1 confilict, incearca sa il rezolve
//        for (var lecture : possibleResults) {
//            for (var newPeriod : periods) {
//                for (var newClassroom : classrooms) {
//                    if (possibleResults.stream().noneMatch(lec -> lec.allocatedPeriod.equals(newPeriod) && lec.classroom.equals(newClassroom))) {
//                        lecture.allocatedPeriod = newPeriod;
//                        lecture.classroom = newClassroom;
//
//                        if (hardConstraintChecker.checkConstraints(possibleResults) == 0) {
//                            return possibleResults;
//                        }
//                    }
//                }
//            }
//        }
//
//        return possibleResults;
//    }
}
