public class Main {
    public static void main(String[] args) {
        InputModel inputModel = new InputModel();
        var rawSolution = inputModel.generateRawSolution();
        var periodList = inputModel.getPeriodList();
        var classroomList = inputModel.getClassroomList();

//        SimulatedAnnealing ann = new SimulatedAnnealing(periodList, classroomList);
//        ann.runSimulatedAnnealing(rawSolution);

        TabuSearch tabuSearch = new TabuSearch(periodList, classroomList);
        tabuSearch.runTabuSearch(rawSolution);
    }
}