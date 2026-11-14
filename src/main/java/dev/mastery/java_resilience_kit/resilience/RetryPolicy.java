package dev.mastery.java_resilience_kit.resilience;

import java.time.Duration;
import java.util.Objects;

public final class RetryPolicy {

	private final int maxAttempts;
	private final Duration delay;

	private RetryPolicy(Builder builder) {
		this.maxAttempts = builder.maxAttempts;
		this.delay = builder.delay;

		checkMaxAttempts();
		checkDelay();
	}

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder {

		private int maxAttempts = 3;
		private Duration delay = Duration.ZERO;

		public Builder maxAttempts(int maxAttempts){
			this.maxAttempts = maxAttempts;
			return this;
		}

		public Builder delay(Duration delay){
			this.delay = delay;
			return this;
		}

		public RetryPolicy build(){
			return new RetryPolicy(this);
		}
	}

	private void checkMaxAttempts(){
		if (maxAttempts < 1){
			throw new IllegalArgumentException("maxAttempts should be greater than 0");
		}
	}

	private void checkDelay(){
		Objects.requireNonNull(delay, "delay must not be null");

		if (delay.isNegative()){
			throw new IllegalArgumentException("delay must not be negative");
		}
	}

	public int maxAttempts() {
		return maxAttempts;
	}

	public Duration delay() {
		return delay;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RetryPolicy policy)) return false;
		return maxAttempts == policy.maxAttempts && Objects.equals(delay, policy.delay);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maxAttempts, delay);
	}
}
