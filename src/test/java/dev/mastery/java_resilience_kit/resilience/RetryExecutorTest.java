package dev.mastery.java_resilience_kit.resilience;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RetryExecutorTest {

	@Test
	void shouldNotSleepOnSuccess() throws Exception {
		RetryPolicy policy = RetryPolicy.builder().build();
		RetryExecutor executor = new RetryExecutor(policy);
		AtomicInteger attempts = new AtomicInteger();

		Callable<String> operation = () -> {
			if (attempts.incrementAndGet() == 1){
				throw new IOException("Temporary failure");
			}

			return "Success";
		};

		String result = executor.execute(operation);

		Assertions.assertEquals("Success", result);
		Assertions.assertEquals(2, attempts.get());
	}

	@Test
	void shouldDelayBetweenRetries() throws Exception {
		RetryPolicy policy = RetryPolicy.builder()
				.delay(Duration.ofMillis(100))
				.build();
		RetryExecutor executor = new RetryExecutor(policy);
		AtomicInteger attempts = new AtomicInteger();

		Callable<String> operation = () -> {
			if (attempts.incrementAndGet() < 3){
				throw new IOException("Temporary failure");
			}

			return "Success";
		};

		long start = System.nanoTime();

		String result = executor.execute(operation);

		long elapsedNanos = System.nanoTime() - start;

		Assertions.assertEquals("Success", result);
		Assertions.assertEquals(3, attempts.get());

		assertTrue(
				elapsedNanos >= Duration.ofMillis(200).toNanos(),
				"Expected at least 200 ms for two retry delays"
		);
	}

	@Test
	void shouldNotDelayAfterFinalFailure() {
		RetryPolicy policy = RetryPolicy.builder()
				.delay(Duration.ofMillis(100))
				.build();
		RetryExecutor executor = new RetryExecutor(policy);
		AtomicInteger attempts = new AtomicInteger();

		Callable<String> operation = () -> {
			if (attempts.incrementAndGet() < 4){
				throw new IOException("Temporary failure");
			}

			return "Success";
		};

		long start = System.nanoTime();

		IOException exception = Assertions.assertThrows(IOException.class, () -> executor.execute(operation));

		long elapsedNanos = System.nanoTime() - start;

		Assertions.assertEquals(3, attempts.get());
		Assertions.assertEquals("Temporary failure", exception.getMessage());
		assertTrue(
				elapsedNanos <= Duration.ofMillis(250).toNanos(),
				"Expected at most 250 ms for two retry delays"
		);
	}

	@Test
	void shouldNotRetryInterruptedOperation() {
		RetryPolicy policy = RetryPolicy.builder().build();
		RetryExecutor executor = new RetryExecutor(policy);
		AtomicInteger attempts = new AtomicInteger();

		Callable<String> operation = () -> {
			if (attempts.incrementAndGet() == 1){
				throw new InterruptedException("Interruption failure");
			}

			return "Success";
		};

		InterruptedException exception = Assertions.assertThrows(InterruptedException.class, () -> executor.execute(operation));

		Assertions.assertEquals(1, attempts.get());
		Assertions.assertEquals("Interruption failure", exception.getMessage());
	}

	@Test
	void shouldStopWhenDelayIsInterrupted() {
		//TODO
	}
}