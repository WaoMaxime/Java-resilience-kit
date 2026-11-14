package dev.mastery.java_resilience_kit.resilience;

import java.util.Objects;
import java.util.concurrent.Callable;

public final class RetryExecutor {

	private final RetryPolicy policy;

	public RetryExecutor(RetryPolicy policy) {
		this.policy = Objects.requireNonNull(
				policy,
				"The policy cannot be null"
		);
	}

	public <T> T execute(Callable<T> operation) throws Exception {
		Objects.requireNonNull(operation);

		int counter = 0;

		while (counter < policy.maxAttempts()) {
			try {
				counter++;
				return operation.call();
			} catch (InterruptedException exception) {
				Thread.currentThread().interrupt();
				throw exception;
			} catch (Exception exception) {
				if (counter == policy.maxAttempts()) {
					throw exception;
				}
				Thread.sleep(policy.delay());
			}
		}
		throw new IllegalStateException(
				"Retry execution terminated unexpectedly"
		);
	}
}
