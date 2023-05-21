package com.animatronics.spring.security.mongodb.controllers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.animatronics.spring.security.mongodb.models.ERole;
import com.animatronics.spring.security.mongodb.models.Role;
import com.animatronics.spring.security.mongodb.models.User;
import com.animatronics.spring.security.mongodb.models.MoneyDiscipline;
import com.animatronics.spring.security.mongodb.payload.request.LoginRequest;
import com.animatronics.spring.security.mongodb.payload.request.SignupRequest;
import com.animatronics.spring.security.mongodb.payload.response.MessageResponse;
import com.animatronics.spring.security.mongodb.payload.response.UserInfoResponse;
import com.animatronics.spring.security.mongodb.repository.MDRepository;
import com.animatronics.spring.security.mongodb.repository.RoleRepository;
import com.animatronics.spring.security.mongodb.repository.UserRepository;
import com.animatronics.spring.security.mongodb.security.jwt.JwtUtils;
import com.animatronics.spring.security.mongodb.security.services.UserDetailsImpl;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  MDRepository mdRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    ResponseCookie jwtCookie = jwtUtils.generateJwtLocalStorage(userDetails);

    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(new UserInfoResponse(userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles,
            jwtCookie.getValue()));
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!"));
    }

    // avatar
    String gravatarUrl = generateGravatarUrl(signUpRequest.getEmail());

    // Create new user's account
    User user = new User(signUpRequest.getUsername(),
        signUpRequest.getEmail(),
        encoder.encode(signUpRequest.getPassword()), gravatarUrl);

    Set<String> strRoles = signUpRequest.getRoles();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin":
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);

            break;
          case "mod":
            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(modRole);

            break;
          default:
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    mdRepository.save(new MoneyDiscipline(user.getId(), 0, new String[] { "create account" },
        new String[]{ "+Rp 0" }, signUpRequest.getUsername(), new LocalDateTime[] { LocalDateTime.now() }));

    return ResponseEntity.ok(new MessageResponse("User and money notes registered successfully!"));
  }

  @GetMapping("/users")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<List<User>> getAllUsers() {
    try {
      List<User> users = userRepository.findAll();

      if (users.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(users, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/users/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<User> updateUserById(@PathVariable("id") String id, @RequestBody User user) {
    Optional<User> userData = userRepository.findById(id);

    if (userData.isPresent()) {
      User _user = userData.get();
      _user.setUsername(user.getUsername());
      _user.setEmail(user.getEmail());
      _user.setPassword(user.getPassword());
      // _user.setRoles(user.getRoles());
      return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/users")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<MessageResponse> deleteAllUsers() {
    try {
      userRepository.deleteAll();
      return ResponseEntity.ok(new MessageResponse("All users have been deleted successfully!"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new MessageResponse("Could not delete all users!"));
    }
  }

  @DeleteMapping("/users/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<MessageResponse> deleteUserById(@PathVariable("id") String id) {
    try {
      String user = userRepository.findById(id).get().getUsername();
      Optional<User> userData = userRepository.findByUsername(user);
      Set<Role> roles = userData.get().getRoles();

      for (Role role : roles) {
        if (role.getId().equals("6420f5471a943f643d51bcf6")) {
          return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Could not delete admin user!"));
        }
      }
      userRepository.deleteById(id);
      mdRepository.deleteById(id);
      return ResponseEntity.ok(new MessageResponse("User with ID " + id + " has been deleted successfully!"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new MessageResponse("Could not delete user with ID " + id));
    }
  }

  private String generateGravatarUrl(String email) {
    try {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(email.trim().toLowerCase().getBytes());
        byte[] digest = md.digest();
        StringBuilder builder = new StringBuilder();
        for (byte b : digest) {
            builder.append(String.format("%02x", b));
        }
        String hash = builder.toString();
        return "https://www.gravatar.com/avatar/" + hash;
    } catch (NoSuchAlgorithmException e) {
        // Handle the exception
        return ""; // Or provide a default avatar URL
    }
}

}
