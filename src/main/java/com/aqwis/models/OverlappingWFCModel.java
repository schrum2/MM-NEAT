package com.aqwis.models;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static javafx.application.Platform.exit;

public class OverlappingWFCModel extends WFCModel {
    int[][][][] propagator;
    int N;

    Byte[][] patterns;
    List<Color> colors;
    int foundation;

    protected Boolean propagate() {
        boolean change = false;
        boolean b;
        int x2, y2, sx, sy;
        boolean[] allowed;

        for (int x1 = 0; x1 < FMX; x1++) {
            for (int y1 = 0; y1 < FMY; y1++) {
                if (changes[x1][y1]) {
                    changes[x1][y1] = false;
                    for (int dx = -N + 1; dx < N; dx++) {
                        for (int dy = -N + 1; dy < N; dy++) {
                            x2 = x1 + dx;
                            y2 = y1 + dy;

                            sx = x2;
                            if (sx < 0) {
                                sx += FMX;
                            } else if (sx >= FMX) {
                                sx -= FMX;
                            }

                            sy = y2;
                            if (sy < 0) {
                                sy += FMY;
                            } else if (sy >= FMY) {
                                sy -= FMY;
                            }

                            if (!periodic && (sx + N > FMX || sy + N > FMY)) {
                                continue;
                            }
                            allowed = wave[sx][sy];

                            for (int t2 = 0; t2 < T; t2++) {
                                b = false;
                                int[] prop = propagator[t2][N - 1 - dx][N - 1 - dy];
                                for (int i1 = 0; i1 < prop.length && !b; i1++) {
                                    b = wave[x1][y1][prop[i1]];
                                }

                                if (allowed[t2] && !b) {
                                    changes[sx][sy] = true;
                                    change = true;
                                    allowed[t2] = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return change;
    }

    protected void clear() {
        super.clear();

        if (foundation != 0)
        {
            for (int x = 0; x < FMX; x++)
            {
                for (int t = 0; t < T; t++) {
                    if (t != foundation) {
                        wave[x][FMY - 1][t] = false;
                    }
                }
                changes[x][FMY - 1] = true;

                for (int y = 0; y < FMY - 1; y++) {
                    wave[x][y][foundation] = false;
                    changes[x][y] = true;
                }

                while (propagate()) {}
            }
        }
    }

    protected boolean onBoundary(int x, int y) {
        return !periodic && (x + N > FMX || y + N > FMY);
    }

    public BufferedImage graphics() {
        BufferedImage result = new BufferedImage(FMX, FMY, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < FMY; y++) for (int x = 0; x < FMX; x++)
        {
            List<Byte> contributors = new ArrayList<>();
            for (int dy = 0; dy < N; dy++) for (int dx = 0; dx < N; dx++)
            {
                int sx = x - dx;
                if (sx < 0) sx += FMX;

                int sy = y - dy;
                if (sy < 0) sy += FMY;

                if (onBoundary(sx, sy)) continue;
                for (int t = 0; t < T; t++) if (wave[sx][sy][t]) contributors.add(patterns[t][dx + dy * N]);
            }

            int r = 0, g = 0, b = 0;
            for (Byte c : contributors)
            {
                Color color = colors.get(c);
                r += color.getRed();
                g += color.getGreen();
                b += color.getBlue();
            }

            float lambda = 1.0f / (float) contributors.size();

            Color finalColor = new Color((int) (lambda * r), (int) (lambda * g), (int) (lambda * b));
            result.setRGB(x, y, finalColor.getRGB());
        }

        return result;
    }

    public OverlappingWFCModel(String name, int N, int width, int height, boolean periodicInput, boolean periodicOutput, int symmetry, int foundation) {
        this.N = N;
        FMX = width;
        FMY = height;
        periodic = periodicOutput;

        File imageFile;
        try {
            imageFile = new File(String.format("WaveFunctionCollapse/samples/%s.bmp", name));
            if (!imageFile.canRead()) {
                throw new Exception("No such file");
            }
        } catch (Exception e) {
            imageFile = new File(String.format("WaveFunctionCollapse/samples/%s.jpg", name));
        }
        BufferedImage bitmap = null;
        try {
            InputStream a = new FileInputStream(imageFile);
            bitmap = ImageIO.read(new MemoryCacheImageInputStream(a));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        int SMX = bitmap.getWidth();
        int SMY = bitmap.getHeight();

        byte[][] sample = new byte[SMX][SMY];
        colors = new ArrayList<>();

        for (int y = 0; y < SMY; y++) {
            for (int x = 0; x < SMX; x++) {
                Color color = new Color(bitmap.getRGB(x, y));

                int i = 0;
                for (Color listColor : colors) {
                    if (listColor.equals(color)) {
                        break;
                    }
                    i++;
                }

                if (i == colors.size()) {
                    colors.add(color);
                }
                sample[x][y] = (byte) i;
            }
        }

        int C = colors.size();
        int W = (int) Math.round(Math.pow(C, N*N));

        Function<BiFunction<Integer, Integer, Byte>, Byte[]> pattern = f -> {
            Byte[] result = new Byte[N*N];
            for (int y = 0; y < N; y++) {
                for (int x = 0; x < N; x++) {
                    result[x + y*N] = f.apply(x, y);
                }
            }
            return result;
        };

        BiFunction<Integer, Integer, Byte[]> patternFromSample = (x, y) -> pattern.apply((dx, dy) -> sample[(x+dx) % SMX][(y+dy) % SMY]);

        Function<Byte[], Byte[]> rotate = p -> pattern.apply((x, y) -> p[N-1-y+x*N]);
        Function<Byte[], Byte[]> reflect = p -> pattern.apply((x, y) -> p[N-1-x+y*N]);
        Function<Byte[], Integer> index = p -> {
            int result = 0;
            int power = 1;
            for (int i = 0; i < p.length; i++) {
                result += p[p.length-1-i]*power;
                power *= C;
            }
            return result;
        };

        Function<Integer, Byte[]> patternFromIndex = ind -> {
            int residue = ind;
            int power = W;
            Byte[] result = new Byte[N*N];

            for (int i = 0; i < result.length; i++) {
                power /= C;
                int count = 0;
                while (residue >= power) {
                    residue -= power;
                    count++;
                }
                result[i] = (byte) count;
            }

            return result;
        };

        Map<Integer, Integer> weights = new LinkedHashMap<>();
        for (int y = 0; y < (periodicInput ? SMY : SMY - N + 1); y++) {
            for (int x = 0; x < (periodicInput ? SMX : SMX - N + 1); x++) {
                Byte[][] ps = new Byte[8][];

                ps[0] = patternFromSample.apply(x, y);
                ps[1] = reflect.apply(ps[0]);
                ps[2] = rotate.apply(ps[0]);
                ps[3] = reflect.apply(ps[2]);
                ps[4] = rotate.apply(ps[2]);
                ps[5] = reflect.apply(ps[4]);
                ps[6] = rotate.apply(ps[4]);
                ps[7] = reflect.apply(ps[6]);

                for (int k = 0; k < symmetry; k++) {
                    int ind = index.apply(ps[k]);
                    if (weights.containsKey(ind)) {
                        weights.replace(ind, weights.get(ind)+1);
                    } else {
                        weights.put(ind, 1);
                    }
                }
            }
        }

        T = weights.size();
        this.foundation = (foundation + T) % T;

        patterns = new Byte[T][];
        stationary = new double[T];
        propagator = new int[T][][][];

        int outerCounter = 0;
        for (int w : weights.keySet()) {
            patterns[outerCounter] = patternFromIndex.apply(w);
            stationary[outerCounter] = weights.get(w);
            outerCounter++;
        }

        wave = new boolean[FMX][][];
        changes = new boolean[FMX][];

        for (int x = 0; x < FMX; x++) {
            wave[x] = new boolean[FMY][];
            changes[x] = new boolean[FMY];

            for (int y = 0; y < FMY; y++) {
                wave[x][y] = new boolean[T];
                changes[x][y] = false;

                for (int t = 0; t < T; t++) {
                    wave[x][y][t] = true;
                }
            }
        }

        QuadFunction<Byte[], Byte[], Integer, Integer, Boolean> agrees = (p1, p2, dx, dy) -> {
            int xmin = dx < 0 ? 0 : dx;
            int xmax = dx < 0 ? dx + N : N;
            int ymin = dy < 0 ? 0 : dy;
            int ymax = dy < 0 ? dy + N : N;

            for (int y = ymin; y < ymax; y++) {
                for (int x = xmin; x < xmax; x++) {
                    if (!(p1[x + N * y].equals(p2[x - dx + N * (y - dy)]))) {
                        return false;
                    }
                }
            }

            return true;
        };

        for (int t = 0; t < T; t++) {
            propagator[t] = new int[2*N-1][][];
            for (int x = 0; x < 2*N-1; x++) {
                propagator[t][x] = new int[2*N-1][];
                for (int y = 0; y < 2*N-1; y++) {
                    List<Integer> list = new ArrayList<>();
                    for (int t2 = 0; t2 < T; t2++) {
                        if (agrees.apply(patterns[t], patterns[t2], x - N + 1, y - N + 1)) {
                            list.add(t2);
                        }
                    }
                    propagator[t][x][y] = new int[list.size()];
                    for (int c = 0; c < list.size(); c++) {
                        propagator[t][x][y][c] = list.get(c);
                    }
                }
            }
        }
    }
}

@FunctionalInterface
interface QuadFunction<A, B, C, D, R> {
    R apply(A a, B b, C c, D d);
    default <V> QuadFunction<A, B, C, D, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c, D d) -> after.apply(apply(a, b, c, d));
    }
}