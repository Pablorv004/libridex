package com.main.libridex.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SecureUserDTO {
    private Integer id;
    private String name;
    private String surname;
    private String email;
    private String role;
    private boolean activated;
}
