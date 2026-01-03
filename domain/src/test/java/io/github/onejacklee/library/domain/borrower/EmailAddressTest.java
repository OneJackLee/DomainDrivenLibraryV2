package io.github.onejacklee.library.domain.borrower;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("EmailAddress")
class EmailAddressTest {

    @Nested
    @DisplayName("creation")
    class Creation {

        @Test
        @DisplayName("should create with valid email")
        void shouldCreateWithValidEmail() {
            EmailAddress email = EmailAddress.create("john.doe@example.com");

            assertThat(email.value()).isEqualTo("john.doe@example.com");
        }

        @Test
        @DisplayName("should normalize email to lowercase")
        void shouldNormalizeToLowercase() {
            EmailAddress email = EmailAddress.create("John.Doe@Example.COM");

            assertThat(email.value()).isEqualTo("john.doe@example.com");
        }

        @Test
        @DisplayName("should trim whitespace")
        void shouldTrimWhitespace() {
            EmailAddress email = EmailAddress.create("  john.doe@example.com  ");

            assertThat(email.value()).isEqualTo("john.doe@example.com");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "user@domain.com",
                "user.name@domain.com",
                "user+tag@domain.com",
                "user@subdomain.domain.com",
                "user123@domain.co.uk"
        })
        @DisplayName("should accept valid email formats")
        void shouldAcceptValidFormats(String validEmail) {
            EmailAddress email = EmailAddress.create(validEmail);

            assertThat(email.value()).isEqualTo(validEmail.toLowerCase().trim());
        }
    }

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("should throw when email is null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> EmailAddress.create(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Email address cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "invalid",
                "invalid@",
                "@domain.com",
                "user@",
                "user@domain",
                "user domain.com",
                "user@@domain.com"
        })
        @DisplayName("should throw for invalid email format")
        void shouldThrowForInvalidFormat(String invalidEmail) {
            assertThatThrownBy(() -> EmailAddress.create(invalidEmail))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid email address");
        }
    }

    @Nested
    @DisplayName("equality")
    class Equality {

        @Test
        @DisplayName("should be equal for same value")
        void shouldBeEqualForSameValue() {
            EmailAddress email1 = EmailAddress.create("john.doe@example.com");
            EmailAddress email2 = EmailAddress.create("john.doe@example.com");

            assertThat(email1).isEqualTo(email2);
            assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
        }

        @Test
        @DisplayName("should be equal when normalized")
        void shouldBeEqualWhenNormalized() {
            EmailAddress email1 = EmailAddress.create("John.Doe@Example.COM");
            EmailAddress email2 = EmailAddress.create("john.doe@example.com");

            assertThat(email1).isEqualTo(email2);
        }

        @Test
        @DisplayName("should not be equal for different values")
        void shouldNotBeEqualForDifferentValues() {
            EmailAddress email1 = EmailAddress.create("john.doe@example.com");
            EmailAddress email2 = EmailAddress.create("jane.doe@example.com");

            assertThat(email1).isNotEqualTo(email2);
        }
    }

    @Test
    @DisplayName("toString should return value")
    void toStringShouldReturnValue() {
        EmailAddress email = EmailAddress.create("john.doe@example.com");

        assertThat(email.toString()).isEqualTo("john.doe@example.com");
    }
}
