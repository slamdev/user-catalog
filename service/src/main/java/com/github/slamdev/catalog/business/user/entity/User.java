package com.github.slamdev.catalog.business.user.entity;

import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.net.URI;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String companyName;

    @NotNull
    private URI website;

    @NotBlank
    @Email
    private String email;
}
