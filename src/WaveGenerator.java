import java.util.HashMap;
import java.util.Random;
import java.util.function.BiFunction;

import Exception.InvalidGeneratorId;

public class WaveGenerator {
    private static HashMap<Integer, BiFunction<Double, Double, Double>> waveGeneratorMap;

    public static double sin(double freq, double t) {
        return Math.sin(freq * t * Math.PI * 2);
    }

    public static double square(double freq, double t) {
        double T = 1.0 / freq;
        int sw = (int)(Math.floor(t/(0.5*T)) + 1) % 2;
        return sw==1 ? 1 : -1;
    }

    public static double sawtooth(double freq, double t) {
        double T = 1.0 / freq;
        return ((t % T) / T)*2 - 1;
    }

    public static double noise(double freq, double t) {
        Random rand = new Random();
        return rand.nextDouble() * 2 - 1;
    }

    public static BiFunction<Double, Double, Double>  getWaveGenerator(Integer generatorId) throws InvalidGeneratorId {
        HashMap<Integer, BiFunction<Double, Double, Double>> wgm;
        wgm = WaveGenerator.getWaveGeneratorMapInstance();

        if (wgm.containsKey(generatorId)) {
            return wgm.get(generatorId);
        }

        throw new InvalidGeneratorId();
    }

    private static HashMap<Integer, BiFunction<Double, Double, Double>> getWaveGeneratorMapInstance() {
        if (WaveGenerator.waveGeneratorMap != null) {
            return WaveGenerator.waveGeneratorMap;
        }

        WaveGenerator.waveGeneratorMap = new HashMap<Integer, BiFunction<Double, Double, Double>>();
        WaveGenerator.waveGeneratorMap.put(0, WaveGenerator::sin);
        WaveGenerator.waveGeneratorMap.put(1, WaveGenerator::square);
        WaveGenerator.waveGeneratorMap.put(2, WaveGenerator::sawtooth);
        WaveGenerator.waveGeneratorMap.put(10, WaveGenerator::noise);

        return WaveGenerator.waveGeneratorMap;
    }
}
