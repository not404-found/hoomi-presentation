package com.hoomicorp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Unit test for simple App.
 */
public class HoomiManagementAppTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void test() {
        assertEquals(5, new MakeChange(1125).getNoteCount());
        assertEquals(4, new MakeChange(715).getNoteCount());
        assertEquals(5, new MakeChange(323).getNoteCount());
    }

    public static class MakeChange {
        private final List<Integer> notes = List.of(500, 200, 100, 50, 20, 10, 5, 2, 1);
        private final AtomicInteger amount;

        public MakeChange(final int amount) {
            this.amount = new AtomicInteger(amount);
        }

        public int getNoteCount() {
            int notesCount = 0;
            List<Integer> lowerNotes = notes.stream().filter(item -> amount.get() >= item).collect(Collectors.toList());
            for (int i = 0; amount.get() > 0; i++) {
                amount.set(amount.get() - lowerNotes.get(0));
                lowerNotes = notes.stream().filter(item -> amount.get() >= item).collect(Collectors.toList());
                notesCount++;
            }
            return notesCount;
        }
    }
}
