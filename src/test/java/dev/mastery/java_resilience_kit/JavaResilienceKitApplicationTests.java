package dev.mastery.java_resilience_kit;

import dev.mastery.java_resilience_kit.resilience.RetryPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@SpringBootTest
class JavaResilienceKitApplicationTests {

	@Test
	void shouldCreatePolicyWithDefaults(){
		RetryPolicy policy = RetryPolicy.builder().build();

		Assertions.assertEquals(3, policy.maxAttempts());
		Assertions.assertEquals(Duration.ZERO, policy.delay());
	}

	@Test
	void shouldCreatePolicyWithCustomValues(){
		int customMaxAttempts = 5;
		Duration customDuration = Duration.of(3, ChronoUnit.HOURS);

		RetryPolicy policy = RetryPolicy.builder()
				.maxAttempts(customMaxAttempts)
				.delay(customDuration)
				.build();

		Assertions.assertEquals(customMaxAttempts, policy.maxAttempts());
		Assertions.assertEquals(customDuration, policy.delay());
	}

	@Test
	void shouldRejectZeroMaxAttempts(){
		int customMaxAttempts = 0;
		Duration customDuration = Duration.of(3, ChronoUnit.HOURS);

		Exception exception = Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> RetryPolicy.builder()
						.maxAttempts(customMaxAttempts)
						.delay(customDuration)
						.build()
		);

		Assertions.assertEquals("maxAttempts should be greater than 0", exception.getMessage());
	}

	@Test
	void shouldRejectNegativeMaxAttempts(){
		int customMaxAttempts = -5;
		Duration customDuration = Duration.of(3, ChronoUnit.HOURS);

		Exception exception = Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> RetryPolicy.builder()
						.maxAttempts(customMaxAttempts)
						.delay(customDuration)
						.build()
		);

		Assertions.assertEquals("maxAttempts should be greater than 0", exception.getMessage());
	}

	@Test
	void shouldRejectNegativeDelay(){
		int customMaxAttempts = 5;
		Duration customDuration = Duration.of(-3, ChronoUnit.HOURS);

		Exception exception = Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> RetryPolicy.builder()
						.maxAttempts(customMaxAttempts)
						.delay(customDuration)
						.build()
		);

		Assertions.assertEquals("delay must not be negative", exception.getMessage());
	}

	@Test
	void shouldRejectNullDelay(){
		int customMaxAttempts = 5;

		Exception exception = Assertions.assertThrows(
				NullPointerException.class,
				() -> RetryPolicy.builder()
						.maxAttempts(customMaxAttempts)
						.delay(null)
						.build()
		);

		Assertions.assertEquals("delay must not be null", exception.getMessage());
	}

	@Test
	void shouldCreateIndependentPolicies(){
		RetryPolicy policyA = RetryPolicy.builder().build();
		RetryPolicy policyB = RetryPolicy.builder().maxAttempts(5).build();

		Assertions.assertNotEquals(policyA,policyB);
	}

}
