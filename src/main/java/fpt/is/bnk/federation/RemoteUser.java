package fpt.is.bnk.federation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Admin 12/1/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RemoteUser {

    // User
    Long id;
    String username;
    String email;

    // Profile
    String firstName;
    String lastName;
    String dob;

    // Base Entity
    Boolean active;

}
