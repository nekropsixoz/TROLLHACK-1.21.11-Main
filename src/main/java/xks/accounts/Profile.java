package xks.accounts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author sekynda
 * @since 25.06.2023
 */
@Getter
@Setter
@AllArgsConstructor
public class Profile {
    private String name = "Paster";
    private int uid = 1337;
    private String role = "User";
}