package heatsim.simulation;
import heatsim.settings.Settings;

import java.util.*;
import java.util.concurrent.*;

public class ParallelGrid extends AbstractFastGrid {
    public ParallelGrid(int width, int height) {
        temperatures = new double[width * height];
        Arrays.fill(temperatures, 0);
        this.width = width;
        this.height = height;
        previousTemperatures = new double[width * height];
    }

    public void recalculateGrid() {
        System.arraycopy(temperatures, 0, previousTemperatures, 0, height * width);

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        List<Future<Double>> futures = new ArrayList<>();

        int rowsPerThread = (width - 2 * Settings.BORDER_SIZE) / numThreads;
        int startX = Settings.BORDER_SIZE;
        int endX = width - Settings.BORDER_SIZE;

        for (int t = 0; t < numThreads; t++) {
            int fromX = startX + t * rowsPerThread;
            int toX = (t == numThreads - 1) ? endX : fromX + rowsPerThread;

            futures.add(pool.submit(() -> {
                double localMaxChange = 0;
                for (int x = fromX; x < toX; x++) {
                    int rowOffset = x * width;
                    for (int y = Settings.BORDER_SIZE; y < height - Settings.BORDER_SIZE; y++) {
                        if (temperatures[rowOffset + y] == Settings.HEAT_RETENTION_THRESHOLD) continue;
                        double totalSum = getNeighboursSum(rowOffset, y);
                        if (totalSum == 0) continue;
                        totalSum /= 4;
                        temperatures[rowOffset + y] = totalSum;

                        double tempChange = Math.abs(totalSum - previousTemperatures[rowOffset + y]);
                        if (tempChange > localMaxChange) {
                            localMaxChange = tempChange;
                        }

                        //totalCalculations.incrementAndGet();
                    }
                }
                return localMaxChange;
            }));
        }

        double maxChange = 0;
        for (Future<Double> f : futures) {
            try {
                double local = f.get();
                if (local > maxChange) maxChange = local;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.maxTempChange = maxChange;
        pool.shutdown();
    }

}
