package com.javarush.island.voetsky.Database;

import com.javarush.island.voetsky.Model.Nature;
import com.javarush.island.voetsky.Model.UtilityClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.javarush.island.voetsky.Model.Model.scheduledThreadPool;

public class Island implements Runnable {

    private static volatile Island instance;

    private int MAX_TICK = 0;
    private int currentTick = 0;

    public void setMaxTick(int maxTick) {
        MAX_TICK = maxTick;
    }

    private Object[][] matrix;
    private int xSize;
    private int ySize;
    private ArrayList<Nature> simulationList;

    public Object[][] getMatrix() {
        return matrix;
    }

    public boolean isOver() {
        return currentTick < MAX_TICK;
    }

    public int getxSize() {
        return xSize;
    }

    public int getySize() {
        return ySize;
    }

    public void makeIsland(int x, int y, int maxTick) {
        xSize = x;
        ySize = y;
        setMaxTick(maxTick);
        matrix = new Object[xSize][ySize];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = new ArrayList<Nature>();
            }
        }
    }

    public void showIsland() throws Exception {
        fillDisplayMatrix();
        generalNatureStats();



    }

    public synchronized void startNature() {
        for (var i : simulationList) {
            new Thread(i).start();
        }
    }

    public void generalNatureStats() {
        List<Nature> statsList = new ArrayList<>();
        HashMap<String, Long> natureStats = new HashMap<>();
        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                ArrayList<Nature> tempList = (ArrayList<Nature>) matrix[x][y];
                if (!tempList.isEmpty()) {
                    statsList.addAll(tempList);
                    for (var i : statsList.stream().map(Object::toString).collect(Collectors.toSet())) {
                        natureStats.put(i, statsList.stream().filter(k -> Objects.equals(k.toString(), i)).count());
                    }
                }
            }
        }
        System.out.println("----------------------------------------------\n");
        System.out.println("Статистика по острову:\n");
        System.out.println("\t" + natureStats);
        System.out.println("\n----------------------------------------------");
    }

    public void fillDisplayMatrix() throws Exception {
        System.out.println("Как работает дисплей пользователя:\n");
        HashMap<String, Long>[][] result = new HashMap[xSize][ySize];

        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                ArrayList<Nature> tempList = (ArrayList<Nature>) matrix[x][y];
                if (!tempList.isEmpty()) {
                    HashMap<String, Long> currentIndexNature = new HashMap<>();
                    for (var i : tempList.stream().map(Object::toString).collect(Collectors.toSet())) {
                        currentIndexNature.put(i, tempList.stream().filter(f -> Objects.equals(f.toString(), i)).count());
                    }
                    result[x][y] = currentIndexNature;
                }
            }
        }
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                if (result[i][j] == null) {
                    System.out.print("\t{}");
                } else {
                    System.out.print("\t" + result[i][j]);
                }
            }
            System.out.println();
        }
    }

    public boolean checkAnimalCount(int x, int y, Nature nature) {
        ArrayList<Nature> temp = showActualNode(x, y);
        temp.stream()
                .filter(g -> g != nature && g.getClass().isInstance(nature.getClass()))
                .collect(Collectors.toList());
        if (nature.getMaxNatureCount() >= temp.size()){
            return true;
        }
        return false;
    }

    public ArrayList<Nature> showActualNode(int x, int y) {
        return (ArrayList<Nature>) matrix[x][y];
    }

    public void setStartRandomPosition(ArrayList<Nature> arrayList) {
        for (var nature : arrayList) {
            setAnimalPosition(UtilityClass.random(xSize -1), UtilityClass.random(ySize -1), nature);
        }
        simulationList = arrayList;

        System.out.println("\nКак на самом деле заполнена матрица Object[][]\n");


        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print("\t" + matrix[i][j]);
            }
            System.out.println("\t");
        }

        System.out.println("----------------------------------------------\n");
    }

    public void setAnimalPosition(int x, int y, Nature nature) {
        if (x < xSize && y < ySize && x >= 0 && y >= 0) {
            ArrayList<Nature> tempList = (ArrayList<Nature>) matrix[x][y];
            tempList.add(nature);
            matrix[x][y] = tempList;
            nature.setCoordinates(x, y);

        } else {
            throw new RuntimeException("Вариации координат выходят за границы массива");
        }

    }

    public void removeAnimal(int x, int y, Nature nature) {
        if (x < xSize && y < ySize && x >= 0 && y >= 0) {
            ArrayList<Nature> tempList = (ArrayList<Nature>) matrix[x][y];
            tempList.remove(nature);
            matrix[x][y] = tempList;

        } else {
            throw new RuntimeException("Вариации координат выходят за границы массива");
        }
    }

    public synchronized void removeDead(Nature nature, int x, int y) {

        simulationList.removeIf(nature1 -> nature1 == nature);

        ArrayList<Nature> tempList = (ArrayList<Nature>) matrix[x][y];
        tempList.removeIf(nature1 -> nature1 == nature);
        matrix[x][y] = tempList;

    }

//    public void setAnimalPosition(int x, int y, NatureOld nature, int[] bound) {
//        if (x < xSize && y < ySize && x > 0 && y > 0) {
//            ArrayList<NatureOld> tempList = (ArrayList<NatureOld>) matrix[x][y];
//            tempList.add(nature);
//            matrix[x][y] = tempList;
//            nature.setCoordinates(x, y);
//
//        } else {
//            throw new RuntimeException("Вариации координат выходят за границы массива");
//        }
    //
    //    }
//    public synchronized void addNew(int x, int y, NatureOld nature){
//        if (x < xSize && y < ySize && x > 0 && y > 0) {
//            ArrayList<NatureOld> tempList = (ArrayList<NatureOld>) matrix[x][y];
//            tempList.add(nature);
//            matrix[x][y] = tempList;
//            nature.setCoordinates(x, y);
//
//        } else {
//            throw new RuntimeException("Вариации координат выходят за границы массива");
//        }
//
//    }

    private Island() {
    }

    public static Island getInstance() {
        if (instance == null) { // Первая проверка без блокировки
            synchronized (Island.class) {
                if (instance == null) { // Вторая проверка внутри синхронизированного блока
                    instance = new Island();
                }
            }
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            if (isOver()) {
                currentTick++;
                showIsland();
            } else {
                System.out.println("\nКонец симуляции");
                scheduledThreadPool.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
