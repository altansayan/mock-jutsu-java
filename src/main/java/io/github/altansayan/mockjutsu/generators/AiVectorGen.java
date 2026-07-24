package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** AI / Vector Database mock generator — L2-normalized float vectors. Mirrors ai_vector.py. */
public final class AiVectorGen {
    private AiVectorGen() {}

    public static String generate(String type, String locale) {
        return generate(type, locale, "");
    }

    public static String generate(String type, String locale, String qualifier) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "ai_embedding" -> {
                int dim = 1536;
                if (!qualifier.isEmpty()) try { dim = Math.max(1, Integer.parseInt(qualifier)); } catch (NumberFormatException ignored) {}
                yield embedding(rng, dim);
            }
            case "ai_vector" -> {
                int dim = 384;
                if (!qualifier.isEmpty()) try { dim = Math.max(1, Integer.parseInt(qualifier)); } catch (NumberFormatException ignored) {}
                yield embedding(rng, dim);
            }
            case "ai_sparse_vector" -> {
                int dims = 10000, nnz = 128;
                if (!qualifier.isEmpty()) {
                    String[] parts = qualifier.split("\\|", 2);
                    try { dims = Math.max(1, Integer.parseInt(parts[0])); } catch (NumberFormatException ignored) {}
                    if (parts.length > 1) try { nnz = Math.max(1, Integer.parseInt(parts[1])); } catch (NumberFormatException ignored) {}
                }
                yield sparseVector(rng, dims, nnz);
            }
            default -> "ERROR: Unknown AI vector type '" + type + "'";
        };
    }

    // Gaussian random vector normalized to L2 unit length.
    private static String embedding(ThreadLocalRandom rng, int dim) {
        double[] values = new double[dim];
        double norm = 0.0;
        for (int i = 0; i < dim; i++) {
            values[i] = rng.nextGaussian();
            norm += values[i] * values[i];
        }
        norm = Math.sqrt(norm);
        if (norm == 0.0) {
            values[0] = 1.0;
            norm = 1.0;
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < dim; i++) {
            if (i > 0) sb.append(",");
            sb.append(String.format(java.util.Locale.US, "%.6f", values[i] / norm));
        }
        return sb.append("]").toString();
    }

    private static String sparseVector(ThreadLocalRandom rng, int dims, int nnz) {
        int k = Math.min(nnz, dims);
        java.util.TreeSet<Integer> indices = new java.util.TreeSet<>();
        while (indices.size() < k) indices.add(rng.nextInt(0, dims));

        double[] raw = new double[k];
        double norm = 0.0;
        for (int i = 0; i < k; i++) {
            raw[i] = rng.nextDouble(0.001, 1.0);
            norm += raw[i] * raw[i];
        }
        norm = Math.sqrt(norm);

        StringBuilder sb = new StringBuilder("{\"indices\":[");
        boolean first = true;
        for (int idx : indices) {
            if (!first) sb.append(",");
            sb.append(idx);
            first = false;
        }
        sb.append("],\"values\":[");
        int i = 0;
        first = true;
        for (int idx : indices) {
            if (!first) sb.append(",");
            sb.append(String.format(java.util.Locale.US, "%.6f", raw[i] / norm));
            i++;
            first = false;
        }
        sb.append("]}");
        return sb.toString();
    }
}
