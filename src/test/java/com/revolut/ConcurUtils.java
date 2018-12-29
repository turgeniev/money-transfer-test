package com.revolut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Helper functions for concurrency tests.
 */
public class ConcurUtils {

    public static <T> void assertNoFailures(List<Future<T>> results) {
        int failures = 0;
        for (Future<T> future : results) {
            try {
                future.get();
            } catch (ExecutionException e) {
                System.out.println(e);
                failures++;
            } catch (InterruptedException e) {
                System.out.println(e);
                return;
            }
        }
        assertEquals(0, failures);
    }

    public static <T> List<Callable<T>> generateShuffled(int count, Callable<T> first, Callable<T> ... rest) {
        List<Callable<T>> callables = new ArrayList<>();
        callables.add(first);
        if (rest != null) {
            callables.addAll(Arrays.asList(rest));
        }

        List<Callable<T>> result = new ArrayList<>(count * callables.size());
        callables.forEach(c -> {
                    for (int i = 0; i < count; i++) {
                        result.add(c);
                    }
                });

        Collections.shuffle(result);
        return result;
    }
}
